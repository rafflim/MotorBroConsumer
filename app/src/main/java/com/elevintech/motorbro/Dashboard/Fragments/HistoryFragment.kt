package com.elevintech.motorbro.Dashboard.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbro.Model.History
import com.elevintech.motorbro.Model.OdometerUpdate
import com.elevintech.motorbro.Model.Reminders
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils

import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.row_history_layout.view.*


class HistoryFragment : Fragment() {


    val historyAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()

        historyAdapter.clear()
    }

    fun setupRecyclerView() {
        historyRecyclerView.isNestedScrollingEnabled = false
        historyRecyclerView.adapter = historyAdapter
    }

    fun setupViews() {

        val db = MotoroBroDatabase()
        db.getUserOdometers {
            // TODO: somehow get the last value only

            for (odo in it) {
                historyAdapter.add(historyItem(odo))
            }
        }
//        MotoroBroDatabase().getUserHistory {
//
//            val historyList = it
//            if(historyList.isNotEmpty()){
//                if (noDataLayout != null) {
//                    noDataLayout.visibility = View.GONE
//                }
//            } else {
//                if (noDataLayout != null) {
//                    noDataLayout.visibility = View.VISIBLE
//                }
//            }
//
//            for (history in historyList){
//                historyAdapter.add(historyItem(history))
//            }
//        }
    }

    inner class historyItem(val odometer: OdometerUpdate): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val distance = odometer.odometer.toString()
            val date = odometer.date

//            viewHolder.itemView.historyKilometers.text = distance + "km"
//            viewHolder.itemView.historyDate.text = date
        }

        override fun getLayout(): Int {
            return R.layout.row_history_layout

        }
    }
}
