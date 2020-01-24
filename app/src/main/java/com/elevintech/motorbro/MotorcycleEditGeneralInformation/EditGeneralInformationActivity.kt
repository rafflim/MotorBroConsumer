package com.elevintech.motorbro.MotorcycleEditGeneralInformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_edit_general_information.*

class EditGeneralInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_general_information)

        genInfoBackImage.setOnClickListener {
            finish()
        }
    }
}
