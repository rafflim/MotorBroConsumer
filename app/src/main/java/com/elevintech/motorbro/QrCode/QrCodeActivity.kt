package com.elevintech.motorbro.QrCode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_qr_code.*

class QrCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        val uid = FirebaseAuth.getInstance().uid!!

        val qrCodeBitmap = Utils().generateQrCodeBitmap(uid)

        imageView.setImageBitmap(qrCodeBitmap)

    }
}
