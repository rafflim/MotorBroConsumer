package com.elevintech.motorbro.More

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.ContactUs.ContactUsActivity
import com.elevintech.motorbro.MainActivity
import com.elevintech.motorbro.PrivacyPolicy.PrivacyPolicyActivity
import com.elevintech.motorbro.TermsServices.TermsServicesActivity
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_more.*

class MoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        feedbackLayout.setOnClickListener {
            val intent = Intent(this, ContactUsActivity::class.java)
            startActivity(intent)
        }

        termsServicesLayout.setOnClickListener {
            val intent = Intent(this, TermsServicesActivity::class.java)
            startActivity(intent)
        }

        privacyPolicyLayout.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        signOutLayout.setOnClickListener {
            logOut()
        }

    }

    private fun logOut() {

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
