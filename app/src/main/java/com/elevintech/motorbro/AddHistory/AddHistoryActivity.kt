package com.elevintech.motorbro.AddHistory

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Model.History
import com.elevintech.motorbro.TypeOf.TypeOfHistoryActivity
import com.elevintech.motorbro.TypeOf.TypeOfPartsActivity
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_add_history.*
import java.text.DecimalFormat
import java.util.*

class AddHistoryActivity : AppCompatActivity() {

    var birthDayInMilliseconds = 0.toLong()

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    companion object {
        val SELECT_PART_TYPE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_history)

        backButton.setOnClickListener {
            finish()
        }

        checkMarkButton.setOnClickListener {
            saveBikePartsData()
        }

        dateText.setOnClickListener {
            setDatePickerAction()
            openDatePicker()
        }

        typeOfHistoryText.setOnClickListener {
            val intent = Intent(applicationContext, TypeOfHistoryActivity::class.java)
            startActivityForResult(intent, SELECT_PART_TYPE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (data != null){
                if (requestCode == SELECT_PART_TYPE){
                    var partType = data!!.getStringExtra("selectedHistory").toString()
                    typeOfHistoryText.setText(partType)
                }
            }
        }
    }

    private fun openDatePicker() {

        // INSTANTIATE CALENDAR
        var cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -21)

        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        // INSTANTIATE DATE PICKER DIALOG
        var dialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day)

        // SET DATE PICKER DIALOG STYLE
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // SHOW THE DATE PICKER DIALOG
        dialog.show()

    }

    private fun setDatePickerAction(){


        // SET ACTION AFTER SELECTING THE DATE
        mDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->

            // FORMAT  DAY and MONTH to always show two digits (add zero if single digit)
            var mFormat = DecimalFormat("00");
            var monthString = mFormat.format(month + 1)
            var dayString = mFormat.format(day)

            birthDayInMilliseconds = Utils().convertDateToMilliseconds(year,month,day,12, 0, 0)

            // PUT THE SELECTED DATE ON THE DATE PLACEHOLDER
            dateText.setText("${year}-${monthString}-${dayString}")

            // TODO: How to get current AGE??

            // If todays month is greater than current month
            // if todays day is greater than or equals to day

        }

    }

    fun saveBikePartsData() {

        if (validateFields()){


            var showDialog = Utils().showProgressDialog(this, "Saving history")

            var history = History()

            history.date = dateText.text.toString()
            history.dateLong = Utils().convertDateToTimestamp(dateText.text.toString(), "yyyy-MM-dd")
            history.kilometers = odometerText.text.toString().toDouble()
            history.typeOfHistory = typeOfHistoryText.text.toString()
            history.brand = brandText.text.toString()
            history.price = priceText.text.toString().toDouble()

            val database = MotoroBroDatabase()
            database.saveHistory(history) {
                showDialog.dismiss()
                Toast.makeText(this, "Successfully saved history", Toast.LENGTH_SHORT).show()
                finish()
            }

        } else {

            Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show()

        }


    }

    private fun validateFields(): Boolean {

        return (!(
                dateText.text.toString() == "" ||
                        odometerText.text.toString()== ""||
                        typeOfHistoryText.text.toString() == ""||
                        brandText.text.toString() == ""||
                        priceText.text.toString() == ""
                ))


    }


}