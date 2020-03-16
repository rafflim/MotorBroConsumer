package com.elevintech.motorbro.TypeOf

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elevintech.motorbro.Constants
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_type_of_fuel.*
import kotlinx.android.synthetic.main.row_fuel.view.*

class TypeOfFuelActivity : AppCompatActivity() {

    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    val totalList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_fuel)

        viewAdapter = FuelAdapter(totalList)

        recycler_view_type_of_fuel.apply {
            //this.adapter = partsListAdapter
            this.adapter = viewAdapter
            setHasFixedSize(true)
        }

        add_parts_floating_button.setOnClickListener {
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
        totalList.clear()
        viewAdapter.notifyDataSetChanged()
        // fuel Created By User
        MotoroBroDatabase().getUser {
            // Default fuel
            // Put this after getting the users custom part, para sabay silang magdisplay sa recyclerview
//            for (part in Constants.TYPE_OF.fuel) {
//                fuelListAdapter.add(fuelItem(part))
//            }
//
//            for (customPart in it.customFuel) {
//                var properlyCapitalized = (customPart.toLowerCase()).capitalize()
//                fuelListAdapter.add(fuelItem(properlyCapitalized))
//            }

            val deletedParts = it.deletedFuels

            for (part in Constants.TYPE_OF.fuel) {
                var isIncluded = false
                for (deletedPart in deletedParts) {
                    if (part == deletedPart.trim()) {
                        println("HI THER")
                        println("$deletedPart and $part")
                        isIncluded = true
                        break
                    }
                }
                if (!isIncluded) { totalList.add(part) }
            }

            for (customPart in it.customFuel) {
                val properlyCapitalized = (customPart.toLowerCase()).capitalize()
                //totalList.add(properlyCapitalized)
                var isIncluded = false
                for (deletedPart in deletedParts) {

                    if (properlyCapitalized == deletedPart.trim()) {
                        println("HI THER")
                        println("$deletedPart and $properlyCapitalized")
                        isIncluded = true
                        break
                    }
                }

                if (!isIncluded) { totalList.add(properlyCapitalized) }
            }
            viewAdapter.notifyDataSetChanged()
        }
    }


//    inner class fuelItem(val part: String): Item<ViewHolder>() {
//
//
//        override fun bind(viewHolder: ViewHolder, position: Int) {
//
//            viewHolder.itemView.fuel_name.text = part
//            viewHolder.itemView.setOnClickListener {
//                val returnIntent = Intent()
//                returnIntent.putExtra("selectedFuel", part)
//                setResult(Activity.RESULT_OK, returnIntent)
//                finish()
//            }
//        }
//
//        override fun getLayout(): Int {
//
//            return R.layout.row_fuel
//        }
//    }


    inner class FuelAdapter(private val myDataset: ArrayList<String>) :
        RecyclerView.Adapter<FuelAdapter.MyViewHolder>() {

        private var removedPosition: Int = 0
        private var removedItem: String = ""
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        inner class MyViewHolder(val v: View) : RecyclerView.ViewHolder(v)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FuelAdapter.MyViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_fuel, parent, false)
            // set the view's size, margins, paddings and layout parameters

            return MyViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            val fuel = myDataset[position]
            viewHolder.itemView.fuel_name.text = fuel
            viewHolder.itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("selectedFuel", fuel)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }

            viewHolder.itemView.removeItem.setOnClickListener {
                removeItem(viewHolder.adapterPosition, viewHolder)
            }


        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size

        private fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
            removedItem = myDataset[position]
            removedPosition = position

            myDataset.removeAt(position)
            notifyItemRemoved(position)

            val db = MotoroBroDatabase()
            db.saveDeletedFuels(removedItem) {
                println("deleted $removedItem")
            }

            Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).show()
        }
    }







}
