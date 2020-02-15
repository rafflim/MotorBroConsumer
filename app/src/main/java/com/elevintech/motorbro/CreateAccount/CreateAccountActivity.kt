package com.elevintech.motorbro.CreateAccount

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.elevintech.motorbro.BikeRegistration.BikeRegistrationActivity
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Model.User
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var maleGenderLayoutIsClicked = false
    var femaleGenderLayoutIsClicked = false
    var genderIsSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        createAccountBackImageView.setOnClickListener {
            finish()
        }

        maleGenderLayout.setOnClickListener {
            selectGenderLayout(true)
        }

        femaleGenderLayout.setOnClickListener {
            selectGenderLayout(false)
        }

        createAccountButton.setOnClickListener {
            if (hasCompletedValues()) {
                registerUser()
            }
        }
    }

    fun registerUser() {

        var gender: String
        if (maleGenderLayoutIsClicked) {
            gender = "male"
        } else {
            gender = "female"
        }

        val firebaseDatabase = MotoroBroDatabase()

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val progressDialog = Utils().easyProgressDialog(this, "Registering your profile....")
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val uid = task.result!!.user!!.uid
                    val user = User(uid, firstNameEditText.text.toString(), lastNameEditText.text.toString(), gender, email)


                    firebaseDatabase.createUserType {

                        firebaseDatabase.createUser(user){

                            progressDialog.dismiss()
                            val intent = Intent(applicationContext, BikeRegistrationActivity::class.java)
                            startActivity(intent)

                            finish()

                        }

                    }


                } else {
                    Toast.makeText(baseContext, "${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }

    }

    fun hasCompletedValues(): Boolean {
        if (firstNameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the first name field", Toast.LENGTH_LONG).show()
            return false
        }

        if (lastNameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the last name field", Toast.LENGTH_LONG).show()
            return false
        }

        if (!genderIsSelected) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_LONG).show()
            return false
        }

        if (emailEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the email field", Toast.LENGTH_LONG).show()
            return false
        }

        if (passwordEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the password field", Toast.LENGTH_LONG).show()
            return false
        }

        if (confirmPasswordEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the confirm password field", Toast.LENGTH_LONG).show()
            return false
        }

        if (passwordEditText.text.toString() != confirmPasswordEditText.text.toString()) {
            Toast.makeText(this, "Please make sure your password is the same as your confirm password text", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    fun selectGenderLayout(isMaleGender: Boolean) {
        genderIsSelected = true
        if (isMaleGender) {

            maleGenderLayoutIsClicked = true
            femaleGenderLayoutIsClicked = false
            maleGenderLayout.background = getResources().getDrawable(R.drawable.genderbuttonselectedbackground)
            femaleGenderLayout.background = getResources().getDrawable(R.drawable.genderbuttonbackground)

        } else {
            maleGenderLayoutIsClicked = false
            femaleGenderLayoutIsClicked = true
            femaleGenderLayout.background = getResources().getDrawable(R.drawable.genderbuttonselectedbackground)
            maleGenderLayout.background = getResources().getDrawable(R.drawable.genderbuttonbackground)
        }
    }
}
