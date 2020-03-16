package com.elevintech.motorbro.AddBrand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_add_brand.*

class AddBrandActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_brand)

        saveButton.setOnClickListener{

            val showProgressDialog = Utils().showProgressDialog(this, "Saving custom brand....")

            MotoroBroDatabase().saveCustomBrand(addTypeOfPartsText.text.toString()){
                showProgressDialog.dismiss()
                finish()
            }
        }

        backView.setOnClickListener {
            finish()
        }
    }
}
