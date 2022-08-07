@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.logsscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.classattendanceapp.R
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){
    val logsList = classAttendanceViewModel.logsList.collectAsState()

    val isInitialLogDataRetrievalDone = classAttendanceViewModel.isInitialLogDataRetrievalDone.collectAsState()

    val showAddLogsAlertDialog = classAttendanceViewModel.floatingButtonClicked.collectAsState()

    val searchBarText = classAttendanceViewModel.searchBarText.collectAsState()

    var subjectIdInAlertDialog by remember{
        mutableStateOf<Int?>(null)
    }

    var subjectNameInAlertDialog by remember{
        mutableStateOf<String?>(null)
    }

    var presentOrAbsentInAlertDialog by remember{
        mutableStateOf<String?>(null)
    }

    // null -> no editing to be done
    // id of log -> editing to be done of log with given id
    var editingLog by remember{
        mutableStateOf<Int?>(null)
    }


    val coroutineScope = rememberCoroutineScope()


    // Making Log Dialog Box
    if(showAddLogsAlertDialog.value){
        LogsScreenAlertDialog(
            classAttendanceViewModel = classAttendanceViewModel,
            initialSubjectNameInAlertDialog = subjectNameInAlertDialog,
            initialSubjectIdInAlertDialog = subjectIdInAlertDialog,
            initialEditingLog = editingLog
        )
    }


    if(logsList.value.isEmpty() && isInitialLogDataRetrievalDone.value){
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
                text = stringResource(R.string.no_logs),
                color = Color.White
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
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = true,
            ){
                items(
                    logsList.value.filter{
                        if(searchBarText.value.isNotBlank()){
                            searchBarText.value.lowercase() in it.subjectName.lowercase()
                        }else{
                            true
                        }
                    }.size
                ){
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


