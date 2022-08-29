package com.gps.classattendanceapp.presenter.screens.mapsscreen

import android.content.Context
import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gps.classattendanceapp.components.location.ClassLocationManager
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first

data class MapsScreenUiState(
    val classAttendanceViewModel: ClassAttendanceViewModel,
    val context: Context,
    val lifecycleOwner: LifecycleOwner,
    val searchBarText : State<String>,
    val currentLocation: State<Location?>,
    val isInternetAlive: MutableState<Boolean>,
    val coroutineScope: CoroutineScope,
    val searchLocation: MutableState<String>
)

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun rememberMapsScreenUiState(
    classAttendanceViewModel: ClassAttendanceViewModel,
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    searchBarText: State<String> = classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle(),
    currentLocation: State<Location?> = produceState<Location?>(
        initialValue = null,
        producer = {
            value = ClassLocationManager.getLocationFlow(context).first()
        }
    ),
    isInternetAlive: MutableState<Boolean> = mutableStateOf(false),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    searchLocation: MutableState<String> = mutableStateOf("")
)  = remember{
    MapsScreenUiState(
        classAttendanceViewModel = classAttendanceViewModel,
        context = context,
        lifecycleOwner = lifecycleOwner,
        searchBarText = searchBarText,
        currentLocation = currentLocation,
        isInternetAlive = isInternetAlive,
        coroutineScope = coroutineScope,
        searchLocation = searchLocation
    )
}