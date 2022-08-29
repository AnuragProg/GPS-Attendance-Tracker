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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch

@Composable
fun SubjectScreenAlertDialog(
    subjectToEdit: ModifiedSubjects?,
    resetSubjectToEdit: ()->Unit,
    changeShowLocationSelectionPopup: (Boolean) -> Unit,
    classAttendanceViewModel: ClassAttendanceViewModel,
    latitudeFromMap: String?,
    longitudeFromMap: String?,
    changeLatitudeFromMap:(String?)->Unit,
    changeLongitudeFromMap:(String?)->Unit
){

    val uiState = if(subjectToEdit!=null){
        rememberSubjectScreenAlertDialogUiState(
            subjectName = subjectToEdit.subjectName,
            presents = subjectToEdit.daysPresent.toString(),
            absents = subjectToEdit.daysAbsent.toString(),
            subjectId = subjectToEdit._id,
            range = if(subjectToEdit.range ==null) "" else subjectToEdit.range.toString()
        )
    }else{
        rememberSubjectScreenAlertDialogUiState()
    }

    LaunchedEffect(Unit){
        if(subjectToEdit==null){
            uiState.latitude.value = ""
            uiState.longitude.value = ""
        }else{
            uiState.latitude.value = if(subjectToEdit.latitude==null) "" else subjectToEdit.latitude.toString()
            uiState.longitude.value = if(subjectToEdit.longitude==null) "" else subjectToEdit.longitude.toString()
        }
    }

    LaunchedEffect(latitudeFromMap, longitudeFromMap){
        if(latitudeFromMap!=null && longitudeFromMap!=null){
            uiState.latitude.value = latitudeFromMap
            uiState.longitude.value = longitudeFromMap
        }
    }
    

    AlertDialog(
        onDismissRequest = {
            classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
            resetSubjectToEdit()
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.add_new_subject),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                OutlinedTextField(
                    value = uiState.subjectName.value,
                    onValueChange = { uiState.subjectName.value = it },
                    label = {
                        Text(stringResource(R.string.subject_name) + " (Required)")
                    },
                    maxLines = 1,
                    trailingIcon = {
                        if(uiState.subjectName.value.isNotEmpty()){
                            IconButton(
                                onClick = {
                                    uiState.subjectName.value = ""
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
                            value = uiState.presents.value,
                            onValueChange = {
                                uiState.presents.value = it
                            },
                            label = {
                                Text(stringResource(R.string.presents))
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
                                            uiState.presents.value =
                                                (uiState.presents.value.toLong() + 1).toString()
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            if (uiState.presents.value.toLong() > 0) {
                                                uiState.presents.value =
                                                    (uiState.presents.value.toLong() - 1).toString()
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
                            value = uiState.absents.value,
                            onValueChange = {
                                uiState.absents.value = it
                            },
                            label = {
                                Text(stringResource(R.string.absents))
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
                                            uiState.absents.value =
                                                (uiState.absents.value.toLong() + 1).toString()
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            if (uiState.absents.value.toLong() > 0) {
                                                uiState.absents.value =
                                                    (uiState.absents.value.toLong() - 1).toString()
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
                            value = uiState.latitude.value,
                            onValueChange = {
                                uiState.latitude.value = it
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
                            if(uiState.latitude.value.isNotEmpty()){
                                Icon(
                                    modifier = Modifier.clickable {
                                        uiState.latitude.value = ""
                                        changeLatitudeFromMap(null)
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
                            value = uiState.longitude.value,
                            onValueChange = {
                                uiState.longitude.value=it
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
                            if(uiState.longitude.value.isNotEmpty()){
                                Icon(
                                    modifier = Modifier.clickable {
                                        uiState.longitude.value = ""
                                        changeLongitudeFromMap(null)
                                    },
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(

                    value = uiState.range.value,
                    onValueChange = {
                        uiState.range.value = it
                    },
                    label = {
                        Text(stringResource(R.string.rangeInM))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    maxLines = 1,
                    trailingIcon = {
                        if(uiState.range.value.isNotEmpty()){
                            IconButton(
                                onClick = {
                                    uiState.range.value = ""
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
                OutlinedButton(
                    onClick = {
                        changeShowLocationSelectionPopup(true)
                    }
                ) {
                    Text("Select Location From Map")
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
                            uiState.coroutineScope.launch {
                                if (uiState.subjectName.value.isBlank()) {
                                    Toast.makeText(uiState.context,
                                        "Subject Name can't be empty!",
                                        Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                try {
                                    val daysPresent =
                                        if (uiState.presents.value.isBlank()) 0 else uiState.presents.value.toLong()
                                    val daysAbsent =
                                        if (uiState.absents.value.isBlank()) 0 else uiState.absents.value.toLong()
                                    val lat = latitudeFromMap?.toDouble()
                                    val lon = longitudeFromMap?.toDouble()
                                    val ran = if(uiState.range.value.isBlank()) null else uiState.range.value.toDouble()


                                    if (uiState.subjectId.value != null) {
                                        val subject = classAttendanceViewModel.getSubjectWithId(
                                            uiState.subjectId.value!!)!!
                                        classAttendanceViewModel.updateSubject(
                                            Subject(
                                                _id = subject._id,
                                                subjectName = uiState.subjectName.value.trim(),
                                                daysPresent = daysPresent,
                                                daysAbsent = daysAbsent,
                                                daysPresentOfLogs = subject.daysPresentOfLogs,
                                                daysAbsentOfLogs = subject.daysAbsentOfLogs,
                                                latitude = lat,
                                                longitude = lon,
                                                range = ran
                                            )
                                        )
                                    } else {
                                        classAttendanceViewModel.insertSubject(
                                            Subject(
                                                _id = 0,
                                                subjectName = uiState.subjectName.value.trim(),
                                                daysPresent = daysPresent,
                                                daysAbsent = daysAbsent,
                                                daysPresentOfLogs = 0,
                                                daysAbsentOfLogs = 0,
                                                latitude = lat,
                                                longitude = lon,
                                                range = ran
                                            )
                                        )
                                    }
                                    classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                                    resetSubjectToEdit()

                                } catch (e: NumberFormatException) {
                                    Toast.makeText(uiState.context,
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
                            classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                            resetSubjectToEdit()
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    )
}