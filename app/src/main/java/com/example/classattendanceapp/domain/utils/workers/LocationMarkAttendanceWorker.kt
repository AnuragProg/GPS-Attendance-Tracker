package com.example.classattendanceapp.domain.utils.workers


import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.classattendanceapp.data.db.ClassAttendanceDatabase
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.domain.utils.internetcheck.NetworkCheck
import com.example.classattendanceapp.domain.utils.location.ClassLocationManager
import com.example.classattendanceapp.domain.utils.maths.CoordinateCalculations
import com.example.classattendanceapp.domain.utils.notifications.NotificationHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject


@HiltWorker
class LocationMarkAttendanceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val dataStore: DataStore<Preferences>
): Worker(context, workParams) {

    private val TIMETABLEID = "timetable_id"
    private val SUBJECTNAME = "subject_name"
    private val HOUR = "hour"
    private val MINUTE = "minute"
    private val SUBJECTID = "subject_id"

    private val longitudeDataStoreKey = doublePreferencesKey("userLongitude")
    private val latitudeDataStoreKey = doublePreferencesKey("userLatitude")

    override fun doWork(): Result {


        Log.d("broadcast", "starting doWork and retrieving data from inputData")


        val subjectId = inputData.getInt(SUBJECTID, -1)
        val subjectName = inputData.getString(SUBJECTNAME)
        val timeTableId = inputData.getInt(TIMETABLEID, -1)
        val hour = inputData.getInt(HOUR, -1)
        val minute = inputData.getInt(MINUTE, -1)

        if(timeTableId == -1 || subjectId == -1 || subjectName == null || hour == -1 || minute == -1){
            return Result.retry()
        }

        Log.d("broadcast", "retrieved data are subjectName -> $subjectName | timeTableId -> $timeTableId" +
                " | hour -> $hour | minute -> $minute")
        Log.d("broadcast", "beginning NetworkCheck")


        if (NetworkCheck.isInternetAvailable(context)) {


            Log.d("broadcast", "NetworkCheck successful")


            val location = MutableStateFlow<Location?>(null)


            Log.d("broadcast", "Launching locationGetterJob")


            val locationGetterJob = CoroutineScope(Dispatchers.IO).launch {

                Log.d("broadcast", "Started LocationGetterJob")

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
                try{
                    withTimeout(15000) {
                        location.collectLatest { currentLocation ->
                            if (currentLocation != null) {
                                Log.d("broadcast", "location StateFlow has been updated with location -> $currentLocation")
                                // Getting Institute Location from datastore
                                val userSpecifiedLocation = dataStore.data.map{ pref ->
                                    pref[latitudeDataStoreKey]
                                }.combine(
                                    dataStore.data.map{ pref ->
                                        pref[longitudeDataStoreKey]
                                    }
                                ){ lat, lon ->
                                    Pair(lat, lon)
                                }.first()

                                // Dao instance to mark either Absent or Present
                                val classAttendanceDao = ClassAttendanceDatabase.getInstance(context).classAttendanceDao

                                Log.d("broadcast", "current -> latitude : ${currentLocation.latitude} | longitude : ${currentLocation.longitude}")
                                Log.d("broadcast", "user -> latitude : ${userSpecifiedLocation.first} | longitude : ${userSpecifiedLocation.second}")
                                if(userSpecifiedLocation.first!=null && userSpecifiedLocation.second!=null){
                                    val distance = CoordinateCalculations.distanceBetweenPointsInM(
                                        lat1 = currentLocation.latitude,
                                        long1 = currentLocation.longitude,
                                        lat2 = userSpecifiedLocation.first!!,
                                        long2 = userSpecifiedLocation.second!!,
                                    )
                                    Log.d("broadcast", "Calculated Distance is $distance meters")
                                    if(distance<20){
                                        Log.d("broadcast", "Marking present in database")
                                        classAttendanceDao.insertLogs(
                                            Logs(
                                                0,
                                                subjectId,
                                                subjectName,
                                                Calendar.getInstance().time,
                                                true
                                            )
                                        )
                                        createNotificationChannelAndShowNotification(timeTableId, subjectName, hour, minute, context, "Present \nLatitude = " + String.format("%.6f",currentLocation.latitude) + "\nLongitude = " + String.format("%.6f",currentLocation.longitude)+"\nDistance = " +String.format("%.6f",distance))
                                    }else{
                                        Log.d("broadcast", "Marking absent in database")
                                        classAttendanceDao.insertLogs(
                                            Logs(
                                                0,
                                                subjectId,
                                                subjectName,
                                                Calendar.getInstance().time,
                                                false
                                            )
                                        )
                                        createNotificationChannelAndShowNotification(timeTableId, subjectName, hour, minute, context, "Absent \nLatitude = " + String.format("%.6f",currentLocation.latitude)+ "\nLongitude = " + String.format("%.6f",currentLocation.longitude) + "\nDistance = " +String.format("%.6f", distance))
                                    }
                                }else{
                                    createNotificationChannelAndShowNotification(timeTableId, subjectName, hour, minute, context)
                                }
                                locationGetterJob.cancel()
                                Log.d("broadcast", "Cancelling withTimeout coroutine")
                                this@withTimeout.cancel()
                            }

                        }
                    }
                }catch(e: TimeoutCancellationException){
                    Log.d("broadcast", "${e.cause} thrown, creating simple notification")
                    createNotificationChannelAndShowNotification(timeTableId, subjectName, hour, minute, context)
                }finally{
                    this.cancel()
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