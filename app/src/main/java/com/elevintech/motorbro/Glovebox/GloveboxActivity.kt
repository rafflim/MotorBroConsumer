package com.elevintech.motorbro.Glovebox

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
    }
}
