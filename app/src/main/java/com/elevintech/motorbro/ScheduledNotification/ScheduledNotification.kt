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


class ScheduledNotification {

    /* Creation of notification channel moved inside DashboardActivity
    *  because nag-eerror  sya pag hindi pa na-create yung activity
    * */

//    var channelId = "com.elevintech.motorbro"
//
//    // Values displayed on phone's system settings
//    val notificationName = "Daily Odometer Update"
//    val notificationDescription = "Daily notification reminder for any odometer changes"
//
//    // Values displayed by the notification when it pops up on the phone's notification tray
//    val notificationTitle = "Odometer Update"
//    val notificationText = "Wassup bro! Update odometer?"

    fun startAlarm(context: Context){
//        println("Alarm Started")

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




}