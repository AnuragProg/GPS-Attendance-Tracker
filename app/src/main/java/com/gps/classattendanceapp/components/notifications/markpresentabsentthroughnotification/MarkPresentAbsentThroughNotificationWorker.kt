package com.gps.classattendanceapp.components.notifications.markpresentabsentthroughnotification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gps.classattendanceapp.components.notifications.NotificationKeys
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*

@HiltWorker
class MarkPresentAbsentThroughNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val classAttendanceRepository: ClassAttendanceRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val subjectId = inputData.getInt(NotificationKeys.SUBJECT_ID.key, -1)
        val attendance = inputData.getBoolean(NotificationKeys.ATTENDANCE_STATUS.key, false)
        if(subjectId == -1){
            return Result.failure()
        }
        val subject = classAttendanceRepository.getSubjectWithId(subjectId)
        if(subject!=null){
            classAttendanceRepository.insertLogs(
                com.gps.classattendanceapp.data.models.Log(
                    _id = 0,
                    subjectId = subject._id,
                    subjectName = subject.subjectName,
                    timestamp = Calendar.getInstance().time,
                    wasPresent = attendance,
                    latitude = null,
                    longitude = null,
                    distance = null
                )
            )
        }
        return Result.success()
    }
}