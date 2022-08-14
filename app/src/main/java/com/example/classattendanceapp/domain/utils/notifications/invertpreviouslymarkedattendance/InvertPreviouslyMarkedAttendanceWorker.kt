package com.example.classattendanceapp.domain.utils.notifications.invertpreviouslymarkedattendance

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import com.example.classattendanceapp.domain.utils.notifications.NotificationKeys
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
                if(retrievedLog.wasPresent){
                    if(retrievedSubject.daysAbsentOfLogs>0){ retrievedSubject.daysAbsentOfLogs-- }
                    retrievedSubject.daysPresentOfLogs++
                }else{
                    retrievedSubject.daysAbsentOfLogs++
                    if(retrievedSubject.daysPresentOfLogs>0){ retrievedSubject.daysPresentOfLogs-- }
                }
                classAttendanceRepository.updateSubject(
                    retrievedSubject
                )
                classAttendanceRepository.updateLog(
                    retrievedLog
                )
            }
        }
        return Result.success()
    }
}