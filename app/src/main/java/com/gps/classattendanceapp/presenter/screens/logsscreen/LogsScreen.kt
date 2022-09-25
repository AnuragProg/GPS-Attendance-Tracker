@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.gps.classattendanceapp.presenter.screens.logsscreen

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.presenter.theme.boxSizePercentage
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun LogsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
    addLogIdtoDelete: (Int)->Unit,
    removeLogIdToDelete: (Int)->Unit,
    snackbarHostState: SnackbarHostState
){
    val logs = classAttendanceViewModel.filteredLogs.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val showAddLogsAlertDialog = classAttendanceViewModel.floatingButtonClicked.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current

    val lazyScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0
    )


    var logToEdit by remember{
        mutableStateOf<com.gps.classattendanceapp.domain.models.ModifiedLogs?>(null)
    }

    // Making Log Dialog Box
    if(showAddLogsAlertDialog.value){
        LogsScreenAlertDialog(
            classAttendanceViewModel = classAttendanceViewModel,
            logToEdit = logToEdit,
            resetLogToEdit = {logToEdit=null}
        )
    }

    when(logs.value){
        is Resource.Success -> {
            if((logs.value.data?.size ?: 0) == 0){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        modifier = Modifier
                            .size(
                                height = (configuration.screenHeightDp* boxSizePercentage).dp,
                                width = (configuration.screenWidthDp* boxSizePercentage).dp
                            ),
                        painter = painterResource(id = R.drawable.box),
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = stringResource(R.string.no_logs),
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }else{
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        state = lazyScrollState
                    ) {
                        items(
                            logs.value.data ?: emptyList(),
                            key = { it._id!! }
                        ) { log ->
                            var isCardVisible by remember { mutableStateOf(false) }
                            var isLogSelected by remember { mutableStateOf(false) }
                            val dismissState = rememberDismissState()

                            LaunchedEffect(Unit) {
                                isCardVisible = true
                            }

                            if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
//
                                LaunchedEffect(Unit) {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Confirm delete",
                                        actionLabel = "Confirm",
                                        duration = SnackbarDuration.Short
                                    )

                                    if (result.name == SnackbarResult.ActionPerformed.name) {
                                        classAttendanceViewModel.deleteLogs(log._id!!)
                                    } else {
                                        dismissState.reset()
                                    }
                                }
                            } else if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                                logToEdit = log
                                classAttendanceViewModel.changeFloatingButtonClickedState(true)
                                coroutineScope.launch {
                                    dismissState.reset()
                                }
                            }

                            Box {
                                AnimatedVisibility(
                                    visible = isCardVisible,
                                    enter = slideInHorizontally { it / 10 } + fadeIn(),
                                    exit = slideOutHorizontally { -it / 10 } + fadeOut()
                                ) {
                                    SwipeToDismiss(
                                        state = dismissState,
                                        background = {
                                            if (dismissState.dismissDirection == DismissDirection.StartToEnd) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .requiredHeightIn(min = 80.dp)
                                                        .padding(start = 10.dp),
                                                    contentAlignment = Alignment.CenterStart
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Delete,
                                                        contentDescription = null,
                                                        tint = Color.Black
                                                    )
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .requiredHeightIn(min = 80.dp)
                                                        .padding(end = 10.dp),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Edit,
                                                        contentDescription = null,
                                                        tint = Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    ) {
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
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center

            ){
                CircularProgressIndicator()
            }
        }
        is Resource.Error -> {}
    }
}


