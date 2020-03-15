package com.elevintech.motorbro.Garage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbro.BikeRegistration.BikeRegistrationActivity
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
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

    override fun onResume() {
        super.onResume()

        val db = MotoroBroDatabase()
        db.getUserBikes {

            if (it.size > 0)
                displayBikes(it)
        }
    }

    private fun displayBikes(bikeList: MutableList<BikeInfo>) {

        reycler_view.isNestedScrollingEnabled = true
        reycler_view.layoutManager = LinearLayoutManager(this)

        var adapter = GroupAdapter<ViewHolder>()

        for (bike in bikeList){
            adapter.add(BikeItem(bike))
        }

        reycler_view.adapter = adapter

    }

    inner class BikeItem(val bike: BikeInfo): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            Glide.with(this@GarageActivity).load(bike.imageUrl).into(viewHolder.itemView.bikeImageView)
            viewHolder.itemView.bikeName.text = bike.brand.capitalize() + " " +  bike.model.capitalize()
            viewHolder.itemView.plateNumberText.text = "Plate #" + bike.plateNumber

            viewHolder.itemView.setOnClickListener {

                val intent = Intent(this@GarageActivity, ViewBikeActivity::class.java)
                intent.putExtra("bike", bike)
                startActivity(intent)


            }

        }

        override fun getLayout(): Int {
            return R.layout.row_bike

        }
    }
}
