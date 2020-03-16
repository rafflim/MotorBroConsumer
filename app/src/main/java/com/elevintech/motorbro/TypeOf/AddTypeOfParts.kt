package com.elevintech.motorbro.TypeOf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_add_type_of_parts.*

class AddTypeOfParts : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_type_of_parts)

        saveButton.setOnClickListener{

            val showProgressDialog = Utils().showProgressDialog(this, "Saving custom part....")

            MotoroBroDatabase().saveCustomPart(addTypeOfPartsText.text.toString()){
                showProgressDialog.dismiss()
                finish()
            }
        }

        backView.setOnClickListener {
            finish()
        }

    }

}
