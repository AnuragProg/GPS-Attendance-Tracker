package com.example.classattendanceapp.presenter.screens.logsscreen

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){
    val context = LocalContext.current

    val logsList = classAttendanceViewModel.logsList.collectAsState()

    val isInitialLogDataRetrievalDone = classAttendanceViewModel.isInitialLogDataRetrievalDone.collectAsState()

    val showAddLogsAlertDialog = classAttendanceViewModel.floatingButtonClicked.collectAsState()

    var showAddLogsSubjectNameAlertDialog by remember{
        mutableStateOf(false)
    }

    var subjectIdInAlertDialog by remember{
        mutableStateOf<Int?>(null)
    }

    var subjectNameInAlertDialog by remember{
        mutableStateOf<String?>(null)
    }

    var presentOrAbsentInAlertDialog by remember{
        mutableStateOf<String?>(null)
    }

    var showPresentOrAbsentAlertDialog by remember{
        mutableStateOf(false)
    }

    // null -> no editing to be done
    // id of log -> editing to be done of log with given id
    var editingLog by remember{
        mutableStateOf<Int?>(null)
    }



    val selectedYear = classAttendanceViewModel.currentYear.collectAsState()
    val selectedMonth = classAttendanceViewModel.currentMonth.collectAsState()
    val selectedDay = classAttendanceViewModel.currentDay.collectAsState()
    val selectedHour = classAttendanceViewModel.currentHour.collectAsState()
    val selectedMinute = classAttendanceViewModel.currentMinute.collectAsState()


    val coroutineScope = rememberCoroutineScope()


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


    // Making Log Dialog Box
    if(showAddLogsAlertDialog.value){

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


    if(logsList.value.isEmpty() && isInitialLogDataRetrievalDone.value){
        Log.d("logs", "showing icons for no logs")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(id = R.drawable.logs),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(5.dp),
                text = stringResource(R.string.no_logs)
            )
        }
    } else if(logsList.value.isEmpty() && !isInitialLogDataRetrievalDone.value){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center

        ){
            CircularProgressIndicator()
        }
    }else{
        Log.d("logs", "showing logs instead")
        // Original UI
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = true,
            ){
                items(logsList.value.size){
                    val currentIndex = logsList.value.size - 1 - it
                    var showOverFlowMenu by remember{ mutableStateOf(false) }
                    var showAdditionalCardDetails by remember{ mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .combinedClickable(
                                onClick = {
                                    showAdditionalCardDetails = !showAdditionalCardDetails
                                },
                                onLongClick = {
                                    showOverFlowMenu = true
                                }
                            )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Row(
                                modifier = Modifier
                                    .height(60.dp)
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = if(showAdditionalCardDetails) Modifier.fillMaxWidth() else Modifier.width(80.dp),
                                    text = logsList.value[currentIndex].subjectName,
                                    overflow = if (!showAdditionalCardDetails) {
                                        TextOverflow.Ellipsis
                                    } else {
                                        TextOverflow.Visible
                                    },
                                    maxLines = 1
                                )
                                AnimatedVisibility(
                                    visible = !showAdditionalCardDetails
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Text(
                                            text = logsList.value[currentIndex].day + " | " + logsList.value[currentIndex].month + " " + logsList.value[currentIndex].date.toString() + "," + logsList.value[currentIndex].year.toString(),
                                        )
                                        Text(
                                            when (logsList.value[currentIndex].wasPresent) {
                                                true -> stringResource(R.string.present)
                                                else -> stringResource(R.string.absent)
                                            }
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = showAdditionalCardDetails
                            ) {

                                Box(
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text("Date : ")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Time :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Day :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Status : ")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Latitude :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Longitude :")

                                    }
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            "${logsList.value[currentIndex].month} ${logsList.value[currentIndex].date}, ${logsList.value[currentIndex].year}"
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            "${
                                                if (logsList.value[currentIndex].hour < 10) "0${logsList.value[currentIndex].hour}"
                                                else logsList.value[currentIndex].hour
                                            }:${
                                                if (logsList.value[currentIndex].minute < 10) "0${logsList.value[currentIndex].minute}"
                                                else logsList.value[currentIndex].minute
                                            }"
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            logsList.value[currentIndex].day
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            when (logsList.value[currentIndex].wasPresent) {
                                                true -> stringResource(R.string.present)
                                                else -> stringResource(R.string.absent)
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            logsList.value[currentIndex].latitude?.let{
                                                String.format("%.5f",
                                                    logsList.value[currentIndex].latitude)
                                            }?: "Unknown"
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            logsList.value[currentIndex].longitude?.let{
                                                String.format("%.5f",
                                                    logsList.value[currentIndex].longitude)
                                            } ?: "Unknown"
                                        )
                                    }
                                }
                            }
                        }
                        if (showOverFlowMenu) {
                            DropdownMenu(
                                expanded = showOverFlowMenu,
                                onDismissRequest = {
                                    showOverFlowMenu = false
                                }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        coroutineScope.launch {
                                            classAttendanceViewModel.deleteLogs(logsList.value[currentIndex]._id)
                                        }
                                        showOverFlowMenu = false
                                    }
                                ) {
                                    Text(stringResource(R.string.delete))
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        editingLog = logsList.value[currentIndex]._id

                                        subjectIdInAlertDialog =
                                            logsList.value[currentIndex].subjectId
                                        subjectNameInAlertDialog =
                                            logsList.value[currentIndex].subjectName
                                        presentOrAbsentInAlertDialog =
                                            if (logsList.value[currentIndex].wasPresent) "Present" else "Absent"
                                        classAttendanceViewModel.changeCurrentYear(logsList.value[currentIndex].year)
                                        classAttendanceViewModel.changeCurrentMonth(logsList.value[currentIndex].monthNumber)
                                        classAttendanceViewModel.changeCurrentDay(logsList.value[currentIndex].date)
                                        classAttendanceViewModel.changeCurrentHour(logsList.value[currentIndex].hour)
                                        classAttendanceViewModel.changeCurrentMinute(logsList.value[currentIndex].minute)

                                        classAttendanceViewModel.changeFloatingButtonClickedState(
                                            state = true,
                                            doNotMakeChangesToTime = true
                                        )

                                        showOverFlowMenu = false
                                    }
                                ) {
                                    Text(stringResource(R.string.edit))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


