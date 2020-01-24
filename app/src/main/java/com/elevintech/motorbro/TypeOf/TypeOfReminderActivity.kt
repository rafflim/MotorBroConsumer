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
import kotlinx.android.synthetic.main.activity_type_of_reminder.*
import kotlinx.android.synthetic.main.row_reminders.view.*

class TypeOfReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_reminder)

        add_reminders_floating_button.setOnClickListener {
            val intent = Intent(applicationContext, AddTypeOfReminders::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        displayReminders()
    }

    private fun displayReminders() {
        var remindersListAdapter = GroupAdapter<ViewHolder>()

        // Parts Created By User
        MotoroBroDatabase().getUser{
            // Default Parts
            // Put this after getting the users custom part, para sabay silang magdisplay sa recyclerview
            for (part in Constants.TYPE_OF.reminders) {
                remindersListAdapter.add(reminderItem(part))
            }

            for (customReminder in it.customReminders){

                var properlyCapitalized = (customReminder.toLowerCase()).capitalize()
                remindersListAdapter.add(reminderItem(properlyCapitalized))
            }
        }

        recycler_view_type_of_reminders.adapter = remindersListAdapter

    }

    inner class reminderItem(val part: String): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.reminder_name.text = part
            viewHolder.itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("selectedReminder", part)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        override fun getLayout(): Int {

            return R.layout.row_reminders
        }
    }
}
