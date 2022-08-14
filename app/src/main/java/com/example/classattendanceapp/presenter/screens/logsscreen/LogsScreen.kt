@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.logsscreen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
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
                ){ log ->
                    LogCard(
                        changeSubjectIdInAlertDialog = { subjectIdInAlertDialog = it },
                        changeSubjectNameInAlertDialog = { subjectNameInAlertDialog = it },
                        log = log,
                        changeEditingLog = {editingLog=it},
                        classAttendanceViewModel = classAttendanceViewModel
                    )

                }
            }
        }
    }
}


