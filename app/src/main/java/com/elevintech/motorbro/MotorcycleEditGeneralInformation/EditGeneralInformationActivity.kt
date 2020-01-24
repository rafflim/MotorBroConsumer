package com.elevintech.motorbro.MotorcycleEditGeneralInformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_edit_general_information.*

class EditGeneralInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_general_information)

        genInfoBackImage.setOnClickListener {
            finish()
        }
        checkMarkButton.setOnClickListener {
            val db = MotoroBroDatabase()
            db.getUserBike {
                saveUserValues(it)
            }
        }
    }

    private fun saveUserValues(bike: BikeInfo) {

        val fuelLiter = fuelLiterText.text.toString().toDoubleOrNull()
        val odometer = odometerText.text.toString().toDoubleOrNull()
        val income = incomeText.text.toString().toDoubleOrNull()

        if (fuelLiter != null) {
            bike.fuelLiter = fuelLiter
        }

        if (odometer != null) {
            bike.odometerValue = odometer
        }

        if (income != null) {
            bike.income = income
        }

        val db = MotoroBroDatabase()
        db.updateBikeInfo(bike) {
            println("Success!!!")
            finish()
        }
    }
}
