package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel

@Composable
fun ClassAttendanceTopBar(
    classAttendanceViewModel: ClassAttendanceViewModel
){
    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(context.getString(R.string.app_name))
        },
        actions = {
            IconButton(onClick = {
                classAttendanceViewModel.changeAddLocationCoordinateState(true)
            }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }
        }
    )
}