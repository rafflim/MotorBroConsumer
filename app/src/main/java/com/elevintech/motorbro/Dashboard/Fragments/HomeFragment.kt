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
import com.elevintech.motorbro.Achievements.AchievementsActivity
import com.elevintech.motorbro.AddOdometer.AddOdometerActivity
import com.elevintech.motorbro.AdsView.AdsViewActivity
import com.elevintech.motorbro.Chat.ChatListActivity
import com.elevintech.motorbro.Garage.GarageActivity
import com.elevintech.motorbro.Glovebox.GloveboxActivity
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.More.MoreActivity
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.MotorcycleEditGeneralInformation.EditGeneralInformationActivity
import com.elevintech.motorbro.MyAccount.MyAccountActivity
import com.elevintech.motorbro.Shop.ShopActivity
import com.elevintech.motorbro.TypeOf.TypeOfBrandActivity
import com.elevintech.motorbro.TypeOf.TypeOfFuelActivity
import com.elevintech.motorbro.TypeOf.TypeOfPartsActivity
import com.elevintech.motorbro.Utils.Constants
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.motorbro.ViewBike.ViewBikeActivity

import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.text.NumberFormat
import java.util.*

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

        shopButton2.setOnClickListener {
            val intent = Intent(context, ShopActivity::class.java)
            startActivity(intent)
        }

        chatButton.setOnClickListener {
            val intent = Intent(context, ChatListActivity::class.java)
            startActivity(intent)
        }

        setupHomeLinks()
    }

    private fun setupHomeLinks() {
        home_garage_layout.setOnClickListener {
            val intent = Intent(context, GarageActivity::class.java)
            startActivity(intent)
        }

        home_glovebox_layout.setOnClickListener {
            val intent = Intent(context, GloveboxActivity::class.java)
            startActivity(intent)
        }

        home_myaccount_layout.setOnClickListener {
            val intent = Intent(context, MyAccountActivity::class.java)
            startActivity(intent)
        }

        home_achievements_layout.setOnClickListener {
            val intent = Intent(context, AchievementsActivity::class.java)
            startActivity(intent)
        }

        home_parts_layout.setOnClickListener {
            val intent = Intent(context, TypeOfPartsActivity::class.java)
            startActivity(intent)
        }

        home_brandslist_layout.setOnClickListener {
            val intent = Intent(context, TypeOfBrandActivity::class.java)
            startActivity(intent)
        }

        home_typesfuel_layout.setOnClickListener {
            val intent = Intent(context, TypeOfFuelActivity::class.java)
            startActivity(intent)
        }

        home_settings_layout.setOnClickListener {
            val intent = Intent(context, MoreActivity::class.java)
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
                var numberFormatted = NumberFormat.getCurrencyInstance().format(firstOdo.odometer)

//               var equals = NumberFormat.getNumberInstance(Locale.US).format(35634646)
//                var cib = NumberFormat.getNumberInstance(Locale.US).format(firstOdo.odometer)

                view.odometerStatementText.text = "Odometer : $numberFormatted km updated as of ${firstOdo.date}"

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

        displayMessageBadge(view)
    }

    private fun displayQrCode(view: View) {
        val uid = FirebaseAuth.getInstance().uid!!
        val qrCodeBitmap = Utils().generateQrCodeBitmap(uid)
        view.qrCodeImage.setImageBitmap(qrCodeBitmap)
    }

    private fun displayMessageBadge(view: View){

        MotoroBroDatabase().getUnreadMessageCount{ unreadMessageCount ->

            if (!isAdded) {
                return@getUnreadMessageCount
            }
            view.chatButton.setBadgeValue(unreadMessageCount)
        }
    }

    private fun setBikeValues(v: View, bike: BikeInfo) {

        v.motorNameText.setText(bike.yearBought + " " + bike.brand.capitalize() + " " + bike.model.capitalize())
        v.plateNumberText.setText("#" + bike.plateNumber.toUpperCase())
        if (bike.lastOdometerUpdate != ""){
            val lastOdometerUpdate = Utils().convertMillisecondsToDate( bike.lastOdometerUpdate.toLong(),  "MMM d, yyyy")

            val doubleOdo = bike.odometer.toDoubleOrNull()

            println(doubleOdo)

            val formattedOdo = doubleOdo?.let { Utils().numberFormatWithComma(it) }

            v.odometerStatementText.text = "Odometer : ${formattedOdo}km updated as of ${lastOdometerUpdate}"
        }

        MotoroBroDatabase().getUserBikes {
            if (!isAdded) { return@getUserBikes }

            if (it.count() > 1)
                changePrimaryBike.visibility = View.VISIBLE
        }

//        if (bike.imageUrl != "") {
//            Glide.with(this).load(bike.imageUrl).into(v.motorcycleImage)
//        } else {
//            // Put an empty image here
//            Glide.with(this).load(R.drawable.new_empty_data_icon).into(v.motorcycleImage)
//        }

//        motorcycleLayout.setOnClickListener {
//            val intent = Intent(activity, ViewBikeActivity::class.java)
//            intent.putExtra("bike", bike)
//            startActivity(intent)
//        }

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
