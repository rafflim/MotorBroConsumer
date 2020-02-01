package com.elevintech.motorbro.TypeOf

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.Constants
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_type_of_fuel.*
import kotlinx.android.synthetic.main.row_fuel.view.*

class TypeOfFuelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_fuel)

        add_fuel_floating_button.setOnClickListener {
            val intent = Intent(applicationContext, AddTypeOfFuel::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        displayFuel()
    }


    private fun displayFuel() {
        var fuelListAdapter = GroupAdapter<ViewHolder>()


        // fuel Created By User
        MotoroBroDatabase().getUser {
            // Default fuel
            // Put this after getting the users custom part, para sabay silang magdisplay sa recyclerview
            for (part in Constants.TYPE_OF.fuel) {
                fuelListAdapter.add(fuelItem(part))
            }

            for (customPart in it.customFuel) {

                var properlyCapitalized = (customPart.toLowerCase()).capitalize()
                fuelListAdapter.add(fuelItem(properlyCapitalized))
            }
        }

        recycler_view_type_of_fuel.adapter = fuelListAdapter


    }


    inner class fuelItem(val part: String): Item<ViewHolder>() {


        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.fuel_name.text = part
            viewHolder.itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("selectedFuel", part)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        override fun getLayout(): Int {

            return R.layout.row_fuel
        }
    }










}
