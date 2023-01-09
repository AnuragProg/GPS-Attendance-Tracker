package com.gps.classattendanceapp.components.notifications.deletepreviouslymarkedattendance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.gps.classattendanceapp.components.notifications.NotificationKeys


class DeletePreviouslyMarkedAttendanceBroadcastReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        if(context==null || intent==null){
            return
        }
        val logsId = intent.getIntExtra(NotificationKeys.LOGS_ID.key, -1)
        val notificationId = intent.getIntExtra(NotificationKeys.NOTIFICATION_ID.key, -1)


        if(notificationId==-1 || logsId == -1) return
        NotificationManagerCompat.from(context).cancel(notificationId)

        val workData = workDataOf(
            NotificationKeys.LOGS_ID.key to logsId
        )

        val workRequest = OneTimeWorkRequestBuilder<DeletePreviousMarkedAttendanceWorker>()
            .setInputData(workData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}