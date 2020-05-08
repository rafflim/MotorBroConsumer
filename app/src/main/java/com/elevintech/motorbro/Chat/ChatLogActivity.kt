package com.elevintech.motorbro.Chat

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.ChatMessage
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.Model.User
import com.elevintech.motorbro.MotorBroDatabase.ChatDatabase
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.row_chat_from.view.*
import kotlinx.android.synthetic.main.row_chat_to.view.*


class ChatLogActivity : AppCompatActivity() {

    // MARK: Disable pagination for now

    var paginationStartAt = 0 // https://www.youtube.com/watch?v=poqTHxtDXwU&t=316s
    val adapter = GroupAdapter<ViewHolder>()
    var recipientTokenArray: List<String> = listOf()
    val uid = FirebaseAuth.getInstance().uid
    lateinit var shop: Shop
    lateinit var chatRoomId: String
    lateinit var shopId: String
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        shopId = intent.getStringExtra("shopId")
        chatRoomId = intent.getStringExtra("chatRoomId")!!

        setAsRead()
        getShop()
        getChats()
        getUser()



        btnBack.setOnClickListener {
            finish()
        }


    }

    private fun getChats(){

        if(chatRoomId != ""){
            val chatDatabase = ChatDatabase()
            chatDatabase.getChatRoomMessages(chatRoomId){

                val chatList = it
                displayChats(chatList)

            }
        }

    }

    private fun setAsRead() {
        if(chatRoomId != ""){
            MotoroBroDatabase().getChatRoomById(chatRoomId){ chatRoom ->
                if (chatRoom.lastMessage.toId == uid) {
                    if(chatRoom.lastMessage.read == false){
                        MotoroBroDatabase().updateLastMessageAsRead(chatRoomId)
                    }
                }
            }
        }

    }


    private fun getUser(){
        MotoroBroDatabase().getUser {
            user = it
        }
    }

    private fun getShop() {
        MotoroBroDatabase().getShop(shopId){
            shop = it
            recipientTokenArray = ArrayList(shop.deviceTokens.values)

            updateUi()

            // MARK: Need to move it here because the recipient token array can be empty?
            btnSendChat.setOnClickListener {
                val message = txtChatMessage.text.toString()
                if ( message != "" && user.firstName != "" && recipientTokenArray.isNotEmpty() )
                    sendChat()
            }
        }
    }

    private fun updateUi() {
        profileName.text = shop.name.capitalize()
        if (shop.imageUrl != ""){
            Glide.with(this).load(shop.imageUrl).into(imgMainProfile)
        }
    }


    private fun sendChat(){

        val createdDate = System.currentTimeMillis() / 1000
        val message = txtChatMessage.text.toString()
        val senderId = FirebaseAuth.getInstance().currentUser?.uid!!
        val senderName = user.firstName
        val receiverId = shop.shopId
        val fcmTokenList = ArrayList(shop.deviceTokens.values)  // list of the device tokens of users who will receive the chat message (e.g. the shop owner and employees)

        val db = MotoroBroDatabase()

        txtChatMessage.setText("")

        // create new chat room
        if (chatRoomId == ""){
            val participants = mapOf("user" to senderId, "shop" to receiverId)
            db.createNewChatRoom ( participants ){ chatRoomId ->

                // save message in chat room
                val chatMessage = ChatMessage(createdDate, senderId, receiverId, message, false, chatRoomId, fcmTokenList, senderName)
                db.saveMessageInChatRoom(chatMessage){

                    // save message in last messages
                    db.updateChatRoomLastMessage(chatRoomId, chatMessage){
                        this.chatRoomId = chatRoomId

                        getChats()
                    }

                }

            }

        } else {

            val chatMessage = ChatMessage(createdDate, senderId, receiverId, message, false, chatRoomId!!, fcmTokenList, senderName)

            // save message in chat room
            db.saveMessageInChatRoom(chatMessage){

                // save message in last messages
                db.updateChatRoomLastMessage(chatRoomId, chatMessage){

                }

            }
        }

        recycler_view_chat_logs.addOnLayoutChangeListener { view, left, top, right, bottom, odlLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                recycler_view_chat_logs.scrollBy(0, oldBottom - bottom);
            }
        }

    }


    private fun displayPreviousChats(chatLogList : MutableList<ChatMessage>){

        val uid = FirebaseAuth.getInstance().uid
        for ((index, chatMessage) in chatLogList.withIndex()) {

            var previousChat = ChatMessage()
            if (index > 0)
                previousChat =  chatLogList[index-1]

            if (chatMessage.fromId == uid){
                adapter.add(index, ChatToItem(chatMessage, previousChat))
            } else {
                adapter.add(index, ChatFromItem(chatMessage, previousChat))
            }

        }

        adapter.notifyDataSetChanged()
        // TODO: scroll sa tamang position pagkaload ng paginated messages
        recycler_view_chat_logs.scrollToPosition(7 - 1)
    }

    private fun displayChats(chatLogList : MutableList<ChatMessage>){
        recycler_view_chat_logs.adapter = adapter
        val uid = FirebaseAuth.getInstance().uid

        for ((index, chatMessage) in chatLogList.withIndex()) {

            // we need to get this to compare dates and determine whether to display date separator or not
            var previousChat = ChatMessage()

            if (index > 0)
                previousChat =  chatLogList[index-1]

            if (chatMessage.fromId == uid){
                adapter.add(ChatToItem(chatMessage, previousChat))
                adapter.notifyItemInserted(index)
            } else {
                adapter.add(ChatFromItem(chatMessage, previousChat))
                adapter.notifyItemInserted(index)
            }

        }

        recycler_view_chat_logs.scrollToPosition(adapter.itemCount - 1)

    }

    inner class ChatFromItem(val chat : ChatMessage, val previousChat: ChatMessage): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.row_chat_from
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textViewFromRow.text = chat.message
            viewHolder.itemView.timeFrom.text = chat.getTime()

            println(
                "THIS CHAT: " + chat.createdDate + " || PREVIOUS CHAT: " + previousChat.createdDate + " || MESSAGE: " + chat.message
            )

            // display date separator
            if (chat.getDate() != previousChat.getDate()){
                viewHolder.itemView.txtDateFrom.visibility = View.GONE
                viewHolder.itemView.txtDateFrom.text = chat.getDate()
            }

        }

    }

    inner class ChatToItem(val chat : ChatMessage, val previous: ChatMessage): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.row_chat_to
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textViewToRow.text = chat.message
            viewHolder.itemView.timeTo.text = chat.getTime()

            // display date separator
            if (chat.getDate() != previous.getDate()){
                viewHolder.itemView.txtDateTo.visibility = View.GONE
                viewHolder.itemView.txtDateTo.text = chat.getDate()
            }

            println(
                "THIS CHAT: " + chat.createdDate + " || PREVIOUS CHAT: " + previous.createdDate + " || MESSAGE: " + chat.message
            )
        }

    }

    inner class TestItem: Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.row_chat_to
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textViewToRow.text = "message"
            viewHolder.itemView.timeTo.text = "time"

        }

    }


}
