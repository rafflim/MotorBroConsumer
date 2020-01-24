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
import kotlinx.android.synthetic.main.activity_type_of_history.*
import kotlinx.android.synthetic.main.row_history.view.*

class TypeOfHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_history)

        add_history_floating_button.setOnClickListener {
            val intent = Intent(applicationContext, AddTypeOfHistory::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        displayHistory()
    }


    private fun displayHistory() {
        var historyListAdapter = GroupAdapter<ViewHolder>()


        // history Created By User
        MotoroBroDatabase().getUser {
            // Default history
            // Put this after getting the users custom part, para sabay silang magdisplay sa recyclerview
            for (part in Constants.TYPE_OF.history) {
                historyListAdapter.add(historyItem(part))
            }

            for (customPart in it.customHistory) {

                var properlyCapitalized = (customPart.toLowerCase()).capitalize()
                historyListAdapter.add(historyItem(properlyCapitalized))
            }
        }

        recycler_view_type_of_history.adapter = historyListAdapter


    }


    inner class historyItem(val part: String): Item<ViewHolder>() {


        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.history_name.text = part
            viewHolder.itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("selectedHistory", part)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        override fun getLayout(): Int {

            return R.layout.row_history
        }
    }










}
