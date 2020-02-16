package com.elevintech.motorbro.CreateAccount

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elevintech.motorbro.BikeRegistration.BikeRegistrationActivity
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Model.User
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_account.*
import com.github.florent37.runtimepermission.RuntimePermission
import java.io.File
import java.util.*

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var maleGenderLayoutIsClicked = false
    var femaleGenderLayoutIsClicked = false
    var genderIsSelected = false

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11

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

        imgMainProfile.setOnClickListener {
            askUploadSource()
        }
    }

    private fun askUploadSource(){

        val options = arrayOf("Open Gallery", "Capture Photo")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Please pick your image source")
        builder.setItems(options){ _, which ->

            if(which == 0){

                // ASK PERMISSION TO OPEN GALLERY
                RuntimePermission.askPermission(this)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .onAccepted{

                        // OPEN GALLERY
                        openGallery()

                    }
                    .ask()

            }else if(which == 1){

                // ASK PERMISSION TO OPEN CAMERA
                RuntimePermission.askPermission(this)
                    .request(Manifest.permission.CAMERA)
                    .onAccepted{

                        // OPEN CAMERA
                        openCamera()
                    }
                    .ask()

            }
        }
        builder.show()
    }

    fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var filename = UUID.randomUUID().toString() + ".jpg"
        var file = File(this.externalCacheDir, filename)
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, OPEN_CAMERA)
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_GALLERY && data!= null) { imageUri = data!!.data }

        if (resultCode == RESULT_OK && imageUri != null) {

            // do something with the image here
            mainProfilePhoto.setImageURI(imageUri)
            mainProfilePhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.GONE

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

                    val user = User()

                    user.uid = task.result!!.user!!.uid
                    user.firstName = firstNameEditText.text.toString()
                    user.lastName = lastNameEditText.text.toString()
                    user.gender = gender
                    user.email = email
                    user.usersRegistrationProgress = 1

                    firebaseDatabase.createUserType {

                            firebaseDatabase.uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->

                                user.profileImage = imageUrl

                                firebaseDatabase.createUser(user){

                                    progressDialog.dismiss()
                                    val intent = Intent(applicationContext, BikeRegistrationActivity::class.java)
                                    startActivity(intent)

                                    finish()

                                }

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

        if (imageUri == null){
            Toast.makeText(this, "Please upload a profile image", Toast.LENGTH_LONG).show()
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
