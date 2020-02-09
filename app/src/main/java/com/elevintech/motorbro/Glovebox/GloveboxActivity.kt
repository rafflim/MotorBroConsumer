package com.elevintech.motorbro.Glovebox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
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

    override fun onResume() {
        super.onResume()

        MotoroBroDatabase().getInsuranceDocument {
            if (it != null){
                insuranceLayoutNotAdded.visibility = GONE
                insuranceLayoutAdded.visibility = VISIBLE
            }else{
                insuranceLayoutNotAdded.visibility = VISIBLE
                insuranceLayoutAdded.visibility = GONE
            }
        }

        MotoroBroDatabase().getLicenseDocument {
            if (it != null){
                driversLicenseLayout.visibility = GONE
                driversLicenseLayoutAdded.visibility = VISIBLE
            }else {
                driversLicenseLayout.visibility = VISIBLE
                driversLicenseLayoutAdded.visibility = GONE
            }
        }

        MotoroBroDatabase().getMotorRegistrationDocument {
            if (it != null){
                motorRegistrationLicenseLayout.visibility = GONE
                motorRegistrationLicenseLayoutAdded.visibility = VISIBLE
            }else {
                motorRegistrationLicenseLayout.visibility = VISIBLE
                motorRegistrationLicenseLayoutAdded.visibility = GONE
            }
        }
    }
}
