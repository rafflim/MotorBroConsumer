package com.elevintech.motorbro.Garage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_garage.*

class GarageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garage)

        garageBackImageView.setOnClickListener {
            finish()
        }
    }
}
