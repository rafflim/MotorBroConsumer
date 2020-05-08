package com.elevintech.motorbro.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbro.AdsView.AdsViewActivity
import com.elevintech.motorbro.Model.ChatLastMessage
import com.elevintech.motorbro.Model.ChatMessage
import com.elevintech.motorbro.Model.ChatRoom
import com.elevintech.motorbro.Model.Shop
import com.elevintech.motorbro.MotorBroDatabase.ChatDatabase
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.row_chat.*
import kotlinx.android.synthetic.main.row_chat.view.*
import kotlinx.android.synthetic.main.row_chat.view.unreadDot

class ChatListActivity : AppCompatActivity() {

    val uid = FirebaseAuth.getInstance().uid
    val chatListAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        backButton.setOnClickListener {
            finish()
        }

        adsLayoutFuel.setOnClickListener {
            val intent = Intent(this, AdsViewActivity::class.java)
            intent.putExtra("adType", com.elevintech.motorbro.Utils.Constants.AD_TYPE.POSH)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.clear()
        chatListAdapterReference.clear()
        chatListRecyclerView.adapter = chatListAdapter

        getChatRooms()
    }


    // ADAPTER REFERENCE - the chat messages and it's current order in the recycler view
    // the position of the chat messages are saved here
    // the adapter uses it to find the current position of a chat message by it's ID used for change it's position in the recycler view
    // (example kapag may bagong chat na na-receive, need idisplay yun sa taas ng recycler view kasi yun yung pinaka-latest)
    val chatListAdapterReference: MutableList<String> = ArrayList()

    fun getChatRooms(){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-rooms")
            .whereEqualTo("participants.user", uid)
            .orderBy("lastMessage.createdDate", Query.Direction.DESCENDING)

        ref.addSnapshotListener { querysnapshot, e ->

            for ( snapshot in querysnapshot!!.documentChanges){

                if ( snapshot.type == DocumentChange.Type.ADDED ){

                    // get the chats
                    val chatRoom = snapshot.document.toObject(ChatRoom::class.java)!!
                    chatRoom.id = snapshot.document.id

                    // display in the recycler view
                    chatListAdapter.add(ChatItem(chatRoom, chatRoom.participants["shop"]!!))

                    // add to adapter reference
                    chatListAdapterReference.add(chatRoom.id)

                }

                if ( snapshot.type == DocumentChange.Type.MODIFIED  ){

                    // get new chat
                    val chatRoom = snapshot.document.toObject(ChatRoom::class.java)!!
                    val newMessage = chatRoom.lastMessage.message

                    if( chatRoom.lastMessage.read == false ){

                        chatRoom.id = snapshot.document.id

                        // get the chat item position based from adapter reference (we will be moving it to the top of the recycler view as it is the new latest message)
                        val oldPosition = chatListAdapterReference.indexOf(chatRoom.id)
                        val newPosition = 0

                        // update the adapter reference, move it to the first position as well
                        chatListAdapterReference.remove(chatRoom.id)
                        chatListAdapterReference.add(0, chatRoom.id)

                        // change the value of the chat message text
                        chatListAdapter.notifyItemChanged(oldPosition, "$newMessage")

                        // move to the top of the recyclerview
                        chatListAdapter.notifyItemMoved(oldPosition, newPosition) /* move the chat on the first row */

                    }



                }
            }

        }
    }

    inner class ChatItem(val chatRoom: ChatRoom, val shopId: String): Item<ViewHolder>() {


        // UPDATE CHAT MESSAGE
        override fun bind(viewHolder: ViewHolder, position: Int, payloads: List<Any>) {

            if (payloads.isNotEmpty()){

                val newMessage = payloads[0] as String
                viewHolder.itemView.chatPreview.text = newMessage
                viewHolder.itemView.chatPreviewUnread.text = newMessage
                viewHolder.itemView.unreadDot.visibility = View.VISIBLE
                viewHolder.itemView.chatDate.text = Utils().getCurrentTime()

            } else {
                super.bind(viewHolder, position, payloads)
            }
        }



        override fun bind(viewHolder: ViewHolder, position: Int) {
            MotoroBroDatabase().getShop(shopId) { shop ->


                viewHolder.itemView.chatPreview.text = chatRoom.lastMessage.message
                viewHolder.itemView.chatPreviewUnread.text = chatRoom.lastMessage.message
                viewHolder.itemView.shopName.text = shop.name
                viewHolder.itemView.chatDate.text = Utils().convertMillisecondsToDate(chatRoom.lastMessage.createdDate, "MMM dd - hh:mm a")

                viewHolder.itemView.setOnClickListener {
                    val intent = Intent(this@ChatListActivity, ChatLogActivity::class.java)
                    intent.putExtra("shopId", shop.shopId)
                    intent.putExtra("chatRoomId", chatRoom.lastMessage.chatRoomId)
                    startActivity(intent)
                }

                // Display the profile image (if they have one)
                if (shop.imageUrl != "")
                    Glide.with(applicationContext).load(shop.imageUrl)
                        .into(viewHolder.itemView.imgMainProfile)


                if (chatRoom.lastMessage.toId == uid) {
                    if (chatRoom.lastMessage.read == false) {
                        viewHolder.itemView.unreadDot.visibility = View.VISIBLE
                    }
                }
            }
        }

        override fun getLayout(): Int {
            return R.layout.row_chat

        }
    }
}
