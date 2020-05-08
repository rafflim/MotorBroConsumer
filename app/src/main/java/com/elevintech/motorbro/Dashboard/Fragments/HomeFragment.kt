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
import com.elevintech.motorbro.AdsView.AdsViewActivity
import com.elevintech.motorbro.Garage.GarageActivity
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.MotorcycleEditGeneralInformation.EditGeneralInformationActivity
import com.elevintech.motorbro.MyAccount.MyAccountActivity
import com.elevintech.motorbro.Utils.Constants
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.motorbro.ViewBike.ViewBikeActivity

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
            if (!isAdded) {
                return@getUserOdometers
            }

            if (it.isNotEmpty()){
                val firstOdo = it.first()
                val firstOdoDistance = Utils().numberFormatWithComma( firstOdo.odometer )
                view.odometerStatementText.text = "Odometer : $firstOdoDistance km updated as of ${firstOdo.date}"
            }

            adsLayout1.setOnClickListener {
                val intent = Intent(context, AdsViewActivity::class.java)
                intent.putExtra("adType", Constants.AD_TYPE.SAMURAI_PAINT)
                startActivity(intent)
            }

            adsLayout2.setOnClickListener {
                val intent = Intent(context, AdsViewActivity::class.java)
                intent.putExtra("adType", Constants.AD_TYPE.OWENS)
                startActivity(intent)
            }

            db.getUserBikes {
                if (!isAdded) {
                    return@getUserBikes
                }

                var isThereAPrimaryBike = false
                var primaryBike = BikeInfo()

                if (it.isEmpty()) {
                    return@getUserBikes
                }

                for (bike in it) {
                    if (bike.primary == true ){
                        isThereAPrimaryBike = true
                        primaryBike = bike
                    }
                }


                if (!isThereAPrimaryBike) {
                    // pag walang primary bike set tayo ng primary bike, yung pinakauna.. this is for old users
                    MotoroBroDatabase().updateNoPreviousMainBike(it[0]) {
                        if (!isAdded) {
                            return@updateNoPreviousMainBike
                        }
                        // then setup the views with the bike
                        setBikeValues(view, it[0])


                    }
                } else {
                    setBikeValues(view, primaryBike)
                }
            }

//            db.getUserMainBikeFromBikes {
//                // For now just get the user bikes
//                // if there is no primary bike get the first bike of the array do it on the getuserbikes method
//                if (!isAdded) {
//                    return@getUserMainBikeFromBikes
//                }
//                if (it != null) {
//                    setBikeValues(view, it)
//                } else {
//                }
//            }

            displayQrCode(view)
        }
    }

    private fun displayQrCode(view: View) {
        val uid = FirebaseAuth.getInstance().uid!!
        val qrCodeBitmap = Utils().generateQrCodeBitmap(uid)
        view.qrCodeImage.setImageBitmap(qrCodeBitmap)
    }

    private fun setBikeValues(v: View, bike: BikeInfo) {

        v.motorNameText.setText(bike.yearBought + " " + bike.brand.capitalize() + " " + bike.model.capitalize())
        v.plateNumberText.setText("#" + bike.plateNumber.toUpperCase())
        if (bike.lastOdometerUpdate != ""){
            val lastOdometerUpdate = Utils().convertMillisecondsToDate( bike.lastOdometerUpdate.toLong(),  "MMM d, yyyy")
            v.odometerStatementText.text = "Odometer : ${bike.odometer} km updated as of ${lastOdometerUpdate}"
        }

        MotoroBroDatabase().getUserBikes {
            if (!isAdded) { return@getUserBikes }

            if (it.count() > 1)
                changePrimaryBike.visibility = View.VISIBLE
        }

        if (bike.imageUrl != "") {
            Glide.with(this).load(bike.imageUrl).into(v.motorcycleImage)
        } else {
            // Put an empty image here
            Glide.with(this).load(R.drawable.new_empty_data_icon).into(v.motorcycleImage)
        }

        motorcycleLayout.setOnClickListener {

            val intent = Intent(activity, ViewBikeActivity::class.java)
            intent.putExtra("bike", bike)
            startActivity(intent)

        }

        changePrimaryBike.setOnClickListener {

            val intent = Intent(activity, GarageActivity::class.java)
            startActivity(intent)

        }

    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}
