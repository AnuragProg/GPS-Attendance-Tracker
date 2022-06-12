package com.example.classattendanceapp.presenter.navigationcomponents

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun AddLocationCoordinateDialog(
    classAttendanceViewModel: ClassAttendanceViewModel
){
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    var slatitude by remember{
        mutableStateOf("")
    }
    var slongitude by remember{
        mutableStateOf("")
    }

    val showAddLocationCoordinateDialog = classAttendanceViewModel.showAddLocationCoordinateDialog.collectAsState()

    if(showAddLocationCoordinateDialog.value){
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeAddLocationCoordinateState(false)
            },
            text = {
                Column(){
                    Text("Add Coordinates")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = slatitude,
                        onValueChange = {
                            slatitude = it
                        },
                        placeholder = {
                            Text("Latitude")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                    OutlinedTextField(
                        value = slongitude,
                        onValueChange = {
                            slongitude = it
                        },
                        placeholder = {
                            Text("Longitude")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Enter min 6 decimal digits for better accuracy",
                        color = Color.Red,
                        fontSize = 10.sp
                    )
                }
            },
            buttons = {
                Row(){
                    TextButton(onClick = {
                        // TODO -> Add location to preferences datastore
                        coroutineScope.launch{
                            try{
                                if(slatitude.isNotBlank() && slongitude.isNotBlank()){
                                    classAttendanceViewModel.writeOrUpdateCoordinateInDataStore(
                                        latitude = slatitude.toDouble(),
                                        longitude = slongitude.toDouble()
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
                                classAttendanceViewModel.changeAddLocationCoordinateState(false)
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