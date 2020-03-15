package com.elevintech.motorbro.MyAccount

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.User
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_my_account.*
import kotlinx.android.synthetic.main.forgot_password_layout.view.*
import java.io.File
import java.util.*

class MyAccountActivity : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        auth = FirebaseAuth.getInstance()
        // uri exposure fix
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        myAccountBackImageView.setOnClickListener {
            finish()
        }

        changePasswordButton.setOnClickListener {
            createAlertDialog()
        }

        imgProfile.setOnClickListener {
            askUploadSource()
        }

        setupUserFields()

    }

    private fun setupUserFields() {
        val db = MotoroBroDatabase()
        db.getUser { user ->

            if (user.profileImage != "") {
                Glide.with(this).load(user.profileImage).into(profileImageView)
            }

            firstNameText.setText(user.firstName)
            lastNameText.setText(user.lastName)
            emailText.setText(user.email)
            phoneText.setText(user.phoneNumber)

            saveButton.setOnClickListener{
                if (anyValuesAreChanged(user)){
                    val progressDialog = Utils().easyProgressDialog(this, "Saving New Values")
                    progressDialog.show()
                    updateUser(user){
                        progressDialog.dismiss()
                        finish()
                    }
                }
            }
        }
    }

    private fun updateUser(user: User, callback: ()->Unit) {

        val db = MotoroBroDatabase()
        val userFields = mutableMapOf<String, Any>("profileImage" to user.profileImage,
                                                                            "firstName" to firstNameText.text.toString(),
                                                                            "lastName" to lastNameText.text.toString(),
                                                                            "phoneNumber" to phoneText.text.toString())

        if (imageUri != null){
            db.uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->
                userFields["profileImage"] = imageUrl
                db.udpateUserFields(userFields){
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    callback()
                }
            }
        } else {
            db.udpateUserFields(userFields){
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                callback()
            }
        }
    }

    private fun anyValuesAreChanged(user: User): Boolean {
        return (user.firstName != firstNameText.text.toString() || user.lastName != lastNameText.text.toString() || imageUri != null || user.phoneNumber != phoneText.text.toString())
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
            profileImageView.setImageURI(imageUri)
        }
    }

    private fun createAlertDialog(){
        // instantiate dialog
        val builder = AlertDialog.Builder(this)
        val optionDialog = AlertDialog.Builder(this).create()
        // instantiate the view for the dialog
        val viewInflated = layoutInflater.inflate(R.layout.forgot_password_layout, null)
        // inflate the view in the dialog
        builder.setView(viewInflated)
        // set the dialog title
        builder.setTitle("Forgot Password")
        builder.setCancelable(true) // can be set to false, to make the dialog undismissable
        builder.setPositiveButton("Send Email",
            DialogInterface.OnClickListener { dialog, which ->
                println("works")
                val email = viewInflated.emailEditDialogText.text.toString()
                //val someEmail = emailEditDialogText.
                println(email)
                auth.sendPasswordResetEmail(email)

                Toast.makeText(this, "Email Sent", Toast.LENGTH_SHORT).show()

                // dismiss dialog after
                dialog.dismiss()
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })

        builder.show()

    }
}
