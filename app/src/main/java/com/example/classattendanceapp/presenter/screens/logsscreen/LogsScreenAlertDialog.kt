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
import com.example.classattendanceapp.domain.models.ModifiedLogs
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
    logToEdit: ModifiedLogs?,
    resetLogToEdit: ()->Unit
){

    val uiState = remember{
        LogsScreenAlertDialogUiState()
    }

    val localLog by remember{
        mutableStateOf(logToEdit ?: ModifiedLogs())
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isInitialLogDataRetrievalDone = classAttendanceViewModel.isInitialLogDataRetrievalDone.collectAsStateWithLifecycle()

    val selectedYear by classAttendanceViewModel.currentYear.collectAsStateWithLifecycle()
    val selectedMonth by classAttendanceViewModel.currentMonth.collectAsStateWithLifecycle()
    val selectedDay by classAttendanceViewModel.currentDay.collectAsStateWithLifecycle()
    val selectedHour by classAttendanceViewModel.currentHour.collectAsStateWithLifecycle()
    val selectedMinute by classAttendanceViewModel.currentMinute.collectAsStateWithLifecycle()

    val datePickerDialogState =
        remember{
            mutableStateOf(DatePickerDialog(
                context,
                { _: DatePicker, year, month, day ->
                    classAttendanceViewModel.changeCurrentYear(year)
                    classAttendanceViewModel.changeCurrentMonth(month)
                    classAttendanceViewModel.changeCurrentDay(day)
                },
                logToEdit?.year ?: selectedYear,
                logToEdit?.monthNumber  ?: selectedMonth,
                logToEdit?.date ?: selectedDay
            ))
        }
    LaunchedEffect(Unit){
        /*
        Populating the empty fields with fields of log to be edited
         */
        if(logToEdit!=null){
            uiState.subjectName = logToEdit.subjectName!!
            uiState.isPresent = logToEdit.wasPresent
            classAttendanceViewModel.changeCurrentYear(logToEdit.year!!)
            classAttendanceViewModel.changeCurrentMonth(logToEdit.monthNumber!!)
            classAttendanceViewModel.changeCurrentDay(logToEdit.date!!)
            classAttendanceViewModel.changeCurrentHour(logToEdit.hour!!)
            classAttendanceViewModel.changeCurrentMinute(logToEdit.minute!!)
        }
    }

    LaunchedEffect(Unit){
        /*
        Changing the DatePickerDialog with latest changed value
         */
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
            resetLogToEdit()
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
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(uiState.subjectName ?: stringResource(R.string.select_subject))
                        if(logToEdit==null){
                            IconButton(
                                onClick = {

                                    uiState.showSubjectListOverflowMenu = true

                                }
                            ) {
                                Icon(
                                    Icons.Filled.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        modifier = Modifier.requiredHeightIn(max=300.dp),
                        expanded = uiState.showSubjectListOverflowMenu,
                        onDismissRequest = {
                            uiState.showSubjectListOverflowMenu = false
                        }
                    ) {
                        val subjectsList = classAttendanceViewModel.subjectsList.collectAsStateWithLifecycle()
                        val isInitialSubjectDataRetrievalDone = classAttendanceViewModel.isInitialSubjectDataRetrievalDone.collectAsState()
                        if(subjectsList.value.isNotEmpty()){
                            subjectsList.value.forEach{ subject->
                                DropdownMenuItem(
                                    onClick = {
                                        localLog.subjectId = subject._id

                                        uiState.subjectName = subject.subjectName /* To show user selected subject */

                                        localLog.subjectName = subject.subjectName /* To enter into the database */
                                        uiState.showSubjectListOverflowMenu = false
                                    }
                                ) {
                                    Text(subject.subjectName)
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
                            selected = uiState.isPresent,
                            onClick = {
                                uiState.isPresent = true /* To show user is Present */
                                localLog.wasPresent = true /* To enter into database */
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
                            selected = !uiState.isPresent,
                            onClick = {
                                uiState.isPresent = false /* To show user isAbsent */
                                localLog.wasPresent = false /* To enter into database */
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
                            "${DateToSimpleFormat.getMonthStringFromNumber(selectedMonth)} ${selectedDay}, ${selectedYear}"
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
                                if (selectedHour < 10) {
                                    "0${selectedHour}"
                                } else {
                                    selectedHour
                                }
                            }:${
                                if (selectedMinute < 10) {
                                    "0${selectedMinute}"
                                } else {
                                    selectedMinute
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
                            selectedHour,
                            selectedMinute,
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
                                localLog.subjectName!=null
                            ){
                                val logsTime = Calendar.getInstance()
                                logsTime.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0)

                                if(logToEdit == null){
                                    classAttendanceViewModel.insertLogs(
                                        Log(
                                            _id = 0,
                                            subjectId = localLog.subjectId!!,
                                            subjectName = localLog.subjectName!!,
                                            timestamp = logsTime.time,
                                            wasPresent = localLog.wasPresent,
                                            latitude = null,
                                            longitude = null,
                                            distance = null
                                        )
                                    )

                                }else{
                                    classAttendanceViewModel.updateLog(
                                        Log(
                                            _id = localLog._id!!,
                                            subjectId = localLog.subjectId!!,
                                            subjectName = localLog.subjectName!!,
                                            timestamp = logsTime.time,
                                            wasPresent = localLog.wasPresent,
                                            latitude = null,
                                            longitude = null,
                                            distance = null
                                        )
                                    )
                                }
                                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                                resetLogToEdit()
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
                        resetLogToEdit()
                        classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}