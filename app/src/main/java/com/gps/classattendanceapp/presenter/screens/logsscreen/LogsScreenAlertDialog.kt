@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.gps.classattendanceapp.presenter.screens.logsscreen

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.data.models.Log
import com.gps.classattendanceapp.domain.models.ModifiedLogs
import com.gps.classattendanceapp.presenter.utils.DateToSimpleFormat
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
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
        mutableStateOf(logToEdit ?: com.gps.classattendanceapp.domain.models.ModifiedLogs())
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    val selectedYear by classAttendanceViewModel.currentYear.collectAsStateWithLifecycle()
    val selectedMonth by classAttendanceViewModel.currentMonth.collectAsStateWithLifecycle()
    val selectedDay by classAttendanceViewModel.currentDay.collectAsStateWithLifecycle()
    val selectedHour by classAttendanceViewModel.currentHour.collectAsStateWithLifecycle()
    val selectedMinute by classAttendanceViewModel.currentMinute.collectAsStateWithLifecycle()

    val datePickerDialogState =
        remember{
            mutableStateOf(
                DatePickerDialog(
                    context,

                    { _: DatePicker, year, month, day ->
                        classAttendanceViewModel.changeCurrentYear(year)
                        classAttendanceViewModel.changeCurrentMonth(month)
                        classAttendanceViewModel.changeCurrentDay(day)
                    },
                    logToEdit?.year ?: selectedYear,
                    logToEdit?.monthNumber  ?: selectedMonth,
                    logToEdit?.date ?: selectedDay
                )
            )
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
        shape = RoundedCornerShape(10.dp),
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
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        uiState.showSubjectListOverflowMenu = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(uiState.subjectName)
//                        if(logToEdit==null){
//                            IconButton(
//                                onClick = {
//                                    uiState.showSubjectListOverflowMenu = true
//                                }
//                            ) {
//                                Icon(
//                                    modifier = Modifier.rotate(
//                                        if(uiState.showSubjectListOverflowMenu) 180f
//                                        else 0f
//                                    ),
//                                    imageVector = Icons.Filled.ArrowDropDown,
//                                    contentDescription = null
//                                )
//                            }
//                        }
                    }

                    DropdownMenu(
                        modifier = Modifier.requiredHeightIn(max=300.dp),
                        expanded = uiState.showSubjectListOverflowMenu,
                        onDismissRequest = {
                            uiState.showSubjectListOverflowMenu = false
                        }
                    ) {
                        val subjectsList = classAttendanceViewModel.subjects.collectAsStateWithLifecycle()

                        when(subjectsList.value){
                            is Resource.Error -> {}
                            is Resource.Loading -> {
                                CircularProgressIndicator()
                            }
                            is Resource.Success -> {
                                if(subjectsList.value.data?.size == 0){
                                    Text(
                                        modifier = Modifier.padding(10.dp),
                                        text = stringResource(R.string.no_subject_to_select_from)
                                    )
                                }else{
                                    subjectsList.value.data!!.forEach { subject ->
                                        DropdownMenuItem(
                                            onClick = {
                                                localLog.subjectId = subject._id

                                                uiState.subjectName =
                                                    subject.subjectName /* To show user selected subject */

                                                localLog.subjectName =
                                                    subject.subjectName /* To enter into the database */
                                                uiState.showSubjectListOverflowMenu = false
                                            }
                                        ) {
                                            Text(subject.subjectName)
                                        }
                                    }
                                }
                            }
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
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Gray
                            )
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
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Gray
                            )
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
                            datePickerDialogState.value
                                .show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text(
                            "${DateToSimpleFormat.getMonthStringFromNumber(selectedMonth)} ${selectedDay}, ${selectedYear}"
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedButton(
                        onClick = {
                            timePickerDialogState.show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        )
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
                            },
                            textStyle = TextStyle(color = Color.Gray)
                        )
                        negativeButton(
                            stringResource(R.string.cancel),
                            onClick = {
                                timePickerDialogState.hide()
                            },
                            textStyle = TextStyle(color = Color.Gray)

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
                        ),
                        colors = TimePickerDefaults.colors(
                            activeBackgroundColor = Color.DarkGray,
                            inactiveBackgroundColor = Color.LightGray,
                            selectorColor = Color.White,
                            selectorTextColor = Color.Black,
                        ),
                        title = "Select Time"
                    ){
                        classAttendanceViewModel.changeCurrentHour(it.hour)
                        classAttendanceViewModel.changeCurrentMinute(it.minute)
                    }
                }

            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.End
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
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text(stringResource(R.string.log))
                }

                Spacer(modifier = Modifier.width(10.dp))

                TextButton(
                    onClick = {
                        resetLogToEdit()
                        classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}