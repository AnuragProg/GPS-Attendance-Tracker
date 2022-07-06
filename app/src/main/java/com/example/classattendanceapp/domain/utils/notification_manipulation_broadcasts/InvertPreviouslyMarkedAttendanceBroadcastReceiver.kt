package com.example.classattendanceapp.domain.utils.notification_manipulation_broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.classattendanceapp.domain.utils.workers.InvertPreviouslyMarkedAttendanceWorker

class InvertPreviouslyMarkedAttendanceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context==null || intent==null){
            return
        }
        val logsId = intent.getIntExtra("logs_id", -1)
        val notificationId = intent.getIntExtra("notification_id", -1)

        Log.d("invert_attendance", "logsId = $logsId in broadcast receiver")

        if(notificationId==-1 || logsId == -1) return

        Log.d("invert_attendance", "notificationId is $notificationId")
        NotificationManagerCompat.from(context).cancel(notificationId)

        val inputDataForWorker = workDataOf(
            "logs_id" to logsId
        )
        val invertPreviouslyMarkedAttendanceWorkRequest = OneTimeWorkRequestBuilder<InvertPreviouslyMarkedAttendanceWorker>()
            .setInputData(inputDataForWorker)
            .build()

        WorkManager.getInstance(context)
            .enqueue(invertPreviouslyMarkedAttendanceWorkRequest)
    }
}