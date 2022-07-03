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
class MarkPresentAbsentThroughNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val classAttendanceDao: ClassAttendanceDao
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val subjectId = inputData.getInt("subjectId", -1)
        val attendance = inputData.getBoolean("attendance", false)
        val subject = classAttendanceDao.getSubjectWithId(subjectId)

        return Result.success()
    }
}