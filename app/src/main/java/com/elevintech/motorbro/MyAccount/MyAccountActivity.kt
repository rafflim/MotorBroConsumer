package com.elevintech.motorbro.MyAccount

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.User
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.github.florent37.runtimepermission.RuntimePermission
import kotlinx.android.synthetic.main.activity_my_account.*
import java.io.File
import java.util.*

class MyAccountActivity : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        myAccountBackImageView.setOnClickListener {
            finish()
        }

        val db = MotoroBroDatabase()
        db.getUser { user ->

            Glide.with(this).load(user.profileImage).into(profileImageView)
            firstNameText.setText(user.firstName)
            lastNameText.setText(user.lastName)
            emailText.setText(user.email)

            saveButton.setOnClickListener{

                if (anyValuesAreChanged(user)){

                    val progressDialog = Utils().easyProgressDialog(this, "Saving New Values")
                    progressDialog.show()

                    updateUser(user){
                        progressDialog.dismiss()
                    }
                }

            }

        }

        imgProfile.setOnClickListener {
            askUploadSource()
        }



    }

    private fun updateUser(user: User, callback: ()->Unit) {

        val db = MotoroBroDatabase()

        var userFields = mutableMapOf<String, Any>("profileImage" to user.profileImage,
                                                                            "firstName" to firstNameText.text.toString(),
                                                                            "lastName" to lastNameText.text.toString())

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

        return (user.firstName != firstNameText.text.toString() || user.lastName != lastNameText.text.toString() || imageUri != null)

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
}
