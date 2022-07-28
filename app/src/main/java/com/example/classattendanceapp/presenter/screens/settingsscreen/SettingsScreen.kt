package com.example.classattendanceapp.presenter.screens.settingsscreen

import android.widget.Toast
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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
    var currentLatitudeInDataStore by remember{
        mutableStateOf<Double?>(null)
    }
    var currentLongitudeInDataStore by remember{
        mutableStateOf<Double?>(null)
    }
    var currentRangeInDataStore by remember{
        mutableStateOf<Double?>(null)
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        classAttendanceViewModel.getCoordinateInDataStore().collectLatest{ latitudeLongitudeRange ->
            currentLatitudeInDataStore = latitudeLongitudeRange.first
            currentLongitudeInDataStore = latitudeLongitudeRange.second
            currentRangeInDataStore = latitudeLongitudeRange.third

        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Current Coordinates:")
        Text(
            "Latitude: " + (currentLatitudeInDataStore?.let {
                String.format("%.5f",
                    currentLatitudeInDataStore)
            } ?: "None")
        )
        Text(
            "Longitude: "+ (currentLongitudeInDataStore?.let{
                String.format("%.5f",
                    currentLongitudeInDataStore)
            } ?: "None")
        )
        Text(
            "Range: " + (currentRangeInDataStore?.let{
                String.format("%.5f",
                    currentRangeInDataStore)
            } ?: "None")
        )
        OutlinedTextField(
            value = latitude?: "",
            onValueChange = { latitude = it },
            label = {
                Text(stringResource(R.string.latitude))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
        OutlinedTextField(
            value = longitude ?: "",
            onValueChange = { longitude = it },
            label = {
                Text(stringResource(R.string.longitude))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
        OutlinedTextField(
            value = range ?: "",
            onValueChange = { range = it },
            label = {
                Text(stringResource(R.string.range))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            TextButton(
                onClick = {
                    coroutineScope.launch{
                        if (latitude == null || longitude == null || range == null) {
                            return@launch
                        }
                        try {
                            val latitudeInDoubleFormat = latitude!!.toDouble()
                            val longitudeInDoubleFormat = longitude!!.toDouble()
                            val rangeInDoubleFormat = range!!.toDouble()

                            classAttendanceViewModel.writeOrUpdateCoordinateInDataStore(
                                latitudeInDoubleFormat,
                                longitudeInDoubleFormat,
                                rangeInDoubleFormat
                            )

                        } catch (e: NumberFormatException) {
                            Toast.makeText(context, "Please Fill Fields Properly", Toast.LENGTH_SHORT).show()
                        }finally{
                            latitude = ""
                            longitude = ""
                            range = ""
                        }
                    }


                }) {
                Text(stringResource(R.string.save))
            }
            TextButton(
                onClick = {
                    classAttendanceViewModel.deleteCoordinateInDataStore()
                }) {
                Text(stringResource(R.string.clear_fields))
            }

        }
    }
}