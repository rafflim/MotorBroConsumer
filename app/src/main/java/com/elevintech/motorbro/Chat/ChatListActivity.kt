package com.elevintech.motorbro.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.ChatLastMessage
import com.elevintech.motorbro.Model.ChatMessage
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.MotorBroDatabase.ChatDatabase
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.row_chat.view.*

class ChatListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        getListOfLastMessages()

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getListOfLastMessages(){

        val chatDatabase = ChatDatabase()
        chatDatabase.getLastMessages {

            print("printing count of last messages")
            print(it.count())
            displayListOfLastMessages(it)
        }
    }

    private fun displayListOfLastMessages(listOfLasMessages: MutableList<ChatLastMessage>){

        chatListRecyclerView.isNestedScrollingEnabled = true
        var chatListAdapter = GroupAdapter<ViewHolder>()

        for (lastChatMessage in listOfLasMessages){

            val db = MotoroBroDatabase()
            db.getShop(lastChatMessage.partnerId) {
                val shop = it
                chatListAdapter.add(chatItem(lastChatMessage, shop))
            }

        }

        chatListRecyclerView.adapter = chatListAdapter

    }

    inner class chatItem(val chat: ChatLastMessage, val shop: Shop): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.userName.text = shop.name.capitalize()
            viewHolder.itemView.chatPreview.text = chat.lastMessage
            viewHolder.itemView.chatPreviewUnread.text = chat.lastMessage

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(this@ChatListActivity, ChatLogActivity::class.java)
                intent.putExtra("shop", shop)
                startActivity(intent)
            }

//            // Display the fire emoji (if the users matched)
//            DateFilipinaDatabase().hasMatched(chat.fromId, chat.toId) {
//                if (it){
//                    imgHasMatched.visibility = View.VISIBLE
//                }
//            }

            // Display the profile image (if they have one)
            if (shop.imageUrl != "")
                Glide.with(applicationContext).load(shop.imageUrl).into(viewHolder.itemView.imgMainProfile)


            // Display unread message (if the last message is not from user and not yet read)
//            val loggedInUser = FirebaseAuth.getInstance().uid
//            println("chat.fromId: " + chat.fromId)
//            println("chat.message: " + chat.message)
//            if (chat.fromId != loggedInUser && !chat.read){
//                viewHolder.itemView.chatPreview.visibility = View.GONE
//                viewHolder.itemView.chatPreviewUnread.visibility = View.VISIBLE
//
//                viewHolder.itemView.chatPreview.text = chat.message
//                viewHolder.itemView.chatPreviewUnread.text = chat.message
//
//                viewHolder.itemView.unreadDot.visibility = View.VISIBLE
//
//            } else {
//                viewHolder.itemView.chatPreview.visibility = View.VISIBLE
//                viewHolder.itemView.chatPreviewUnread.visibility = View.GONE
//
//                viewHolder.itemView.chatPreview.text = chat.message
//                viewHolder.itemView.chatPreviewUnread.text = chat.message
//
//
//                viewHolder.itemView.unreadDot.visibility = View.GONE
//            }


        }

        override fun getLayout(): Int {
            return R.layout.row_chat

        }
    }
}
