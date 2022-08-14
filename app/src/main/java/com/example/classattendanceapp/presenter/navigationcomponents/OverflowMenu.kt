package com.example.classattendanceapp.presenter.navigationcomponents

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
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
                val uri = classAttendanceViewModel.writeSubjectsStatsToExcel(context, classAttendanceViewModel.subjectsList.value)
                coroutineScope.launch{
                    val result = snackbarHostState.showSnackbar(
                        "Saved Successfully",
                        "Open"
                    )
                    when(result){
                        SnackbarResult.ActionPerformed -> {
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.setDataAndType(uri, "*/*")
                            context.startActivity(intent)
                        }
                        SnackbarResult.Dismissed -> {

                        }
                    }
                }
                changeOverflowMenuVisibility(false)
            }
        ) {
            Text("Export Subject Stats")
        }
        DropdownMenuItem(
            onClick = {
                val uri = classAttendanceViewModel.writeLogsStatsToExcel(context, classAttendanceViewModel.logsList.value)
                coroutineScope.launch{
                    val result = snackbarHostState.showSnackbar(
                        "Saved Successfully",
                        "Open"
                    )
                    when(result){
                        SnackbarResult.ActionPerformed->{
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.setDataAndType(uri, "*/*")
                            context.startActivity(intent)
                        }
                        SnackbarResult.Dismissed->{

                        }
                    }
                }
                changeOverflowMenuVisibility(false)
            }
        ) {
            Text("Export Log Stats")
        }
    }
}