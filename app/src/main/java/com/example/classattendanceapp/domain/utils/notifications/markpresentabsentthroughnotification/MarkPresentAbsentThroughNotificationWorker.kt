package com.example.classattendanceapp.domain.utils.notifications.markpresentabsentthroughnotification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import com.example.classattendanceapp.domain.utils.notifications.NotificationKeys
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
            if(attendance){
                subject.daysPresentOfLogs++
            }else{
                subject.daysAbsentOfLogs++
            }
            classAttendanceRepository.insertLogs(
                com.example.classattendanceapp.data.models.Log(
                    _id = 0,
                    subjectId = subject._id,
                    subjectName = subject.subjectName,
                    timestamp = Calendar.getInstance().time,
                    wasPresent = attendance
                )
            )
            classAttendanceRepository.updateSubject(
                subject
            )
        }
        return Result.success()
    }
}