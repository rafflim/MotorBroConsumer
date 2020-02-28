package com.elevintech.motorbro.Shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.Model.ShopProduct
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.ShopView.ShopViewActivity
import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_shop.*
import kotlinx.android.synthetic.main.row_shop_item_layout.view.*

class ShopActivity : AppCompatActivity() {

    val shopAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)


        backButton.setOnClickListener {
            finish()
        }

        setupViews()
        getShops()
    }

    fun setupViews() {
        shopRecyclerView.isNestedScrollingEnabled = false
        shopRecyclerView.layoutManager = GridLayoutManager(this, 2)


//        val product1 = ShopProduct("", 0, "", "Posh New Brakes", 2000.0)
//        val product2 = ShopProduct("", 0, "", "Posh New Helmet", 2000.0)
//        val product3 = ShopProduct("", 0, "", "Posh New Jacket", 2000.0)
//        val product4 = ShopProduct("", 0, "", "Posh New Brakes", 2000.0)
//
//
//
//        shopAdapter.add(shopItem(product1))
//        shopAdapter.add(shopItem(product2))
//        shopAdapter.add(shopItem(product3))
//        shopAdapter.add(shopItem(product4))

        shopRecyclerView.adapter = shopAdapter

    }

    fun getShops() {
        val db = MotoroBroDatabase()
        db.getShops {
            for (shop in it) {
                shopAdapter.add(shopItem(shop))
            }
        }
    }

    inner class shopItem(val shop: Shop): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {

            if (shop.imageUrl != ""){
                Glide.with(this@ShopActivity).load(shop.imageUrl).into(viewHolder.itemView.shopImageView)
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
