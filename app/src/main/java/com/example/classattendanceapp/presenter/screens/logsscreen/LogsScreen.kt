@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.logsscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun LogsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
    addLogIdtoDelete: (Int)->Unit,
    removeLogIdToDelete: (Int)->Unit
){
    val logsList = remember{
        mutableStateListOf<ModifiedLogs>()
    }

    val coroutineScope = rememberCoroutineScope()

    val isInitialLogDataRetrievalDone = classAttendanceViewModel.isInitialLogDataRetrievalDone.collectAsStateWithLifecycle()

    val showAddLogsAlertDialog = classAttendanceViewModel.floatingButtonClicked.collectAsStateWithLifecycle()

    val searchBarText by classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle()

    val lazyScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0
    )


    var logToEdit by remember{
        mutableStateOf<ModifiedLogs?>(null)
    }



    LaunchedEffect(Unit){
        classAttendanceViewModel.logsList.collectLatest{
            if(it.isNotEmpty()){
                lazyScrollState.scrollToItem(it.size-1)
            }
        }
    }

    LaunchedEffect(Unit){
        classAttendanceViewModel.getAllLogsAdvanced().collect{
            logsList.clear()
            logsList.addAll(
                it.filter{
                    if(searchBarText.isNotBlank()){
                        searchBarText.lowercase() in it.subjectName!!.lowercase()
                    }else{
                        true
                    }
                }
            )
        }
    }

    // Making Log Dialog Box
    if(showAddLogsAlertDialog.value){
        LogsScreenAlertDialog(
            classAttendanceViewModel = classAttendanceViewModel,
            logToEdit = logToEdit,
            resetLogToEdit = {logToEdit=null}
        )
    }


    if(logsList.isEmpty() && isInitialLogDataRetrievalDone.value){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                modifier = Modifier.size(200.dp),
                imageVector = Icons.Outlined.ListAlt,
                contentDescription = null,
                tint = Color.White
            )

            Text(
                modifier = Modifier.padding(5.dp),
                text = stringResource(R.string.no_logs),
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else if(logsList.isEmpty() && !isInitialLogDataRetrievalDone.value){
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
                    logsList,
                    key = {it._id!!}
                ){ log ->
                    var isLogSelected by remember{mutableStateOf(false)}
                    val dismissState = rememberDismissState()

                    if(dismissState.isDismissed(DismissDirection.StartToEnd)){
                        coroutineScope.launch{
                            classAttendanceViewModel.deleteLogs(log._id!!)
                        }
                    }else if(dismissState.isDismissed(DismissDirection.EndToStart)){
                        logToEdit = log
                        classAttendanceViewModel.changeFloatingButtonClickedState(true)
                        coroutineScope.launch{
                            dismissState.reset()
                        }
                    }

                    Box{
                        SwipeToDismiss(
                            state = dismissState,
                            background ={
                                if(dismissState.dismissDirection == DismissDirection.StartToEnd){
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .requiredHeightIn(min = 80.dp)
                                            .padding(start = 10.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ){
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }else{
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .requiredHeightIn(min = 80.dp)
                                            .padding(end = 10.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ){
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        ){
                            LogCard(
                                log = log,
                                changeIsLogSelected = { selected ->
                                    if (selected) {
                                        addLogIdtoDelete(log._id!!)
                                    } else {
                                        removeLogIdToDelete(log._id!!)
                                    }

                                    isLogSelected = selected
                                }
                            )
                        }
                        if (isLogSelected) {
                            Surface(
                                modifier = Modifier.matchParentSize(),
                                color = Color.Blue.copy(alpha = 0.2f),
                                onClick = {
                                    removeLogIdToDelete(log._id!!)
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


