package com.example.classattendanceapp.presenter.screens.logsscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.classattendanceapp.R
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogCard(

    changeSubjectIdInAlertDialog : (Int?)->Unit,
    changeSubjectNameInAlertDialog : (String?)->Unit,
    changeEditingLog: (Int?)->Unit,
    log: ModifiedLogs,
    classAttendanceViewModel: ClassAttendanceViewModel,
){
    var showAdditionalCardDetails by remember{
        mutableStateOf(false)
    }
    var showOverFlowMenu by remember{
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                    text = log.subjectName,
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
                            text = log.day + " | " + log.month + " " + log.date.toString() + "," + log.year.toString(),
                        )
                        Text(
                            when (log.wasPresent) {
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
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Distance: ")

                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            "${log.month} ${log.date}, ${log.year}"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "${
                                if (log.hour < 10) "0${log.hour}"
                                else log.hour
                            }:${
                                if (log.minute < 10) "0${log.minute}"
                                else log.minute
                            }"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            log.day
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            when (log.wasPresent) {
                                true -> stringResource(R.string.present)
                                else -> stringResource(R.string.absent)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            log.latitude?.let{
                                String.format("%.5f",
                                    log.latitude)
                            }?: "Unknown"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            log.longitude?.let{
                                String.format("%.5f",
                                    log.longitude)
                            } ?: "Unknown"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            log.distance?.let{
                                String.format("%.5f",
                                    log.distance)
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
                            classAttendanceViewModel.deleteLogs(log._id)
                        }
                        showOverFlowMenu = false
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
                DropdownMenuItem(
                    onClick = {
                        changeEditingLog(log._id)
                        changeSubjectIdInAlertDialog(log.subjectId)
                        changeSubjectNameInAlertDialog(log.subjectName)
                        classAttendanceViewModel.changeCurrentYear(log.year)
                        classAttendanceViewModel.changeCurrentMonth(log.monthNumber)
                        classAttendanceViewModel.changeCurrentDay(log.date)
                        classAttendanceViewModel.changeCurrentHour(log.hour)
                        classAttendanceViewModel.changeCurrentMinute(log.minute)

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