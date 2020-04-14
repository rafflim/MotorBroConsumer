package com.elevintech.motorbro.Dashboard.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elevintech.motorbro.Model.BikeParts
import com.elevintech.motorbro.Model.History
import com.elevintech.motorbro.Model.OdometerUpdate
import com.elevintech.motorbro.Model.Reminders
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils

import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.row_history_layout.view.*


class HistoryFragment : Fragment() {


    //val historyAdapter = GroupAdapter<ViewHolder>()
    lateinit var historyAdapter: MyAdapter
    var listOfHistory = ArrayList<History>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        //historyAdapter.clear()
        //historyAdapter
        listOfHistory.clear()
        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()

        //historyAdapter.clear()
        listOfHistory.clear()
    }

    fun setupRecyclerView() {

        historyAdapter = MyAdapter(listOfHistory)
        historyRecyclerView.layoutManager = LinearLayoutManager(activity)
        historyRecyclerView.isNestedScrollingEnabled = true
        historyRecyclerView.adapter = historyAdapter

        //historyRecyclerView.adapter = newHistoryAdapter
    }

    private fun setupViews() {

        val db = MotoroBroDatabase()
        db.getUserHistory {
            // TODO: somehow get the last value only
            val historyList = it
            if(historyList.isNotEmpty()){
                if (noDataLayout != null) {
                    noDataLayout.visibility = View.GONE
                }
            } else {
                if (noDataLayout != null) {
                    noDataLayout.visibility = View.VISIBLE
                }
            }

            this.listOfHistory.addAll(historyList)
            historyAdapter.notifyDataSetChanged()

//            for (history in it) {
//                historyAdapter.add(historyItem(history))
//            }
        }
    }

    inner class historyItem(val history: History): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            var historyTitle = ""

            when(history.typeOfHistory){
                "bike-parts" -> { historyTitle = "Added ${history.value}"}
                "odometer" -> { historyTitle = "Updated odometer to ${history.value} km " }
                "refueling" -> { historyTitle = "Refueled ${history.value}"}
            }

            viewHolder.itemView.historyDate.text = Utils().convertMillisecondsToDate(history.dateLong, "MMM d, yyyy")
            viewHolder.itemView.historyTitle.text = historyTitle
        }

        override fun getLayout(): Int {
            return R.layout.row_history_layout
        }
    }

    inner class MyAdapter(private val myDataset: ArrayList<History>) :
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
                .inflate(R.layout.row_history_layout, parent, false)
            // set the view's size, margins, paddings and layout parameters

            return MyViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            val history = myDataset[position]

            var historyTitle = ""

            when(history.typeOfHistory){
                "bike-parts" -> { historyTitle = "Added ${history.value}"}
                "odometer" -> { historyTitle = "Updated odometer to ${history.value} km " }
                "refueling" -> { historyTitle = "Refueled ${history.value}"}
            }

            viewHolder.itemView.historyDate.text = Utils().convertMillisecondsToDate(history.dateLong, "MMM d, yyyy")
            viewHolder.itemView.historyTitle.text = historyTitle

            viewHolder.itemView.deleteHistory.setOnClickListener {
                // delete the item
                createAlertDialog(position, history)
            }

        }

        private fun deleteHistory(position: Int, history: History) {

            myDataset.removeAt(position)
            notifyDataSetChanged()

            MotoroBroDatabase().deleteUserHistory(history) {

            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size


        private fun createAlertDialog(position: Int, history: History){
            // instantiate dialog
            val builder = AlertDialog.Builder(activity)
            val optionDialog = AlertDialog.Builder(activity).create()
            // instantiate the view for the dialog
            val viewInflated = layoutInflater.inflate(R.layout.dialog_delete_item, null)
            // inflate the view in the dialog
            builder.setView(viewInflated)
            // set the dialog title
            builder.setTitle("Delete item?")
            builder.setCancelable(true) // can be set to false, to make the dialog undismissable
            builder.setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which ->
                    println("works")
                    // dismiss dialog after
                    dialog.dismiss()
                    deleteHistory(position, history)
                })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })

            builder.show()
        }
    }
}
