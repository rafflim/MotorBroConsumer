package com.elevintech.motorbro.TypeOf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.elevintech.motorbro.AddParts.AddPartsActivity
import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_parts.*
import kotlinx.android.synthetic.main.row_parts.view.*
import android.app.Activity
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elevintech.motorbro.Constants
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_type_of_parts.*
import kotlinx.android.synthetic.main.fragment_parts.recycler_view_type_of_parts
import kotlinx.android.synthetic.main.row_type_of_parts.*
import kotlinx.android.synthetic.main.row_type_of_parts.view.*


class TypeOfPartsActivity : AppCompatActivity() {

    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    val totalList = ArrayList<checkboxObj>()
    //private val partsListAdapter = MyAdapter<MyAdapter.MyViewHolder>()

    var isFromAddParts = false
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "mindorks-welcome"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_parts)

        //isFromAddParts = intent.getSerializableExtra("fromAddExtra", false)
        isFromAddParts = intent.getBooleanExtra("fromAddExtra", false)
        viewAdapter = MyAdapter(totalList)

        if (isFromAddParts) {
            addItemsButton.visibility = View.VISIBLE
        }

        recycler_view_type_of_parts.apply {
            //this.adapter = partsListAdapter
            this.adapter = viewAdapter
            setHasFixedSize(true)
        }
        // If from nav bar then set click will be different
        add_parts_floating_button.setOnClickListener {
            val intent = Intent(applicationContext, AddTypeOfParts::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        deleteItemsButton.setOnClickListener {
            val listToDelete = totalList.filter { it.isChecked }
            var filteredList = totalList.filter { !it.isChecked }

            if (listToDelete.isEmpty()) {
                Snackbar.make(addItemsButton, "Please pick at least one parts / services", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val totalArrayList = ArrayList<checkboxObj>(filteredList)
            totalList.clear()
            totalList.addAll(totalArrayList)
            viewAdapter.notifyDataSetChanged()

            //remove these items 1by1
            for (part in listToDelete) {
                val db = MotoroBroDatabase()
                db.saveDeletedParts(part.name) {
                    println("deleted ${part.name}")
                }
            }
        }

        addItemsButton.setOnClickListener {
            val listToAdd = totalList.filter { it.isChecked }

            if (listToAdd.isEmpty()) {
                Snackbar.make(addItemsButton, "Please pick at least one parts / services", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var partsRemaining = ""
            var isFirst = true

            for (part in listToAdd) {
                if (!isFirst) {
                    partsRemaining += ", "
                }
                partsRemaining += part.name
                isFirst = false
            }

            val returnIntent = Intent()
                returnIntent.putExtra("selectedPart", partsRemaining)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
        }

    }

    override fun onResume() {
        super.onResume()
        displayParts()
    }


    private fun displayParts() {
        // Parts Created By User
        totalList.clear()
        viewAdapter.notifyDataSetChanged()

        MotoroBroDatabase().getUser {
            // Default Parts
            // Put this after getting the users custom part, para sabay silang magdisplay sa recyclerview
            val deletedParts = it.deletedParts

            for (part in Constants.TYPE_OF.parts) {
                var isIncluded = false
                for (deletedPart in deletedParts) {
                    if (part == deletedPart.trim()) {
                        println("HI THER")
                        println("$deletedPart and $part")
                        isIncluded = true
                        break
                    }
                }
                if (!isIncluded) {
                    val part = checkboxObj(part, false)
                    totalList.add(part)
                }
            }

            for (customPart in it.customParts) {
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

                if (!isIncluded) {
                    val part = checkboxObj(properlyCapitalized, false)
                    totalList.add(part)
                }
            }

            viewAdapter.notifyDataSetChanged()

        }

    }



//    inner class PartsItem(val part: String): Item<ViewHolder>() {
//
//
//        override fun bind(viewHolder: ViewHolder, position: Int) {
//
//            viewHolder.itemView.parts_name.text = part
//            viewHolder.itemView.setOnClickListener {
//                val returnIntent = Intent()
//                returnIntent.putExtra("selectedPart", part)
//                setResult(Activity.RESULT_OK, returnIntent)
//                finish()
//            }
//
//            viewHolder.itemView.removeItem.setOnClickListener {
//
//            }
//        }
//
//        override fun getLayout(): Int {
//
//            return com.elevintech.myapplication.R.layout.row_type_of_parts
//        }


    inner class MyAdapter(private val myDataset: ArrayList<checkboxObj>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

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
        ): MyAdapter.MyViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_type_of_parts, parent, false)
            // set the view's size, margins, paddings and layout parameters

            return MyViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            val part = myDataset[position]
            viewHolder.itemView.parts_name.text = part.name

//            viewHolder.itemView.setOnClickListener {
//
//                val returnIntent = Intent()
//                returnIntent.putExtra("selectedPart", part)
//                setResult(Activity.RESULT_OK, returnIntent)
//                finish()
//            }

//            viewHolder.itemView.removeItem.setOnClickListener {
//                removeItem(viewHolder.adapterPosition, viewHolder)
//            }

            viewHolder.itemView.checkbox.isChecked = part.isChecked

            viewHolder.itemView.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

                part.isChecked = isChecked

                val partsChecked = myDataset.filter { it.isChecked }
                if (partsChecked.count() == 0){
                    addItemsButton.alpha = 0.65f
                    deleteItemsButton.alpha = 0.65f
                } else {
                    addItemsButton.alpha = 1f
                    deleteItemsButton.alpha = 1f
                }

            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size

//        fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
//            removedItem = myDataset[position]
//            removedPosition = position
//
//            myDataset.removeAt(position)
//            notifyItemRemoved(position)
//
//            val db = MotoroBroDatabase()
//            db.saveDeletedParts(removedItem) {
//                println("deleted $removedItem")
//            }
//
//            Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).show()
//            // Add the delete to the users deleted parts
//        }
    }

}

class checkboxObj(var name: String = "", var isChecked: Boolean = false)
