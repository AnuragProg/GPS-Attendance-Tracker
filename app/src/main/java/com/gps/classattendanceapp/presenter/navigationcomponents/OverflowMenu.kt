package com.gps.classattendanceapp.presenter.navigationcomponents

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.gps.classattendanceapp.components.LOGS_EXCEL_FILE_NAME
import com.gps.classattendanceapp.components.SUBJECT_EXCEL_FILE_NAME
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch

@Composable
fun OverflowMenu(
    classAttendanceViewModel: ClassAttendanceViewModel,
    isOverflowMenuVisible: Boolean,
    changeOverflowMenuVisibility: (Boolean) -> Unit,
    snackbarHostState: SnackbarHostState
){
    val context = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    DropdownMenu(
        expanded = isOverflowMenuVisible,
        onDismissRequest = {
            changeOverflowMenuVisibility(false)
        },
    ) {
        DropdownMenuItem(
            onClick = {
                changeOverflowMenuVisibility(false)
                listOf(
                    classAttendanceViewModel::refreshSubjects,
                    classAttendanceViewModel::refreshLogs
                ).forEach{
                    coroutineScope.launch {
                        it()
                    }
                }
            }
        ) {
            Text("Refresh")
        }
        DropdownMenuItem(
            onClick = {
                val filePath = classAttendanceViewModel.writeSubjectsStatsToExcel(context, classAttendanceViewModel.subjects.value.data!!)
                Log.d("excel", "FilePath is $filePath")
                coroutineScope.launch{
                    snackbarHostState.showSnackbar(
                        message = "Saved successfully at $filePath",
                        actionLabel = null,
                        duration = SnackbarDuration.Long
                    )
                }
                changeOverflowMenuVisibility(false)
            }
        ) {
            Text("Export Subject Stats")
        }
        DropdownMenuItem(
            onClick = {
                val filePath = classAttendanceViewModel.writeLogsStatsToExcel(context, classAttendanceViewModel.logs.value.data!!)
                coroutineScope.launch{
                    snackbarHostState.showSnackbar(
                        message = "Saved successfully at $filePath",
                        actionLabel = null,
                        duration = SnackbarDuration.Long
                    )
                }
                changeOverflowMenuVisibility(false)
            }
        ) {
            Text("Export Log Stats")
        }
    }
}