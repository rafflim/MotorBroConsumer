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
import kotlinx.android.synthetic.main.row_fuel.view.checkbox
import kotlinx.android.synthetic.main.row_type_of_parts.view.*

class TypeOfFuelActivity : AppCompatActivity() {

    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    val totalList = ArrayList<CheckboxObj>()
    private var isFromAddRefuel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_fuel)

        isFromAddRefuel = intent.getBooleanExtra("fromAddExtra", false)
        if (isFromAddRefuel) {
            addItemsButton.visibility = View.VISIBLE
        }

        viewAdapter = FuelAdapter(totalList)

        recycler_view_type_of_fuel.apply {
            //this.adapter = partsListAdapter
            this.adapter = viewAdapter
            setHasFixedSize(true)
        }

        addItemsButton.setOnClickListener {

            val selectedFuel = totalList.filter { it.isChecked }
            if (selectedFuel.count() != 1){
                Snackbar.make(addItemsButton, "Please select only one fuel type", Snackbar.LENGTH_LONG).show()
            }

            else {

                val returnIntent = Intent()
                returnIntent.putExtra("selectedFuel", selectedFuel.first().name)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()

            }

        }

        add_parts_floating_button.setOnClickListener {
            val intent = Intent(applicationContext, AddTypeOfFuel::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        deleteItemsButton.setOnClickListener {
            val listToDelete = totalList.filter { it.isChecked }
            var filteredList = totalList.filter { !it.isChecked }

            if (listToDelete.isEmpty()) {
                Snackbar.make(addItemsButton, "Please pick at least one fuel type", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val totalArrayList = ArrayList<CheckboxObj>(filteredList)
            totalList.clear()
            totalList.addAll(totalArrayList)
            viewAdapter.notifyDataSetChanged()

            for (part in listToDelete) {
                val db = MotoroBroDatabase()
                db.saveDeletedFuels(part.name) {
                    println("deleted ${part.name}")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        displayFuel()
    }

    private fun checkIfFuelIsActive(deletedFuels: List<String>, fuel: String):Boolean{

        // isActive == not in the deleted parts list
        var isActive = true

        for (deletedFuel in deletedFuels){

            if (deletedFuel.trim().toLowerCase() == fuel.trim().toLowerCase() ){
                isActive = false
            }

        }

        return isActive
    }

    private fun displayFuel() {
        totalList.clear()
        viewAdapter.notifyDataSetChanged()
        // fuel Created By User
        MotoroBroDatabase().getUser {
            val defaultFuels = Constants.TYPE_OF.fuel
            val customFuels = it.customFuel
            val deletedFuels = it.deletedFuels

            val allFuels = defaultFuels + customFuels
            val allFuelWithoutDeletedFuel = allFuels.filter { checkIfFuelIsActive(deletedFuels, it) }

            for (Fuel in allFuelWithoutDeletedFuel) {
                val Fuel = CheckboxObj(Fuel, false)
                totalList.add(Fuel)
            }

            totalList.sortBy { it.name }

            viewAdapter.notifyDataSetChanged()
        }
    }


    inner class FuelAdapter(private val myDataset: ArrayList<CheckboxObj>) :
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
            viewHolder.itemView.fuel_name.text = fuel.name
            viewHolder.itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("selectedFuel", fuel.name)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }

            viewHolder.itemView.checkbox.isChecked = fuel.isChecked

            // MARK: wag to idelete bro kasi pag tinanggal to pag iniscroll down ung recycler view nawawala ung check.
//            if (fuel.isChecked) {
//                viewHolder.itemView.checkbox.isChecked = true
//            } else {
//                viewHolder.itemView.checkbox.isChecked = false
//            }

//            viewHolder.itemView.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
//
//                fuel.isChecked = isChecked
//
//                val partsChecked = myDataset.filter { it.isChecked }
//                if (partsChecked.count() == 0){
//                    addItemsButton.alpha = 0.65f
//                    deleteItemsButton.alpha = 0.65f
//                } else {
//                    addItemsButton.alpha = 1f
//                    deleteItemsButton.alpha = 1f
//                }
//
//            }

            viewHolder.itemView.checkbox.setOnClickListener {
                fuel.isChecked = !fuel.isChecked

                val partsChecked = myDataset.filter { it.isChecked }
                if (partsChecked.count() == 0){
                    addItemsButton.alpha = 0.65f
                    deleteItemsButton.alpha = 0.65f
                } else {
                    addItemsButton.alpha = 1f
                    deleteItemsButton.alpha = 1f
                }
            }

//            viewHolder.itemView.removeItem.setOnClickListener {
//                removeItem(viewHolder.adapterPosition, viewHolder)
//            }


        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size

//        private fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
//            removedItem = myDataset[position]
//            removedPosition = position
//
//            myDataset.removeAt(position)
//            notifyItemRemoved(position)
//
//            val db = MotoroBroDatabase()
//            db.saveDeletedFuels(removedItem) {
//                println("deleted $removedItem")
//            }
//
//            Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).show()
//        }
    }







}
