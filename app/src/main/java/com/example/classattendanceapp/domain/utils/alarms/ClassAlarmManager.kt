package com.example.classattendanceapp.domain.utils.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.classattendanceapp.data.models.TimeTable
import java.util.*

object ClassAlarmManager {

    fun registerAlarm(
        context: Context,
        timeTableId: Int,
        timeTable: TimeTable
    ){

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ClassAlarmBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.alarms.ClassAlarmBroadcastReceiver"
            Log.d("action", action!! + " \n For Registering Alarm")
            putExtra(AlarmKeys.TIMETABLE_ID.key, timeTableId)
            putExtra(AlarmKeys.SUBJECT_ID.key, timeTable.subjectId)
            putExtra(AlarmKeys.SUBJECT_NAME.key, timeTable.subjectName)
            putExtra(AlarmKeys.HOUR.key, timeTable.hour)
            putExtra(AlarmKeys.MINUTE.key, timeTable.minute)
            putExtra(AlarmKeys.DAYOFTHEWEEK.key, timeTable.dayOfTheWeek)
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timeTable.hour)
        calendar.set(Calendar.MINUTE, timeTable.minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, timeTable.dayOfTheWeek)

        val pendingIntent = PendingIntent.getBroadcast(context, timeTableId, intent, PendingIntent.FLAG_IMMUTABLE)
        if(Calendar.getInstance().time.time >= calendar.time.time){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.time.time + AlarmManager.INTERVAL_DAY*7, pendingIntent)
        }else{
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.time.time, pendingIntent)
        }
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