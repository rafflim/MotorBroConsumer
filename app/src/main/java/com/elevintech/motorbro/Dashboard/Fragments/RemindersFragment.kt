package com.elevintech.motorbro.Dashboard.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elevintech.motorbro.Model.Reminders
import com.elevintech.motorbro.Model.ShopProduct

import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_reminders.*
import kotlinx.android.synthetic.main.fragment_shop.*
import kotlinx.android.synthetic.main.row_shop_item_layout.view.*

class RemindersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onResume() {
        super.onResume()

        setupViews()
    }

    fun setupViews() {
        remindersRecyclerView.isNestedScrollingEnabled = false

        val reminder1 = Reminders("", "Payment", 0.0, 0.0)
        val reminder2 = Reminders("", "Payment", 0.0, 0.0)
        val reminder3 = Reminders("", "Payment", 0.0, 0.0)
        val reminder4 = Reminders("", "Payment", 0.0, 0.0)

        val reminderAdamter = GroupAdapter<ViewHolder>()

        reminderAdamter.add(reminderItem(reminder1))
        reminderAdamter.add(reminderItem(reminder2))
        reminderAdamter.add(reminderItem(reminder3))
        reminderAdamter.add(reminderItem(reminder4))

        remindersRecyclerView.adapter = reminderAdamter

    }

    inner class reminderItem(val reminder: Reminders): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            // TODO: Add the items to add here to be dynamic

        }

        override fun getLayout(): Int {
            return R.layout.row_reminders_layout

        }
    }
}
