package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screens(
    val screen_name: String,
    val route: String,
    val icon: ImageVector
) {

    LOGSSCREEN("Logs","LogsScreen", Icons.Filled.Login),
    SUBJECTSSCREEN("Subjects","SubjectsScreen", Icons.Filled.Subject),
    TIMETABLESCREEN("TimeTable","TimeTableScreen", Icons.Filled.Timer),
}