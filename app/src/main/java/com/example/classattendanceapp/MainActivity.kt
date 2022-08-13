package com.example.classattendanceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.classattendanceapp.presenter.navigationcomponents.ClassAttendanceNavigationHost
import com.example.classattendanceapp.ui.theme.ClassAttendanceAppTheme
import dagger.hilt.android.AndroidEntryPoint

/*
Official name -> GPS Attendance Tracker
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClassAttendanceAppTheme{
                ClassAttendanceNavigationHost()
            }
        }
    }
}
