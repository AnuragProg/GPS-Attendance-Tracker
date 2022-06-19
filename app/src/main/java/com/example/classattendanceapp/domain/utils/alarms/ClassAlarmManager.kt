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

    private const val TIMETABLEID = "timetable_id"
    private const val SUBJECTNAME = "subject_name"
    private const val HOUR = "hour"
    private const val MINUTE = "minute"
    private const val SUBJECTID = "subject_id"
    private const val DAYOFTHEWEEK = "day_of_the_week"

    fun registerAlarm(
        context: Context,
        timeTableId: Int,
        timeTable: TimeTable
    ){

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ClassAlarmBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.alarms.ClassAlarmBroadcastReceiver"
            Log.d("action", action!! + " \n For Registering Alarm")
            putExtra(TIMETABLEID, timeTableId)
            putExtra(SUBJECTID, timeTable.subjectId)
            putExtra(SUBJECTNAME, timeTable.subjectName)
            putExtra(HOUR, timeTable.hour)
            putExtra(MINUTE, timeTable.minute)
            putExtra(DAYOFTHEWEEK, timeTable.dayOfTheWeek)
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timeTable.hour)
        calendar.set(Calendar.MINUTE, timeTable.minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, timeTable.dayOfTheWeek)

        val pendingIntent = PendingIntent.getBroadcast(context, timeTableId, intent, PendingIntent.FLAG_IMMUTABLE)
        if(Calendar.getInstance().time.time >= calendar.time.time){
            Log.d("broadcast", "Now time -> ${Calendar.getInstance().time.time} and set time is ${calendar.time.time}")
            Log.d("broadcast", "Setting Alarm for ${calendar.time.time + AlarmManager.INTERVAL_DAY*7} next week")
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.time.time + AlarmManager.INTERVAL_DAY*7, pendingIntent)
        }else{
            Log.d("broadcast", "Now time -> ${Calendar.getInstance().time.time} and set time is ${calendar.time.time}")
            Log.d("broadcast", "Setting Alarm for ${calendar.time.time} this week")
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.time.time, pendingIntent)
        }
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.time.time, AlarmManager.INTERVAL_DAY*7, pendingIntent)
    }

    fun cancelAlarm(
        context: Context,
        timeTable: TimeTable
    ){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ClassAlarmBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.alarms.ClassAlarmBroadcastReceiver"
        }
        val pendingIntent = PendingIntent.getBroadcast(context, timeTable._id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE )
        alarmManager.cancel(pendingIntent)
    }
}