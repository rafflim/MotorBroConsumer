package com.elevintech.motorbro.Dashboard.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbro.AddRefueling.AddRefuelingActivity
import com.elevintech.motorbro.Model.Refueling
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils

import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_reminders.*
import kotlinx.android.synthetic.main.fragment_reminders.view.*
import kotlinx.android.synthetic.main.row_reminders_layout.view.*

class RefuelFragment : Fragment() {
    // TODO: Rename and change types of parameters

    val reminderAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_reminders, container, false)
        getTimesRefueled(v)
        return v

    }

    private fun getTimesRefueled(v: View) {
        MotoroBroDatabase().getUserRefueling {

            val refuelList = it
            if(refuelList.isNotEmpty()){
                if (noDataLayout != null) {
                    //noDataLayout.visibility = View.GONE
                    v.monthCount.text = "${refuelList.size}"
                    v.totalCount.text = "${refuelList.size}"
                }
            } else {
                if (noDataLayout != null) {
                    //noDataLayout.visibility = View.VISIBLE

                    //refuelList.size
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        reminderAdapter.clear()
        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()

        reminderAdapter.clear()

    }

    private fun setupRecyclerView() {
        refuelRecyclerView.isNestedScrollingEnabled = false
        refuelRecyclerView.adapter = reminderAdapter
    }

    private fun setupViews() {

        MotoroBroDatabase().getUserRefueling {

            val refuelList = it
            if(refuelList.isNotEmpty()){
                if (noDataLayout != null) {
                    noDataLayout.visibility = View.GONE
                }
            } else {
                if (noDataLayout != null) {
                    noDataLayout.visibility = View.VISIBLE
                }
            }

            for (refuel in refuelList){
                reminderAdapter.add(ReminderItem(refuel))
            }
        }

    }

    inner class ReminderItem(val refuel: Refueling): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            // TODO: Add the items to add here to be dynamic
//            viewHolder.itemView.startDateText.text = Utils().convertMillisecondsToDate(reminder.startDateLong, "MMM d, yyyy")
//            viewHolder.itemView.endDateText.text = Utils().convertMillisecondsToDate(reminder.endDateLong, "MMM d, yyyy")
//            viewHolder.itemView.headerText.text = (reminder.typeOfReminder.toLowerCase()).capitalize()

            viewHolder.itemView.dateText.text = Utils().convertMillisecondsToDate(refuel.dateLong, "MMM d, yyyy")
            viewHolder.itemView.headerText.text = "Refuel " + refuel.location
//            viewHolder.itemView.odometerText.text = refuel.kilometers.toString()

            viewHolder.itemView.pricePerGallonText.text = "Price per Gallon: " + refuel.pricePerGallon.toString()
            viewHolder.itemView.noteText.text = refuel.note
//            viewHolder.itemView.fuelTypeText.text = "Fuel type: " + refuel.typeOfFuel

            viewHolder.itemView.setOnClickListener {
                // intent to go to parts edit page
                val intent = Intent(activity, AddRefuelingActivity::class.java)
                intent.putExtra("refuelObject", refuel)
                intent.putExtra("editRefuel", true)
                startActivity(intent)
            }

//                if (refuel != "") {
//                    MotoroBroDatabase().getBikeById(bikePart.bikeId) {
//                        viewHolder.itemView.primaryBikeName.text = it.yearBought + " " + it.brand + " " + it.model
//                    }
//                }
        }

        override fun getLayout(): Int {
            return R.layout.row_reminders_layout

        }
    }
}
