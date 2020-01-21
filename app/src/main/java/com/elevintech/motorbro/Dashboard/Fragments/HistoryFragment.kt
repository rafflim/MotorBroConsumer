package com.elevintech.motorbro.Dashboard.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbro.Model.History
import com.elevintech.motorbro.Model.Reminders

import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_reminders.*


class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onResume() {
        super.onResume()

        setupViews()
    }

    fun setupViews() {
        historyRecyclerView.isNestedScrollingEnabled = false

        val history1 = History("", "Payment", "5000km", 5000.0, "$5,000.00")
        val history2 = History("", "Payment", "5000km", 5000.0, "$5,000.00")
        val history3 = History("", "Payment", "5000km", 5000.0, "$5,000.00")
        val history4 = History("", "Payment", "5000km", 5000.0, "$5,000.00")

        val historyAdapter = GroupAdapter<ViewHolder>()

        historyAdapter.add(historyItem(history1))
        historyAdapter.add(historyItem(history2))
        historyAdapter.add(historyItem(history3))
        historyAdapter.add(historyItem(history4))

        historyRecyclerView.adapter = historyAdapter

    }

    inner class historyItem(val history: History): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            // TODO: Add the items to add here to be dynamic

        }

        override fun getLayout(): Int {
            return R.layout.row_history_layout

        }
    }
}
