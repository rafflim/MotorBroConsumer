package com.elevintech.motorbro.TypeOf

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
import kotlinx.android.synthetic.main.activity_type_of_brand.*
import kotlinx.android.synthetic.main.row_type_of_brand.view.*


class TypeOfBrandActivity : AppCompatActivity() {

    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    val totalList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_brand)

        viewAdapter = BrandAdapter(totalList)
        recycler_view_type_of_brands.apply {
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
    }

    override fun onResume() {
        super.onResume()
        displayParts()
    }

    private fun displayParts() {
        // Parts Created By User


        MotoroBroDatabase().getUser{
            // Default Parts
            // Put this after getting the users custom part, para sabay silang magdisplay sa recyclerview

            for (part in Constants.TYPE_OF.parts) {
                totalList.add(part)
            }

            for (customPart in it.customParts){
                var properlyCapitalized = (customPart.toLowerCase()).capitalize()
                totalList.add(properlyCapitalized)
            }

            viewAdapter.notifyDataSetChanged()
//
//            for (part in totalList) {
//                //partsListAdapter.add(PartsItem(part))
//            }

            //viewAdapter = MyAdapter(totalList)
        }
    }

    inner class BrandAdapter(private val myDataset: ArrayList<String>) :
        RecyclerView.Adapter<BrandAdapter.MyViewHolder>() {

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
        ): BrandAdapter.MyViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_type_of_brand, parent, false)
            // set the view's size, margins, paddings and layout parameters

            return MyViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            val part = myDataset[position]
            viewHolder.itemView.parts_name.text = part

//            viewHolder.itemView.setOnClickListener {
//                                val returnIntent = Intent()
//                returnIntent.putExtra("selectedPart", part)
//                setResult(Activity.RESULT_OK, returnIntent)
//                finish()
//            }

            viewHolder.itemView.removeItem.setOnClickListener {
                removeItem(viewHolder.adapterPosition, viewHolder)
            }


        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size

        fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
            removedItem = myDataset[position]
            removedPosition = position

            myDataset.removeAt(position)
            notifyItemRemoved(position)

            Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).setAction("UNDO") {
                 myDataset.add(removedPosition, removedItem)
                notifyItemInserted(removedPosition)
                //notifyItemRemoved(removedPosition)
            }.show()
        }
    }
}
