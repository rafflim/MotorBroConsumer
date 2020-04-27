package com.elevintech.motorbro.ScheduledNotification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.elevintech.motorbro.AddOdometer.AddOdometerActivity
import com.elevintech.myapplication.R
import java.util.*

class Receiver: BroadcastReceiver(){

    var channelId = "com.elevintech.motorbro"

    // Values displayed by the notification when it pops up on the phone's notification tray
    val notificationTitle = "Odometer Update"
    val notificationText = "Wassup bro! Update odometer?"

    override fun onReceive(p0: Context?, p1: Intent) {

        val setHour: Int = p1.getIntExtra("HOUR_OF_DAY", -1)
        val setMinute: Int = p1.getIntExtra("MINUTE", -1)
        val rightNow: Calendar = Calendar.getInstance()
        val currentHour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        val currentMinute: Int = rightNow.get(Calendar.MINUTE)

        println("currentHour: $currentHour")
        println("setHour: $setHour")

        println("currentMinute: $currentMinute")
        println("setMinute: $setMinute")


        if(currentHour == setHour && currentMinute == setMinute){

            println("Alarm Triggered!")

            val intent = Intent(p0, AddOdometerActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(p0, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            var builder = NotificationCompat.Builder(p0!!, channelId)
                .setSmallIcon(R.drawable.motorbroicon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setDefaults(Notification.DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX) //Important for heads-up notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val manager = p0!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.notify(0, builder.build())

        }else{

            println("Alarm time not equal, don't show notification")

        }



    }


}