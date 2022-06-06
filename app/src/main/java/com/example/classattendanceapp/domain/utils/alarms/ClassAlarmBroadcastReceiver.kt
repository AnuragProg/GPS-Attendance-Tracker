package com.example.classattendanceapp.domain.utils.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.classattendanceapp.domain.utils.notifications.NotificationHandler

class ClassAlarmBroadcastReceiver : BroadcastReceiver() {

    private val TIMETABLEID = "timetable_id"
    private val SUBJECTNAME = "subject_name"
    private val HOUR = "hour"
    private val MINUTE = "minute"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("debugging","Broadcast received")
        if(intent != null && context != null){
            Log.d("debugging", "intent and context are not null")
            val subjectId = intent.getIntExtra(TIMETABLEID, -1)
            val subjectName = intent.getStringExtra(SUBJECTNAME)
            val hour = intent.getIntExtra(HOUR, -1)
            val minute = intent.getIntExtra(MINUTE, -1)
            if(hour == -1 || minute == -1 || subjectName == null){
                return
            }
            Log.d("debugging", "Successfully have details $subjectId $subjectName $hour $minute")
            NotificationHandler.createNotificationChannel(context)
            NotificationHandler.createAndShowNotification(
                context,
                subjectId,
                subjectName,
                hour,
                minute
            )
        }
    }
}