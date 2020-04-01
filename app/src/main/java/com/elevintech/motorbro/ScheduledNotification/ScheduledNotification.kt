package com.elevintech.motorbro.ScheduledNotification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.elevintech.myapplication.R
import java.util.*


class ScheduledNotification : AppCompatActivity() {

    var channelId = "com.elevintech.motorbro"

    // Values displayed on phone's system settings
    val notificationName = "Motor Bro"
    val notificationDescription = "Odometer Update"

    // Values displayed by the notification when it pops up on the phone's notification tray
    val notificationTitle = "Odometer Change"
    val notificationText = "Any odometer updates this day?"

    fun startAlarm(context: Context){
//        println("Alarm Started")

        createNotificationChannel()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//        val alarmManager = getSystemService(AlarmManager) as AlarmManager
        val intent = Intent(context, Receiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Set the alarm to start at approximately 8:00 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

//        println("Date Now: " + System.currentTimeMillis())
//        println("Alarm Date: " + calendar.timeInMillis)

        // Make alarm repeat everyday
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

    // Create the NotificationChannel
    // because phones with API 26 and above requires it to show notifications
    private fun createNotificationChannel() {

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