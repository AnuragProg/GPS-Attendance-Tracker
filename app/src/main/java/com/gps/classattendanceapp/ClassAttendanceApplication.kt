package com.gps.classattendanceapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.shreyaspatil.permissionFlow.PermissionFlow
import javax.inject.Inject


@HiltAndroidApp
class ClassAttendanceApplication: Application(), Configuration.Provider{

    @Inject lateinit var workerFactory : HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        PermissionFlow.init(this)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}