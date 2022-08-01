package com.example.classattendanceapp.domain.utils.resetalarmsonreboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class RebootBroadCastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(context==null || intent==null){
            return
        }
        Log.d("reboot", "Reboot BroadCast received")
        val reregisterAlarmWorkRequest = OneTimeWorkRequestBuilder<ForegroundReregisteringAlarmWorker>()
            .build()
        WorkManager.getInstance(context)
            .enqueue(reregisterAlarmWorkRequest)
    }
}