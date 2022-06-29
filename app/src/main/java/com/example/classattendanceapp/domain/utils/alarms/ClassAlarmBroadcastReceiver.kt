package com.example.classattendanceapp.domain.utils.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.example.classattendanceapp.domain.utils.workers.ForegroundLocationMarkAttendanceWorker

class ClassAlarmBroadcastReceiver : BroadcastReceiver() {

    private val TIMETABLEID = "timetable_id"
    private val SUBJECTID = "subject_id"
    private val SUBJECTNAME = "subject_name"
    private val HOUR = "hour"
    private val MINUTE = "minute"
    private val DAYOFTHEWEEK = "day_of_the_week"


    override fun onReceive(context: Context?, intent: Intent?){
        Log.d("broadcast","Broadcast received")
        if(intent != null && context != null) {
            val timeTableId = intent.getIntExtra(TIMETABLEID, -1)
            val subjectId = intent.getIntExtra(SUBJECTID, -1)
            val subjectName = intent.getStringExtra(SUBJECTNAME)
            val hour = intent.getIntExtra(HOUR, -1)
            val minute = intent.getIntExtra(MINUTE, -1)
            val day_of_the_week = intent.getIntExtra(DAYOFTHEWEEK, -1)
            if(timeTableId == -1 || subjectId == -1 || subjectName == null || hour == -1 || minute == -1 || day_of_the_week == -1){
                return
            }
            val inputData = workDataOf(
                TIMETABLEID to timeTableId,
                SUBJECTID to subjectId,
                SUBJECTNAME to subjectName,
                HOUR to hour,
                MINUTE to minute,
                DAYOFTHEWEEK to day_of_the_week
            )
            val startOperationWorkRequest = OneTimeWorkRequestBuilder<ForegroundLocationMarkAttendanceWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueue(startOperationWorkRequest)
        }
    }
}