package com.elevintech.motorbro.AddRefueling

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.elevintech.motorbro.Achievements.AchievementManager
import com.elevintech.motorbro.Model.Achievement
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Model.History
import com.elevintech.motorbro.Model.Refueling
import com.elevintech.motorbro.TypeOf.TypeOfFuelActivity
import com.elevintech.motorbro.TypeOf.TypeOfHistoryActivity
import com.elevintech.motorbro.TypeOf.TypeOfPartsActivity
import com.elevintech.motorbro.Utils.Constants
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_add_parts.*
import kotlinx.android.synthetic.main.activity_add_refueling.*
import kotlinx.android.synthetic.main.activity_add_refueling.backButton
import kotlinx.android.synthetic.main.activity_add_refueling.checkMarkButton
import kotlinx.android.synthetic.main.activity_add_refueling.dateText
import kotlinx.android.synthetic.main.activity_add_refueling.deleteLayout
import kotlinx.android.synthetic.main.activity_add_refueling.noteText
import kotlinx.android.synthetic.main.activity_add_refueling.odometerText
import java.text.DecimalFormat
import java.util.*

class AddRefuelingActivity : AppCompatActivity() {

    var birthDayInMilliseconds = 0.toLong()

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    private var isForEditRefuel = false

    companion object {
        val SELECT_PART_TYPE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_refueling)

        isForEditRefuel = intent.getBooleanExtra("editRefuel", false)

        if (isForEditRefuel) {
            val refuel: Refueling? = intent.getParcelableExtra("refuelObject")

            if (refuel != null) {

                dateText.setText(refuel.date)
                odometerText.setText(refuel.kilometers.toString())
                typeOfFuelText.setText(refuel.typeOfFuel)
                pricePerGallonText.setText(refuel.pricePerGallon.toString())
                totalCostText.setText(refuel.totalCost.toString())
                litersText.setText(refuel.priceGallons.toString())
                locationText.setText(refuel.location)
                noteText.setText(refuel.note)

                disableAllCostButtons()

                deleteLayout.visibility = View.VISIBLE

                deleteLayout.setOnClickListener {
                    deleteRefuel(refuel)
                }
            }
        }
        backButton.setOnClickListener {
            finish()
        }

        checkMarkButton.setOnClickListener {
            saveRefuelData()
        }

        dateText.setOnClickListener {
            setDatePickerAction()
            openDatePicker()
        }

        typeOfFuelText.setOnClickListener {
            val intent = Intent(applicationContext, TypeOfFuelActivity::class.java)
            intent.putExtra("fromAddExtra", true)
            startActivityForResult(intent, SELECT_PART_TYPE)
        }

        pricePerGallonText.setOnFocusChangeListener { v, hasFocus ->
            println("yess i'm getting it")

            calculateText(v)
        }

        litersText.setOnFocusChangeListener { v, hasFocus ->
            calculateText(v)
        }

        totalCostText.setOnFocusChangeListener { v, hasFocus ->
            calculateText(v)
        }

        clearPriceValues.setOnClickListener {
            pricePerGallonText.isEnabled = true
            totalCostText.isEnabled = true
            litersText.isEnabled = true
            clearPriceValues.visibility = View.GONE
        }


    }

    private fun deleteRefuel(refuel: Refueling) {
        MotoroBroDatabase().deleteRefuel(refuel) {
            if (Constants.RESULT_STRING.SUCCESS == it) {
                finish()
            } else {
                Toast.makeText(this, "Unable to delete this part. Please check your internet connection", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun calculateText(v: View) {
        val pricePerLiter = pricePerGallonText.text.toString()
        val liter = litersText.text.toString()
        val totalCost = totalCostText.text.toString()


        // TODO: if 2 of these have empty string then return
        if (pricePerLiter.isEmpty() && liter.isEmpty() || liter.isEmpty() && totalCost.isEmpty() || pricePerLiter.isEmpty() && totalCost.isEmpty()) { return }

        if (pricePerLiter.isEmpty()) {
            val totalCost = totalCost.toFloat()
            val liter = liter.toFloat()

            val result = totalCost / liter
            pricePerGallonText.setText(result.toString())
            disableAllCostButtons()

            return
        }

        if (liter.isEmpty()) {
            val totalCost = totalCost.toFloat()
            val pricePerLiter = pricePerLiter.toFloat()

            val result = totalCost / pricePerLiter
            litersText.setText(result.toString())
            disableAllCostButtons()
            return
        }

        if (totalCost.isEmpty()) {
            val liter = liter.toFloat()
            val pricePerGallon = pricePerLiter.toFloat()

            val result = liter * pricePerGallon
            totalCostText.setText(result.toString())
            disableAllCostButtons()
            return
        }

        // TODO: What if everything has a value??

        // Get the last text view change
        // Base it on that
//        if (v == pricePerGallonText || v == litersText) {
//            val liter = liter.toFloat()
//            val pricePerGallon = pricePerLiter.toFloat()
//
//            val result = liter * pricePerGallon
//            totalCostText.setText(result.toString())
//            // This means the price per gallon text is changed
//        }
    }

    private fun disableAllCostButtons() {
        pricePerGallonText.isEnabled = false
        totalCostText.isEnabled = false
        litersText.isEnabled = false
        clearPriceValues.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (data != null){
                if (requestCode == SELECT_PART_TYPE){
                    var partType = data!!.getStringExtra("selectedFuel").toString()
                    typeOfFuelText.setText(partType)
                }
            }
        }
    }

    private fun openDatePicker() {

        // INSTANTIATE CALENDAR
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 0)

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // INSTANTIATE DATE PICKER DIALOG
        val dialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day)

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

    fun saveRefuelData() {

        if (validateFields()){


            var showDialog = Utils().showProgressDialog(this, "Saving Refueling Data")

            var refueling = Refueling()

            refueling.date = dateText.text.toString()
            refueling.dateLong = Utils().convertDateToTimestamp(dateText.text.toString(), "yyyy-MM-dd")
            refueling.kilometers = odometerText.text.toString().toDouble()
            refueling.typeOfFuel = typeOfFuelText.text.toString()
            refueling.pricePerGallon = pricePerGallonText.text.toString().toDouble()
            refueling.totalCost = totalCostText.text.toString().toDouble()
            refueling.priceGallons = litersText.text.toString().toDouble()
            refueling.location = locationText.text.toString()
            refueling.userId =  FirebaseAuth.getInstance().uid!!
            refueling.note = noteText.text.toString()

            val database = MotoroBroDatabase()

            if (isForEditRefuel) {
                database.editRefuel(refueling) {
                    showDialog.dismiss()
                    finish()
                }
            } else {
                database.saveRefueling(refueling) {
                    database.saveHistory("refueling", it!!, refueling.typeOfFuel) {
                        AchievementManager().setAchievementAsAchieved( Achievement.Names.FIRST_FUEL )
                        AchievementManager().incrementAchievementProgress( Achievement.Names.REFUEL_TIMES, 1)
                        showDialog.dismiss()
                        Toast.makeText(this, "Successfully saved refuel data", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        } else {

            Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show()

        }


    }

    private fun validateFields(): Boolean {

        return (!(
                    dateText.text.toString() == "" ||
                    odometerText.text.toString()== "" ||
                    typeOfFuelText.text.toString()== "" ||
                    pricePerGallonText.text.toString()== "" ||
                    totalCostText.text.toString()== "" ||
                    litersText.text.toString()== ""
                ))
    }

}
