package com.elevintech.motorbro.Garage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_garage.*

class GarageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garage)

        garageBackImageView.setOnClickListener {
            finish()
        }

        val db = MotoroBroDatabase()
        db.getUserBike {
            setBikeValues(it)
        }
    }

    fun setBikeValues(bike: BikeInfo){
        motorcycleName1.setText(bike.brand + " " + bike.model)
        plateNumberText1.setText(bike.plateNumber)
    }
}
