package com.gps.classattendanceapp.components.notifications.invertpreviouslymarkedattendance

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gps.classattendanceapp.components.notifications.NotificationKeys
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class InvertPreviouslyMarkedAttendanceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val classAttendanceRepository: ClassAttendanceRepository
) : CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {

        val logsId = inputData.getInt(NotificationKeys.LOGS_ID.key, -1)
        val retrievedLog = classAttendanceRepository.getLogsWithId(logsId)
        if(retrievedLog!=null){
            val retrievedSubject = classAttendanceRepository.getSubjectWithId(retrievedLog.subjectId)
            retrievedLog.latitude = null
            retrievedLog.longitude = null
            retrievedLog.distance = null
            if(retrievedSubject!=null){
                retrievedLog.wasPresent = !retrievedLog.wasPresent
                classAttendanceRepository.updateLog(
                    retrievedLog
                )
            }
        }
        return Result.success()
    }
}