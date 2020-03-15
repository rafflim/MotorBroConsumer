package com.elevintech.motorbro.ViewBike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_view_bike.*

class ViewBikeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_bike)

        var bike = intent.getSerializableExtra("bike") as BikeInfo

        if (bike.imageUrl != "") {
            Glide.with(this).load(bike.imageUrl).into(bikeImageView)
        } else {
            // Put an empty image here
        }

        brandAndModelText.text = bike.brand.capitalize() + " " + bike.model.capitalize()
        plateNumberText.text = "Plate # " + bike.plateNumber.capitalize()
        nicknameText.text = "Nickname: " + bike.nickname
        yearBoughtText.text = "Year Bought: " + bike.yearBought
        brandText.text = bike.brand.capitalize()
        modelText.text = bike.model.capitalize()

        backView.setOnClickListener {
            finish()
        }

    }
}
