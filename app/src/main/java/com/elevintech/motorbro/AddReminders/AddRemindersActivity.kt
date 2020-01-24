package com.elevintech.motorbro.AddReminders

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Model.Reminders
import com.elevintech.motorbro.TypeOf.TypeOfPartsActivity
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_add_reminders.*
import java.text.DecimalFormat
import java.util.*

class AddRemindersActivity : AppCompatActivity() {

    var birthDayInMilliseconds = 0.toLong()

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    companion object {
        val SELECT_PART_TYPE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminders)

        backButton.setOnClickListener {
            finish()
        }

        checkMarkButton.setOnClickListener {
            saveBikePartsData()
        }

        startDateText.setOnClickListener {
            setDatePickerAction("startDate")
            openDatePicker()
        }

        endDateText.setOnClickListener {
            setDatePickerAction("endDate")
            openDatePicker()
        }

        typeOfHistoryText.setOnClickListener {
            val intent = Intent(applicationContext, TypeOfPartsActivity::class.java)
            startActivityForResult(intent, SELECT_PART_TYPE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (data != null){
                if (requestCode == SELECT_PART_TYPE){
                    var partType = data!!.getStringExtra("selectedPart").toString()
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

    private fun setDatePickerAction(dateType: String){


        // SET ACTION AFTER SELECTING THE DATE
        mDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->

            // FORMAT  DAY and MONTH to always show two digits (add zero if single digit)
            var mFormat = DecimalFormat("00");
            var monthString = mFormat.format(month + 1)
            var dayString = mFormat.format(day)

            birthDayInMilliseconds = Utils().convertDateToMilliseconds(year,month,day,12, 0, 0)

            // PUT THE SELECTED DATE ON THE DATE PLACEHOLDER
            if (dateType == "startDate"){
                startDateText.setText("${year}-${monthString}-${dayString}")
            } else if (dateType == "endDate"){
                endDateText.setText("${year}-${monthString}-${dayString}")
            }

        }

    }

    fun saveBikePartsData() {

        if (validateFields()){


            var showDialog = Utils().showProgressDialog(this, "Saving reminder")

            var reminder = Reminders()

            reminder.startDate = startDateText.text.toString()
            reminder.startDateLong = Utils().convertDateToTimestamp(startDateText.text.toString(), "yyyy-MM-dd")
            reminder.endDate = endDateText.text.toString()
            reminder.endDateLong = Utils().convertDateToTimestamp(endDateText.text.toString(), "yyyy-MM-dd")

            reminder.kilometers = odometerText.text.toString().toDouble()
            reminder.typeOfHistory = typeOfHistoryText.text.toString()
            reminder.brand = brandText.text.toString()
            reminder.price = priceText.text.toString().toDouble()

            val database = MotoroBroDatabase()
            database.saveReminder(reminder) {
                showDialog.dismiss()
                Toast.makeText(this, "Successfully saved reminder", Toast.LENGTH_SHORT).show()
                finish()
            }

        } else {

            Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show()

        }


    }

    private fun validateFields(): Boolean {

        return (!(
                        startDateText.text.toString() == "" ||
                        endDateText.text.toString() == "" ||
                        odometerText.text.toString()== ""||
                        typeOfHistoryText.text.toString() == ""||
                        brandText.text.toString() == ""||
                        priceText.text.toString() == ""
                ))


    }


}

