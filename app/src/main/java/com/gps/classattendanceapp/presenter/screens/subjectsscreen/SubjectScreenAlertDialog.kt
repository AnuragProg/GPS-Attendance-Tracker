package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.components.location.FusedLocation
import com.gps.classattendanceapp.data.models.Subject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun SubjectScreenAlertDialog(
    subjectScreenUiState: SubjectScreenUiState
){

    val coroutineScope = rememberCoroutineScope()

    var showSubjectNameError by remember{mutableStateOf(false)}
    var showCoordinateError by remember{mutableStateOf(false)}
    var showPresentsError by remember{mutableStateOf(false)}
    var showAbsentsError by remember{mutableStateOf(false)}
    var showRangeError by remember{mutableStateOf(false)}

    LaunchedEffect(Unit){
        if(subjectScreenUiState.subjectToEdit.value!=null){
            subjectScreenUiState.fillFieldsWithSubjectToEditFields()
        }
    }

    AlertDialog(
        shape = RoundedCornerShape(10.dp),
        onDismissRequest = {
            subjectScreenUiState.classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
            subjectScreenUiState.subjectToEdit.value = null
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.add_new_subject),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = subjectScreenUiState.subjectName.value,
                    onValueChange = { subjectScreenUiState.subjectName.value = it },
                    label = {
                        Text(stringResource(R.string.subject_name))
                    },
                    maxLines = 1,
                    trailingIcon = {
                        if(subjectScreenUiState.subjectName.value.isNotEmpty()){
                            IconButton(
                                onClick = {
                                    subjectScreenUiState.subjectName.value = ""
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    isError = showSubjectNameError
                )
                if(showSubjectNameError){
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Subject name required",
                        color = Color.Red, fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box {
                        OutlinedTextField(
                            modifier = Modifier.width(135.dp),
                            value = subjectScreenUiState.presents.value,
                            onValueChange = {
                                subjectScreenUiState.presents.value = it
                            },
                            label = {
                                Text(stringResource(R.string.presents))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            maxLines = 1,
                            shape = RoundedCornerShape(10.dp),
                            isError = showPresentsError
                        )
                        Box(
                            modifier = Modifier.matchParentSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(top = 10.dp, end = 10.dp),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            subjectScreenUiState.presents.value =
                                                (subjectScreenUiState.presents.value.toLong() + 1).toString()
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            if (subjectScreenUiState.presents.value.toLong() > 0) {
                                                subjectScreenUiState.presents.value =
                                                    (subjectScreenUiState.presents.value.toLong() - 1).toString()
                                            }
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        }

                    }
                    Box {
                        OutlinedTextField(
                            modifier = Modifier.width(135.dp),
                            value = subjectScreenUiState.absents.value,
                            onValueChange = {
                                subjectScreenUiState.absents.value = it
                            },
                            label = {
                                Text(stringResource(R.string.absents))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            maxLines = 1,
                            shape = RoundedCornerShape(10.dp),
                            isError = showAbsentsError
                        )
                        Box(
                            modifier = Modifier.matchParentSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(end = 10.dp, top = 10.dp),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            subjectScreenUiState.absents.value =
                                                (subjectScreenUiState.absents.value.toLong() + 1).toString()
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            if (subjectScreenUiState.absents.value.toLong() > 0) {
                                                subjectScreenUiState.absents.value =
                                                    (subjectScreenUiState.absents.value.toLong() - 1).toString()
                                            }
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box{
                        OutlinedTextField(
                            modifier = Modifier.width(135.dp),
                            value = subjectScreenUiState.latitude.value,
                            onValueChange = {
                                subjectScreenUiState.latitude.value = it
                            },
                            label = {
                                Text(stringResource(R.string.latitude))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            maxLines = 1,
                            shape = RoundedCornerShape(10.dp),
                            isError = showCoordinateError
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(end = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ){
                            if(subjectScreenUiState.latitude.value.isNotEmpty()){
                                Icon(
                                    modifier = Modifier.clickable {
                                        subjectScreenUiState.latitude.value = ""
                                    },
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null
                                )
                            }
                        }

                    }
                    Box{
                        OutlinedTextField(
                            modifier = Modifier.width(135.dp),
                            value = subjectScreenUiState.longitude.value,
                            onValueChange = {
                                subjectScreenUiState.longitude.value=it
                            },
                            label = {
                                Text(stringResource(R.string.longitude))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            maxLines = 1,
                            shape = RoundedCornerShape(10.dp),
                            isError = showCoordinateError
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(end = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ){
                            if(subjectScreenUiState.longitude.value.isNotEmpty()){
                                Icon(
                                    modifier = Modifier.clickable {
                                        subjectScreenUiState.longitude.value = ""
                                    },
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                if(showCoordinateError){
                    Text(text="Please provide complete coordinate details", color=Color.Red, fontSize=10.sp)
                }
                OutlinedTextField(
                    value = subjectScreenUiState.range.value,
                    onValueChange = {
                        subjectScreenUiState.range.value = it
                    },
                    label = {
                        Text(stringResource(R.string.rangeInM))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    maxLines = 1,
                    trailingIcon = {
                        if(subjectScreenUiState.range.value.isNotEmpty()){
                            IconButton(
                                onClick = {
                                    subjectScreenUiState.range.value = ""
                                }
                            ){
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    isError = showRangeError
                )
                if(showRangeError){
                    Spacer(modifier=Modifier.height(8.dp))
                    Text(text="Range is required", color=Color.Red, fontSize=10.sp)
                }
                Spacer(modifier=Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch{
                            val context = subjectScreenUiState.context
                            FusedLocation.apply{
                                if(!isGpsEnabled(context)){
                                    Toast.makeText(context, "Please enable GPS", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                if(!isPermissionGiven(context)){
                                    Toast.makeText(context, "Please provide location permission to use this feature", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                val locationResult = withTimeoutOrNull(5000){
                                    getLocationFlow(context).first{it!=null}
                                }
                                if(locationResult == null){
                                    Toast.makeText(context, "Unable to fetch current location", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                subjectScreenUiState.latitude.value = locationResult.latitude.toString()
                                subjectScreenUiState.longitude.value = locationResult.longitude.toString()
                            }
                        }
                    }
                ) {
                    Text("Use current location")
                }
            }
        },
        buttons = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row {
                    TextButton(
                        onClick = {
                            subjectScreenUiState.coroutineScope.launch {
                                if (
                                    subjectScreenUiState.subjectName.value.isBlank()
                                ) {
                                    showSubjectNameError = true
                                    return@launch
                                }
                                showSubjectNameError = false
                                try {
                                    val daysPresent =
                                        if (subjectScreenUiState.presents.value.isBlank()) 0 else subjectScreenUiState.presents.value.toLong()
                                    val daysAbsent =
                                        if (subjectScreenUiState.absents.value.isBlank()) 0 else subjectScreenUiState.absents.value.toLong()
                                    val lat = if(subjectScreenUiState.latitude.value.isBlank()) null  else  subjectScreenUiState.latitude.value.toDouble()
                                    val lon = if(subjectScreenUiState.longitude.value.isBlank()) null  else  subjectScreenUiState.longitude.value.toDouble()
                                    if(lat!=null && lon == null){
                                        showCoordinateError = true
                                        return@launch
                                    }
                                    showCoordinateError = false
                                    if(lat != null && lon != null){
                                        showRangeError = true
                                        return@launch
                                    }
                                    showRangeError = false
                                    val ran = if(subjectScreenUiState.range.value.isBlank()) null else subjectScreenUiState.range.value.toDouble()

                                    if (subjectScreenUiState.subjectToEdit.value != null) {
                                        val subject = subjectScreenUiState.classAttendanceViewModel.getSubjectWithId(
                                            subjectScreenUiState.subjectToEdit.value!!._id)!!
                                        subjectScreenUiState.classAttendanceViewModel.updateSubject(
                                            Subject(
                                                _id = subject._id,
                                                subjectName = subjectScreenUiState.subjectName.value.trim(),
                                                daysPresent = daysPresent,
                                                daysAbsent = daysAbsent,
                                                latitude = lat,
                                                longitude = lon,
                                                range = ran
                                            )
                                        )
                                    } else {
                                        subjectScreenUiState.classAttendanceViewModel.insertSubject(
                                            Subject(
                                                _id = 0,
                                                subjectName = subjectScreenUiState.subjectName.value.trim(),
                                                daysPresent = daysPresent,
                                                daysAbsent = daysAbsent,
                                                latitude = lat,
                                                longitude = lon,
                                                range = ran
                                            )
                                        )
                                    }
                                    subjectScreenUiState.classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                                    subjectScreenUiState.clearDialogFields()

                                } catch (e: NumberFormatException) {
                                    Toast.makeText(subjectScreenUiState.context,
                                        "Please enter valid information!",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Gray
                        ),
                    ) {
                        Text(stringResource(R.string.add))
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    TextButton(
                        onClick = {
                            subjectScreenUiState.classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                            subjectScreenUiState.clearDialogFields()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    )
}