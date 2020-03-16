package com.elevintech.motorbro.Dashboard.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.elevintech.motorbro.AddOdometer.AddOdometerActivity
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.MotorcycleEditGeneralInformation.EditGeneralInformationActivity
import com.elevintech.motorbro.MyAccount.MyAccountActivity
import com.elevintech.motorbro.Utils.Utils

import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    val db = MotoroBroDatabase()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setupViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateOdometerLayout.setOnClickListener {
            val intent = Intent(context, AddOdometerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViews(view: View){
        db.getUserOdometers {
            // TODO: somehow get the last value only

            if (it.isNotEmpty()){
                val firstOdo = it.first()
                view.odometerStatementText.text = "Odometer : ${firstOdo.odometer}km updated as of ${firstOdo.date}"
            }

            //db.getUserBike { setBikeValues(it) }
            db.getUserBikes {
                val firstBike = it[0]
                setBikeValues(firstBike)
            }

            displayQrCode(view)
        }
    }

    private fun displayQrCode(view: View) {
        val uid = FirebaseAuth.getInstance().uid!!
        val qrCodeBitmap = Utils().generateQrCodeBitmap(uid)
        view.qrCodeImage.setImageBitmap(qrCodeBitmap)
    }

    private fun setBikeValues(bike: BikeInfo) {
        motorNameText.setText(bike.yearBought + " " + bike.brand.capitalize() + " " + bike.model.capitalize())
        plateNumberText.setText("#" + bike.plateNumber.toUpperCase())
        odometerDetailsText.setText("Odometer: " + bike.odometer + ".00 km")
//        breakDetailsText.setText("Front Break: " + bike.frontBreak + " , Rear Break: " + bike.rearBreak )
        bikeNicknameText.setText("Nickname: " + bike.nickname.capitalize())

        motorcycleImage

        if (bike.imageUrl != "") {
            Glide.with(this).load(bike.imageUrl).into(motorcycleImage)
        } else {
            // Put an empty image here
            Glide.with(this).load(R.drawable.new_empty_data_icon).into(motorcycleImage)
        }
//        fuelLiterText.setText(bike.fuelLiter.toString() + "L")
//        odometerText.setText(bike.odometerValue.toString() + "km")
//        fuelText.setText("₱ " + bike.income.toString() )
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}
