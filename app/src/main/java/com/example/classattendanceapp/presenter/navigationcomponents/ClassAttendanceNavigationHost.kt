package com.example.classattendanceapp.presenter.navigationcomponents

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Indication
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.screens.logsscreen.LogsScreen
import com.example.classattendanceapp.presenter.screens.subjectsscreen.SubjectsScreen
import com.example.classattendanceapp.presenter.screens.timetablescreen.TimeTableScreen
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch


@Composable
fun ClassAttendanceNavigationHost(){

    val context = LocalContext.current
    val navController = rememberNavController()
    val classAttendanceViewModel = hiltViewModel<ClassAttendanceViewModel>()
    var visibility by remember{ mutableStateOf(false) }
    var goneToAnotherScreen by remember{ mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val nonGrantedPermissionList = remember{
        mutableStateListOf<String>()
    }

    fun navigate(
        route: String
    ){
        coroutineScope.launch{
            visibility = false
            navController.navigate(route){
                popUpTo(route)
            }
            goneToAnotherScreen = !goneToAnotherScreen
        }
    }


    AddLocationCoordinateDialog(
        classAttendanceViewModel = classAttendanceViewModel
    )


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
        Column(){
            PermissionHandler ({ nonGrantedPermission ->
                if (!nonGrantedPermissionList.contains(nonGrantedPermission)) {
                    Log.d("permissions", "adding $nonGrantedPermission to list")
                    nonGrantedPermissionList.add(nonGrantedPermission)
                }
            },
                { grantedPermission ->
                    Log.d("permissions", "removing $grantedPermission from list")
                    if(nonGrantedPermissionList.contains(grantedPermission)){
                        nonGrantedPermissionList.remove(grantedPermission)
                    }
                }
            )
            if (nonGrantedPermissionList.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp),
                    elevation = 5.dp,
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ){
                        Text(stringResource(R.string.feature_permission_not_granted_warning_message))
                        for(nonGrantedPermission in nonGrantedPermissionList){
                            TextButton(
                                onClick = {
                                    Toast.makeText(context, context.getString(R.string.grant_app_permanent_location_access), Toast.LENGTH_LONG).show()
                                    val settingsIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    settingsIntent.data = Uri.parse("package:" + context.packageName)
                                    context.startActivity(settingsIntent)
                                }
                            ){
                                Text(nonGrantedPermission)
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

                    LaunchedEffect(goneToAnotherScreen) {
                        visibility = true
                    }
                    LogsScreen(classAttendanceViewModel)

                }

                composable(Screens.SUBJECTSSCREEN.route) {

                    LaunchedEffect(goneToAnotherScreen) {
                        /* This Launched Effect is implemented here because
                        This is the Starting screen so the visibility needs to be true
                        after some time to cause animation
                        ( Any screen that is going to be hosting the launch should implement this )
                        */
                        visibility = true
                    }
                    SubjectsScreen(classAttendanceViewModel)
                }

                composable(Screens.TIMETABLESCREEN.route) {
                    LaunchedEffect(goneToAnotherScreen) {
                        visibility = true
                    }
                    TimeTableScreen(classAttendanceViewModel)
                }
            }
        }
    }
}