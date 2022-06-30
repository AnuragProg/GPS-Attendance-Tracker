package com.example.classattendanceapp.presenter.screens.logsscreen

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch
import java.util.*



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    val logsList = classAttendanceViewModel.logsList.collectAsState()

    val isInitialLogDataRetrievalDone = classAttendanceViewModel.isInitialLogDataRetrievalDone.collectAsState()

    val showAddLogsAlertDialog = classAttendanceViewModel.floatingButtonClicked.collectAsState()

    var showAddLogsSubjectNameAlertDialog by remember{
        mutableStateOf(false)
    }

    var subjectInAlertDialog by remember{
        mutableStateOf<ModifiedSubjects?>(null)
    }

    var presentOrAbsentInAlertDialog by remember{
        mutableStateOf<String?>(null)
    }

    var showPresentOrAbsentAlertDialog by remember{
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()


    // Making Log Dialog Box
    if(showAddLogsAlertDialog.value){
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                presentOrAbsentInAlertDialog = null
                subjectInAlertDialog = null
            },
            text = {
                Column{
                    Text(
                        text = "Add Log",
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
                            Text(subjectInAlertDialog?.subjectName ?: "Select Subject")
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
                                            subjectInAlertDialog = it
                                            showAddLogsSubjectNameAlertDialog = false
                                        }
                                    ) {
                                        Text(it.subjectName)
                                    }
                                }
                            }else if(subjectsList.value.isEmpty() && isInitialLogDataRetrievalDone.value){
                                Text("No Subjects to select from!!")
                            }
                        }
                    }

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
                            Text(presentOrAbsentInAlertDialog ?: "Present/Absent")
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
                                "Present",
                                "Absent"
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

                }
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                if(subjectInAlertDialog!=null){
                                    classAttendanceViewModel.insertLogs(
                                        Logs(
                                            0,
                                            subjectInAlertDialog!!._id,
                                            subjectInAlertDialog!!.subjectName,
                                            Calendar.getInstance().time,
                                            presentOrAbsentInAlertDialog == "Present"
                                        )
                                    )
                                }
                                subjectInAlertDialog = null
                                presentOrAbsentInAlertDialog = "Absent"
                                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                            }
                        }
                    ) {
                        Text("Log")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    TextButton(
                        onClick = {
                            subjectInAlertDialog = null
                            presentOrAbsentInAlertDialog = "Absent"
                            classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                        }
                    ) {
                        Text("Cancel")
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
                text = "No Logs"
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(10.dp)
                    ){
                        var showOverFlowMenu by remember{ mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ){
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ){
                                Text(
                                    modifier = Modifier.width(80.dp),
                                    text = logsList.value[currentIndex].subjectName,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ){

                                Text(
                                    text = logsList.value[currentIndex].day + " | " + logsList.value[currentIndex].month + " " + logsList.value[currentIndex].date.toString() + "," + logsList.value[currentIndex].year.toString(),
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            showOverFlowMenu = true
                                        }
                                    ),
                                contentAlignment = Alignment.CenterEnd
                            ){
                                Text(
                                    when(logsList.value[currentIndex].wasPresent){
                                        true -> "Present"
                                        else -> "Absent"
                                    }
                                )
                            }
                        }
                        if(showOverFlowMenu){
                            DropdownMenu(
                                expanded = showOverFlowMenu,
                                onDismissRequest = {
                                    showOverFlowMenu = false
                                }
                            ) {
                                TextButton(
                                    onClick = {
                                        coroutineScope.launch{
                                            classAttendanceViewModel.deleteLogs(logsList.value[currentIndex]._id)
                                        }
                                        showOverFlowMenu = false
                                    }
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


