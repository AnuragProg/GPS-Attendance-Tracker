package com.gps.classattendanceapp.components.alarms

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.components.location.ClassLocationManager
import com.gps.classattendanceapp.components.maths.CoordinateCalculations
import com.gps.classattendanceapp.components.notifications.NotificationHandler
import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import java.util.*


@HiltWorker
class ForegroundLocationMarkAttendanceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val classAttendanceRepository: ClassAttendanceRepository
) : CoroutineWorker(context, workerParameters){

    private val foregroundNotificationChannelId = "FOREGROUNDLOCATIONNOTIFICATIONCHANNEL"
    private val foregroundNotificaitionId = -10


    override suspend fun doWork(): Result {

        createForegroundNotificationChannel()
        val foregroundNotification = NotificationCompat.Builder(context, foregroundNotificationChannelId)
            .setSmallIcon(R.drawable.processing)
            .setContentTitle("Location Retrieval")
            .setContentText("Retrieving Your Location")
            .build()

        val foregroundNotificationInfo = ForegroundInfo(foregroundNotificaitionId, foregroundNotification)
        setForeground(foregroundNotificationInfo)


        val subjectId = inputData.getInt(AlarmKeys.SUBJECT_ID.key, -1)
        val subjectName = inputData.getString(AlarmKeys.SUBJECT_NAME.key)
        val timeTableId = inputData.getInt(AlarmKeys.TIMETABLE_ID.key, -1)
        val hour = inputData.getInt(AlarmKeys.HOUR.key, -1)
        val minute = inputData.getInt(AlarmKeys.MINUTE.key, -1)
        val dayOfTheWeek = inputData.getInt(AlarmKeys.DAYOFTHEWEEK.key, -1)
        if(timeTableId == -1 || subjectId == -1 || subjectName == null || hour == -1 || minute == -1 || dayOfTheWeek == -1){
            return Result.retry()
        }

        if(
            context.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ){
            createNotificationChannelAndShowNotification(
                timeTableId = timeTableId,
                subjectId = subjectId,
                subjectName = subjectName,
                hour =hour,
                minute = minute,
                context = context
            )
            return Result.success()
        }
        if(
            Build.VERSION.SDK_INT >= 29
        ){
            if(
                context.checkSelfPermission(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_DENIED
            ){
                createNotificationChannelAndShowNotification(
                    timeTableId = timeTableId,
                    subjectId = subjectId,
                    subjectName = subjectName,
                    hour = hour,
                    minute = minute,
                    context = context
                )
                return Result.success()
            }
        }

        val subject = classAttendanceRepository.getSubjectWithId(subjectId)
        val userSpecifiedLocation = Triple(
            first = subject?.latitude,
            second = subject?.longitude,
            third = subject?.range,
        )

        // if no userSpecifiedLocation found in database
        if(userSpecifiedLocation.first==null || userSpecifiedLocation.second==null || userSpecifiedLocation.third==null){
            createNotificationChannelAndShowNotification(
                timeTableId = timeTableId,
                subjectId = subjectId,
                subjectName = subjectName,
                hour = hour,
                minute = minute,
                context = context
            )
        }else{
            try{
                withTimeout(5000){


                    val currentLocation = ClassLocationManager.getLocationFlow(context).first()

                    val distance = CoordinateCalculations.distanceBetweenPointsInM(
                        lat1 = currentLocation.latitude,
                        long1 = currentLocation.longitude,
                        lat2 = userSpecifiedLocation.first!!,
                        long2 = userSpecifiedLocation.second!!,
                    )
                    if (distance <= userSpecifiedLocation.third!!) {
                        val logsId = classAttendanceRepository.insertLogs(
                            com.gps.classattendanceapp.data.models.Log(
                                0,
                                subjectId,
                                subjectName,
                                Calendar.getInstance().time,
                                true,
                                latitude = currentLocation.latitude,
                                longitude = currentLocation.longitude,
                                distance = distance
                            )
                        )
                        createNotificationChannelAndShowNotification(
                            timeTableId = timeTableId,
                            logsId = logsId.toInt(),
                            markedPresentOrAbsent = true,
                            subjectId = subjectId,
                            subjectName = subjectName,
                            hour = hour,
                            minute = minute,
                            context = context,
                            message = "Present \nLatitude = " + String.format("%.6f",
                                currentLocation.latitude) + "\nLongitude = " + String.format(
                                "%.6f",
                                currentLocation.longitude) + "\nDistance = " + String.format(
                                "%.6f",
                                distance)
                        )
                    } else {
                        val logsId = classAttendanceRepository.insertLogs(
                            com.gps.classattendanceapp.data.models.Log(
                                0,
                                subjectId,
                                subjectName,
                                Calendar.getInstance().time,
                                false,
                                latitude = currentLocation.latitude,
                                longitude = currentLocation.longitude,
                                distance = distance
                            )
                        )
                        createNotificationChannelAndShowNotification(
                            logsId = logsId.toInt(),
                            markedPresentOrAbsent = false,
                            timeTableId = timeTableId,
                            subjectId = subjectId,
                            subjectName = subjectName,
                            hour = hour,
                            minute = minute,
                            context = context,
                            message = "Absent \nLatitude = " + String.format("%.6f",
                                currentLocation.latitude) + "\nLongitude = " + String.format(
                                "%.6f",
                                currentLocation.longitude) + "\nDistance = " + String.format(
                                "%.6f",
                                distance)
                        )
                    }
                }
            }
            catch(timeout: TimeoutCancellationException){
                createNotificationChannelAndShowNotification(
                    timeTableId = timeTableId,
                    subjectId = subjectId,
                    subjectName = subjectName,
                    hour = hour,
                    minute = minute,
                    context = context
                )
            }
        }
        ClassAlarmManager.registerAlarm(
            context,
            timeTableId,
            TimeTable(
                timeTableId,
                subjectId,
                subjectName,
                hour,
                minute,
                dayOfTheWeek
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
        subjectId: Int,
        logsId: Int?=null,
        markedPresentOrAbsent: Boolean?=null,
        subjectName: String?,
        hour: Int,
        minute: Int,
        context: Context,
        message: String? = null
    ){
        if (timeTableId == -1 || hour == -1 || minute == -1 || subjectName == null) {
            return
        }
        NotificationHandler.createNotificationChannel(context)
        NotificationHandler.createAndShowNotification(
            context = context,
            logsId = logsId,
            markedPresentOrAbsent = markedPresentOrAbsent,
            timeTableId = timeTableId,
            subjectId = subjectId,
            subjectName = subjectName,
            hour = hour,
            minute = minute,
            message = message
        )
    }
}