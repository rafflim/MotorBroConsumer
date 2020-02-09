package com.elevintech.motorbro.Glovebox

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.elevintech.motorbro.Model.MotorRegistration
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.github.florent37.runtimepermission.RuntimePermission
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_motor_registration.*
import java.io.File
import java.text.DecimalFormat
import java.util.*

class MotorRegistrationActivity : AppCompatActivity() {


    var imageUri: Uri? = null
    var dateInMilliseconds = 0.toLong()
    var uploadSource = ""

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motor_registration)

        backButton.setOnClickListener {
            finish()
        }

        // uri exposure fix
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        backButton.setOnClickListener {
            finish()
        }

        checkMarkButton.setOnClickListener {

            if (hasCompletedValues()){

                saveMotorRegistraion()

            }

        }

        motorRegistrationLayout.setOnClickListener {
            askUploadSource(1)
        }

        registrationExpirationText.setOnClickListener {
            setDatePickerAction()
            openDatePicker()
        }

        getMotorRegistrationDocument()


    }


    private fun getMotorRegistrationDocument() {

        MotoroBroDatabase().getMotorRegistrationDocument { motorRegistration ->
            if (motorRegistration != null){
                updateUI(motorRegistration)
            }
        }

    }

    private fun updateUI(motorRegistration: MotorRegistration) {
        registrationExpirationText.setText(motorRegistration.expiration)

        motorRegistrationImageView.visibility = View.VISIBLE
        Picasso.get().load(motorRegistration.imageUrl).into(motorRegistrationImageView)

    }

    private fun openDatePicker() {

        // INSTANTIATE CALENDAR
        var cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 0)

        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        // INSTANTIATE DATE PICKER DIALOG
        var dialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day)

        // SET DATE PICKER DIALOG STYLE
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // SHOW THE DATE PICKER DIALOG
        dialog.show()

    }

    private fun setDatePickerAction() {

        // SET ACTION AFTER SELECTING THE DATE
        mDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->

            // FORMAT  DAY and MONTH to always show two digits (add zero if single digit)
            var mFormat = DecimalFormat("00");
            var monthString = mFormat.format(month + 1)
            var dayString = mFormat.format(day)

            dateInMilliseconds = Utils().convertDateToMilliseconds(year, month, day, 12, 0, 0)

            // PUT THE SELECTED DATE ON THE DATE PLACEHOLDER
            registrationExpirationText.setText("${dayString}/${monthString}/${year}")


        }
    }

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

    private fun askUploadSource(requestCode: Int){

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
                        openGallery(requestCode)
                        uploadSource = "gallery"

                    }
                    .ask()

            }else if(which == 1){

                // ASK PERMISSION TO OPEN CAMERA
                RuntimePermission.askPermission(this)
                    .request(Manifest.permission.CAMERA)
                    .onAccepted{

                        // OPEN CAMERA
                        openCamera(requestCode)
                        uploadSource = "camera"
                    }
                    .ask()

            }
        }
        builder.show()
    }

    private fun saveMotorRegistraion() {

        val progressDialog = Utils().easyProgressDialog(this, "Saving Motorcycle Registration")
        progressDialog.show()

        MotoroBroDatabase().uploadDocumentsToFirebaseStorage(imageUri!!) { imageUrl ->

            val motorRegistration = MotorRegistration()
            motorRegistration.expiration = registrationExpirationText.text.toString()
            motorRegistration.expirationDate = dateInMilliseconds
            motorRegistration.imageUrl = imageUrl


            MotoroBroDatabase().saveMotorRegistrationDocument(motorRegistration){
                progressDialog.dismiss()

                finish()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (uploadSource == "gallery" && data!= null) imageUri = data!!.data

        if (resultCode == RESULT_OK) {

            if (requestCode == 1){

                motorRegistrationImageView.setImageURI(imageUri)
                motorRegistrationImageView.visibility = View.VISIBLE

            }
        }

    }

    fun hasCompletedValues(): Boolean {


        if (registrationExpirationText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the expiration field", Toast.LENGTH_LONG).show()
            return false
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_LONG).show()
            return false
        }


        return true
    }
}
