package com.example.classattendanceapp.components.notifications.invertpreviouslymarkedattendance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.classattendanceapp.components.notifications.NotificationKeys

class InvertPreviouslyMarkedAttendanceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context==null || intent==null){
            return
        }
        val logsId = intent.getIntExtra(NotificationKeys.LOGS_ID.key, -1)
        val notificationId = intent.getIntExtra(NotificationKeys.NOTIFICATION_ID.key, -1)


        if(notificationId==-1 || logsId == -1) return

        NotificationManagerCompat.from(context).cancel(notificationId)

        val inputDataForWorker = workDataOf(
            NotificationKeys.LOGS_ID.key to logsId
        )
        val invertPreviouslyMarkedAttendanceWorkRequest = OneTimeWorkRequestBuilder<InvertPreviouslyMarkedAttendanceWorker>()
            .setInputData(inputDataForWorker)
            .build()

        WorkManager.getInstance(context)
            .enqueue(invertPreviouslyMarkedAttendanceWorkRequest)
    }
}