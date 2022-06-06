package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.classattendanceapp.presenter.screens.LogsScreen
import com.example.classattendanceapp.presenter.screens.SubjectsScreen
import com.example.classattendanceapp.presenter.screens.TimeTableScreen
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClassAttendanceNavigationHost(){

    val navController = rememberNavController()
    val classAttendanceViewModel = hiltViewModel<ClassAttendanceViewModel>()

    var previousDestination by remember{ mutableStateOf<String?>(null) }
    var nextDestination by remember{ mutableStateOf<String?>(null) }
    var isGoingToNextScreen by remember{ mutableStateOf(false)}




    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ClassAttendanceBottomNavigationBar(
                navController = navController
            ) { pDestination, nDestination, isGoing ->
                previousDestination = pDestination
                nextDestination = nDestination
                isGoingToNextScreen = isGoing
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    classAttendanceViewModel.changeFloatingButtonClickedState(true)
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    ) {
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            navController = navController,
            startDestination = Screens.SUBJECTSSCREEN.route,
        ){
            composable(Screens.LOGSSCREEN.route){
                var visibility by remember{ mutableStateOf(false) }
                AnimatedVisibility(
                    visible = visibility,
                    enter = scaleIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 100
                        )
                    ),
                    exit = scaleOut(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 100
                        )
                    ),
                ){
                    LogsScreen(classAttendanceViewModel)
                }
                if(it.destination.route == Screens.LOGSSCREEN.route) visibility = true
                if(isGoingToNextScreen) visibility = false
                if(nextDestination == Screens.LOGSSCREEN.route) visibility = true
            }

            composable(Screens.SUBJECTSSCREEN.route){
                var visibility by remember{ mutableStateOf(false) }
                AnimatedVisibility(
                    visible = visibility,
                    enter = scaleIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 100
                        )
                    ),
                    exit = scaleOut(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 100
                        )
                    )
                ) {
                    SubjectsScreen(classAttendanceViewModel)
                }
                if(it.destination.route == Screens.SUBJECTSSCREEN.route) visibility = true
                if(isGoingToNextScreen) visibility = false
                if(nextDestination == Screens.SUBJECTSSCREEN.route) visibility = true
            }

            composable(Screens.TIMETABLESCREEN.route){
                var visibility by remember{ mutableStateOf(false) }
                AnimatedVisibility(
                    visible = visibility,
                    enter = scaleIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 100
                        )
                    ),
                    exit = scaleOut(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 100
                        )
                    )
                ) {
                    TimeTableScreen(classAttendanceViewModel)
                }
                if(it.destination.route == Screens.TIMETABLESCREEN.route) visibility = true
                if(isGoingToNextScreen) visibility = false
                if(nextDestination == Screens.TIMETABLESCREEN.route) visibility = true
            }
        }
    }
}