package com.elevintech.motorbro.BikeRegistration

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.elevintech.motorbro.Dashboard.DashboardActivity
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_bike_registration.*
import java.io.File
import java.util.*

class BikeRegistrationActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var OPEN_CAMERA = 10
    private var OPEN_GALLERY = 11

    private var openedFromWhatActivity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_registration)

        // uri exposure fix
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        openedFromWhatActivity = intent.getStringExtra("previousActivity")!!

        if (openedFromWhatActivity == "garage") {
            createAccountBackImageView.visibility = View.VISIBLE
        } else {
            createAccountBackImageView.visibility = View.GONE
        }


        imgBikeProfile.setOnClickListener {
            askUploadSource()
        }

        sendButton.setOnClickListener {
            registerBike()
        }

        createAccountBackImageView.setOnClickListener {
            finish()
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

    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var filename = UUID.randomUUID().toString() + ".jpg"
        var file = File(this.externalCacheDir, filename)
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, OPEN_CAMERA)
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_GALLERY && data!= null) { imageUri = data.data }

        if (resultCode == RESULT_OK && imageUri != null) {

            // do something with the image here
            bikePhoto.setImageURI(imageUri)
            bikePhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.GONE

        }

    }

    private fun validateFields(): Boolean {
        if (editBrandText.text.isEmpty()) {
            Toast.makeText(this, "Brand Text Field is Empty", Toast.LENGTH_LONG).show()
            return false
        }

        if(editModelText.text.isEmpty()) {
            Toast.makeText(this, "Model Text Field is Empty", Toast.LENGTH_LONG).show()
            return false
        }

        if (editPlateNumberText.text.isEmpty()) {
            Toast.makeText(this, "Your Plate Number field is empty", Toast.LENGTH_LONG).show()
            return false
        }

        if (editOdometerText.text.isEmpty()) {
            Toast.makeText(this, "Your Odometer field is empty", Toast.LENGTH_LONG).show()
            return false
        }

        if (editNicknameText.text.isEmpty()) {
            Toast.makeText(this, "Your Nickname field is empty", Toast.LENGTH_LONG).show()
            return false
        }

        if (yearBoughtEditText.text.isEmpty()) {
            Toast.makeText(this, "Your year acquired field is empty", Toast.LENGTH_LONG).show()
            return false
        }

//        if (imageUri == null){
//            Toast.makeText(this, "Please upload a bike image", Toast.LENGTH_LONG).show()
//            return false
//        }

        return true

    }

    private fun registerBike() {
        //add validation here

        if (!validateFields()) {
            return
        }

        val bike = BikeInfo()

        bike.brand = editBrandText.text.toString()
        bike.model = editModelText.text.toString()
        bike.plateNumber = editPlateNumberText.text.toString()
        bike.odometer = editOdometerText.text.toString()
        bike.nickname = editNicknameText.text.toString()
        bike.yearBought = yearBoughtEditText.text.toString() // TODO: Fix year bought to year selector
        bike.userId = FirebaseAuth.getInstance().uid!!
        bike.bikeId = FirebaseFirestore.getInstance().collection("bikes").document().id
        bike.primary = (openedFromWhatActivity == "splashPage" || openedFromWhatActivity == "createAccount") // set to true if created from splash page or create account activity

        val database = MotoroBroDatabase()
        val progressDialog = Utils().easyProgressDialog(this, "Registering Bike...")
        progressDialog.show()

        if (imageUri == null) {

            val resourceId = R.drawable.motor_empty_one
            val imageUriEmpty = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build()

            database.uploadImageToFirebaseStorage(imageUriEmpty!!) { imageUrl ->
                bike.imageUrl = imageUrl
                database.saveBikeInfo(bike) {
                    if (openedFromWhatActivity == "splashPage" || openedFromWhatActivity == "createAccount"){
                        database.updateUserRegistrationProgress(2) {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Bike Registration Successful!", Toast.LENGTH_LONG).show()
                            val intent = Intent(applicationContext, DashboardActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (openedFromWhatActivity == "garage"){
                        progressDialog.dismiss()
                        finish()
                    }
                }
            }
        } else {
            database.uploadImageToFirebaseStorage(imageUri!!) { imageUrl ->
                bike.imageUrl = imageUrl
                database.saveBikeInfo(bike) {
                    if (openedFromWhatActivity == "splashPage" || openedFromWhatActivity == "createAccount"){
                        database.updateUserRegistrationProgress(2) {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Bike Registration Successful!", Toast.LENGTH_LONG).show()
                            val intent = Intent(applicationContext, DashboardActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (openedFromWhatActivity == "garage"){
                        progressDialog.dismiss()
                        finish()
                    }
                }
            }
        }


    }
}
