package com.example.classattendanceapp.presenter.navigationcomponents

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.screens.logsscreen.LogsScreen
import com.example.classattendanceapp.presenter.screens.mapsscreen.MapsScreen
import com.example.classattendanceapp.presenter.screens.subjectsscreen.SubjectsScreen
import com.example.classattendanceapp.presenter.screens.timetablescreen.TimeTableScreen
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClassAttendanceNavigationHost(){

    val context = LocalContext.current as Activity

    val navController = rememberNavController()

    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    val classAttendanceViewModel = hiltViewModel<ClassAttendanceViewModel>()

    val nonGrantedPermissionSet = remember{
        mutableSetOf<String>()
    }
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

    listOfSubjectIdsToDelete.forEach{
        Log.d("selectedid", "subject id is $it")
    }

    listOfLogIdsToDelete.forEach{
        Log.d("selectedid", "log id is $it")
    }

    LaunchedEffect(Unit){
        navController.currentBackStackEntryFlow.collectLatest{
            currentFloatingActionButtonIcon = when(it.destination.route){
                Screens.MAPSSCREEN.route ->{
                    Icons.Filled.Save
                }
                else -> {
                    Icons.Filled.Add
                }
            }

            showFloatingActionButton = when(it.destination.route){
                Screens.MAPSSCREEN.route -> false
                else -> true
            }
        }
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
    ) {


        // Handling Permissions
        PermissionHandler(
            grantedPermissions = {

            } ,
            nonGrantedPermissions = {

            }
        )

        Column{
            if (nonGrantedPermissionSet.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    elevation = 5.dp,
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ){
                        Text(stringResource(R.string.feature_permission_not_granted_warning_message))
                        for(nonGrantedPermission in nonGrantedPermissionSet){
                            TextButton(
                                onClick = {
                                    Toast.makeText(context, context.getString(R.string.grant_app_permanent_location_access), Toast.LENGTH_LONG).show()
                                    val settingsIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    settingsIntent.data = Uri.parse("package:" + context.packageName)
                                    context.startActivity(settingsIntent)
                                }
                            ){
                                Text(
                                    nonGrantedPermission

                                )
                            }
                        }
                    }
                }
            }

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
}