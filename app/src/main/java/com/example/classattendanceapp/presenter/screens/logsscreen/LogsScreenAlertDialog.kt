package com.example.classattendanceapp.presenter.screens.logsscreen

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.presenter.utils.DateToSimpleFormat
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.*

@Composable
fun LogsScreenAlertDialog(
    classAttendanceViewModel: ClassAttendanceViewModel,
    initialSubjectNameInAlertDialog: String? = null,
    initialSubjectIdInAlertDialog: Int? = null,
    initialPresentOrAbsentInAlertDialog: String? = null,
    initialEditingLog: Int? = null
){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isInitialLogDataRetrievalDone = classAttendanceViewModel.isInitialLogDataRetrievalDone.collectAsState()

    val selectedYear = classAttendanceViewModel.currentYear.collectAsState()
    val selectedMonth = classAttendanceViewModel.currentMonth.collectAsState()
    val selectedDay = classAttendanceViewModel.currentDay.collectAsState()
    val selectedHour = classAttendanceViewModel.currentHour.collectAsState()
    val selectedMinute = classAttendanceViewModel.currentMinute.collectAsState()

    var showAddLogsSubjectNameAlertDialog by remember{
        mutableStateOf(false)
    }

    var showPresentOrAbsentAlertDialog by remember{
        mutableStateOf(false)
    }

    var subjectIdInAlertDialog by remember{
        mutableStateOf(initialSubjectIdInAlertDialog)
    }

    var subjectNameInAlertDialog by remember{
        mutableStateOf(initialSubjectNameInAlertDialog)
    }

    var presentOrAbsentInAlertDialog by remember{
        mutableStateOf(initialPresentOrAbsentInAlertDialog)
    }


    // null -> no editing to be done
    // id of log -> editing to be done of log with given id
    var editingLog by remember{
        mutableStateOf(initialEditingLog)
    }

    val datePickerDialogState =
        remember{
            mutableStateOf(DatePickerDialog(
                context,
                { _: DatePicker, year, month, day ->
                    classAttendanceViewModel.changeCurrentYear(year)
                    classAttendanceViewModel.changeCurrentMonth(month)
                    classAttendanceViewModel.changeCurrentDay(day)
                },
                selectedYear.value,
                selectedMonth.value,
                selectedDay.value
            ))
        }

    LaunchedEffect(Unit){
        combine(
            classAttendanceViewModel.currentDay,
            classAttendanceViewModel.currentMonth,
            classAttendanceViewModel.currentYear,
        ){ day, month, year ->
            Triple(day, month, year)
        }.collectLatest{
            datePickerDialogState.value = DatePickerDialog(
                context,
                {_:DatePicker, year, month, day ->
                    classAttendanceViewModel.changeCurrentYear(year)
                    classAttendanceViewModel.changeCurrentMonth(month)
                    classAttendanceViewModel.changeCurrentDay(day)
                },
                it.third,
                it.second,
                it.first,
            )
        }
    }

    val timePickerDialogState = rememberMaterialDialogState()

    AlertDialog(
        onDismissRequest = {
            classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
            presentOrAbsentInAlertDialog = null
            subjectIdInAlertDialog = null
            subjectNameInAlertDialog = null
            editingLog = null
        },
        text = {
            Column{
                Text(
                    text = stringResource(R.string.add_log),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(5.dp))

                OutlinedButton(
                    onClick = {
                        showAddLogsSubjectNameAlertDialog = true
                    }
                ) {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(subjectNameInAlertDialog ?: stringResource(R.string.select_subject))
                        IconButton(
                            onClick = {
                                showAddLogsSubjectNameAlertDialog = true
                            }
                        ) {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Open subjects"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showAddLogsSubjectNameAlertDialog,
                        onDismissRequest = {
                            showAddLogsSubjectNameAlertDialog = false
                        }
                    ) {
                        val subjectsList = classAttendanceViewModel.subjectsList.collectAsState()
                        val isInitialSubjectDataRetrievalDone = classAttendanceViewModel.isInitialSubjectDataRetrievalDone.collectAsState()
                        if(subjectsList.value.isNotEmpty()){
                            subjectsList.value.forEach{
                                DropdownMenuItem(
                                    onClick = {
                                        subjectIdInAlertDialog = it._id
                                        subjectNameInAlertDialog = it.subjectName
                                        showAddLogsSubjectNameAlertDialog = false
                                    }
                                ) {
                                    Text(it.subjectName)
                                }
                            }
                        }else if(subjectsList.value.isEmpty() && isInitialLogDataRetrievalDone.value){
                            Text(stringResource(R.string.no_subject_to_select_from))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        showPresentOrAbsentAlertDialog = true
                    }
                ) {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(presentOrAbsentInAlertDialog ?: stringResource(R.string.present_absent))
                        IconButton(
                            onClick = {
                                showPresentOrAbsentAlertDialog = true
                            }
                        ) {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Open present or absent menu"
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = showPresentOrAbsentAlertDialog,
                        onDismissRequest = {
                            showPresentOrAbsentAlertDialog = false
                        }
                    ) {
                        listOf(
                            stringResource(R.string.present),
                            stringResource(R.string.absent)
                        ).forEach{
                            DropdownMenuItem(
                                onClick = {
                                    presentOrAbsentInAlertDialog = it
                                    showPresentOrAbsentAlertDialog = false
                                }
                            ) {
                                Text(it)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        datePickerDialogState.value.show()
                    }
                ) {
                    Text(
                        "${DateToSimpleFormat.getMonthStringFromNumber(selectedMonth.value)} ${selectedDay.value}, ${selectedYear.value}"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        timePickerDialogState.show()
                    }
                ) {
                    Text(
                        "${
                            if(selectedHour.value<10){
                                "0${selectedHour.value}"
                            }else{
                                selectedHour.value
                            }}:${
                            if(selectedMinute.value<10){
                                "0${selectedMinute.value}"
                            }else{
                                selectedMinute.value
                            }
                        }")
                }

                MaterialDialog(
                    dialogState = timePickerDialogState,
                    buttons = {
                        positiveButton(
                            stringResource(R.string.ok),
                            onClick = {
                                timePickerDialogState.hide()
                            }
                        )
                        negativeButton(
                            stringResource(R.string.cancel),
                            onClick = {
                                timePickerDialogState.hide()
                            }
                        )
                    },
                    onCloseRequest = {
                        timePickerDialogState.hide()
                    }
                ){
                    timepicker(
                        is24HourClock = true,
                        initialTime = LocalTime.of(
                            selectedHour.value,
                            selectedMinute.value,
                            0,
                            0
                        )
                    ){
                        classAttendanceViewModel.changeCurrentHour(it.hour)
                        classAttendanceViewModel.changeCurrentMinute(it.minute)
                    }
                }

            }
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            if(
                                subjectNameInAlertDialog!=null
                                &&
                                presentOrAbsentInAlertDialog !=null
                            ){
                                val logsTime = Calendar.getInstance()
                                logsTime.set(selectedYear.value, selectedMonth.value, selectedDay.value, selectedHour.value, selectedMinute.value, 0)
                                if(editingLog == null){
                                    classAttendanceViewModel.insertLogs(
                                        Logs(
                                            0,
                                            subjectIdInAlertDialog!!,
                                            subjectNameInAlertDialog!!,
                                            logsTime.time,
                                            presentOrAbsentInAlertDialog == "Present"
                                        )
                                    )
                                }else{
                                    classAttendanceViewModel.updateLog(
                                        Logs(
                                            editingLog!!,
                                            subjectIdInAlertDialog!!,
                                            subjectNameInAlertDialog!!,
                                            logsTime.time,
                                            presentOrAbsentInAlertDialog == "Present"
                                        )
                                    )
                                }
                            }
                            subjectIdInAlertDialog = null
                            subjectNameInAlertDialog = null
                            presentOrAbsentInAlertDialog = null
                            editingLog = null
                            classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                        }
                    }
                ) {
                    Text(stringResource(R.string.log))
                }

                Spacer(modifier = Modifier.width(10.dp))

                TextButton(
                    onClick = {
                        subjectIdInAlertDialog = null
                        subjectNameInAlertDialog = null
                        presentOrAbsentInAlertDialog = null
                        editingLog = null
                        classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}