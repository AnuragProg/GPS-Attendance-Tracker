package com.gps.classattendanceapp.components.resetalarmsonreboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class RebootBroadCastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(context==null || intent==null){
            return
        }
        val reregisterAlarmWorkRequest = OneTimeWorkRequestBuilder<ForegroundReregisteringAlarmWorker>()
            .build()
        WorkManager.getInstance(context)
            .enqueue(reregisterAlarmWorkRequest)
    }
}