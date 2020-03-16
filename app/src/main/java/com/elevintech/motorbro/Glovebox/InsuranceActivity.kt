package com.elevintech.motorbro.Glovebox

import android.Manifest
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import com.elevintech.motorbro.Model.Insurance
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_insurance.*
import java.io.File
import java.util.*
import com.github.florent37.runtimepermission.RuntimePermission
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class InsuranceActivity : AppCompatActivity() {

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    var dateInMilliseconds = 0.toLong()

    var FRONT_INSURANCE_PAPER = 1
    var BACK_INSURANCE_PAPER = 2

    var imageUri: Uri? = null

    var frontInsurancePaperUri: Uri? = null
    var backInsurancePaperUri: Uri? = null

    var frontInsurancePaperUrl: String = ""
    var backInsurancePaperUrl: String = ""

    var uploadSource: String = ""

    lateinit var progressDialog: ProgressDialog

    var isEditing = false
    var insurance: Insurance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insurance)

        progressDialog = Utils().easyProgressDialog(this, "Saving Insurance Document")

        // uri exposure fix
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        backButton.setOnClickListener {
            finish()
        }

        checkMarkButton.setOnClickListener {

            if (hasCompletedValues()){

                // check if we are editing and if there are any values changed at all
                if (hasChanges()){
                    progressDialog.show()
                    saveInsuranceData()

                } else {

                    finish()

                }


            }

        }

        frontInsuranceLayout.setOnClickListener {
            askUploadSource(FRONT_INSURANCE_PAPER)
        }

        backInsuranceLayout.setOnClickListener {
            askUploadSource(BACK_INSURANCE_PAPER)
        }

        insuranceExpirationText.setOnClickListener {
            setDatePickerAction()
            openDatePicker()
        }

        getInsuranceDocument()
    }

    private fun hasChanges(): Boolean {
        if (isEditing){

            return (insurance!!.number != insuranceNumberText.text.toString() ||
                    insurance!!.expiration != insuranceExpirationText.text.toString() ||
                    frontInsurancePaperUri != null ||
                    backInsurancePaperUri != null)

        } else {

            // return true if not editing, it means user is creating a new document
            return true
        }
    }

    private fun getInsuranceDocument() {

        MotoroBroDatabase().getInsuranceDocument{insuranceDoc ->
            if (insuranceDoc != null){
                insurance = insuranceDoc
                isEditing = true

                updateUI()
            }
        }

    }

    private fun updateUI() {
        insuranceNumberText.setText(insurance!!.number)
        insuranceExpirationText.setText(insurance!!.expiration)

        frontInsuranceImageView.visibility = VISIBLE
        Picasso.get().load(insurance!!.frontImageUrl).into(frontInsuranceImageView)

        backInsuranceImageView.visibility = VISIBLE
        Picasso.get().load(insurance!!.backImageUrl).into(backInsuranceImageView)
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


                        // TODO: can't resize selected image, because it's uri is "content://" instead of "file://"
                        // we still need to resize it though
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

        if (frontInsurancePaperUri != null && backInsurancePaperUri != null){

            MotoroBroDatabase().uploadDocumentsToFirebaseStorage(frontInsurancePaperUri!!){ frontInsurancePaper ->
                frontInsurancePaperUrl = frontInsurancePaper

                MotoroBroDatabase().uploadDocumentsToFirebaseStorage(backInsurancePaperUri!!){ backInsurancePaper->
                    backInsurancePaperUrl = backInsurancePaper

                    val newInsurance = Insurance()
                    newInsurance.number = insuranceNumberText.text.toString()
                    newInsurance.expiration = insuranceExpirationText.text.toString()
                    newInsurance.expirationDate = dateInMilliseconds
                    newInsurance.frontImageUrl = frontInsurancePaperUrl
                    newInsurance.backImageUrl = backInsurancePaperUrl

                    MotoroBroDatabase().saveInsuranceDocuments(newInsurance){
                        progressDialog.dismiss()

                        finish( )
                    }
                }
            }
        }
        else if ( frontInsurancePaperUri != null &&  backInsurancePaperUri == null){

            MotoroBroDatabase().uploadDocumentsToFirebaseStorage(frontInsurancePaperUri!!) { frontInsurancePaper ->
                frontInsurancePaperUrl = frontInsurancePaper


                val newInsurance = Insurance()
                newInsurance.number = insuranceNumberText.text.toString()
                newInsurance.expiration = insuranceExpirationText.text.toString()
                newInsurance.expirationDate = dateInMilliseconds
                newInsurance.frontImageUrl = frontInsurancePaperUrl
                newInsurance.backImageUrl = insurance!!.backImageUrl

                MotoroBroDatabase().saveInsuranceDocuments(newInsurance) {
                    progressDialog.dismiss()

                    finish()
                }
            }

        } else if ( frontInsurancePaperUri == null &&  backInsurancePaperUri != null ) {

            MotoroBroDatabase().uploadDocumentsToFirebaseStorage(backInsurancePaperUri!!) { backInsurancePaper->
                backInsurancePaperUrl = backInsurancePaper


                val newInsurance = Insurance()
                newInsurance.number = insuranceNumberText.text.toString()
                newInsurance.expiration = insuranceExpirationText.text.toString()
                newInsurance.expirationDate = dateInMilliseconds
                newInsurance.frontImageUrl = insurance!!.frontImageUrl
                newInsurance.backImageUrl = backInsurancePaperUrl

                MotoroBroDatabase().saveInsuranceDocuments(newInsurance) {
                    progressDialog.dismiss()

                    finish()
                }

            }
        } else if ( frontInsurancePaperUri == null &&  backInsurancePaperUri == null ) {

            val newInsurance = Insurance()
            newInsurance.number = insuranceNumberText.text.toString()
            newInsurance.expiration = insuranceExpirationText.text.toString()
            newInsurance.expirationDate = dateInMilliseconds
            newInsurance.frontImageUrl = insurance!!.frontImageUrl
            newInsurance.backImageUrl = insurance!!.backImageUrl

            MotoroBroDatabase().saveInsuranceDocuments(newInsurance) {
                progressDialog.dismiss()

                finish()
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (uploadSource == "gallery" && data!= null) imageUri = data!!.data

        if (resultCode == RESULT_OK) {

            if (requestCode == FRONT_INSURANCE_PAPER){

                frontInsuranceImageView.setImageURI(imageUri)
                frontInsuranceImageView.visibility = VISIBLE

                frontInsurancePaperUri = imageUri

            }
            else if (requestCode == BACK_INSURANCE_PAPER){

                backInsuranceImageView.setImageURI(imageUri)
                backInsuranceImageView.visibility = VISIBLE

                backInsurancePaperUri = imageUri
            }
        }

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

    private fun setDatePickerAction(){

        // SET ACTION AFTER SELECTING THE DATE
        mDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->

            // FORMAT  DAY and MONTH to always show two digits (add zero if single digit)
            var mFormat = DecimalFormat("00");
            var monthString = mFormat.format(month + 1)
            var dayString = mFormat.format(day)

            dateInMilliseconds = Utils().convertDateToMilliseconds(year,month,day,12, 0, 0)

            // PUT THE SELECTED DATE ON THE DATE PLACEHOLDER
            insuranceExpirationText.setText("${dayString}/${monthString}/${year}")


        }

    }

    fun hasCompletedValues(): Boolean {
        if (insuranceNumberText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the insurance number field", Toast.LENGTH_LONG).show()
            return false
        }

        if (insuranceExpirationText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the insurace expiration field", Toast.LENGTH_LONG).show()
            return false
        }

        if (frontInsurancePaperUri == null && insurance == null) {
            Toast.makeText(this, "Please upload a front insurance paper image", Toast.LENGTH_LONG).show()
            return false
        }

        if (backInsurancePaperUri == null && insurance == null) {
            Toast.makeText(this, "Please upload a back insurance paper image", Toast.LENGTH_LONG).show()
            return false
        }


        return true
    }


}
