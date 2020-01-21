package com.elevintech.motorbro.AddParts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.FirebaseDatabase.FirebaseDatabase
import com.elevintech.motorbro.Model.BikeParts
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_add_parts.*

class AddPartsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parts)

        backButton.setOnClickListener {
            finish()
        }

        checkMarkButton.setOnClickListener {
            saveBikePartsData()
            finish()
        }
    }

    fun saveBikePartsData() {

        val createdDate = dateText.toString().toDouble()
        val odometer = odometerText.toString().toDouble()
        val typeOfParts = typeOfPartsText.toString()
        val brand = brandText.toString()
        val price = priceText.toString().toDouble()

        val bikeParts = BikeParts("", createdDate, odometer, typeOfParts, brand, price)
        val database = FirebaseDatabase()
        database.saveBikeParts(bikeParts) {
            print("saved")
        }
    }
}
