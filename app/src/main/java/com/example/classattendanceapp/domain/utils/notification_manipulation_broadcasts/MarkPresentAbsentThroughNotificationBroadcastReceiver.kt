package com.example.classattendanceapp.domain.utils.notification_manipulation_broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.classattendanceapp.domain.utils.workers.MarkPresentAbsentThroughNotificationWorker

class MarkPresentAbsentThroughNotificationBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context!=null && intent!=null){
            val attendance = intent.getBooleanExtra("attendance", false)
            val subjectId = intent.getIntExtra("subjectId", -1)
            val notificationId = intent.getIntExtra("notificationPush", -1)
            if(notificationId!=0 && subjectId!=-1){
                NotificationManagerCompat.from(context).cancel(notificationId)
                val markPresentAbsentThroughNotificationWorkerData = workDataOf(
                    "subjectId" to subjectId,
                    "attendance" to attendance
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