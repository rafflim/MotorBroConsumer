package com.elevintech.motorbro.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.elevintech.motorbro.AddParts.AddPartsActivity
import com.elevintech.motorbro.AdsView.AdsViewActivity
import com.elevintech.motorbro.Dashboard.DashboardActivity
import com.elevintech.motorbro.Model.BikeParts
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils

import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_parts.*
import kotlinx.android.synthetic.main.fragment_parts.view.*
import kotlinx.android.synthetic.main.row_parts.view.*

/**
 * A simple [Fragment] subclass.
 */
class PartsFragment : Fragment() {

    val partsListAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_parts, container, false)
        displayBikeCount(v)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        adsLayout.setOnClickListener {
            val intent = Intent(context, AdsViewActivity::class.java)
            startActivity(intent)
        }
//        add_parts_floating_button.setOnClickListener {
//            val intent = Intent(context, AddPartsActivity::class.java)
//            startActivity(intent)
//        }
    }

    override fun onResume() {
        super.onResume()
        partsListAdapter.clear()
        displayParts()

    }

    override fun onDestroy() {
        super.onDestroy()
        partsListAdapter.clear()

    }

    private fun setupRecyclerView() {
        recycler_view_type_of_parts.isNestedScrollingEnabled = false
        recycler_view_type_of_parts.adapter = partsListAdapter
    }

    private fun displayBikeCount(v: View) {

        MotoroBroDatabase().getUserMainBikeFromBikes {
            if (it != null) {
                MotoroBroDatabase().getUserBikeParts(it.id) {
                    val bikePartsList = it
                    val userPartsCountString = bikePartsList.filter { bike -> !bike.createdByShop }.count().toString()
                    var shopPartsCountString = bikePartsList.filter { bike -> bike.createdByShop }.count().toString()

                    if (userPartsCountString == "") {
                        v.userPartsCount.setText("0")
                    } else {
                        v.userPartsCount.setText(userPartsCountString)
                    }

                    if (shopPartsCountString == "") {
                        v.shopPartsCount.setText("0")
                    } else {
                        v.shopPartsCount.setText(shopPartsCountString)
                    }
                }
            }
        }
    }

    private fun displayParts() {


        MotoroBroDatabase().getUserMainBikeFromBikes {
            if (it != null) {
                MotoroBroDatabase().getUserBikeParts(it.id) {
                    println(it)

                    val bikePartsList = it
                    if(bikePartsList.isNotEmpty()) {
                        if (noDataLayout != null) {
                            noDataLayout.visibility = GONE
                        }
                    } else {
                        if (noDataLayout != null) {
                            noDataLayout.visibility = VISIBLE
                        }
                    }

                    bikePartsList.reversed()
                    for (bikePart in bikePartsList){
                        println("bike23 " + bikePart)
                        partsListAdapter.add(PartsItem(bikePart))
                    }
                }
            }
        }

    }

    inner class PartsItem(val bikePart: BikeParts): Item<ViewHolder>() {


        override fun bind(viewHolder: ViewHolder, position: Int) {
//            viewHolder.itemView.partsName.text = bikePart.typeOfParts
            viewHolder.itemView.odometerText.text = "Brand: " + bikePart.brand
//            viewHolder.itemView.cashText.text = " ₱" + bikePart.price.toString()

            val dateString = Utils().convertMillisecondsToDate(bikePart.dateLong, "MMM d, yyyy")

            viewHolder.itemView.partsNameCombine.text = "$dateString - ${bikePart.typeOfParts} - ${bikePart.brand} - ${bikePart.price}"
            viewHolder.itemView.partsOdoCombine.text = "Odo = ${bikePart.odometer} km"

            var note = ""

            if (bikePart.note.isEmpty()) {
                note = "No note"
            } else {
                note = bikePart.note
            }

            viewHolder.itemView.noteText.text = note

            if (bikePart.dateLong != 0.toLong()) {
                viewHolder.itemView.dateText.text = dateString
            } else {
                viewHolder.itemView.dateText.text = "Date not supported"
            }


            viewHolder.itemView.setOnClickListener {
                // intent to go to parts edit page
                val intent = Intent(activity, AddPartsActivity::class.java)
                intent.putExtra("bikeParts", bikePart)
                intent.putExtra("isForEditParts", true)
                startActivity(intent)
            }

            if (bikePart.bikeId != "") {
                MotoroBroDatabase().getBikeById(bikePart.bikeId) {
                    viewHolder.itemView.primaryBikeName.text = "Bike: " + it.yearBought + " " + it.brand + " " + it.model
                }
            }

            if (bikePart.createdByShop){
                MotoroBroDatabase().getShop(bikePart.shopId) {
                    viewHolder.itemView.createdByShopLayout.visibility = View.VISIBLE
                    viewHolder.itemView.createdByShopText.text = "Shop: " + it.name
                }
            }

            if (bikePart.imageUrl.isNotEmpty()) {
                viewHolder.itemView.productImage.visibility = View.VISIBLE
                Glide.with(activity as DashboardActivity).load(bikePart.imageUrl).into(viewHolder.itemView.productImage)
            } else {
                viewHolder.itemView.productImage.visibility = View.GONE
            }


            // Get date bought
        }

        override fun getLayout(): Int {
            return R.layout.row_parts
        }
    }
}
