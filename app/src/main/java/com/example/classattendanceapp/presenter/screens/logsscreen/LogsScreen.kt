@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.logsscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.collectLatest



@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun LogsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
    addLogIdtoDelete: (Int)->Unit,
    removeLogIdToDelete: (Int)->Unit
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



    LaunchedEffect(Unit){
        classAttendanceViewModel.logsList.collectLatest{
            if(it.isNotEmpty()){
                lazyScrollState.scrollToItem(it.size-1)
            }
        }
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
//            Image(
//                modifier = Modifier.size(150.dp),
//                painter = painterResource(id = R.drawable.logs),
//                contentDescription = null
//            )
            Icon(
                modifier = Modifier.size(200.dp),
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                tint = Color.White
            )

            Text(
                modifier = Modifier.padding(5.dp),
                text = stringResource(R.string.no_logs),
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp
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
                ){ log ->
                    var isLogSelected by remember{mutableStateOf(false)}
                    Box{
                        LogCard(
                            changeSubjectIdInAlertDialog = { subjectIdInAlertDialog = it },
                            changeSubjectNameInAlertDialog = { subjectNameInAlertDialog = it },
                            log = log,
                            changeEditingLog = { editingLog = it },
                            classAttendanceViewModel = classAttendanceViewModel,
                            changeIsLogSelected = { selected ->
                                if(selected){
                                    addLogIdtoDelete(log._id)
                                }else{
                                    removeLogIdToDelete(log._id)
                                }

                                isLogSelected = selected
                            }
                        )
                        if (isLogSelected) {
                            Surface(
                                modifier = Modifier.matchParentSize(),
                                color = Color.Blue.copy(alpha = 0.2f),
                                onClick = {
                                    removeLogIdToDelete(log._id)
                                    isLogSelected = false
                                }
                            ) {}
                        }
                    }
                }
            }
        }
    }
}


