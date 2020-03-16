//package com.elevintech.motorbro.AlarmReceiver
//
//import android.app.IntentService
//import android.app.Notification
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context.ALARM_SERVICE
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import android.view.Menu
//import java.util.*
//
//class AlarmManager() {
//    init {
//        val alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmIntent = new Intent(context of current file, AlarmReceiver1.class);
//        AlarmReceiver1 = broadcast receiver
//
//                pendingIntent = PendingIntent.getBroadcast(  Menu.this, 0, alarmIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
//        alarmManager.cancel(pendingIntent);
//
//        Calendar alarmStartTime = Calendar.getInstance();
//        Calendar now = Calendar.getInstance();
//        alarmStartTime.set(Calendar.HOUR_OF_DAY, 8);
//        alarmStartTime.set(Calendar.MINUTE, 00);
//        alarmStartTime.set(Calendar.SECOND, 0);
//        if (now.after(alarmStartTime)) {
//            Log.d("Hey","Added a day");
//            alarmStartTime.add(Calendar.DATE, 1);
//        }
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//            alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//        Log.d("Alarm","Alarms set for everyday 8 am.");
//
//    }
//}
//
//public class NotificationService1: IntentService {
//
//    lateinit var notificationManager: NotificationManager
//    lateinit var pendingIntent: PendingIntent
//    private var NOTIFICATION_ID = 1
//    lateinit var notification: Notification
//
//    override fun onHandleIntent(intent: Intent?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        val context = applicationContext
//
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Context context = this.getApplicationContext();
//        notificationManager =
//            (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent mIntent = new Intent(this, Activity to be opened after clicking on the
//                notif);
//        Bundle bundle = new Bundle();
//        bundle.putString("test", "test");
//        mIntent.putExtras(bundle);
//        pendingIntent = PendingIntent.getActivity(context, 0, mIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Resources res = this.getResources();
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        notification = new NotificationCompat.Builder(this)
//            .setContentIntent(pendingIntent)
//            .setSmallIcon(R.drawable.ic_launcher)
//            .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
//            .setTicker("ticker value")
//            .setAutoCancel(true)
//            .setPriority(8)
//            .setSound(soundUri)
//            .setContentTitle("Notif title")
//            .setContentText("Text").build();
//        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
//        notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
//        notification.ledARGB = 0xFFFFA500;
//        notification.ledOnMS = 800;
//        notification.ledOffMS = 1000;
//        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(NOTIFICATION_ID, notification);
//        Log.i("notif","Notifications sent.");
//
//    }
//
//}