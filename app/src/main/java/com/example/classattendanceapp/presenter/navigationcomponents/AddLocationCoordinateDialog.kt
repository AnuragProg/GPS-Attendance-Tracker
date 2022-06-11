package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel


@Composable
fun AddLocationCoordinateDialog(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    var slatitude by remember{
        mutableStateOf("")
    }
    var slongitude by remember{
        mutableStateOf("")
    }
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
            }
        },
        buttons = {
            Row(){
                TextButton(onClick = {
                    // TODO -> Add location to preferences datastore
                    classAttendanceViewModel.changeAddLocationCoordinateState(false)
                }) {
                    Text("Register")
                }
                Spacer(modifier = Modifier.width(10.dp))
                TextButton(onClick = {
                    classAttendanceViewModel.changeAddLocationCoordinateState(false)
                }) {
                    Text("Cancel")
                }
            }
        }
    )
}