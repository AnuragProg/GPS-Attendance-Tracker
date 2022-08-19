package com.example.classattendanceapp.presenter.navigationcomponents

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.classattendanceapp.presenter.screens.logsscreen.LogsScreen
import com.example.classattendanceapp.presenter.screens.mapsscreen.MapsScreen
import com.example.classattendanceapp.presenter.screens.subjectsscreen.SubjectsScreen
import com.example.classattendanceapp.presenter.screens.timetablescreen.TimeTableScreen
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi


@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun ClassAttendanceNavigationHost(){

    val context = LocalContext.current as Activity

    val navController = rememberNavController()

    val coroutineScope = rememberCoroutineScope()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    val classAttendanceViewModel = hiltViewModel<ClassAttendanceViewModel>()

    var currentFloatingActionButtonIcon by remember{
        mutableStateOf(Icons.Filled.Add)
    }

    var showFloatingActionButton by remember{
        mutableStateOf(false)
    }

    val scaffoldState = rememberScaffoldState()


    val listOfSubjectIdsToDelete = remember{
        mutableStateListOf<Int>()
    }
    val listOfLogIdsToDelete = remember{
        mutableStateListOf<Int>()
    }



    LaunchedEffect(Unit){
        showFloatingActionButton = true
    }



    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ClassAttendanceTopBar(
                classAttendanceViewModel,
                navController,
                scaffoldState.snackbarHostState,
                listOfSubjectIdsToDelete,
                listOfLogIdsToDelete,
                {listOfSubjectIdsToDelete.remove(it)},
                {listOfLogIdsToDelete.remove(it)},
                {listOfSubjectIdsToDelete.clear()},
                {listOfLogIdsToDelete.clear()}
            )

        },
        bottomBar = {
            ClassAttendanceBottomNavigationBar(
                navController = navController,
                navigate = { route ->
                    showFloatingActionButton = route != Screens.MAPSSCREEN.route && route!=Screens.TIMETABLESCREEN.route
                    navController.navigate(route)
                }
            )
        },
        floatingActionButton = {

            AnimatedVisibility(
                visible = showFloatingActionButton,
                enter = scaleIn(),
                exit = scaleOut()
            ){
                FloatingActionButton(
                    modifier = Modifier.size(50.dp),
                    onClick = {
                        if (
                            currentBackStackEntry.value?.destination?.route != Screens.MAPSSCREEN.route
                        ) {
                            classAttendanceViewModel.changeFloatingButtonClickedState(true)
                        }
                    }
                ) {
                    AnimatedContent(
                        targetState = currentFloatingActionButtonIcon,
                        transitionSpec = {
                            when (currentFloatingActionButtonIcon) {
                                Icons.Filled.Save -> {
                                    fadeIn() with fadeOut()
                                }
                                else -> {
                                    fadeIn() with fadeOut()
                                }
                            }

                        }
                    ) { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ){

            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                navController = navController,
                startDestination = Screens.SUBJECTSSCREEN.route,
            ) {


                composable(Screens.LOGSSCREEN.route) {
                    BackHandler(enabled = true) {
                        context.moveTaskToBack(true)
                    }
                    LogsScreen(
                        classAttendanceViewModel,
                        {listOfLogIdsToDelete.add(it)},
                        {listOfLogIdsToDelete.remove(it)},
                    )
                }

                composable(Screens.SUBJECTSSCREEN.route) {

                    BackHandler(enabled=true) {
                        context.moveTaskToBack(true)
                    }
                    SubjectsScreen(
                        classAttendanceViewModel,
                        {listOfSubjectIdsToDelete.add(it)},
                        {listOfSubjectIdsToDelete.remove(it)}
                    )
                }

                composable(Screens.TIMETABLESCREEN.route) {
                    BackHandler(enabled=true) {
                        context.moveTaskToBack(true)
                    }
                    TimeTableScreen(classAttendanceViewModel)
                }
                
                composable(Screens.MAPSSCREEN.route){
                    BackHandler(enabled=true) {
                        context.moveTaskToBack(true)
                    }
                    MapsScreen(classAttendanceViewModel)
                }
            }
        }
    }
