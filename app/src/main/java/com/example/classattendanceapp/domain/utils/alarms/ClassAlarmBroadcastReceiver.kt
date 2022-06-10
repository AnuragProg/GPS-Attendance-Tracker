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
    private val SUBJECTNAME = "subject_name"
    private val HOUR = "hour"
    private val MINUTE = "minute"

    private val radius : Double = 0.0002
    private val latitude: Double = 30.2709
    private val longitude: Double = 77.9865

    override fun onReceive(context: Context?, intent: Intent?){
        Log.d("broadcast","Broadcast received")
        if(intent != null && context != null) {
            val timeTableId = intent.getIntExtra(TIMETABLEID, -1)
            val subjectName = intent.getStringExtra(SUBJECTNAME)
            val hour = intent.getIntExtra(HOUR, -1)
            val minute = intent.getIntExtra(MINUTE, -1)

            val inputData = workDataOf(
                TIMETABLEID to timeTableId,
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