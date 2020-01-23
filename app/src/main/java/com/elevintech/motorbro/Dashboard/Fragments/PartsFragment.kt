package com.elevintech.motorbro.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbro.AddParts.AddPartsActivity

import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_parts.*
import kotlinx.android.synthetic.main.row_parts.view.*

/**
 * A simple [Fragment] subclass.
 */
class PartsFragment : Fragment() {

    var listOfParts = listOf<String>("Headlight", "Tires", "Brakes", "Helmet", "Back Seat", "Front Seat", "Accelerator", "Battery")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayParts()

        add_parts_floating_button.setOnClickListener {
            val intent = Intent(context, AddPartsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayParts() {
        var partsListAdapter = GroupAdapter<ViewHolder>()

        for (part in listOfParts) {
            partsListAdapter.add(partsItem(part))
        }

        recycler_view_type_of_parts.adapter = partsListAdapter
    }

    inner class partsItem(val part: String): Item<ViewHolder>() {


        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.parts_name.text = part
        }

        override fun getLayout(): Int {

            return R.layout.row_parts
        }
    }
}
