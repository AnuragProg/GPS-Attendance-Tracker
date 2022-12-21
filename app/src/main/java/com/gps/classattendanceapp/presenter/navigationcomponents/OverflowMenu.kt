package com.gps.classattendanceapp.presenter.navigationcomponents

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook

@Composable
fun OverflowMenu(
    classAttendanceViewModel: ClassAttendanceViewModel,
    isOverflowMenuVisible: Boolean,
    changeOverflowMenuVisibility: (Boolean) -> Unit,
    snackbarHostState: SnackbarHostState
){

    val context = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    val mimeType = remember{"application/vnd.ms-excel"}

    var workbook by remember{mutableStateOf<HSSFWorkbook?>(null)}
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if(it.resultCode == RESULT_OK){
                val outputStream = context.contentResolver.openOutputStream(it.data!!.data!!)
                workbook!!.write(outputStream)
                outputStream?.flush()
                outputStream?.close()
            }
        }
    )

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
                val resultWorkbook = classAttendanceViewModel.writeSubjectsStatsToExcel(context, classAttendanceViewModel.subjects.value.data!!)
                val intent = createIntentForSavingXlsFile("Subject.xls")
                workbook = resultWorkbook
                launcher.launch(intent)
                changeOverflowMenuVisibility(false)
            }
        ) {
            Text("Export Subject Stats")
        }
        DropdownMenuItem(
            onClick = {
                val resultWorkbook = classAttendanceViewModel.writeLogsStatsToExcel(context, classAttendanceViewModel.logs.value.data!!)
                val intent = createIntentForSavingXlsFile("Logs.xls")
                workbook = resultWorkbook
                launcher.launch(intent)
                changeOverflowMenuVisibility(false)
            }
        ) {
            Text("Export Log Stats")
        }
    }
}


fun createIntentForSavingXlsFile(filename: String):Intent{
    return Intent(Intent.ACTION_CREATE_DOCUMENT).apply{
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/vnd.ms-excel"
        putExtra(Intent.EXTRA_TITLE, filename)
    }
}