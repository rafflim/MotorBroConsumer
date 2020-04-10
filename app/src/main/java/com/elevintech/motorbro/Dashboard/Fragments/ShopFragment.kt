package com.elevintech.motorbro.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.elevintech.motorbro.Model.ShopProduct

import com.elevintech.myapplication.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_shop.*
import kotlinx.android.synthetic.main.row_shop_item_layout.view.*

/**
 * A simple [Fragment] subclass.
 */
class ShopFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

    override fun onResume() {
        super.onResume()


        setupViews()
    }

    fun setupViews() {
        shopRecyclerView.isNestedScrollingEnabled = false
        shopRecyclerView.layoutManager = GridLayoutManager(activity, 2)


//        val product1 = ShopProduct("", 0, "", "Posh New Brakes", 2000.0)
//        val product2 = ShopProduct("", 0, "", "Posh New Helmet", 2000.0)
//        val product3 = ShopProduct("", 0, "", "Posh New Jacket", 2000.0)
//        val product4 = ShopProduct("", 0, "", "Posh New Brakes", 2000.0)
//
        val shopAdapter = GroupAdapter<ViewHolder>()
//
//        shopAdapter.add(shopItem(product1))
//        shopAdapter.add(shopItem(product2))
//        shopAdapter.add(shopItem(product3))
//        shopAdapter.add(shopItem(product4))

        shopRecyclerView.adapter = shopAdapter

    }

    inner class shopItem(val product: ShopProduct): Item<ViewHolder>() {

        override fun bind(viewHolder: ViewHolder, position: Int) {


            //viewHolder.itemView.nameAndAge.text = product.title
            //viewHolder.itemView.activeTimeAgo.text = "$" + product.price

        }

        override fun getLayout(): Int {
            return R.layout.row_shop_item_layout

        }
    }

}
