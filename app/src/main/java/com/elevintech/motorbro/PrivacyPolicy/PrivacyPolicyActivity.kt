package com.elevintech.motorbro.PrivacyPolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        btnBack.setOnClickListener {
            finish()
        }
    }
}
