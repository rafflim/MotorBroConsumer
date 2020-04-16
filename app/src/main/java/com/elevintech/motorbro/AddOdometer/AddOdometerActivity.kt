package com.elevintech.motorbro.AddOdometer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Achievements.AchievementManager
import com.elevintech.motorbro.Model.Achievement
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.Model.OdometerUpdate
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_add_odometer.*
import kotlinx.android.synthetic.main.activity_add_odometer.backButton
import kotlinx.android.synthetic.main.activity_add_odometer.checkMarkButton
import kotlinx.android.synthetic.main.activity_add_odometer.dateText
import kotlinx.android.synthetic.main.activity_add_odometer.odometerText
import kotlinx.android.synthetic.main.row_bike.view.*
import java.text.DecimalFormat
import java.util.*

class AddOdometerActivity : AppCompatActivity() {

    var selectedBike = BikeInfo()
    var birthDayInMilliseconds = 0.toLong()
    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_odometer)

        bottomSheetDialog = BottomSheetDialog(this)

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

        bikeText.setOnClickListener {
            openBikePicker()
        }
    }

    private fun openBikePicker(){

        val progressDialog = Utils().easyProgressDialog(this, "Gathering your bikes...")
        progressDialog.show()

        MotoroBroDatabase().getUserBikes { bikes ->

            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_select_bike, null)
            val bikeAdapter = GroupAdapter<ViewHolder>()

            for (bike in bikes.filter { !it.deleted }){
                bikeAdapter.add(BikeItem(bike))
            }

            view.findViewById<RecyclerView>(R.id.bikeRecyclerView).isNestedScrollingEnabled = false
            view.findViewById<RecyclerView>(R.id.bikeRecyclerView).layoutManager = LinearLayoutManager(this)
            view.findViewById<RecyclerView>(R.id.bikeRecyclerView).adapter = bikeAdapter

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            progressDialog.dismiss()

        }
    }

    private fun onBikeSelect(bike: BikeInfo){
        selectedBike = bike
        bikeText.setText(bike.brand + " " + bike.model + " (${bike.nickname})")
        bottomSheetDialog.dismiss()
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
            val showDialog = Utils().showProgressDialog(this, "Updating Odometer")
            val odometerValue = odometerText.text.toString().toDouble()

            val odometerDetails = OdometerUpdate()
            odometerDetails.odometer = odometerValue
            odometerDetails.date = dateText.text.toString()
            odometerDetails.dateLong = Utils().convertDateToTimestamp(dateText.text.toString(), "yyyy-MM-dd")
            odometerDetails.userId =  FirebaseAuth.getInstance().uid!!

            val database = MotoroBroDatabase()
            database.saveOdometerUpdate(odometerDetails) {
                database.saveHistory("odometer", it!!, odometerValue){
                    database.updateBikeOdometer(selectedBike, odometerValue){
                        AchievementManager().setAchievementAsAchieved( Achievement.Names.FIRST_ODOMETER )
                        AchievementManager().incrementAchievementProgress( Achievement.Names.DISTANCE_TRAVELED, odometerValue.toInt())

                        showDialog.dismiss()
                        Toast.makeText(this, "Successfully updated Odometer", Toast.LENGTH_SHORT).show()
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
                        odometerText.text.toString()== ""
                ))


    }

    inner class BikeItem(val bike: BikeInfo): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.bikeName.text = bike.nickname
            viewHolder.itemView.plateNumberText.text = bike.plateNumber
            viewHolder.itemView.deleteButton.visibility = View.INVISIBLE

            viewHolder.itemView.setOnClickListener { onBikeSelect(bike) }

            if (bike.imageUrl != "")
                Glide.with(this@AddOdometerActivity).load(bike.imageUrl).into(viewHolder.itemView.bikeImageView)

            if (bike.primary)
                viewHolder.itemView.isPrimary.visibility = View.VISIBLE

        }

        override fun getLayout(): Int {
            return R.layout.row_bike

        }
    }
}
