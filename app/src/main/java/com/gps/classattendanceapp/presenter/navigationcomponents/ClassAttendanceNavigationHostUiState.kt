package com.gps.classattendanceapp.presenter.navigationcomponents

import android.app.Activity
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel

data class ClassAttendanceNavigationHostUiState(
    val context: Activity,
    val navController: NavHostController,
    val currentBackStackEntry: State<NavBackStackEntry?>,
    val classAttendanceViewModel: ClassAttendanceViewModel,
    val scaffoldState: ScaffoldState,
    val listOfSubjectIdsToDelete: SnapshotStateList<Int>,
    val listOfLogIdsToDelete: SnapshotStateList<Int>,
    var showFloatingActionButton: MutableState<Boolean>,
    val lifecycleOwner: LifecycleOwner,
    val showLocationInformationDialog: MutableState<Boolean>,
    val launchPermissions: MutableState<Boolean>
)

@Composable
fun rememberClassAttendanceNavigationHostUiState(
    context: Activity = LocalContext.current as Activity,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    navController: NavHostController = rememberNavController(),
    currentBackStackEntry: State<NavBackStackEntry?> = navController.currentBackStackEntryAsState(),
    classAttendanceViewModel: ClassAttendanceViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    listOfSubjectIdsToDelete: SnapshotStateList<Int> = mutableStateListOf(),
    listOfLogIdsToDelete: SnapshotStateList<Int> = mutableStateListOf(),
    showFloatingActionButton: MutableState<Boolean> = mutableStateOf(true),
    showLocationInformationDialog: MutableState<Boolean> = mutableStateOf(true),
    launchPermissions: MutableState<Boolean> = mutableStateOf(false)
): ClassAttendanceNavigationHostUiState{
    return remember{
        ClassAttendanceNavigationHostUiState(
            context, navController, currentBackStackEntry, classAttendanceViewModel, scaffoldState, listOfSubjectIdsToDelete, listOfLogIdsToDelete, showFloatingActionButton, lifecycleOwner, showLocationInformationDialog, launchPermissions
        )
    }
}