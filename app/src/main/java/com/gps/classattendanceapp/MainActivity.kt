package com.gps.classattendanceapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.gps.classattendanceapp.presenter.navigationcomponents.ClassAttendanceNavigationHost
import com.gps.classattendanceapp.presenter.theme.ClassAttendanceAppTheme
import dagger.hilt.android.AndroidEntryPoint

/*
Official name -> GPS Attendance Tracker
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContent {
            ClassAttendanceAppTheme{
                ClassAttendanceNavigationHost()
            }
        }
    }

    private fun requestPermissions(){
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){
//            callback()
        }

        permissionLauncher.launch(
            requiredPermissions
        )
    }
}
