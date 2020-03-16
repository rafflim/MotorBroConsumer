package com.elevintech.motorbro.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Model.ChatLastMessage
import com.elevintech.motorbro.Model.ChatMessage
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.MotorBroDatabase.ChatDatabase
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
            sendChat()
        }

        getChats()

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun sendChat(){

        val createdDate = System.currentTimeMillis() / 1000
        val message = txtChatMessage.text.toString()
        val fromId = FirebaseAuth.getInstance().currentUser?.uid!!
        val toId = shop.shopId

        val chatMessage = ChatMessage(createdDate, fromId, toId, message, false)
        val chatMessageRead = ChatMessage(createdDate, fromId, toId, message, true)

        val lastSenderMessage = ChatLastMessage(shop.shopId, message, createdDate)
        val lastReceiverMessage = ChatLastMessage(fromId, message, createdDate)

        val chatDatabase = ChatDatabase()
        chatDatabase.saveChatToSender(chatMessageRead){}
        chatDatabase.saveChatToReceiver(chatMessage){}
        chatDatabase.saveLastMessageToSender(chatMessageRead, lastSenderMessage){}
        chatDatabase.saveLastMessageToReceiver(chatMessage, lastReceiverMessage){}
//        chatDatabase.incrementChatBadgeCountOfUser(chatMessage.toId){}

        txtChatMessage.setText("")

    }

    fun getChats(){
        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = shop.shopId

        val chatDatabase = ChatDatabase()
        chatDatabase.getChatLog(fromId, toId){

//            print("chat count is: " + it.count())
            val chatList = it
            displayChats(chatList)

        }
    }

    fun getPreviousChats(){

        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = shop.shopId

        val chatDatabase = ChatDatabase()
        chatDatabase.getPreviousChatLog(fromId, toId, paginationStartAt){

            val chatList = it

            if (chatList.isNotEmpty()){
                paginationStartAt = chatList.last().createdDate.toInt()

                displayPreviousChats(chatList.asReversed())

            }

//            chatSwipeRefreshLayout.isRefreshing = false

        }

    }

    fun displayPreviousChats(chatLogList : MutableList<ChatMessage>){

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

    fun displayChats(chatLogList : MutableList<ChatMessage>){
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
                viewHolder.itemView.txtDateFrom.visibility = View.VISIBLE
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
                viewHolder.itemView.txtDateTo.visibility = View.VISIBLE
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
