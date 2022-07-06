package com.example.classattendanceapp.domain.utils.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class InvertPreviouslyMarkedAttendanceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val classAttendanceDao: ClassAttendanceDao
) : CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {
        val logsId = inputData.getInt("logs_id", -1)
        Log.d("invert_attendance", "logsId = $logsId in worker")
        val retrievedLog = classAttendanceDao.getLogsWithId(logsId)
        if(retrievedLog!=null){
            retrievedLog.wasPresent = !retrievedLog.wasPresent
            classAttendanceDao.updateLog(
                retrievedLog
            )
            Log.d("invert_attendance", "updation of log done")
        }else{
            Log.d("invert_attendance", "retrievedLog was empty")
        }
        return Result.success()
    }

}