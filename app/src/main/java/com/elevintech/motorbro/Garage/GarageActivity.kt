package com.elevintech.motorbro.Garage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.ViewBike.ViewBikeActivity
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

        firstBikeLayout.setOnClickListener {
            val intent = Intent(this, ViewBikeActivity::class.java)
            //TODO: Add the bike id here
            startActivity(intent)
        }

        secondBikeLayout.setOnClickListener {
            // if locked then dont show anything
        }

        thirdBikeLayout.setOnClickListener {

        }
    }

    fun setBikeValues(bike: BikeInfo){
        motorcycleName1.setText(bike.brand + " " + bike.model)
        plateNumberText1.setText(bike.plateNumber)
    }
}
