package com.example.classattendanceapp.presenter.navigationcomponents


import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
            /*
            Commented to add future items to the overflow menu
             */
//            IconButton(onClick = {
//                classAttendanceViewModel.changeOverFlowMenuState(true)
//            }) {
//                Icon(
//                    Icons.Filled.MoreVert,
//                    contentDescription = null
//                )
//            }
        }
    )
}