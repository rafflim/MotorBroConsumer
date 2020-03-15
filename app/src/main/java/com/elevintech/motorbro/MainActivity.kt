package com.elevintech.motorbro

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbro.CreateAccount.CreateAccountActivity
import com.elevintech.motorbro.Dashboard.DashboardActivity
import com.elevintech.motorbro.Model.UserType
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var maleGenderLayoutIsClicked = false
    var femaleGenderLayoutIsClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        createAccountText.setOnClickListener {
            val intent = Intent(applicationContext, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            doLogin()
        }


    }

    fun doLogin(){

        if (userNameEditText.text.toString() == "" || passwordEditText.text.toString() == ""){
            Toast.makeText(baseContext, "Please Fill up the username and password field", Toast.LENGTH_SHORT).show()
        } else {

            var progressDialog = Utils().easyProgressDialog(this, "Logging in....")
            progressDialog.show()

            FirebaseAuth.getInstance().signInWithEmailAndPassword("${userNameEditText.text}", "${passwordEditText.text}")
                .addOnSuccessListener {
                    checkUserType(progressDialog)

                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(baseContext, "Authentication failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun checkUserType(progressDialog: ProgressDialog) {
        var db = MotoroBroDatabase()
        db.getUserType{ userType ->

            if (userType == UserType.Type.EMPLOYEE || userType == UserType.Type.OWNER){
                progressDialog.dismiss()

                Toast.makeText(baseContext, "Please use the MotorBroShop app", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
            } else {
                progressDialog.dismiss()
                goToDashBoardActivity()
            }

        }
    }

    fun goToDashBoardActivity(){
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
