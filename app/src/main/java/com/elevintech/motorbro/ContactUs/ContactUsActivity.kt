package com.elevintech.motorbro.ContactUs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_contact_us.*

class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        btnBack.setOnClickListener {
            finish()
        }
    }
}
