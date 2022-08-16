package com.example.classattendanceapp.presenter.screens.subjectsscreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch

@Composable
fun SubjectScreenAlertDialog(
    editingSubject: Int?,
    subjectNameTextField: String,
    initialPresent: String,
    initialAbsent: String,
    latitude : String,
    longitude : String,
    range: String,
    changeShowLocationSelectionPopup: (Boolean) -> Unit,
    classAttendanceViewModel: ClassAttendanceViewModel
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var lsubjectNameTextField by remember{
        mutableStateOf(subjectNameTextField)
    }
    var linitialPresent by remember{
        mutableStateOf(initialPresent)
    }
    var linitialAbsent by remember{
        mutableStateOf(initialAbsent)
    }
    var leditingSubject by remember{
        mutableStateOf(editingSubject)
    }
    var llatitude by remember{
        mutableStateOf(latitude)
    }
    var llongitude by remember{
        mutableStateOf(longitude)
    }
    var lrange by remember{
        mutableStateOf(range)
    }

    AlertDialog(
        onDismissRequest = {
            classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
            lsubjectNameTextField = ""
            linitialPresent = 0.toString()
            linitialAbsent = 0.toString()
            leditingSubject = null
            llatitude = ""
            llongitude = ""
            lrange = ""
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
                    value = lsubjectNameTextField,
                    onValueChange = { lsubjectNameTextField = it },
                    label = {
                        Text(stringResource(R.string.subject_name) + " (Required)")
                    },
                    maxLines = 1,
                    trailingIcon = {
                        if(lsubjectNameTextField.isNotEmpty()){
                            IconButton(
                                onClick = {
                                    lsubjectNameTextField = ""
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
                            value = linitialPresent,
                            onValueChange = {
                                linitialPresent = it
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
                                            linitialPresent =
                                                (linitialPresent.toLong() + 1).toString()
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            if (linitialPresent.toLong() > 0) {
                                                linitialPresent =
                                                    (linitialPresent.toLong() - 1).toString()
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
                            value = linitialAbsent,
                            onValueChange = {
                                linitialAbsent = it
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
                                            linitialAbsent =
                                                (linitialAbsent.toLong() + 1).toString()
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            if (linitialAbsent.toLong() > 0) {
                                                linitialAbsent =
                                                    (linitialAbsent.toLong() - 1).toString()
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
                            value = llatitude,
                            onValueChange = {
                                llatitude = it
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
                            if(llatitude.isNotEmpty()){
                                Icon(
                                    modifier = Modifier.clickable {
                                        llatitude = ""
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
                            value = llongitude,
                            onValueChange = {
                                llongitude = it
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
                            if(llongitude.isNotEmpty()){
                                Icon(
                                    modifier = Modifier.clickable {
                                        llongitude = ""
                                    },
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(

                    value = lrange,
                    onValueChange = {
                        lrange = it
                    },
                    label = {
                        Text(stringResource(R.string.rangeInM))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    maxLines = 1,
                    trailingIcon = {
                        if(lrange.isNotEmpty()){
                            IconButton(
                                onClick = {
                                    lrange = ""
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
                            coroutineScope.launch {
                                if (lsubjectNameTextField.isBlank()) {
                                    Toast.makeText(context,
                                        "Subject Name can't be empty!",
                                        Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                try {
                                    val daysPresent =
                                        if (linitialPresent.isBlank()) 0 else linitialPresent.toLong()
                                    val daysAbsent =
                                        if (linitialAbsent.isBlank()) 0 else linitialAbsent.toLong()
                                    val lat = if(llatitude.isBlank()) null else llatitude.toDouble()
                                    val lon = if(llongitude.isBlank()) null else llongitude.toDouble()
                                    val ran = if(lrange.isBlank()) null else lrange.toDouble()


                                    if (leditingSubject != null) {
                                        val subject = classAttendanceViewModel.getSubjectWithId(
                                            leditingSubject!!)!!
                                        classAttendanceViewModel.updateSubject(
                                            Subject(
                                                _id = subject._id,
                                                subjectName = lsubjectNameTextField.trim(),
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
                                                subjectName = lsubjectNameTextField.trim(),
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

                                    lsubjectNameTextField = ""
                                    linitialPresent = 0.toString()
                                    linitialAbsent = 0.toString()
                                    leditingSubject = null
                                    llatitude = ""
                                    llongitude = ""
                                    lrange = ""

                                } catch (e: NumberFormatException) {
                                    Toast.makeText(context,
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
                            lsubjectNameTextField = ""
                            linitialPresent = 0.toString()
                            linitialAbsent = 0.toString()
                            leditingSubject = null
                            llatitude = ""
                            llongitude = ""
                            lrange = ""
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    )
}