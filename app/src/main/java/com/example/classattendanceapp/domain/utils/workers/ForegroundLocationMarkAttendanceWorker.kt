package com.example.classattendanceapp.domain.utils.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.db.ClassAttendanceDatabase
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.utils.alarms.ClassAlarmManager
import com.example.classattendanceapp.domain.utils.internetcheck.NetworkCheck
import com.example.classattendanceapp.domain.utils.location.ClassLocationManager
import com.example.classattendanceapp.domain.utils.maths.CoordinateCalculations
import com.example.classattendanceapp.domain.utils.notifications.NotificationHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import java.util.*


@HiltWorker
class ForegroundLocationMarkAttendanceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val dataStore: DataStore<Preferences>
) : CoroutineWorker(context, workerParameters){

    private val TIMETABLEID = "timetable_id"
    private val SUBJECTNAME = "subject_name"
    private val HOUR = "hour"
    private val MINUTE = "minute"
    private val SUBJECTID = "subject_id"
    private val DAYOFTHEWEEK = "day_of_the_week"

    private val longitudeDataStoreKey = doublePreferencesKey("userLongitude")
    private val latitudeDataStoreKey = doublePreferencesKey("userLatitude")
    private val rangeDataStoreKey = doublePreferencesKey("userRange")

    private val foregroundNotificationChannelId = "FOREGROUNDLOCATIONNOTIFICATIONCHANNEL"
    private val foregroundNotificaitionId = -10


    override suspend fun doWork(): Result {

        createForegroundNotificationChannel()
        val foregroundNotification = NotificationCompat.Builder(context, foregroundNotificationChannelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Location Retrieval")
            .setContentText("Retrieving Your Location")
            .build()

        val foregroundNotificationInfo = ForegroundInfo(foregroundNotificaitionId, foregroundNotification)
        setForeground(foregroundNotificationInfo)


        Log.d("worker", "starting doWork and retrieving data from inputData")
        val subjectId = inputData.getInt(SUBJECTID, -1)
        val subjectName = inputData.getString(SUBJECTNAME)
        val timeTableId = inputData.getInt(TIMETABLEID, -1)
        val hour = inputData.getInt(HOUR, -1)
        val minute = inputData.getInt(MINUTE, -1)
        val day_of_the_week = inputData.getInt(DAYOFTHEWEEK, -1)
        if(timeTableId == -1 || subjectId == -1 || subjectName == null || hour == -1 || minute == -1 || day_of_the_week == -1){
            return Result.retry()
        }

        Log.d("worker", "retrieved data are subjectName -> $subjectName | timeTableId -> $timeTableId" +
                " | hour -> $hour | minute -> $minute")

        if(
            context.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ){
            Log.d("worker", "Don't have fine location permission so sending normal notification")
            createNotificationChannelAndShowNotification(
                timeTableId, subjectName, hour, minute, context
            )
            return Result.success()
        }
        if(
            Build.VERSION.SDK_INT >= 29
        ){
            Log.d("worker", "Checking for background location permission")
            if(
                context.checkSelfPermission(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_DENIED
            ){
                Log.d("worker", "Don't have background location permission so sending normal notification")

                createNotificationChannelAndShowNotification(
                    timeTableId, subjectName, hour, minute, context
                )
                return Result.success()
            }
        }

        val userSpecifiedLocation = combine(
            dataStore.data.map { pref ->
                pref[latitudeDataStoreKey] },
            dataStore.data.map { pref ->
                pref[longitudeDataStoreKey] },
            dataStore.data.map { pref ->
                pref[rangeDataStoreKey] }
        ) { lat, lon, range ->
            Triple(lat, lon, range)
        }.first()
        Log.d("worker", "Received userSpecifiedLocation from datastore")

        // Getting Institute Location from datastore
        // if no userSpecifiedLocation found in datastore
        if(userSpecifiedLocation.first==null || userSpecifiedLocation.second==null || userSpecifiedLocation.third==null){
            Log.d("worker", "No coordinates stored by user so showing normal notification")
            createNotificationChannelAndShowNotification(
                timeTableId,
                subjectName,
                hour,
                minute,
                context
            )
            return Result.success()
        }else{
            Log.d("worker", "Retrieving single location")
            val currentLocation = ClassLocationManager.getLocation(context).single()
            if(currentLocation==null) {
                createNotificationChannelAndShowNotification(
                    timeTableId,
                    subjectName,
                    hour,
                    minute,
                    context
                )
            }
            else{
                Log.d("worker",
                    "location StateFlow has been updated with location -> $currentLocation")
                // Dao instance to mark either Absent or Present
                val classAttendanceDao =
                    ClassAttendanceDatabase.getInstance(context).classAttendanceDao

                Log.d("worker",
                    "current -> latitude : ${currentLocation.latitude} | longitude : ${currentLocation.longitude}")
                Log.d("worker",
                    "user -> latitude : ${userSpecifiedLocation.first} | longitude : ${userSpecifiedLocation.second}")

                val distance = CoordinateCalculations.distanceBetweenPointsInM(
                    lat1 = currentLocation.latitude,
                    long1 = currentLocation.longitude,
                    lat2 = userSpecifiedLocation.first!!,
                    long2 = userSpecifiedLocation.second!!,
                )
                Log.d("worker", "Calculated Distance is $distance meters")
                if (distance <= userSpecifiedLocation.third!!) {
                    Log.d("worker", "Marking present in database")
                    val subjectWithId = classAttendanceDao.getSubjectWithId(subjectId)
                    subjectWithId.daysPresent++
                    classAttendanceDao.insertSubject(
                        subjectWithId
                    )
                    classAttendanceDao.insertLogs(
                        Logs(
                            0,
                            subjectId,
                            subjectName,
                            Calendar.getInstance().time,
                            true,
                            latitude = currentLocation.latitude,
                            longitude = currentLocation.longitude
                        )
                    )
                    Log.d("worker", "Creating Present marked notification")
                    createNotificationChannelAndShowNotification(timeTableId,
                        subjectName,
                        hour,
                        minute,
                        context,
                        "Present \nLatitude = " + String.format("%.6f",
                            currentLocation.latitude) + "\nLongitude = " + String.format(
                            "%.6f",
                            currentLocation.longitude) + "\nDistance = " + String.format(
                            "%.6f",
                            distance))
                } else {
                    Log.d("worker", "Marking absent in database")
                    val subjectWithId = classAttendanceDao.getSubjectWithId(subjectId)
                    subjectWithId.daysAbsent++
                    classAttendanceDao.insertSubject(
                        subjectWithId
                    )
                    classAttendanceDao.insertLogs(
                        Logs(
                            0,
                            subjectId,
                            subjectName,
                            Calendar.getInstance().time,
                            false,
                            latitude = currentLocation.latitude,
                            longitude = currentLocation.longitude
                        )
                    )
                    Log.d("worker", "Creating Absent marked notification")
                    createNotificationChannelAndShowNotification(timeTableId,
                        subjectName,
                        hour,
                        minute,
                        context,
                        "Absent \nLatitude = " + String.format("%.6f",
                            currentLocation.latitude) + "\nLongitude = " + String.format(
                            "%.6f",
                            currentLocation.longitude) + "\nDistance = " + String.format(
                            "%.6f",
                            distance))
                }
            }
        }
        Log.d("worker", "Reregistering exact Alarm of same time interval")
        ClassAlarmManager.registerAlarm(
            context,
            timeTableId,
            TimeTable(
                timeTableId,
                subjectId,
                subjectName,
                hour,
                minute,
                day_of_the_week
            )
        )


        return Result.success()

    }

    private fun createForegroundNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val foregroundChannel = NotificationChannel(
                foregroundNotificationChannelId,
                "Location Retrieval Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(foregroundChannel)
        }
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
        Log.d("worker", "Successfully have details $timeTableId $subjectName $hour $minute")
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