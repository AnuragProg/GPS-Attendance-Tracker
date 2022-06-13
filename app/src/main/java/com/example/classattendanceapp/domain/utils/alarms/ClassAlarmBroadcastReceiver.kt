package com.example.classattendanceapp.domain.utils.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.work.*
import com.example.classattendanceapp.domain.utils.internetcheck.NetworkCheck
import com.example.classattendanceapp.domain.utils.location.ClassLocationManager
import com.example.classattendanceapp.domain.utils.notifications.NotificationHandler
import com.example.classattendanceapp.domain.utils.workers.LocationMarkAttendanceWorker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlin.coroutines.CoroutineContext

class ClassAlarmBroadcastReceiver : BroadcastReceiver() {

    private val TIMETABLEID = "timetable_id"
    private val SUBJECTID = "subject_id"
    private val SUBJECTNAME = "subject_name"
    private val HOUR = "hour"
    private val MINUTE = "minute"

    override fun onReceive(context: Context?, intent: Intent?){
        Log.d("broadcast","Broadcast received")
        if(intent != null && context != null) {
            val timeTableId = intent.getIntExtra(TIMETABLEID, -1)
            val subjectId = intent.getIntExtra(SUBJECTID, -1)
            val subjectName = intent.getStringExtra(SUBJECTNAME)
            val hour = intent.getIntExtra(HOUR, -1)
            val minute = intent.getIntExtra(MINUTE, -1)
            if(timeTableId == -1 || subjectId == -1 || subjectName == null || hour == -1 || minute == -1){
                return
            }
            val inputData = workDataOf(
                TIMETABLEID to timeTableId,
                SUBJECTID to subjectId,
                SUBJECTNAME to subjectName,
                HOUR to hour,
                MINUTE to minute
            )
            val startOperationWorkRequest = OneTimeWorkRequestBuilder<LocationMarkAttendanceWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueue(startOperationWorkRequest)
        }
    }
}