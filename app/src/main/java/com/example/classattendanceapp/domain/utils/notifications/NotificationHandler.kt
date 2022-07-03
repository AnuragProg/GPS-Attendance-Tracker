package com.example.classattendanceapp.domain.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.classattendanceapp.MainActivity
import com.example.classattendanceapp.R
import com.example.classattendanceapp.domain.utils.notification_manipulation_broadcasts.MarkPresentAbsentThroughNotificationBroadcastReceiver

object NotificationHandler {

    private const val CHANNELID = "CLASSATTENDANCECHANNEL"
    private const val OPENMAINACTIVITYREQUESTCODE = 1


    fun createNotificationChannel(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                    CHANNELID,
                "Class Attendance Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply{
                description = "Creates Channel for Class Attendance Channel"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun createAndShowNotification(
        context: Context,
        timeTableId: Int,
        subjectId: Int,
        subjectName: String,
        hour: Int,
        minute: Int,
        message: String?, // present or absent
        notificationType: NotificationTypes? = null
    ){
        val intent = Intent(context, MainActivity::class.java).apply{
            putExtra("subject_id", timeTableId)
        }


        val pendingIntent = PendingIntent.getActivity(context, OPENMAINACTIVITYREQUESTCODE, intent, PendingIntent.FLAG_IMMUTABLE)

        val contentMessage = message?.let{
            "$subjectName - $hour:$minute marked $it"
        } ?: "$subjectName - $hour:$minute"

        Log.d("mark", "putting subjectId $subjectId")
        val markPresentIntent = Intent(context, MarkPresentAbsentThroughNotificationBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.notification_manipulation_broadcasts.MarkPresentAbsentThroughNotificationBroadcastReceiver"
            putExtra("attendance", true)
            putExtra("notificationPush", timeTableId)
            putExtra("subjectId", subjectId.toString())
        }

        val markPresentPendingIntent = PendingIntent.getBroadcast(context, -20, markPresentIntent, Intent.FILL_IN_DATA)

        val markAbsentIntent = Intent(context, MarkPresentAbsentThroughNotificationBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.notification_manipulation_broadcasts.MarkPresentAbsentThroughNotificationBroadcastReceiver"
            putExtra("attendance", false)
            putExtra("notificationPush", timeTableId)
            putExtra("subjectId", subjectId.toString())
        }
        val markAbsentPendingIntent = PendingIntent.getBroadcast(context, -30, markAbsentIntent, Intent.FILL_IN_DATA)

        Log.d("mark","SubjectId in presentIntent is ${ markPresentIntent.getStringExtra("subjectId") }")

        val notification = if(message!=null){
            NotificationCompat.Builder(context, CHANNELID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Marked your attendance!")
                .setContentText(contentMessage)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                    message
                ))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .build()
        }else{
            NotificationCompat.Builder(context, CHANNELID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Mark your attendance!")
                .setContentText(contentMessage)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_background, "Present", markPresentPendingIntent)
                .addAction(R.drawable.ic_launcher_background, "Absent", markAbsentPendingIntent)
                .build()
        }
        with(NotificationManagerCompat.from(context)){
            notify(timeTableId, notification)
        }
    }
}