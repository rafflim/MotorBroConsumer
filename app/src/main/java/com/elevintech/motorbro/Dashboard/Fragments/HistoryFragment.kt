package com.elevintech.motorbro.Dashboard.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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
        historyAdapter.clear()
        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()

        historyAdapter.clear()
    }

    fun setupRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(activity)
        historyRecyclerView.isNestedScrollingEnabled = true
        historyRecyclerView.adapter = historyAdapter
    }

    fun setupViews() {

        val db = MotoroBroDatabase()
        db.getUserHistory {
            // TODO: somehow get the last value only

            for (history in it) {
                historyAdapter.add(historyItem(history))
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

    inner class historyItem(val history: History): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            var historyTitle = ""

            when(history.typeOfHistory){
                "bike-parts" -> { historyTitle = "Added ${history.value}"}
                "odometer" -> { historyTitle = "Updated odometer to ${history.value} km " }
                "refueling" -> { historyTitle = "Refueled ${history.value}"}
            }

            viewHolder.itemView.historyDate.text = Utils().convertMillisecondsToDate(history.dateLong, "MMM d, yyyy")
            viewHolder.itemView.historyTitle.text = historyTitle


        }

        override fun getLayout(): Int {
            return R.layout.row_history_layout

        }
    }
}
