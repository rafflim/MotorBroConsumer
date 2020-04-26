package com.elevintech.motorbro.Shop

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.ShopView.ShopViewActivity
import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_shop.*
import kotlinx.android.synthetic.main.row_shop_item_layout.view.*
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.elevintech.motorbro.Favorites.FavoritesActivity
import java.util.*
import kotlin.collections.ArrayList
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class ShopActivity : AppCompatActivity() {


    val shopAdapter = GroupAdapter<ViewHolder>()
    var locationCity = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        backButton.setOnClickListener {
            finish()
        }

        searchButton.setOnClickListener {
            shopAdapter.clear()

            shopFoundWithinCityNotification.visibility = View.GONE

            if ( searchCriteria.text.toString() == "" ){
                getShops(locationCity)

            } else {
                val searchTagsArray = stringToWords( searchCriteria.text.toString() )
                val db = MotoroBroDatabase()
                db.searchShop(searchTagsArray){ shopsSearched ->

                    searchCriteria.hideKeyboard()
                    refresh.visibility = View.VISIBLE

                    if (shopsSearched.count() == 0){
                        noShopsFoundLayout.visibility = View.VISIBLE
                    } else{
                        for (shop in shopsSearched) {
                            shopAdapter.add(shopItem(shop, false))
                        }
                    }

                }

            }

        }

        refresh.setOnClickListener {
            getShops(locationCity)
            refresh.visibility = View.GONE
            noShopsFoundLayout.visibility = View.GONE
            searchCriteria.setText("")
        }

        setupActionBar()
        setupViews()
        askLocationAccess()
    }

    private fun stringToWords(mnemonic: String): List<String> {
        val words = ArrayList<String>()
        for (w in mnemonic.trim(' ').split(" ")) {
            if (w.isNotEmpty()) {
                words.add(w.toLowerCase())
            }
        }
        return words
    }

    private fun setupActionBar() {
        // Set the toolbar as support action bar
        setSupportActionBar(toolbar)

        // Now get the support action bar
        val actionBar = supportActionBar

        actionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.myFavorites -> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.shop_menu, menu)
        return true
    }

    private fun setupViews() {
        shopRecyclerView.isNestedScrollingEnabled = false
        shopRecyclerView.layoutManager = GridLayoutManager(this, 2)
        shopRecyclerView.adapter = shopAdapter

    }

    fun getShops(city: String) {

        shopFoundWithinCityNotification.visibility = View.GONE

        shopAdapter.clear()

        val db = MotoroBroDatabase()
        db.getShopsByCity(city) { shopsWithinCity, shopsOutsideCity ->
//            val sortedList = it.sortedWith(compareBy { it.spotlight })
//            val reversedList = sortedList.reversed()

            val shopsWithinCityHowMany = shopsWithinCity.count()
            if (shopsWithinCityHowMany > 0){
                shopsFoundWithinCityText.text = shopsWithinCityHowMany.toString() + " shops within your area ($city city)"
                shopFoundWithinCityNotification.visibility = View.VISIBLE
            }

            for (shop in shopsWithinCity) {
                if (shop.deviceTokens.isNotEmpty())
                    shopAdapter.add(shopItem(shop, true))
            }

            for (shop in shopsOutsideCity) {
                if (shop.deviceTokens.isNotEmpty())
                    shopAdapter.add(shopItem(shop, false))
            }

        }

    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun askLocationAccess() {

        println("getLocation")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // ASK PERMISSION TO OPEN GALLERY
            RuntimePermission.askPermission(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .onAccepted{

                    getLocation()

                }
                .ask()

        }


        else {
            getLocation()
        }
    }

    fun getLocation(){

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null. (docs. https://developer.android.com/training/location/retrieve-current)
                if (location == null) {
                    println("location is null")
                }

                else {
                    val longitude = location.longitude
                    val latitude = location.latitude

                    val geocoder = Geocoder(applicationContext, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses[0]

                    var city = address.locality
                    city = city.replace("City", "") // sometimes the city value has the word "City" in it, we don't need that
                    city = city.trimEnd() // remove white space
                    locationCity = city

                    getShops(locationCity)

                    /* address object sample values #1
                    * FEISABELLE'S APARTMENT
                    * ----------------------
                    * thoroughfare: San Pedro
                    * locality: Caloocan
                    * subLocality: Novaliches
                    * adminArea: Metro Manila
                    */

                    /* address object sample values #2
                    * BAHAY NUNG ISANG DEVELOPER
                    * --------------------------
                    * thoroughfare: Lentils Drive
                    * locality: Antipolo
                    * adminArea: Calabarzon
                    * subAdminArea: Rizal
                    */

                    // UNCOMMENT FOR EASY PRINTING OF VALUES
                    /*
                    val location = "featureName: " + address.featureName +
                                    ", thoroughfare: " + address.thoroughfare +
                                    ", locality: " +  address.locality +
                                    ", subLocality: " + address.subLocality +
                                    ", adminArea: " + address.adminArea

                    val fineLocation = "premises: " + address.premises +
                                        ", subAdminArea: " + address.subAdminArea +
                                        ", subThoroughfare: " + address.subThoroughfare +
                                        ", postalCode: " + address.postalCode

                    println("location $location")
                    println("fineLocation $fineLocation")
                    */




                }

            }

    }

    inner class shopItem(val shop: Shop, val isWithinCity: Boolean): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            if (shop.imageUrl != ""){
                Glide.with(this@ShopActivity).load(shop.imageUrl).into(viewHolder.itemView.shopImageView)
            }

            if(shop.spotlight) {
                viewHolder.itemView.topStoreLayout.visibility = View.VISIBLE
                viewHolder.itemView.sponsoredLayout.visibility = View.VISIBLE
            } else {
                viewHolder.itemView.topStoreLayout.visibility = View.GONE
                viewHolder.itemView.sponsoredLayout.visibility = View.GONE
            }

            if (isWithinCity)
                viewHolder.itemView.locationIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this@ShopActivity, R.color.colorAccent)))


            //Glide.with(activity!!).load(user.userProfileMainImageUrl).into(viewHolder.itemView.shopImageView)
            viewHolder.itemView.shopName.text = shop.name.capitalize()
//            viewHolder.itemView.shopDescription.text = shop.description

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(this@ShopActivity, ShopViewActivity::class.java)
                intent.putExtra("shop", shop)
                startActivity(intent)
            }

            viewHolder.itemView.mainLocationText.text = shop.address.capitalize()
        }

        override fun getLayout(): Int {
            return R.layout.row_shop_item_layout
        }
    }
}
