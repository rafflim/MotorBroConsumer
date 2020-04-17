package com.elevintech.motorbro.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.ChatLastMessage
import com.elevintech.motorbro.Model.ChatMessage
import com.elevintech.motorbro.Model.ChatRoom
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.MotorBroDatabase.ChatDatabase
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.row_chat.*
import kotlinx.android.synthetic.main.row_chat.view.*
import kotlinx.android.synthetic.main.row_chat.view.unreadDot

class ChatListActivity : AppCompatActivity() {

    val uid = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        // get all chat rooms where user is a participant of
        getUserChatRooms()

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getUserChatRooms(){

        val chatDatabase = ChatDatabase()
        chatDatabase.getChatRoomOfUser {

            if (it.count() > 0){
                displayListOfLastMessages(it)
            }

        }
    }

    private fun displayListOfLastMessages(chatRoomList: MutableList<ChatRoom>){

        chatListRecyclerView.isNestedScrollingEnabled = true
        val chatListAdapter = GroupAdapter<ViewHolder>()

        for (chatRoom in chatRoomList){

            val db = MotoroBroDatabase()
            val shopId = chatRoom.participants["shop"]!!

            db.getShop(shopId) {
                val shop = it
                chatListAdapter.add( ChatItem(chatRoom, shop) )
            }

        }

        chatListRecyclerView.adapter = chatListAdapter

    }

    inner class ChatItem(val chatRoom: ChatRoom, val shop: Shop): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

//            viewHolder.itemView.userName.text = shop.name.capitalize()
            viewHolder.itemView.chatPreview.text = chatRoom.lastMessage.message
            viewHolder.itemView.chatPreviewUnread.text = chatRoom.lastMessage.message
            viewHolder.itemView.shopName.text = shop.name

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(this@ChatListActivity, ChatLogActivity::class.java)
                intent.putExtra("shopId", shop.shopId)
                intent.putExtra("chatRoomId", chatRoom.lastMessage.chatRoomId)
                startActivity(intent)
            }

            // Display the profile image (if they have one)
            if (shop.imageUrl != "")
                Glide.with(applicationContext).load(shop.imageUrl).into(viewHolder.itemView.imgMainProfile)


            if (chatRoom.lastMessage.toId == uid) {
                if(chatRoom.lastMessage.read == false){
                    viewHolder.itemView.unreadDot.visibility = View.VISIBLE
                }
            }



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
