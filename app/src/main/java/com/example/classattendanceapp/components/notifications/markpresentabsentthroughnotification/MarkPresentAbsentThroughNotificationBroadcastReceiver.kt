package com.example.classattendanceapp.components.notifications.markpresentabsentthroughnotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.classattendanceapp.components.notifications.NotificationKeys

class MarkPresentAbsentThroughNotificationBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context!=null && intent!=null){
            val attendance = intent.getBooleanExtra(NotificationKeys.ATTENDANCE_STATUS.key, false)
            val subjectId = intent.getIntExtra(NotificationKeys.SUBJECT_ID.key, -1)
            val notificationId = intent.getIntExtra(NotificationKeys.NOTIFICATION_PUSH.key, -1)
            if(notificationId!=-1 && subjectId!=-1){
                NotificationManagerCompat.from(context).cancel(notificationId)
                val markPresentAbsentThroughNotificationWorkerData = workDataOf(
                    NotificationKeys.SUBJECT_ID.key to subjectId,
                    NotificationKeys.ATTENDANCE_STATUS.key to attendance
                )
                val markPresentAbsentThroughNotificationWorkRequest = OneTimeWorkRequestBuilder<MarkPresentAbsentThroughNotificationWorker>()
                    .setInputData(markPresentAbsentThroughNotificationWorkerData)
                    .build()
                WorkManager.getInstance(context)
                    .enqueue(markPresentAbsentThroughNotificationWorkRequest)
            }
        }
    }
}