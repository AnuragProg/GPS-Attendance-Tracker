@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.logsscreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.excel.Excel
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



@OptIn(ExperimentalFoundationApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun LogsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){
    val logsList = classAttendanceViewModel.logsList.collectAsStateWithLifecycle()

    val isInitialLogDataRetrievalDone = classAttendanceViewModel.isInitialLogDataRetrievalDone.collectAsStateWithLifecycle()

    val showAddLogsAlertDialog = classAttendanceViewModel.floatingButtonClicked.collectAsStateWithLifecycle()

    val searchBarText = classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle()

    val lazyScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0
    )

    var subjectIdInAlertDialog by remember{
        mutableStateOf<Int?>(null)
    }

    var subjectNameInAlertDialog by remember{
        mutableStateOf<String?>(null)
    }

    // null -> no editing to be done
    // id of log -> editing to be done of log with given id
    var editingLog by remember{
        mutableStateOf<Int?>(null)
    }

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit){
        classAttendanceViewModel.logsList.collectLatest{
            if(it.isNotEmpty()){
                lazyScrollState.scrollToItem(it.size-1)
            }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit){
        val logsList = classAttendanceViewModel.logsList.value
        Log.d("excel", "Starting calling")
        val excel = Excel()
        excel.writeLogsStatsToExcel(context, logsList)
        Log.d("excel", "Call ended")

    }

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
                state = lazyScrollState
            ){
                items(
                    logsList.value.filter{
                        if(searchBarText.value.isNotBlank()){
                            searchBarText.value.lowercase() in it.subjectName.lowercase()
                        }else{
                            true
                        }
                    },
                    key = {it._id}
                ){ currentLog ->
                    var showOverFlowMenu by remember{ mutableStateOf(false) }
                    var showAdditionalCardDetails by remember{ mutableStateOf(false) }

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
                                    text = currentLog.subjectName,
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
                                            text = currentLog.day + " | " + currentLog.month + " " + currentLog.date.toString() + "," + currentLog.year.toString(),
                                        )
                                        Text(
                                            when (currentLog.wasPresent) {
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
                                            "${currentLog.month} ${currentLog.date}, ${currentLog.year}"
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            "${
                                                if (currentLog.hour < 10) "0${currentLog.hour}"
                                                else currentLog.hour
                                            }:${
                                                if (currentLog.minute < 10) "0${currentLog.minute}"
                                                else currentLog.minute
                                            }"
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            currentLog.day
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            when (currentLog.wasPresent) {
                                                true -> stringResource(R.string.present)
                                                else -> stringResource(R.string.absent)
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            currentLog.latitude?.let{
                                                String.format("%.5f",
                                                    currentLog.latitude)
                                            }?: "Unknown"
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            currentLog.longitude?.let{
                                                String.format("%.5f",
                                                    currentLog.longitude)
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
                                            classAttendanceViewModel.deleteLogs(currentLog._id)
                                        }
                                        showOverFlowMenu = false
                                    }
                                ) {
                                    Text(stringResource(R.string.delete))
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        editingLog = currentLog._id

                                        subjectIdInAlertDialog =
                                            currentLog.subjectId
                                        subjectNameInAlertDialog =
                                            currentLog.subjectName
                                        classAttendanceViewModel.changeCurrentYear(currentLog.year)
                                        classAttendanceViewModel.changeCurrentMonth(currentLog.monthNumber)
                                        classAttendanceViewModel.changeCurrentDay(currentLog.date)
                                        classAttendanceViewModel.changeCurrentHour(currentLog.hour)
                                        classAttendanceViewModel.changeCurrentMinute(currentLog.minute)

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


