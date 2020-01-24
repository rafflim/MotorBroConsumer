package com.elevintech.motorbro.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.elevintech.motorbro.AddParts.AddPartsActivity
import com.elevintech.motorbro.Model.BikeParts
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase

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



        add_parts_floating_button.setOnClickListener {
            val intent = Intent(context, AddPartsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        displayParts()

    }

    private fun displayParts() {
        recycler_view_type_of_parts.isNestedScrollingEnabled = false

        val partsListAdapter = GroupAdapter<ViewHolder>()

        MotoroBroDatabase().getUserBikeParts {

            val bikePartsList = it
            if(bikePartsList.isNotEmpty()) noDataLayout.visibility = GONE

            for (bikePart in bikePartsList){
                partsListAdapter.add(partsItem(bikePart))
            }

        }

        recycler_view_type_of_parts.adapter = partsListAdapter


    }

    inner class partsItem(val bikePart: BikeParts): Item<ViewHolder>() {


        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.parts_name.text = bikePart.typeOfParts
        }

        override fun getLayout(): Int {

            return R.layout.row_parts
        }
    }
}
