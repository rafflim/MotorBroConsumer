package com.elevintech.motorbro.Garage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbro.BikeRegistration.BikeRegistrationActivity
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.motorbro.ViewBike.ViewBikeActivity
import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_garage.*
import kotlinx.android.synthetic.main.row_bike.view.*

class GarageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garage)

        backView.setOnClickListener {
            finish()
        }

        secondBikeLayout.setOnClickListener {
            // if locked then dont show anything
            Toast.makeText(this, "This motorcycle is locked", Toast.LENGTH_SHORT).show()
        }

        thirdBikeLayout.setOnClickListener {
            Toast.makeText(this, "This motorcycle is locked", Toast.LENGTH_SHORT).show()
        }

        floating_button.setOnClickListener {

            val intent = Intent(applicationContext, BikeRegistrationActivity::class.java)
            intent.putExtra("previousActivity", "garage")
            startActivity(intent)

        }
    }

    fun getBikes(){
        val db = MotoroBroDatabase()
        db.getUserBikes {

            if (it.size > 0)
                displayBikes(it)
        }
    }

    override fun onResume() {
        super.onResume()

        getBikes()
    }

    private fun displayBikes(bikeList: MutableList<BikeInfo>) {

        reycler_view.isNestedScrollingEnabled = true
        reycler_view.layoutManager = LinearLayoutManager(this)

        var adapter = GroupAdapter<ViewHolder>()
        val allActiveBikes = bikeList.filter { !it.deleted }
        val allSortedByMain = allActiveBikes.sortedBy { !it.primary }

        for (bike in allSortedByMain){
            adapter.add(BikeItem(bike, allActiveBikes.count()))
        }

        reycler_view.adapter = adapter

    }

    fun confirmDelete(bike: BikeInfo){

        val options = arrayOf("Yes, Delete this bike", "No, I've changed my mind")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete of bike cannot be undone. Do you want to proceed?")
        builder.setItems(options){ _, which ->

            if(which == 0){
                // IF YES is clicked
                MotoroBroDatabase().deleteBike(bike){
                    getBikes()
                }

            }else if(which == 1){
                // IF NO is clicked

            }
        }
        builder.show()

    }

    fun updateMainBike(bike: BikeInfo){

        val progressDialog = Utils().easyProgressDialog(this, "Updating Main Bike...")
        progressDialog.show()

        MotoroBroDatabase().updateMainBike(bike){
            getBikes()

            progressDialog.dismiss()
        }
    }

    inner class BikeItem(val bike: BikeInfo, val bikeCount: Int): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            Glide.with(this@GarageActivity).load(bike.imageUrl).into(viewHolder.itemView.bikeImageView)
            viewHolder.itemView.bikeName.text = bike.brand.capitalize() + " " +  bike.model.capitalize()
            viewHolder.itemView.plateNumberText.text = "Plate #" + bike.plateNumber

            viewHolder.itemView.setOnClickListener {

                val intent = Intent(this@GarageActivity, ViewBikeActivity::class.java)
                intent.putExtra("bike", bike)
                startActivity(intent)


            }

            viewHolder.itemView.deleteButton.setOnClickListener {
                confirmDelete(bike)
            }

            viewHolder.itemView.setAsNewPrimary.setOnClickListener {
                updateMainBike(bike)
            }

            // disable delete if only one bike is the garage
            if (bikeCount == 1)
                viewHolder.itemView.deleteButton.visibility = View.GONE

            // show primary bike label
            if (bike.primary)
                viewHolder.itemView.isPrimary.visibility = View.VISIBLE
            else
                viewHolder.itemView.setAsNewPrimary.visibility = View.VISIBLE


        }

        override fun getLayout(): Int {
            return R.layout.row_bike

        }
    }
}
