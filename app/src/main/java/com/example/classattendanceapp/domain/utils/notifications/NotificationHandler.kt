package com.example.classattendanceapp.domain.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.classattendanceapp.MainActivity
import com.example.classattendanceapp.R

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
        subjectId: Int,
        subjectName: String,
        hour: Int,
        minute: Int
    ){
        val intent = Intent(context, MainActivity::class.java)

        intent.putExtra("subject_id", subjectId)

        val pendingIntent = PendingIntent.getActivity(context, OPENMAINACTIVITYREQUESTCODE, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNELID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Mark your attendance!")
            .setContentText("$subjectName - $hour:$minute")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .build()
        with(NotificationManagerCompat.from(context)){
            notify(subjectId, notification)
        }
    }
}