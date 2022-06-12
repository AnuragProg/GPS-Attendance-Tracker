package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel


@Composable
fun OverflowMenu(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    val showOverFlowMenu = classAttendanceViewModel.showOverFlowMenu.collectAsState()

    DropdownMenu(
        expanded = showOverFlowMenu.value,
        onDismissRequest = {
            classAttendanceViewModel.changeOverFlowMenuState(false)
        }) {
        DropdownMenuItem(onClick = {
            classAttendanceViewModel.changeAddLocationCoordinateState(true)
            classAttendanceViewModel.changeOverFlowMenuState(false)
        }) {
            Text("Add Coordinate")
        }
    }
}