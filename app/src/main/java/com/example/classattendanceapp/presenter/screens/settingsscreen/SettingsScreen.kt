package com.example.classattendanceapp.presenter.screens.settingsscreen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel


@Composable
fun SettingsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    val context = LocalContext.current
    
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Current Coordinates:")
        Text(
            "Latitude: " + (currentLatitudeInDataStore.value?.let {
                String.format("%.5f",
                    currentLatitudeInDataStore.value)
            } ?: "None")
        )
        Text(
            "Longitude: "+ (currentLongitudeInDataStore.value?.let{
                String.format("%.5f",
                    currentLongitudeInDataStore.value)
            } ?: "None")
        )
        Text(
            "Range: " + (currentRangeInDataStore.value?.let{
                String.format("%.5f",
                    currentRangeInDataStore.value)
            } ?: "None")
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 30.dp),
            contentAlignment = Alignment.CenterEnd
        ){
            TextButton(
                onClick = {
                    classAttendanceViewModel.deleteCoordinateInDataStore()
                }) {
                Text(stringResource(R.string.clear_fields))
            }
        }
    }
}