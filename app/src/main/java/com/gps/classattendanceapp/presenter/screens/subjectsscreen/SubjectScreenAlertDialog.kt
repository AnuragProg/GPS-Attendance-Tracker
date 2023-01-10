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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    var isLoadingCurrentLocation by remember{mutableStateOf(false)}
    var showSubjectNameError by remember{mutableStateOf(false)}
    var showCoordinateError by remember{mutableStateOf(false)}
    var showPresentsError by remember{mutableStateOf(false)}
    var showAbsentsError by remember{mutableStateOf(false)}
    var showRangeError by remember{mutableStateOf(false)}

//    LaunchedEffect(Unit){
//        if(subjectScreenUiState.subjectToEdit.value!=null){
//            subjectScreenUiState.fillFieldsWithSubjectToEditFields()
//        }
//    }


//    val subjectName by subjectScreenUiState.classAttendanceViewModel.subjectName.collectAsStateWithLifecycle()
//    val presents by subjectScreenUiState.classAttendanceViewModel.presents.collectAsStateWithLifecycle()
//    val absents by subjectScreenUiState.classAttendanceViewModel.absents.collectAsStateWithLifecycle()
//    val latitude by subjectScreenUiState.classAttendanceViewModel.latitude.collectAsStateWithLifecycle()
//    val longitude by subjectScreenUiState.classAttendanceViewModel.longitude.collectAsStateWithLifecycle()
//    val range by subjectScreenUiState.classAttendanceViewModel.range.collectAsStateWithLifecycle()

    var subjectName by remember{mutableStateOf("")}
    var presents by remember{ mutableStateOf("0") }
    var absents by   remember{ mutableStateOf("0") }
    var latitude by remember{ mutableStateOf("") }
    var longitude  by remember{ mutableStateOf("") }
    var range by remember{ mutableStateOf("") }

    LaunchedEffect(Unit){
        if(subjectScreenUiState.subjectToEditId == null)
            return@LaunchedEffect

        val subject = subjectScreenUiState.classAttendanceViewModel.getSubjectWithId(subjectScreenUiState.subjectToEditId!!)!!
        subjectName = subject.subjectName
        presents = subject.daysPresent.toString()
        absents = subject.daysAbsent.toString()
        latitude = subject.latitude?.toString() ?: ""
        longitude = subject.longitude?.toString() ?: ""
        range = subject.range?.toString() ?: ""
    }


    AlertDialog(
        shape = RoundedCornerShape(10.dp),
        onDismissRequest = {
            subjectScreenUiState.subjectToEditId = null
//            subjectScreenUiState.classAttendanceViewModel.resetSubjectToEdit()
//            subjectScreenUiState.classAttendanceViewModel.clearSubjectAlertDialogFields()
            subjectScreenUiState.classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
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
                    value = subjectName,
                    onValueChange = {subjectName = it},
                    label = {
                        Text(stringResource(R.string.subject_name))
                    },
                    maxLines = 1,
                    trailingIcon = {
                        if(subjectName.isNotEmpty()){
                            IconButton(
                                onClick = {
//                                    subjectScreenUiState.classAttendanceViewModel.setSubjectName("")
                                    subjectName = ""
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
                            value = presents,
                            onValueChange = {presents = it},
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
//                                            subjectScreenUiState.classAttendanceViewModel.setPresents((presents.toLong() + 1).toString())
                                            presents = (presents.toLong()+1).toString()
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            if (presents.toLong() > 0) {
//                                               subjectScreenUiState.classAttendanceViewModel.setPresents((presents.toLong() - 1).toString())
                                                presents = (presents.toLong()-1).toString()
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
                            value = absents,
                            onValueChange = {absents=it},
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
//                                            subjectScreenUiState.classAttendanceViewModel.setAbsents((absents.toLong() + 1).toString())
                                            absents = (absents.toLong() + 1).toString()
                                        } catch (e: NumberFormatException) {

                                        }
                                    },
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = null
                                )
                                Icon(
                                    modifier = Modifier.clickable {
                                        try {
                                            if (absents.toLong() > 0) {
//                                                subjectScreenUiState.classAttendanceViewModel.setAbsents((absents.toLong() - 1).toString())
                                                absents = (absents.toLong() -1).toString()
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
                            value = latitude,
                            onValueChange = {latitude = it},
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
                            if(latitude.isNotEmpty()){
                                Icon(
                                    modifier = Modifier.clickable {
//                                        subjectScreenUiState.classAttendanceViewModel.setLatitude("")
                                        latitude = ""
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
                            value = longitude,
                            onValueChange = {longitude=it},
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
                            if(longitude.isNotEmpty()){
                                Icon(
                                    modifier = Modifier.clickable {
//                                        subjectScreenUiState.classAttendanceViewModel.setLongitude("")
                                        longitude = ""
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
                    value = range,
                    onValueChange = {range=it},
                    label = {
                        Text(stringResource(R.string.rangeInM))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    maxLines = 1,
                    trailingIcon = {
                        if(range.isNotEmpty()){
                            IconButton(
                                onClick = {
//                                    subjectScreenUiState.classAttendanceViewModel.setRange("")
                                    range=""
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch{
                                val context = subjectScreenUiState.context
                                isLoadingCurrentLocation = true
                                FusedLocation.apply{
                                    if(!isGpsEnabled(context)){
                                        Toast.makeText(context, "Please enable GPS", Toast.LENGTH_SHORT).show()
                                        return@apply
                                    }
                                    if(!isPermissionGiven(context)){
                                        Toast.makeText(context, "Please provide location permission to use this feature", Toast.LENGTH_SHORT).show()
                                        return@apply
                                    }

                                    val locationResult = withTimeoutOrNull(5000){
                                        getLocationFlow(context).first{it!=null}
                                    }
                                    if(locationResult == null){
                                        Toast.makeText(context, "Unable to fetch current location", Toast.LENGTH_SHORT).show()
                                        return@apply
                                    }
//                                    subjectScreenUiState.classAttendanceViewModel.apply{
//                                        setLatitude(locationResult.latitude.toString())
//                                        setLongitude(locationResult.longitude.toString())
//                                    }
                                    latitude = locationResult.latitude.toString()
                                    longitude = locationResult.longitude.toString()
                                }
                                isLoadingCurrentLocation = false
                            }
                        }
                    ) {
                        Text("Use current location")
                    }
                    if(isLoadingCurrentLocation){
                        Spacer(Modifier.width(10.dp))
                        CircularProgressIndicator(modifier=Modifier.size(15.dp))
                    }
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
                                if (subjectName.isBlank()) {
                                    showSubjectNameError = true
                                    return@launch
                                }
                                showSubjectNameError = false
                                try {
                                    val daysPresent =
                                        if (presents.isBlank()) 0 else presents.toLong()
                                    val daysAbsent =
                                        if (absents.isBlank()) 0 else absents.toLong()
                                    val lat = if(latitude.isBlank()) null  else  latitude.toDouble()
                                    val lon = if(longitude.isBlank()) null  else  longitude.toDouble()
                                    if(lat!=null && lon == null){
                                        showCoordinateError = true
                                        return@launch
                                    }
                                    showCoordinateError = false
                                    val ran = if(range.isBlank()) null else range.toDouble()
                                    if(lat != null && lon != null && ran == null){
                                        showRangeError = true
                                        return@launch
                                    }
                                    showRangeError = false

                                    if (subjectScreenUiState.subjectToEditId!= null) {
                                        val subject = subjectScreenUiState.classAttendanceViewModel.getSubjectWithId(subjectScreenUiState.subjectToEditId!!)!!
                                        subjectScreenUiState.classAttendanceViewModel.updateSubject(
                                            Subject(
                                                _id = subject._id,
                                                subjectName = subjectName.trim(),
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
                                                subjectName = subjectName.trim(),
                                                daysPresent = daysPresent,
                                                daysAbsent = daysAbsent,
                                                latitude = lat,
                                                longitude = lon,
                                                range = ran
                                            )
                                        )
                                    }
//                                    subjectScreenUiState.classAttendanceViewModel.resetSubjectToEdit()
//                                    subjectScreenUiState.classAttendanceViewModel.clearSubjectAlertDialogFields()

                                } catch (e: NumberFormatException) {
                                    Toast.makeText(subjectScreenUiState.context,
                                        "Please enter valid information!",
                                        Toast.LENGTH_SHORT).show()
                                }
                                subjectScreenUiState.subjectToEditId = null
                                subjectScreenUiState.classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
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
                            subjectScreenUiState.subjectToEditId = null
                            subjectScreenUiState.classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
//                            subjectScreenUiState.classAttendanceViewModel.clearSubjectAlertDialogFields()
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