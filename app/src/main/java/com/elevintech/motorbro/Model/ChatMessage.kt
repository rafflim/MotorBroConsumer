package com.elevintech.motorbro.Model

import com.elevintech.motorbro.Utils.Utils
import java.io.Serializable

class ChatMessage(val createdDate: Long = 0,
                  val fromId: String = "",
                  val toId: String = "",
                  val message: String = "",
                  val read: Boolean = false,
                  val chatRoomId: String = "",
                  val recipientTokens: List<String> = listOf(), /* array of the device tokens of users who work for the shop (e.g. the shop owner and employees) */
                  val senderName: String = "") : Serializable {

    fun getDate() : String {
        return Utils().convertMillisecondsToDate(createdDate, "MMM d yyyy")
    }

    // TODO convert to local time
    fun getTime(): String {
        var time = Utils().convertMillisecondsToDate(createdDate, "h:mm a")
        println(time)

        return time
    }
}