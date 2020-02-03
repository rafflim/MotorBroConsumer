package com.elevintech.motorbro.AddOdometer

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.elevintech.motorbro.Model.OdometerUpdate
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_add_odometer.*
import kotlinx.android.synthetic.main.activity_add_odometer.backButton
import kotlinx.android.synthetic.main.activity_add_odometer.checkMarkButton
import kotlinx.android.synthetic.main.activity_add_odometer.dateText
import kotlinx.android.synthetic.main.activity_add_odometer.odometerText
import kotlinx.android.synthetic.main.activity_add_parts.*
import java.text.DecimalFormat
import java.util.*

class AddOdometerActivity : AppCompatActivity() {

    var birthDayInMilliseconds = 0.toLong()
    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_odometer)

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        backButton.setOnClickListener {
            finish()
        }

        dateText.setOnClickListener {
            setDatePickerAction()
            openDatePicker()
        }

        checkMarkButton.setOnClickListener {

            saveOdometerDetails()
        }
    }

    private fun openDatePicker() {

        // INSTANTIATE CALENDAR
        var cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 0)

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

    private fun saveOdometerDetails(){

        if (validateFields()){
            val odometer = odometerText.toString()

            var showDialog = Utils().showProgressDialog(this, "Updating Odometer")

            var odometerDetails = OdometerUpdate()

            odometerDetails.odometer = odometerText.text.toString().toDouble()
            odometerDetails.date = dateText.text.toString()
            odometerDetails.dateLong = Utils().convertDateToTimestamp(dateText.text.toString(), "yyyy-MM-dd")

            val database = MotoroBroDatabase()

            database.saveOdometerUpdate(odometerDetails) {
                showDialog.dismiss()
                Toast.makeText(this, "Successfully updated Odometer", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show()

        }

    }

    private fun validateFields(): Boolean {

        return (!(
                dateText.text.toString() == "" ||
                        odometerText.text.toString()== ""
                ))


    }
}
