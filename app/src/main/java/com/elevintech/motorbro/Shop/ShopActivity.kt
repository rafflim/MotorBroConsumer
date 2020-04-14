package com.elevintech.motorbro.Shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.elevintech.motorbro.Favorites.FavoritesActivity


class ShopActivity : AppCompatActivity() {


    val shopAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        backButton.setOnClickListener {
            finish()
        }

        searchButton.setOnClickListener {
            shopAdapter.clear()

            if ( searchCriteria.text.toString() == "" ){
                getShops()
            } else {
                val searchTagsArray = stringToWords( searchCriteria.text.toString() )
                val db = MotoroBroDatabase()
                db.searchShop(searchTagsArray){
                    for (shop in it) {
                        shopAdapter.add(shopItem(shop))
                    }
                }
            }
        }

        setupActionBar()
        setupViews()
        getShops()
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

    private fun getShops() {
        val db = MotoroBroDatabase()
        db.getShops {
//            for (shop in it) {
//                shopAdapter.add(shopItem(shop))
//            }
//
//            it.sortBy {  }

            val sortedList = it.sortedWith(compareBy { it.spotlight })
            val reversedList = sortedList.reversed()
            for (shop in reversedList) {
                shopAdapter.add(shopItem(shop))
            }

        }
    }

    inner class shopItem(val shop: Shop): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            if (shop.imageUrl != ""){
                Glide.with(this@ShopActivity).load(shop.imageUrl).into(viewHolder.itemView.shopImageView)
            }

            if(shop.spotlight) {
                viewHolder.itemView.topStoreLayout.visibility = View.VISIBLE
            } else {
                viewHolder.itemView.topStoreLayout.visibility = View.GONE
            }

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
