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
import com.elevintech.motorbro.Constants
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_type_of_parts.*
import kotlinx.android.synthetic.main.fragment_parts.recycler_view_type_of_parts


class TypeOfPartsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_parts)

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
        var partsListAdapter = GroupAdapter<ViewHolder>()



        // Parts Created By User
        MotoroBroDatabase().getUser{
            // Default Parts
            // Put this after getting the users custom part, para sabay silang magdisplay sa recyclerview
            for (part in Constants.TYPE_OF.parts) {
                partsListAdapter.add(partsItem(part))
            }

            for (customPart in it.customParts){

                var properlyCapitalized = (customPart.toLowerCase()).capitalize()
                partsListAdapter.add(partsItem(properlyCapitalized))
            }
        }

        recycler_view_type_of_parts.adapter = partsListAdapter



    }

    inner class partsItem(val part: String): Item<ViewHolder>() {


        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.parts_name.text = part
            viewHolder.itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra("selectedPart", part)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        override fun getLayout(): Int {

            return R.layout.row_type_of_parts
        }
    }
}
