package com.elevintech.motorbro.Dashboard.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbro.AddHistory.AddHistoryActivity
import com.elevintech.motorbro.Model.History
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

    fun setupRecyclerView() {
        historyRecyclerView.isNestedScrollingEnabled = false
        historyRecyclerView.adapter = historyAdapter
    }

    fun setupViews() {

        MotoroBroDatabase().getUserHistory {

            val historyList = it
            if(historyList.isNotEmpty()){
                noDataLayout.visibility = View.GONE
            } else {
                noDataLayout.visibility = View.INVISIBLE
            }

            for (history in historyList){
                historyAdapter.add(historyItem(history))
            }
        }
    }

    inner class historyItem(val history: History): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val title = (history.typeOfHistory.toLowerCase()).capitalize()
            val distance = history.kilometers.toString() + " Km"
            val date = Utils().convertMillisecondsToDate(history.dateLong, "MMM d, yyyy")
            val price = "$" + ("%.2f".format(history.price))

            viewHolder.itemView.historyTitle.text = title
            viewHolder.itemView.historyKilometers.text = distance

            viewHolder.itemView.historyDate.text = date
            viewHolder.itemView.historyCash.text = price
        }

        override fun getLayout(): Int {
            return R.layout.row_history_layout

        }
    }
}
