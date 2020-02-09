package com.elevintech.motorbro.MyAccount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_my_account.*

class MyAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        myAccountBackImageView.setOnClickListener {
            finish()
        }

        val db = MotoroBroDatabase()
        db.getUser {
            firstNameText.setText(it.firstName)
            lastNameText.setText(it.lastName)
            emailText.setText(it.email)
        }
    }
}
