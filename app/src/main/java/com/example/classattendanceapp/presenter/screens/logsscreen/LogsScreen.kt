package com.example.classattendanceapp.presenter.screens.logsscreen

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.presenter.utils.ProcessState
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    val logsList = remember{
        mutableStateListOf<ModifiedLogs>()
    }

    val showAddLogsAlertDialog = classAttendanceViewModel.floatingButtonClicked.collectAsState()

    var showAddLogsSubjectNameAlertDialog by remember{
        mutableStateOf(false)
    }

    var subjectInAlertDialog by remember{
        mutableStateOf<ModifiedSubjects?>(null)
    }

    var presentOrAbsentInAlertDialog by remember{
        mutableStateOf("Absent")
    }

    var showPresentOrAbsentAlertDialog by remember{
        mutableStateOf(false)
    }

    var processState by remember{
        mutableStateOf(ProcessState.INITIAL)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        classAttendanceViewModel.getAllLogsAdvanced().collectLatest{
            processState = ProcessState.INITIAL
            logsList.clear()
            Log.d("debugging", "new list is $it")
            logsList.addAll(it)
            processState = ProcessState.DONE
        }
    }

    // Making Log Dialog Box
    if(showAddLogsAlertDialog.value){
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                presentOrAbsentInAlertDialog = "Absent"
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
                    Row(
                        modifier = Modifier.fillMaxWidth()

                    ){
                        OutlinedButton(
                            onClick = {
                                showAddLogsSubjectNameAlertDialog = true
                            }
                        ) {
                            Row(
                                modifier = Modifier.width(120.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(subjectInAlertDialog?.subjectName ?: "")
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
                                val subjectsList = remember{
                                    mutableStateListOf<ModifiedSubjects>()
                                }
                                var subjectFetchingState by remember{
                                    mutableStateOf(ProcessState.INITIAL)
                                }
                                LaunchedEffect(Unit){
                                    Log.d("logs", "fetching one time subject list from db")
                                    subjectsList.clear()
                                    val responseSubjectsList = classAttendanceViewModel.getSubjects().first()
                                    Log.d("logs", "fetching complete with result $responseSubjectsList")
                                    Log.d("logs", "Adding fetched response subjects list to subjectslist")
                                    subjectsList.addAll(responseSubjectsList)
                                    subjectFetchingState = ProcessState.DONE
                                }
                                if(subjectsList.isNotEmpty() && subjectFetchingState == ProcessState.DONE){
                                    subjectsList.forEach{
                                        DropdownMenuItem(
                                            onClick = {
                                                subjectInAlertDialog = it
                                                showAddLogsSubjectNameAlertDialog = false
                                            }
                                        ) {
                                            Text(it.subjectName)
                                        }
                                    }
                                }else if(subjectsList.isEmpty() && subjectFetchingState == ProcessState.DONE){
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
                                modifier = Modifier.width(80.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(presentOrAbsentInAlertDialog)
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


    if(logsList.isEmpty() && processState == ProcessState.DONE){
        Log.d("logs", "showing icons for no logs")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                modifier = Modifier.size(70.dp),
                painter = painterResource(id = R.drawable.logs),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(5.dp),
                text = "No Logs"
            )
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
                items(logsList.size){
                    val currentIndex = logsList.size - 1 - it
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
                                    text = logsList[currentIndex].subjectName,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ){

                                Text(
                                    text = logsList[currentIndex].day + " | " + logsList[currentIndex].month + " " + logsList[currentIndex].date.toString() + "," + logsList[currentIndex].year.toString(),
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
                                    when(logsList[currentIndex].wasPresent){
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
                                            classAttendanceViewModel.deleteLogs(logsList[currentIndex]._id)
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


