package com.elevintech.motorbro.Glovebox

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.elevintech.motorbro.Model.Insurance
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Constants
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_insurance.*
import java.io.File
import java.util.*

class InsuranceActivity : AppCompatActivity() {

    var imageUri: Uri? = null
    var uploadMainImageFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insurance)

        backButton.setOnClickListener {
            finish()
        }

        checkMarkButton.setOnClickListener {
            saveInsuranceData()
        }

        frontInsuranceLayout.setOnClickListener {
            // TODO: Do the camera here

//            askUploadSource()
            uploadMainImageFlag = true

//            val hasImage = (user.userProfileMainImageUrl != "")
//
//            if (hasImage){
//                confirmDelete{
//                    doDeleteOfMainImage {
//                        reloadUserData()
//                    }
//                }
//            } else {
//                askUploadSource()
//                uploadMainImageFlag = true
//            }

        }

        backInsuranceLayout.setOnClickListener {

        }
    }

//    private fun askUploadSource(){
//
//
//        val options = arrayOf("Open Gallery", "Capture Photo")
//        val builder = android.app.AlertDialog.Builder(this)
//        builder.setTitle("Please pick your image source")
//        builder.setItems(options){ _, which ->
//
//            if(which == 0){
//
//                // ASK PERMISSION TO OPEN GALLERY
//                RuntimePermission.askPermission(this)
//                    .request(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    .onAccepted{
//
//
//                        // TODO: can't resize selected image, because it's uri is "content://" instead of "file://"
//                        // we still need to resize it though
//                        // OPEN GALLERY
//                        openGallery(Constants.REQUEST_CODES.OPEN_GALLERY)
//
//                    }
//                    .ask()
//
//            }else if(which == 1){
//
//                // ASK PERMISSION TO OPEN CAMERA
//                RuntimePermission.askPermission(this)
//                    .request(Manifest.permission.CAMERA)
//                    .onAccepted{
//
//                        // OPEN CAMERA
//                        openCamera(Constants.REQUEST_CODES.OPEN_CAMERA)
//                    }
//                    .ask()
//
//            }
//        }
//        builder.show()
//    }

    fun openCamera(requestCode: Int){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var filename = UUID.randomUUID().toString() + ".jpg"
        var file = File(this.externalCacheDir, filename)
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, requestCode)
    }

    fun openGallery(requestCode: Int){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    private fun saveInsuranceData() {

        val insurance = Insurance()
        insurance.number = insuranceNumberText.text.toString()
        insurance.expiration = insuranceNumberText.text.toString()
        //insurance.expirationDate =
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == RESULT_OK) {
//
//            if (requestCode == Constants.REQUEST_CODES.OPEN_CAMERA && imageUri != null){
//
//                if (uploadMainImageFlag){
//                    uploadToStorageAndSaveMainImageUrl(imageUri!!){
//                        getUserData()
//                    }
//                } else {
//                    uploadToStorageAndSaveImageUrl(imageUri!!){
//                        getUserData()
//                    }
//                }
//
//            }
//
//            else if (requestCode == Constants.REQUEST_CODES.OPEN_GALLERY && data!= null){
//
//                if (uploadMainImageFlag){
//                    uploadToStorageAndSaveMainImageUrl(data!!.data){
//                        getUserData()
//                    }
//                } else {
//                    uploadToStorageAndSaveImageUrl(data!!.data){
//                        getUserData()
//                    }
//                }
//
//            }
//
//        }

//   }

//    private fun uploadToStorageAndSaveImageUrl( imageUri: Uri, isFrontImage: Boolean, callback: () -> Unit ){
//
//        // TODO: Validation here
//
//        val db = MotoroBroDatabase()
//        db.uploadDocumentsToFirebaseStorage(imageUri) {
//            // if front is successful save it
//            db.saveFrontInsuranceImageDocument(it)
//
//        }
//        // UPLOAD TO STORAGE
//        DateFilipinaDatabase().uploadImageToFirebaseStorage(imageUri){
//
//            // SAVE IMAGE URL
//            DateFilipinaDatabase().saveProfileImage(it){
//
//                callback()
//
//            }
//        }
//
//    }


}
