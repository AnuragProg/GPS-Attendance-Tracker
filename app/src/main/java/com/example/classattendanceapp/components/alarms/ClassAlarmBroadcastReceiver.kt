package com.example.classattendanceapp.components.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class ClassAlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?){
        if(intent != null && context != null) {
            val timeTableId = intent.getIntExtra(AlarmKeys.TIMETABLE_ID.key, -1)
            val subjectId = intent.getIntExtra(AlarmKeys.SUBJECT_ID.key, -1)
            val subjectName = intent.getStringExtra(AlarmKeys.SUBJECT_NAME.key)
            val hour = intent.getIntExtra(AlarmKeys.HOUR.key, -1)
            val minute = intent.getIntExtra(AlarmKeys.MINUTE.key, -1)
            val day_of_the_week = intent.getIntExtra(AlarmKeys.DAYOFTHEWEEK.key, -1)
            if(timeTableId == -1 || subjectId == -1 || subjectName == null || hour == -1 || minute == -1 || day_of_the_week == -1){
                return
            }
            val inputData = workDataOf(
                AlarmKeys.TIMETABLE_ID.key to timeTableId,
                AlarmKeys.SUBJECT_ID.key to subjectId,
                AlarmKeys.SUBJECT_NAME.key to subjectName,
                AlarmKeys.HOUR.key to hour,
                AlarmKeys.MINUTE.key to minute,
                AlarmKeys.DAYOFTHEWEEK.key to day_of_the_week
            )
            val startOperationWorkRequest = OneTimeWorkRequestBuilder<ForegroundLocationMarkAttendanceWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueue(startOperationWorkRequest)
        }
    }
}