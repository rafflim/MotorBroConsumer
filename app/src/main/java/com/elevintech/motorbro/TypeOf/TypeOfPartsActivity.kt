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



class TypeOfPartsActivity : AppCompatActivity() {


    var listOfParts = listOf<String>("Headlight", "Tires", "Brakes", "Helmet", "Back Seat", "Front Seat", "Accelerator", "Battery")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_parts)

        displayParts()
    }


    private fun displayParts() {
        var partsListAdapter = GroupAdapter<ViewHolder>()

        for (part in listOfParts) {
            partsListAdapter.add(partsItem(part))
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

            return R.layout.row_parts
        }
    }
}
