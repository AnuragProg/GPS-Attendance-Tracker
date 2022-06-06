package com.example.classattendanceapp.domain.utils.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.classattendanceapp.data.models.TimeTable
import java.util.*

object ClassAlarmManager {

    private val TIMETABLEID = "timetable_id"
    private val SUBJECTNAME = "subject_name"
    private val HOUR = "hour"
    private val MINUTE = "minute"

    fun registerAlarm(
        context: Context,
        timeTableId: Int,
        timeTable: TimeTable
    ){

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ClassAlarmBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.alarms.ClassAlarmBroadcastReceiver.${timeTable.subjectName}.$timeTableId"
            Log.d("action", action!! + " \n For Registering Alarm")
            putExtra(TIMETABLEID, timeTableId)
            putExtra(SUBJECTNAME, timeTable.subjectName)
            putExtra(HOUR, timeTable.hour)
            putExtra(MINUTE, timeTable.minute)
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timeTable.hour)
        calendar.set(Calendar.MINUTE, timeTable.minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, timeTable.dayOfTheWeek)

        val pendingIntent = PendingIntent.getBroadcast(context, timeTableId, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.time.time, AlarmManager.INTERVAL_DAY*7, pendingIntent)
    }

    fun cancelAlarm(
        context: Context,
        timeTable: TimeTable
    ){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ClassAlarmBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.alarms.ClassAlarmBroadcastReceiver.${timeTable.subjectName}.${timeTable._id}"
        }
        val pendingIntent = PendingIntent.getBroadcast(context, timeTable._id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE )
        alarmManager.cancel(pendingIntent)
    }
}