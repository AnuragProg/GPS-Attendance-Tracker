package com.example.classattendanceapp.domain.utils.workers


import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.classattendanceapp.domain.utils.internetcheck.NetworkCheck
import com.example.classattendanceapp.domain.utils.location.ClassLocationManager
import com.example.classattendanceapp.domain.utils.notifications.NotificationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LocationMarkAttendanceWorker(
    private val context: Context, workParams: WorkerParameters
): Worker(context, workParams) {

    private val TIMETABLEID = "timetable_id"
    private val SUBJECTNAME = "subject_name"
    private val HOUR = "hour"
    private val MINUTE = "minute"

    private val radius : Double = 0.0002
    private val latitude: Double = 30.2709
    private val longitude: Double = 77.9865


    override fun doWork(): Result {
        Log.d("broadcast", "starting doWork and retrieving data from inputData")
        val subjectName = inputData.getString(SUBJECTNAME)
        val timeTableId = inputData.getInt(TIMETABLEID, -1)
        val hour = inputData.getInt(HOUR, -1)
        val minute = inputData.getInt(MINUTE, -1)

        Log.d("broadcast", "retrieved data are subjectName -> $subjectName | timeTableId -> $timeTableId" +
                " | hour -> $hour | minute -> $minute")
        Log.d("broadcast", "beginning NetworkCheck")
        if (NetworkCheck.isInternetAvailable(context)) {
            Log.d("broadcast", "NetworkCheck successful")
            val location = MutableStateFlow<Location?>(null)

            Log.d("broadcast", "Launching locationGetterJob")
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
            Log.d("broadcast", "Launching location var updates collection sequence")
            CoroutineScope(Dispatchers.IO).launch{
                location.collectLatest {
                    if (it != null) {
                        Log.d("broadcast", "location StateFlow has been updated with location -> $it")

                        /*
                        TODO -> Check whether gps provided coordinates are in reasonable range
                        TODO -> If they are then mark present other wise absent
                        TODO -> Then send message Present and Absent according to the action performed
                         */

                        createNotificationChannelAndShowNotification(timeTableId, subjectName, hour, minute, context)
                        locationGetterJob.cancel()
                        this.cancel()
                    }
                }
            }


        } else {
            Log.d("broadcast", "NetworkCheck unsuccessful")
            Log.d("broadcast", "Executing Normal Notification sequence")
            createNotificationChannelAndShowNotification(timeTableId, subjectName, hour, minute , context)

        }
        return Result.success()
    }

    private fun createNotificationChannelAndShowNotification(
        timeTableId: Int,
        subjectName: String?,
        hour: Int,
        minute: Int,
        context: Context,
        message: String? = null
    ){
        if (timeTableId == -1 || hour == -1 || minute == -1 || subjectName == null) {
            return
        }
        Log.d("broadcast", "Successfully have details $timeTableId $subjectName $hour $minute")
        NotificationHandler.createNotificationChannel(context)
        NotificationHandler.createAndShowNotification(
            context,
            timeTableId,
            subjectName,
            hour,
            minute,
            message
        )
    }
}