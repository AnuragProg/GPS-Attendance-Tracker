package com.example.classattendanceapp.presenter.screens.settingsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel


@Composable
fun SettingsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    var latitude by remember{
        mutableStateOf<String?>(null)
    }
    var longitude by remember{
        mutableStateOf<String?>(null)
    }
    var range by remember{
        mutableStateOf<String?>(null)
    }
    val currentLatitudeInDataStore = classAttendanceViewModel.currentLatitudeInDataStore.collectAsState()
    val currentLongitudeInDataStore = classAttendanceViewModel.currentLongitudeInDataStore.collectAsState()
    val currentRangeInDataStore = classAttendanceViewModel.currentRangeInDataStore.collectAsState()

    LaunchedEffect(Unit){
        classAttendanceViewModel.getCoordinateInDataStore(
            this
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ){
        Text("Current Coordinates:")
        Text(
            currentLatitudeInDataStore.value?.let{ String.format(".5f", currentLatitudeInDataStore.value) } ?: "None"
        )
        Text(
            currentLongitudeInDataStore.value?.let{ String.format(".5f", currentLongitudeInDataStore.value) } ?: "None"
        )
        Text(
            currentRangeInDataStore.value?.let{ String.format(".5f", currentRangeInDataStore.value) } ?: "None"
        )
        OutlinedTextField(
            value = latitude?: "",
            onValueChange = { latitude = it },
            label = {
                Text("Latitude")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
        OutlinedTextField(
            value = longitude ?: "",
            onValueChange = { longitude = it },
            label = {
                Text("Longitude")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
        OutlinedTextField(
            value = range ?: "",
            onValueChange = { range = it },
            label = {
                Text("Range")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }
}