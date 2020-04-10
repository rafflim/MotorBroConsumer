package com.elevintech.motorbro.EditBike

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_bike_registration.*
import java.io.File
import java.util.*

class EditBikeActivity : AppCompatActivity() {

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11
    var bikeImageIsChanged = false


    lateinit var bike: BikeInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_registration)

        bike = intent.getSerializableExtra("bike") as BikeInfo

        displayBikeData()

        imgBikeProfile.setOnClickListener {

            askUploadSource()

        }

        sendButton.setOnClickListener {

            updateBike()

        }

    }

    private fun displayBikeData() {

        textView11.text = "Edit Bike"
        sendButton.text = "Update Info"

        editBrandText.setText( bike.brand )
        editModelText.setText( bike.model )
        editPlateNumberText.setText( bike.plateNumber )
        editOdometerText.setText( bike.odometer )
        editNicknameText.setText( bike.nickname )
        yearBoughtEditText.setText( bike.yearBought )

        if (bike.imageUrl != "") {

            Glide.with(this).load(bike.imageUrl).into(bikePhoto)
            bikePhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.INVISIBLE

        }
    }

    private fun updateBike() {
        val progressDialog = Utils().easyProgressDialog(this, "Updating Bike....")
        progressDialog.show()

        // the default bikes created at the start of registration do not have a bikeId
        // set it as the user ID
        // TODO: on registration set a value for the user ID
        if (bike.bikeId == ""){
            bike.bikeId = FirebaseAuth.getInstance().uid!!
        }

        bike.brand = editBrandText.text.toString()
        bike.model = editModelText.text.toString()
        bike.plateNumber = editPlateNumberText.text.toString()
        bike.odometer = editOdometerText.text.toString()
        bike.nickname = editNicknameText.text.toString()
        bike.yearBought = yearBoughtEditText.text.toString()


        // IF BIKE IMAGE IS UPDATED
        if (bikeImageIsChanged){

            // UPLOAD NEW IMAGE TO STORE FIRST
            MotoroBroDatabase().uploadImageToFirebaseStorage(imageUri!!){

                bike.imageUrl = it

                // AND THEN SAVE THE NEW VALUES
                MotoroBroDatabase().updateBikeById(bike.bikeId, bike){

                    progressDialog.dismiss()
                    finish()

                }

            }
        }

        // IF BIKE IMAGE IS NOT CHANGED
        else {

            // SAVE DIRECTLY
            MotoroBroDatabase().updateBikeById(bike.bikeId, bike){

                progressDialog.dismiss()
                finish()

            }

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
            bikePhoto.setImageURI(imageUri)
            bikePhoto.visibility = View.VISIBLE
            emptyImageIcon.visibility = View.GONE

            bikeImageIsChanged = true

        }

    }

}
