@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.gps.classattendanceapp.presenter.navigationcomponents

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gps.classattendanceapp.domain.models.ModifiedLogs
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.presenter.screens.logsscreen.LogsScreen
import com.gps.classattendanceapp.presenter.screens.logsscreen.LogsScreenBottomSheet
import com.gps.classattendanceapp.presenter.screens.subjectsscreen.SubjectScreenBottomSheet
import com.gps.classattendanceapp.presenter.screens.subjectsscreen.SubjectsScreen
import com.gps.classattendanceapp.presenter.screens.timetablescreen.TimeTableScreen
import kotlinx.coroutines.delay


@OptIn(ExperimentalAnimationApi::class, ExperimentalLifecycleComposeApi::class,
    ExperimentalMaterialApi::class)
@Composable
fun ClassAttendanceNavigationHost(){

    val uiState = rememberClassAttendanceNavigationHostUiState()
    val deniedPermissions by uiState.classAttendanceViewModel.deniedPermissions.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        uiState.showFloatingActionButton.value = true
    }

    LaunchedEffect(Unit){
        if(deniedPermissions.isNotEmpty()){
            for(i in 1..5){
                delay(5000)
                uiState.classAttendanceViewModel.refreshPermissions(uiState.context)
                if(deniedPermissions.isEmpty()) break
            }
        }
    }

    DisposableEffect(uiState.lifecycleOwner){
        val observer = LifecycleEventObserver{_, event ->
            if(event == Lifecycle.Event.ON_START){
                uiState.classAttendanceViewModel.refreshPermissions(uiState.context)
            }
        }
        uiState.lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            uiState.lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }



    Scaffold(
        scaffoldState = uiState.scaffoldState,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ClassAttendanceTopBar(
                uiState.classAttendanceViewModel,
                uiState.navController,
                uiState.scaffoldState.snackbarHostState,
                uiState.listOfSubjectIdsToDelete,
                uiState.listOfLogIdsToDelete,
                {uiState.listOfSubjectIdsToDelete.clear()},
                {uiState.listOfLogIdsToDelete.clear()}
            )

        },
        bottomBar = {
            ClassAttendanceBottomNavigationBar(
                navController = uiState.navController,
                navigate = { route ->
                    uiState.navController.navigate(route)
                }
            )
        },
        floatingActionButton = {

            AnimatedVisibility(
                visible = uiState.showFloatingActionButton.value,
                enter = scaleIn(),
                exit = scaleOut()
            ){
                FloatingActionButton(
                    modifier = Modifier.size(50.dp),
                    onClick = {
                        uiState.classAttendanceViewModel.changeFloatingButtonClickedState(true)
                    },
                    backgroundColor = MaterialTheme.colors.primarySurface
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ){
            DeniedPermissionsCard(
                uiState = uiState,
                deniedPermissions = deniedPermissions.toList()
            )
            NavHost(
                modifier = Modifier
                    .fillMaxSize(),
                navController = uiState.navController,
                startDestination = Screens.SUBJECTSSCREEN.route,
            ) {


                composable(Screens.LOGSSCREEN.route) {
                    BackHandler(enabled = true) {
                        uiState.context.moveTaskToBack(true)
                    }


                    var selectedLog by remember{
                        mutableStateOf<ModifiedLogs?>(null)
                    }

                    val modalBottomSheetState = rememberModalBottomSheetState(
                        initialValue = ModalBottomSheetValue.Hidden,
                        confirmStateChange = {
                            when(it){
                                ModalBottomSheetValue.Hidden -> {
                                    selectedLog = null
                                    true
                                }
                                else ->{
                                    true
                                }
                            }
                        }
                    )


                    LogsScreenBottomSheet(
                        sheetState = modalBottomSheetState,
                        log = selectedLog
                    ) {
                        LogsScreen(
                            uiState.classAttendanceViewModel,
                            {uiState.listOfLogIdsToDelete.add(it)},
                            {uiState.listOfLogIdsToDelete.remove(it)},
                            uiState.scaffoldState.snackbarHostState
                        ){
                            selectedLog = it
                            modalBottomSheetState.show()
                        }
                    }
                }

                composable(Screens.SUBJECTSSCREEN.route) {

                    BackHandler(enabled=true) {
                        uiState.context.moveTaskToBack(true)
                    }

                    var selectedSubject by remember{
                        mutableStateOf<ModifiedSubjects?>(null)
                    }

                    val modalBottomSheetState = rememberModalBottomSheetState(
                        initialValue = ModalBottomSheetValue.Hidden,
                        confirmStateChange = {
                            when(it){
                                ModalBottomSheetValue.Hidden -> {
                                    selectedSubject = null
                                    true
                                }
                                else ->{
                                    true
                                }
                            }
                        }
                    )


                    SubjectScreenBottomSheet(
                        sheetState = modalBottomSheetState,
                        subject = selectedSubject
                    ) {
                        SubjectsScreen(
                            uiState.classAttendanceViewModel,
                            {subjectId -> uiState.listOfSubjectIdsToDelete.add(subjectId)},
                            {subjectId -> uiState.listOfSubjectIdsToDelete.remove(subjectId)},
                            {
                                selectedSubject = it
                                modalBottomSheetState.show()
                            }
                        )

                    }
                }

                composable(Screens.TIMETABLESCREEN.route) {
                    BackHandler(enabled=true) {
                        uiState.context.moveTaskToBack(true)
                    }
                    TimeTableScreen(uiState.classAttendanceViewModel)
                }
            }
        }
    }
}
