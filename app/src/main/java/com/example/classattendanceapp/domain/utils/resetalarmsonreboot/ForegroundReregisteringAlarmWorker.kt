package com.example.classattendanceapp.domain.utils.resetalarmsonreboot

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import com.example.classattendanceapp.domain.utils.alarms.ClassAlarmManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ForegroundReregisteringAlarmWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val classAttendanceRepository: ClassAttendanceRepository
): CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val timeTableList = classAttendanceRepository.getTimeTable().first()
        for(timeTable in timeTableList){
            ClassAlarmManager.registerAlarm(
                context,
                timeTable._id,
                timeTable
            )
        }
        return Result.success()
    }
}