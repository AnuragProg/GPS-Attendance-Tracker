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
import com.gps.classattendanceapp.components.PATH_TO_SAVE_EXCEL_FILE
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
                val uri = classAttendanceViewModel.writeSubjectsStatsToExcel(context, classAttendanceViewModel.subjects.value.data!!)
                Log.d("excel", "Uri is $uri")
                coroutineScope.launch{
                    val result = snackbarHostState.showSnackbar(
                        "Saved Successfully",
                        "Open"
                    )
                    when(result){
                        SnackbarResult.ActionPerformed -> {
                            try{
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(uri, "application/vnd.ms-excel")
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(intent)

                            }catch(e: ActivityNotFoundException){
                                Toast.makeText(context, "No application available to view excel file!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(Uri.parse(PATH_TO_SAVE_EXCEL_FILE), "*/*")
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(intent)
                            }
                        }
                        SnackbarResult.Dismissed -> {}
                    }
                }
                changeOverflowMenuVisibility(false)
            }
        ) {
            Text("Export Subject Stats")
        }
        DropdownMenuItem(
            onClick = {
                val uri = classAttendanceViewModel.writeLogsStatsToExcel(context, classAttendanceViewModel.logs.value.data!!)
                coroutineScope.launch{
                    val result = snackbarHostState.showSnackbar(
                        "Saved Successfully",
                        "Open"
                    )
                    when(result){
                        SnackbarResult.ActionPerformed->{
                            try{
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(uri, "application/vnd.ms-excel")
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(intent)
                            }catch(e: ActivityNotFoundException){
                                Toast.makeText(context, "No application available to view excel file!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(Uri.parse(PATH_TO_SAVE_EXCEL_FILE), "*/*")
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(intent)
                            }
                        }
                        SnackbarResult.Dismissed->{}
                    }
                }
                changeOverflowMenuVisibility(false)
            }
        ) {
            Text("Export Log Stats")
        }
    }
}