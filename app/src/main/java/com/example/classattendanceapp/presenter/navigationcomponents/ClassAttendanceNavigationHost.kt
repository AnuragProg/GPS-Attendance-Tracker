package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.classattendanceapp.presenter.screens.logsscreen.LogsScreen
import com.example.classattendanceapp.presenter.screens.subjectsscreen.SubjectsScreen
import com.example.classattendanceapp.presenter.screens.timetablescreen.TimeTableScreen
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClassAttendanceNavigationHost(){

    val navController = rememberNavController()
    val classAttendanceViewModel = hiltViewModel<ClassAttendanceViewModel>()
    var visibility by remember{ mutableStateOf(false) }
    var goneToAnotherScreen by remember{ mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val allowToAddTimeTable by remember{
        mutableStateOf(false)
    }



    fun navigate(
        route: String
    ){
        coroutineScope.launch{
            visibility = false
            delay(800)
            navController.navigate(route)
            goneToAnotherScreen = !goneToAnotherScreen
        }
    }


    AddLocationCoordinateDialog(classAttendanceViewModel = classAttendanceViewModel)


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ClassAttendanceTopBar(classAttendanceViewModel)
        },
        bottomBar = {
            ClassAttendanceBottomNavigationBar(
                navController = navController,
                navigate = { route ->
                    navigate(route)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    classAttendanceViewModel.changeFloatingButtonClickedState(true)
                },

            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    ) {

        PermissionHandler()

        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            navController = navController,
            startDestination = Screens.SUBJECTSSCREEN.route,
        ){

            

            composable(Screens.LOGSSCREEN.route){

                LaunchedEffect(goneToAnotherScreen){
                    visibility = true
                }
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
            }

            composable(Screens.SUBJECTSSCREEN.route){

                LaunchedEffect(goneToAnotherScreen){
                    /* This Launched Effect is implemented here because
                    This is the Starting screen so the visibility needs to be true
                    after some time to cause animation
                    ( Any screen that is going to be hosting the launch should implement this )
                    */
                    visibility = true
                }
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
            }

            composable(Screens.TIMETABLESCREEN.route){
                LaunchedEffect(goneToAnotherScreen){
                    visibility = true
                }
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
            }
        }
    }
}