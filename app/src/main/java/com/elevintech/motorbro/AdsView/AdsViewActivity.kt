package com.elevintech.motorbro.AdsView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_ads_view.*

class AdsViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads_view)

        backView.setOnClickListener {
            finish()
        }
    }
}
