package com.example.classattendanceapp.domain.utils.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.domain.utils.alarms.ClassAlarmManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ForegroundReregisteringAlarmWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val classAttendanceDao: ClassAttendanceDao
): CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val timeTableList = classAttendanceDao.getTimeTable().first()
        for(timeTable in timeTableList){
            Log.d("reboot", "Register alarm for $timeTable")
            ClassAlarmManager.registerAlarm(
                context,
                timeTable._id,
                timeTable
            )
        }
        Log.d("reboot", "Registration is done")
        return Result.success()
    }
}