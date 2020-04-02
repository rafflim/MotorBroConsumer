package com.elevintech.motorbro.ShopView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Chat.ChatLogActivity
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_shop_view.*

class ShopViewActivity : AppCompatActivity() {

    lateinit var shop: Shop
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_view)

        // Add the shop data here
        shop = intent.getSerializableExtra("shop") as Shop

        if (shop.imageUrl != ""){
            Glide.with(this).load(shop.imageUrl).into(shopImageView)
        }

        shopName.text = shop.name.capitalize()
        shopDescription.text = shop.description.capitalize()

        val longDate: Long? = shop.dateEstablished.toLongOrNull()

        if (longDate != null) {
            calendarText.text = "Founded: " + Utils().convertMillisecondsToDate(longDate, "MMM d, yyyy")
        } else {
            calendarText.text = "Founded: Unkown"
        }

        mainLocationText.text = shop.address.capitalize()
        branchesLocationText.text = ""

        chatButton.setOnClickListener {

            val user =  FirebaseAuth.getInstance().uid!!
            MotoroBroDatabase().getChatRoomByParticipants( user, shop.shopId ){ chatRoomId ->

                println("ShopViewActivity - chatRoomId is: " )

                val intent = Intent(this, ChatLogActivity::class.java)
                intent.putExtra("shopId", shop.shopId)
                intent.putExtra("chatRoomId", chatRoomId)
                startActivity(intent)

            }

        }

        backButton.setOnClickListener {
            finish()
        }



        MotoroBroDatabase().checkIfFavoriteOrNot(shop.shopId){ isFavorite ->
            if (isFavorite){
                favoriteYesIcon.visibility = View.VISIBLE
            } else {
                favoriteNoIcon.visibility = View.VISIBLE
            }
        }

        favoriteNoIcon.setOnClickListener {
            MotoroBroDatabase().addShopToFavorites(shop.shopId){
                favoriteNoIcon.visibility = View.GONE
                favoriteYesIcon.visibility = View.VISIBLE
            }
        }
    }
}
