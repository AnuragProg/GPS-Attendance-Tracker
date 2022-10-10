package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.data.models.Subject
import kotlinx.coroutines.launch

@Composable
fun SubjectScreenAlertDialog(
    subjectScreenUiState: SubjectScreenUiState
){

    LaunchedEffect(Unit){
        if(subjectScreenUiState.subjectToEdit.value!=null){
            subjectScreenUiState.fillFieldsWithSubjectToEditFields()
        }
    }

    AlertDialog(
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
                        Text(stringResource(R.string.subject_name)+"*")
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
                    }
                )
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
                                Text(stringResource(R.string.presents)+"*")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            maxLines = 1
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
                                Text(stringResource(R.string.absents)+"*")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            maxLines = 1,
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
                            maxLines = 1
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
                    }
                )
                Spacer(modifier=Modifier.height(8.dp))
                Text(
                    modifier=Modifier.fillMaxWidth(),
                    text="(*)Required Fields",
                    fontSize = 13.sp,
                    textAlign = TextAlign.End,
                    color = Color.Red
                )
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
                                    Toast.makeText(subjectScreenUiState.context,
                                        "Subject Name can't be empty!",
                                        Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                try {
                                    val daysPresent =
                                        if (subjectScreenUiState.presents.value.isBlank()) 0 else subjectScreenUiState.presents.value.toLong()
                                    val daysAbsent =
                                        if (subjectScreenUiState.absents.value.isBlank()) 0 else subjectScreenUiState.absents.value.toLong()
                                    val lat = if(subjectScreenUiState.latitude.value.isBlank()) null  else  subjectScreenUiState.latitude.value.toDouble()
                                    val lon = if(subjectScreenUiState.longitude.value.isBlank()) null  else  subjectScreenUiState.longitude.value.toDouble()
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
                        }
                    ) {
                        Text(stringResource(R.string.add))
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    TextButton(
                        onClick = {
                            subjectScreenUiState.classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                            subjectScreenUiState.clearDialogFields()
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    )
}