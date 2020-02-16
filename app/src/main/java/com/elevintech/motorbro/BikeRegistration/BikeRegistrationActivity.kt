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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_registration)

        sendButton.setOnClickListener {
            registerBike()
        }

    }


    fun validateFields(): Boolean {
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
        bike.model = editModelText.text.toString()
        bike.plateNumber = editPlateNumberText.text.toString()
        bike.odometer = editOdometerText.text.toString()
        bike.nickname = editNicknameText.text.toString()

        // TODO: Fix year bought to year selector
        bike.yearBought = yearBoughtEditText.text.toString()
        bike.userId = uid!!

        val database = MotoroBroDatabase()

        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering your profile....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        database.saveBikeInfo(bike) {
            progressDialog.dismiss()
            Toast.makeText(this, "Bike Registration Successful!", Toast.LENGTH_LONG).show()

            val db = MotoroBroDatabase()
            db.updateUserRegistrationProgress(2) {
                val intent = Intent(applicationContext, DashboardActivity::class.java)
                startActivity(intent)
            }


        }
    }
}
