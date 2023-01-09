package com.gps.classattendanceapp.components.notifications.deletepreviouslymarkedattendance

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gps.classattendanceapp.components.notifications.NotificationKeys
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeletePreviousMarkedAttendanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParam: WorkerParameters,
    private val classAttendanceRepository: ClassAttendanceRepository
 ): CoroutineWorker(context, workerParam) {

    override suspend fun doWork(): Result {
        val logsId = inputData.getInt(NotificationKeys.LOGS_ID.key, -1)
        if (logsId == -1) return Result.failure()
        val retrievedLog = classAttendanceRepository.getLogsWithId(logsId) ?: return Result.success()
        classAttendanceRepository.getSubjectWithId(retrievedLog.subjectId) ?: return Result.success()
        classAttendanceRepository.deleteLogs(retrievedLog._id)
        return Result.success()
    }
}