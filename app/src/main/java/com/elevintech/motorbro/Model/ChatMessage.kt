package com.elevintech.motorbro.Model

import com.elevintech.motorbro.Utils.Utils
import java.io.Serializable

class ChatMessage(val createdDate: Long = 0,
                  val fromId: String = "",
                  val toId: String = "",
                  val message: String = "",
                  val read: Boolean = false,
                  val chatRoomId: String = "",
                  val recipientTokens: List<String> = listOf() /* array of the device tokens of users who work for the shop (e.g. the shop owner and employees) */) : Serializable {

    fun getDate() : String {
        return Utils().convertMillisecondsToDate(createdDate * 1000, "MMM d yyyy")
    }

    // TODO convert to local time
    fun getTime(): String {
        return Utils().convertMillisecondsToDate(createdDate * 1000, "h:mm a")
    }
}