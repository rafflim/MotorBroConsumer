package com.elevintech.motorbro.Dashboard.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbro.MotorcycleEditGeneralInformation.EditGeneralInformationActivity
import com.elevintech.motorbro.MyAccount.MyAccountActivity

import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
    }

    override fun onResume() {
        super.onResume()

        clickToEditLayout.setOnClickListener {
            val intent = Intent(activity, EditGeneralInformationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


    }

    override fun onDetach() {
        super.onDetach()
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}
