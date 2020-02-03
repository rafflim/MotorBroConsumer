package com.elevintech.motorbro.Glovebox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_glovebox.*

class GloveboxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glovebox)

        gloveboxBackImageView.setOnClickListener {
            finish()
        }

        registrationLayout.setOnClickListener {
            val intent = Intent(this, MotorRegistrationActivity::class.java)
            startActivity(intent)
        }

        licenseLayout.setOnClickListener {
            val intent = Intent(this, DriversLicenseActivity::class.java)
            startActivity(intent)
        }

        insuranceLayout.setOnClickListener {
            val intent = Intent(this, InsuranceActivity::class.java)
            startActivity(intent)
        }
    }
}
