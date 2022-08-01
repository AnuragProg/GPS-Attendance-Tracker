package com.example.classattendanceapp.domain.utils.notifications.invertpreviouslymarkedattendance

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.domain.utils.notifications.NotificationKeys
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class InvertPreviouslyMarkedAttendanceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val classAttendanceDao: ClassAttendanceDao
) : CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {

        val logsId = inputData.getInt(NotificationKeys.LOGS_ID.key, -1)
        val retrievedLog = classAttendanceDao.getLogsWithId(logsId)

        if(retrievedLog!=null){
            val retrievedSubject = classAttendanceDao.getSubjectWithId(retrievedLog.subjectId)
            if(retrievedSubject!=null){
                retrievedLog.wasPresent = !retrievedLog.wasPresent
                if(retrievedLog.wasPresent){
                    if(retrievedSubject.daysAbsentOfLogs>0){ retrievedSubject.daysAbsentOfLogs-- }
                    retrievedSubject.daysPresentOfLogs++
                }else{
                    retrievedSubject.daysAbsentOfLogs++
                    if(retrievedSubject.daysPresentOfLogs>0){ retrievedSubject.daysPresentOfLogs-- }
                }
                classAttendanceDao.updateSubject(
                    retrievedSubject
                )
                classAttendanceDao.updateLog(
                    retrievedLog
                )
            }
        }
        return Result.success()
    }
}