package com.elevintech.motorbro.ScheduledNotification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.opencensus.internal.Utils
import java.util.*


class ScheduledNotification {

    val whatTimeToTriggerAlarm = 19 // 24 hour format = 7pm
    val whatMinuteToTriggerAlarm = 0 //

    fun startAlarm(context: Context){

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, Receiver::class.java)
        intent.putExtra("HOUR_OF_DAY", whatTimeToTriggerAlarm);
        intent.putExtra("MINUTE", whatMinuteToTriggerAlarm);

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Set the alarm to start at approximately 7:00 p.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, whatTimeToTriggerAlarm)
            set(Calendar.MINUTE, whatMinuteToTriggerAlarm)
        }


/*        println("Date Now: " + System.currentTimeMillis())
        println("Alarm Date: " + calendar.timeInMillis)

        val utils = com.elevintech.motorbro.Utils.Utils()

        println("Date Now: " + utils.convertMillisecondsToDate(System.currentTimeMillis() / 1000, "MM/dd/yyyy hh:mm a"))
        println("Alarm Date: " + utils.convertMillisecondsToDate(calendar.timeInMillis / 1000, "MM/dd/yyyy hh:mm a"))
*/


        // Make alarm repeat everyday
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )


        val t = System.currentTimeMillis();
        if (t <= calendar.timeInMillis) {

            println("alarm time not yet reached")


        } else {

            println("alarm has passed")


        }


    }




}