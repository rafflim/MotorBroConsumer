package com.elevintech.motorbro

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.elevintech.motorbro.Chat.ChatListActivity
import com.elevintech.motorbro.Chat.ChatLogActivity
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService(){

    var channelId = "com.elevintech.motorbro.userchat"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        MotoroBroDatabase().updateFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {

            if (remoteMessage.data.containsKey("notificationType")){

                val notificationData = remoteMessage.data

                if (notificationData["notificationType"] == "chat"){
                    val intent = Intent(this, ChatListActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    buildNotification( notificationData, pendingIntent )
                }

            }
        }

    }

    private fun buildNotification(notificationData: Map<String, String>, pendingIntent: PendingIntent) {

        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.motorbike_circular_icon)
            .setContentTitle( notificationData["title"] )
            .setContentText( notificationData["body"] )
            .setDefaults(Notification.DEFAULT_VIBRATE) //Important for heads-up notification
            .setPriority(Notification.PRIORITY_MAX) //Important for heads-up notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)

        val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(0, builder.build())
    }

    private fun createNotificationChannel() {

        val notificationName = "Motor Bro Chats"
        val notificationDescription = "Chats between shops and user"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, notificationName, importance).apply {
                description = notificationDescription
            }

            channel.description = notificationDescription
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}