package com.elevintech.motorbro.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.Shop
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    lateinit var shop: Shop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        shop = intent.getSerializableExtra("shop") as Shop

        profileName.text = shop.name.capitalize()

        if (shop.imageUrl != ""){
            Glide.with(this).load(shop.imageUrl).into(imgMainProfile)
        }

        btnSendChat.setOnClickListener {

        }
    }
}
