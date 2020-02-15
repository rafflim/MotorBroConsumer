package com.elevintech.motorbro.BikeRegistration

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbro.Dashboard.DashboardActivity
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_bike_registration.*

class BikeRegistrationActivity : AppCompatActivity() {

    var brandDescription = ""
    var modelDescription = ""
    var ccDescription = ""
    var frontBreak = ""
    var rearBreak = ""
    var wheels = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_registration)

        sendButton.setOnClickListener {
            registerBike()
        }
        brandAutoRadio.setOnClickListener {
            brandRadioSelected(false)
        }

        brandManualRadio.setOnClickListener {
            brandRadioSelected(true)
        }

        modelCarbRadio.setOnClickListener {
            modelRadioSelected(true)
        }

        modelFiRadio.setOnClickListener {
            modelRadioSelected(false)
        }

        ccOilRadio.setOnClickListener {
            ccRadioSelected(true)
        }

        ccWoutOilRadio.setOnClickListener {
            ccRadioSelected(false)
        }

        wheels2Radio.setOnClickListener {
            wheelsSelected(2)
        }

        wheels3Radio.setOnClickListener {
            wheelsSelected(3)
        }

        wheels4Radio.setOnClickListener {
            wheelsSelected(4)
        }

        rearBreakDiskRadio.setOnClickListener {
            rearBreakSelected(false)
        }

        rearBreakDrumRadio.setOnClickListener {
            rearBreakSelected(true)
        }

        frontBreakDiskRadio.setOnClickListener {
            frontBreakSelected(false)
        }

        frontBreakDrumRadio.setOnClickListener {
            frontBreakSelected(true)
        }
    }

    fun wheelsSelected(wheelNumber: Int) {
        if (wheelNumber == 2) {
            wheels = "2 Wheels"
        } else if (wheelNumber == 3) {
            wheels = "3 Wheels"
        } else if (wheelNumber == 4) {
            wheels = "4 Wheels"
        } else {
            // No way to get here
        }
    }

    fun rearBreakSelected(isDrum: Boolean) {
        if (isDrum) {
            rearBreak = "Drum"
        } else {
            rearBreak = "Disk"
        }
    }

    fun frontBreakSelected(isDrum: Boolean) {
        if (isDrum) {
            frontBreak = "Drum"
        } else {
            frontBreak = "Disk"
        }
    }

    fun ccRadioSelected(isOil: Boolean) {
        if (isOil) {
            ccDescription = "Oil"
        } else {
            ccDescription = "W/ Oil"
        }
    }

    fun modelRadioSelected(isCarb: Boolean) {
        if (isCarb) {
            modelDescription = "Carb"
        } else {
            modelDescription = "Fi"
        }
    }

    fun brandRadioSelected(isManual: Boolean) {

        if (isManual) {
            brandDescription = "Manual"
            Toast.makeText(this, "$brandDescription is your selected", Toast.LENGTH_LONG).show()
        } else {
            brandDescription = "Auto"
            Toast.makeText(this, "$brandDescription is your selected", Toast.LENGTH_LONG).show()
        }

    }

    fun validateFields(): Boolean {
        if (editBrandText.text.isEmpty()) {
            Toast.makeText(this, "Brand Text Field is Empty", Toast.LENGTH_LONG).show()
            return false
        }

        if (brandDescription == ""){
            Toast.makeText(this, "Please select a brand description", Toast.LENGTH_LONG).show()
            return false
        }

        if(editModelText.text.isEmpty()) {
            Toast.makeText(this, "Model Text Field is Empty", Toast.LENGTH_LONG).show()
            return false
        }

        if (modelDescription == "") {
            Toast.makeText(this, "Please select a model description", Toast.LENGTH_LONG).show()
            return false
        }

        if(editCCText.text.isEmpty()) {
            Toast.makeText(this, "CC Text Field is Empty", Toast.LENGTH_LONG).show()
            return false
        }

        if (ccDescription == "") {
            Toast.makeText(this, "Brand Text Field is Empty", Toast.LENGTH_LONG).show()
            return false
        }

        if (frontBreak == "") {
            Toast.makeText(this, "Please select a front break", Toast.LENGTH_LONG).show()
            return false
        }

        if (rearBreak == "") {
            Toast.makeText(this, "Please select a rear break", Toast.LENGTH_LONG).show()
            return false
        }

        if (wheels == "") {
            Toast.makeText(this, "Please select your wheel type", Toast.LENGTH_LONG).show()
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

        return true

    }

    fun registerBike() {
        //add validation here

        if (!validateFields()) {
            return
        }

        val uid = FirebaseAuth.getInstance().uid

        val bike = BikeInfo()
        bike.brand = editBrandText.text.toString()
        bike.brandDescription = brandDescription
        bike.model = editModelText.text.toString()
        bike.modelDescription = modelDescription
        bike.cc = editCCText.text.toString()
        bike.ccDescription = ccDescription
        bike.frontBreak = frontBreak
        bike.rearBreak = rearBreak
        bike.wheels = wheels
        bike.plateNumber = editPlateNumberText.text.toString()
        bike.odometer = editOdometerText.text.toString()
        bike.nickname = editNicknameText.text.toString()
        bike.userId = uid!!

        val database = MotoroBroDatabase()

        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering your profile....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        database.saveBikeInfo(bike) {
            progressDialog.dismiss()
            Toast.makeText(this, "Bike Registration Successful!", Toast.LENGTH_LONG).show()

            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}
