package com.example.classattendanceapp.presenter.screens.logsscreen

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.models.Log
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

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun LogsScreenAlertDialog(
    classAttendanceViewModel: ClassAttendanceViewModel,
    initialSubjectNameInAlertDialog: String? = null,
    initialSubjectIdInAlertDialog: Int? = null,
    initialEditingLog: Int? = null
){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isInitialLogDataRetrievalDone = classAttendanceViewModel.isInitialLogDataRetrievalDone.collectAsStateWithLifecycle()

    val selectedYear = classAttendanceViewModel.currentYear.collectAsStateWithLifecycle()
    val selectedMonth = classAttendanceViewModel.currentMonth.collectAsStateWithLifecycle()
    val selectedDay = classAttendanceViewModel.currentDay.collectAsStateWithLifecycle()
    val selectedHour = classAttendanceViewModel.currentHour.collectAsStateWithLifecycle()
    val selectedMinute = classAttendanceViewModel.currentMinute.collectAsStateWithLifecycle()

    var showAddLogsSubjectNameAlertDialog by remember{
        mutableStateOf(false)
    }

    var subjectIdInAlertDialog by remember{
        mutableStateOf(initialSubjectIdInAlertDialog)
    }

    var subjectNameInAlertDialog by remember{
        mutableStateOf(initialSubjectNameInAlertDialog)
    }

    var isPresent by remember{
        mutableStateOf(true)
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
            subjectIdInAlertDialog = null
            subjectNameInAlertDialog = null
            editingLog = null
        },
        text = {
            Column{
                Text(
                    text = stringResource(R.string.add_log),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

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
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenu(
                        modifier = Modifier.requiredHeightIn(max=300.dp),
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

                Row{
                    Row{
                        RadioButton(
                            selected = isPresent,
                            onClick = {
                                isPresent = true
                            }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            modifier = Modifier.align(
                                Alignment.CenterVertically
                            ),
                            text = stringResource(R.string.present)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Row{
                        RadioButton(
                            selected = !isPresent,
                            onClick = {
                                isPresent = false
                            }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = stringResource(R.string.absent)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    OutlinedButton(
                        onClick = {
                            datePickerDialogState.value.show()
                        }
                    ) {
                        Text(
                            "${DateToSimpleFormat.getMonthStringFromNumber(selectedMonth.value)} ${selectedDay.value}, ${selectedYear.value}"
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedButton(
                        onClick = {
                            timePickerDialogState.show()
                        }
                    ) {
                        Text(
                            "${
                                if (selectedHour.value < 10) {
                                    "0${selectedHour.value}"
                                } else {
                                    selectedHour.value
                                }
                            }:${
                                if (selectedMinute.value < 10) {
                                    "0${selectedMinute.value}"
                                } else {
                                    selectedMinute.value
                                }
                            }")
                    }
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
                            ){
                                val logsTime = Calendar.getInstance()
                                logsTime.set(selectedYear.value, selectedMonth.value, selectedDay.value, selectedHour.value, selectedMinute.value, 0)
                                if(editingLog == null){
                                    classAttendanceViewModel.insertLogs(
                                        Log(
                                            0,
                                            subjectIdInAlertDialog!!,
                                            subjectNameInAlertDialog!!,
                                            logsTime.time,
                                            isPresent
                                        )
                                    )

                                }else{
                                    classAttendanceViewModel.updateLog(
                                        Log(
                                            editingLog!!,
                                            subjectIdInAlertDialog!!,
                                            subjectNameInAlertDialog!!,
                                            logsTime.time,
                                            isPresent
                                        )
                                    )
                                }
                                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                                subjectIdInAlertDialog = null
                                subjectNameInAlertDialog = null
                                isPresent = true
                                editingLog = null
                            }else{
                                Toast.makeText(context, "Subject Name cannot be empty!", Toast.LENGTH_SHORT).show()
                            }
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
                        isPresent = true
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