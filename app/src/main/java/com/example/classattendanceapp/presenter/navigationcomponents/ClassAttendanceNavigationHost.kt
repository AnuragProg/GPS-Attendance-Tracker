package com.example.classattendanceapp.presenter.navigationcomponents

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.classattendanceapp.presenter.screens.logsscreen.LogsScreen
import com.example.classattendanceapp.presenter.screens.mapsscreen.DeniedPermissionMapScreen
import com.example.classattendanceapp.presenter.screens.mapsscreen.MapsScreen
import com.example.classattendanceapp.presenter.screens.subjectsscreen.SubjectsScreen
import com.example.classattendanceapp.presenter.screens.timetablescreen.TimeTableScreen
import com.google.accompanist.permissions.PermissionStatus


@OptIn(ExperimentalAnimationApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ClassAttendanceNavigationHost(){

    val uiState = rememberClassAttendanceNavigationHostUiState()
    val deniedPermissions by uiState.classAttendanceViewModel.deniedPermissions.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        uiState.showFloatingActionButton.value = true
    }

    PermissionHandler()

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
                {uiState.listOfSubjectIdsToDelete.remove(it)},
                {uiState.listOfLogIdsToDelete.remove(it)},
                {uiState.listOfSubjectIdsToDelete.clear()},
                {uiState.listOfLogIdsToDelete.clear()}
            )

        },
        bottomBar = {
            ClassAttendanceBottomNavigationBar(
                navController = uiState.navController,
                navigate = { route ->
                    uiState.showFloatingActionButton.value = route != Screens.MAPSSCREEN.route
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
                        if (
                            uiState.currentBackStackEntry.value?.destination?.route != Screens.MAPSSCREEN.route
                        ) {
                            uiState.classAttendanceViewModel.changeFloatingButtonClickedState(true)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ){
        Column{
            DeniedPermissionsCard(
                scaffoldPadding = it,
                uiState = uiState,
                deniedPermissions = deniedPermissions
            )
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                navController = uiState.navController,
                startDestination = Screens.SUBJECTSSCREEN.route,
            ) {


                composable(Screens.LOGSSCREEN.route) {
                    BackHandler(enabled = true) {
                        uiState.context.moveTaskToBack(true)
                    }
                    LogsScreen(
                        uiState.classAttendanceViewModel,
                        {uiState.listOfLogIdsToDelete.add(it)},
                        {uiState.listOfLogIdsToDelete.remove(it)},
                    )
                }

                composable(Screens.SUBJECTSSCREEN.route) {

                    BackHandler(enabled=true) {
                        uiState.context.moveTaskToBack(true)
                    }
                    SubjectsScreen(
                        uiState.classAttendanceViewModel,
                        {subjectId -> uiState.listOfSubjectIdsToDelete.add(subjectId)},
                        {subjectId -> uiState.listOfSubjectIdsToDelete.remove(subjectId)}
                    )
                }

                composable(Screens.TIMETABLESCREEN.route) {
                    BackHandler(enabled=true) {
                        uiState.context.moveTaskToBack(true)
                    }
                    TimeTableScreen(uiState.classAttendanceViewModel)
                }

                composable(Screens.MAPSSCREEN.route){
                    BackHandler(enabled=true) {
                        uiState.context.moveTaskToBack(true)
                    }
                    if("Location" in deniedPermissions){
                        DeniedPermissionMapScreen()
                    }else{
                        MapsScreen(uiState.classAttendanceViewModel)
                    }
                }
            }
        }
    }
}
