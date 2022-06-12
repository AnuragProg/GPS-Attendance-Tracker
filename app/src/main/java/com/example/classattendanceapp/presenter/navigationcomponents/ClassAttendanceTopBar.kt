package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel

@Composable
fun ClassAttendanceTopBar(
    classAttendanceViewModel: ClassAttendanceViewModel
){
    val context = LocalContext.current
    val showOverflowMenu = classAttendanceViewModel.showOverFlowMenu.collectAsState()
    TopAppBar(
        title = {
            Text(context.getString(R.string.app_name))
        },
        actions = {
            IconButton(onClick = {
                classAttendanceViewModel.changeOverFlowMenuState(true)
            }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }
            OverflowMenu(classAttendanceViewModel = classAttendanceViewModel)
        }
    )
}