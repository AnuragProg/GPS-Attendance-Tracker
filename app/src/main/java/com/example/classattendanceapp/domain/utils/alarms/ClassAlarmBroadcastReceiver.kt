package com.example.classattendanceapp.domain.utils.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.example.classattendanceapp.domain.utils.internetcheck.NetworkCheck
import com.example.classattendanceapp.domain.utils.location.ClassLocationManager
import com.example.classattendanceapp.domain.utils.notifications.NotificationHandler
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

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("broadcast","Broadcast received")
        if(intent != null && context != null) {
            Log.d("broadcast", "intent and context are not null")
            if (NetworkCheck.isInternetAvailable(context)) {
                Log.d("broadcast", "Network is available")
                val location = MutableStateFlow<Location?>(null)

                val locationGetterJob = CoroutineScope(Dispatchers.IO).launch {
                    Log.d("broadcast", "Started GlobalScope")
                    ClassLocationManager.getLocation(context).collectLatest {
                        if(it != null){
                            Log.d("broadcast", "New Location received")
                            Log.d("broadcast", "Latitude -> ${it.latitude} | Longitude -> ${it.longitude}")
                            location.value = it
                            Log.d("broadcast", "Cancelling getLocation.collectLatest() coroutine")
                            this.cancel()
                        }
                    }
                }
                CoroutineScope(Dispatchers.IO).launch{
                    location.collectLatest {
                        if (it != null) {
                            Log.d("broadcast", "location StateFlow has been updated with location -> $it")

                            /*
                            TODO -> Check whether gps provided coordinates are in reasonable range
                            TODO -> If they are then mark present other wise absent
                            TODO -> Then send message Present and Absent according to the action performed
                             */

                            createNotificationChannelAndShowNotification(intent, context)
                            locationGetterJob.cancel()
                            this.cancel()
                        }
                    }
                }
            } else {
                Log.d("broadcast", "Network check failed")
                Log.d("broadcast", "Executing Normal Notification sequence")
                createNotificationChannelAndShowNotification(intent, context)
            }
        }
    }

    private fun createNotificationChannelAndShowNotification(intent: Intent, context: Context, message: String? = null){

        val subjectId = intent.getIntExtra(TIMETABLEID, -1)
        val subjectName = intent.getStringExtra(SUBJECTNAME)
        val hour = intent.getIntExtra(HOUR, -1)
        val minute = intent.getIntExtra(MINUTE, -1)
        if (hour == -1 || minute == -1 || subjectName == null) {
            return
        }
        Log.d("broadcast", "Successfully have details $subjectId $subjectName $hour $minute")
        NotificationHandler.createNotificationChannel(context)
        NotificationHandler.createAndShowNotification(
            context,
            subjectId,
            subjectName,
            hour,
            minute,
            message
        )
    }
}