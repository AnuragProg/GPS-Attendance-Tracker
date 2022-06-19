package com.example.classattendanceapp.presenter.navigationcomponents

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun AddLocationCoordinateDialog(
    classAttendanceViewModel: ClassAttendanceViewModel
){
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    Log.d("coroutinescope", "$coroutineScope is the coroutine scope")
    var slatitude by remember{
        mutableStateOf("")
    }
    var slongitude by remember{
        mutableStateOf("")
    }
    var srange by remember{
        mutableStateOf("")
    }

    val showAddLocationCoordinateDialog = classAttendanceViewModel.showAddLocationCoordinateDialog.collectAsState()

    var currentLatitudeInDataStore by remember{
        mutableStateOf<Double?>(null)
    }
    var currentLongitudeInDataStore by remember{
        mutableStateOf<Double?>(null)
    }
    var currentRangeInDataStore by remember{
        mutableStateOf<Double?>(null)
    }


    LaunchedEffect(Unit){
        classAttendanceViewModel.getCoordinateInDataStore(this)
    }

    LaunchedEffect(Unit){
        combine(
            classAttendanceViewModel.currentLatitudeInDataStore,
            classAttendanceViewModel.currentLongitudeInDataStore,
            classAttendanceViewModel.currentRangeInDataStore
        ){ lat, lon, range->
            Triple(lat, lon, range)
        }.collectLatest { coordinates ->
            Log.d("coordinates", "Collected coordinates are $coordinates")
            currentLatitudeInDataStore = coordinates.first
            currentLongitudeInDataStore = coordinates.second
            currentRangeInDataStore = coordinates.third
        }
    }

    if(showAddLocationCoordinateDialog.value){
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeAddLocationCoordinateState(false)
            },
            text = {
                Column{
                    Text("Coordinates")
                    Spacer(modifier = Modifier.height(10.dp))
                    if(currentLatitudeInDataStore != null && currentLongitudeInDataStore != null && currentRangeInDataStore != null){
                        Text("Current Coordinates:" +
                                "\nLatitude:${currentLatitudeInDataStore}" +
                                "\nLongitude:${currentLongitudeInDataStore}" +
                                "\nRange:${currentRangeInDataStore}"
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = slatitude,
                        onValueChange = {
                            slatitude = it
                        },
                        label = {
                            Text("Latitude")

                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    slatitude = ""
                                }) {
                                Icon(
                                    Icons.Filled.Cancel,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = slongitude,
                        onValueChange = {
                            slongitude = it
                        },
                        label = {
                            Text("Longitude")

                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    slongitude = ""
                                }) {
                                Icon(
                                    Icons.Filled.Cancel,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = srange,
                        onValueChange = {
                            srange = it
                        },
                        label = {
                            Text("Range (in m)")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    srange = ""
                                }) {
                                Icon(
                                    Icons.Filled.Cancel,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Enter min 6 decimal digits for better accuracy",
                        color = Color.Red,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(onClick = {
                        classAttendanceViewModel.deleteCoordinateInDataStore()
                    }) {
                        Text("Clear Fields")
                    }
                }
            },
            buttons = {
                Row(){
                    TextButton(onClick = {

                        coroutineScope.launch{
                            try{
                                if(slatitude.isNotBlank() && slongitude.isNotBlank()){
                                    classAttendanceViewModel.writeOrUpdateCoordinateInDataStore(
                                        latitude = slatitude.toDouble(),
                                        longitude = slongitude.toDouble(),
                                        range = srange.toDouble()
                                    )
                                    Toast.makeText(context, "Successfully saved Coordinates", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, "Unable to save Coordinates", Toast.LENGTH_SHORT).show()
                                }
                            }catch(e: NumberFormatException){
                                Toast.makeText(context, "Please enter a decimal number", Toast.LENGTH_SHORT).show()
                            }finally{
                                slatitude = ""
                                slongitude = ""
                                srange = ""
//                                classAttendanceViewModel.changeAddLocationCoordinateState(false)
                            }
                        }

                    }) {
                        Text("Register")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    TextButton(onClick = {
                        slatitude = ""
                        slongitude = ""
                        classAttendanceViewModel.changeAddLocationCoordinateState(false)
                    }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}