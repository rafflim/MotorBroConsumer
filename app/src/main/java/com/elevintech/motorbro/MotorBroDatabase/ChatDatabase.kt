package com.elevintech.motorbro.MotorBroDatabase

import com.elevintech.motorbro.Model.ChatLastMessage
import com.elevintech.motorbro.Model.ChatMessage
import com.elevintech.motorbro.Model.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatDatabase {
    fun saveChatToSender(chatMessage : ChatMessage, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-messages").document(chatMessage.fromId).collection(chatMessage.toId)

        ref
            .add(chatMessage)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }

    }

    fun saveChatToReceiver(chatMessage : ChatMessage, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-messages").document(chatMessage.toId).collection(chatMessage.fromId)

        ref
            .add(chatMessage)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }


    }

    fun saveLastMessageToSender(chatMessage: ChatMessage, chatLastMessage : ChatLastMessage, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-last-messages").document(chatMessage.fromId).collection("last-message-from").document(chatMessage.toId)

        ref
            .set(chatLastMessage)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }


    }

    fun saveLastMessageToReceiver(chatMessage: ChatMessage,chatLastMessage : ChatLastMessage, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-last-messages").document(chatMessage.toId).collection("last-message-from").document(chatMessage.fromId)

        ref
            .set(chatLastMessage)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }


    }

    fun getPreviousChatLog(fromId: String, toId: String, previousCreatedDate: Int, callback: (chat : MutableList<ChatMessage>) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("user-chat-messages")
            .document(fromId).collection(toId)
            .orderBy("createdDate", Query.Direction.DESCENDING)
            .startAfter(previousCreatedDate)
            .limit(7)

        docRef.get()
            .addOnSuccessListener {

                val chatLogList = arrayListOf<ChatMessage>()

                for (snapshot in it){
                    val message = snapshot.toObject(ChatMessage::class.java)!!
                    chatLogList.add(message)
                }

                callback(chatLogList)

            }

    }

    fun getChatRoomMessages(chatRoomId: String, callback: (MutableList<ChatMessage>) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-rooms").document(chatRoomId).collection("chat-messages")

        ref
            .addSnapshotListener { querysnapshot, e ->
                val chatLogList = arrayListOf<ChatMessage>()

                for (snapshot in querysnapshot!!.documentChanges.reversed()){
                    if (snapshot.type == DocumentChange.Type.ADDED){
                        val message = snapshot.document.toObject(ChatMessage::class.java)!!
                        chatLogList.add(message)
                    }
                }

                callback(chatLogList)

            }
    }

    fun getChatLog(fromId: String, toId: String, callback: (chat : MutableList<ChatMessage>) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-messages")
            .document(fromId)
            .collection(toId)
            .orderBy("createdDate", Query.Direction.DESCENDING)
//            .limit(Constants.paginatedChatLogCount)


        ref
            .addSnapshotListener { querysnapshot, e ->
                val chatLogList = arrayListOf<ChatMessage>()

                for (snapshot in querysnapshot!!.documentChanges.reversed()){
                    if (snapshot.type == DocumentChange.Type.ADDED){
                        val message = snapshot.document.toObject(ChatMessage::class.java)!!
                        chatLogList.add(message)
                    }
                }

                callback(chatLogList)

            }

    }

    fun getChatRoomOfUser(callback: (MutableList<ChatRoom>)-> Unit){

        val uid = FirebaseAuth.getInstance().uid!!
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-rooms")
                    .whereEqualTo("participants.user", uid)
                    .orderBy("lastMessage.createdDate", Query.Direction.DESCENDING)

        ref
            .addSnapshotListener { querysnapshot, e ->

                val chatRoomList = arrayListOf<ChatRoom>()

                for ( snapshot in querysnapshot!!.documentChanges){
                    if ( snapshot.type == DocumentChange.Type.ADDED || snapshot.type == DocumentChange.Type.MODIFIED ){
                        val chatRoom = snapshot.document.toObject(ChatRoom::class.java)!!
                        chatRoomList.add(chatRoom)
                    }
                }

                callback(chatRoomList)

            }
    }

    fun getLastMessages(callback: (chat : MutableList<ChatLastMessage>)-> Unit){

        val uid = FirebaseAuth.getInstance().uid!!
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-last-messages").document(uid).collection("last-message-from").orderBy("date")


        ref
            .addSnapshotListener { querysnapshot, e ->

                val chatLogList = arrayListOf<ChatLastMessage>()

                for (snapshot in querysnapshot!!.documents){
                    val message = snapshot.toObject(ChatLastMessage::class.java)!!
                    chatLogList.add(message)
                }

                callback(chatLogList)

            }
    }

//    fun incrementChatBadgeCountOfUser(user: String, callback: () -> Unit){
//
//        getUnreadChatCountByUserId(user){
//
//            val unreadChatCount = it
//
//            val db = FirebaseFirestore.getInstance()
//            val ref = db.collection("user-notifications").document(user)
//
//            ref
//                .set(mapOf("unreadChatCount" to unreadChatCount + 1))
//                .addOnSuccessListener { callback() }
//                .addOnFailureListener { callback() }
//
//        }
//    }
}