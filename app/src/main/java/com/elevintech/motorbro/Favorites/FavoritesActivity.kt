package com.elevintech.motorbro.Favorites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.ShopView.ShopViewActivity
import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.row_shop_item_layout.view.*

class FavoritesActivity : AppCompatActivity() {

    val shopAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        backView.setOnClickListener {
            finish()
        }

        setupViews()
        getFavoriteShops()

    }

    private fun getFavoriteShops() {
        MotoroBroDatabase().getFavoriteShops{
            displayShops(it)
        }
    }

    private fun displayShops(shopList: MutableList<Shop>) {

        for (shop in shopList){
            println(shop.name)
            shopAdapter.add(ShopItem(shop))
        }
    }

    fun setupViews() {
        shopRecyclerView.isNestedScrollingEnabled = false
        shopRecyclerView.layoutManager = LinearLayoutManager(this)
        shopRecyclerView.adapter = shopAdapter

    }

    inner class ShopItem(val shop: Shop): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            if (shop.imageUrl != ""){
                Glide.with(this@FavoritesActivity).load(shop.imageUrl).into(viewHolder.itemView.shopImageView)
            }

            //Glide.with(activity!!).load(user.userProfileMainImageUrl).into(viewHolder.itemView.shopImageView)
            viewHolder.itemView.shopName.text = shop.name.capitalize()
//            viewHolder.itemView.shopDescription.text = shop.description

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(this@FavoritesActivity, ShopViewActivity::class.java)
                intent.putExtra("shop", shop)
                startActivity(intent)
            }

            viewHolder.itemView.mainLocationText.text = shop.address.capitalize()
        }

        override fun getLayout(): Int {
            return R.layout.row_favorite_shop

        }
    }



}
