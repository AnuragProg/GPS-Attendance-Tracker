@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.subjectsscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.excel.Excel
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalLifecycleComposeApi::class,
    ExperimentalMaterialApi::class)
@Composable
fun SubjectsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
) {

    val context = LocalContext.current

    val subjectsList = classAttendanceViewModel.subjectsList.collectAsStateWithLifecycle()

    val searchBarText = classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle()

    val isInitialSubjectDataRetrievalDone =
        classAttendanceViewModel.isInitialSubjectDataRetrievalDone.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val showAddSubjectDialog = classAttendanceViewModel.floatingButtonClicked.collectAsStateWithLifecycle()

    var subjectNameTextField by remember {
        mutableStateOf("")
    }

    var initialPresent by remember {
        mutableStateOf("0")
    }
    var initialAbsent by remember {
        mutableStateOf("0")
    }

    var latitude by remember {
        mutableStateOf("")
    }
    var longitude by remember {
        mutableStateOf("")
    }
    var range by remember {
        mutableStateOf("")
    }

    var showLocationSelectionPopUp by remember{
        mutableStateOf(false)
    }

    val startAttendanceArcAnimation =
        classAttendanceViewModel.startAttendanceArcAnimation.collectAsState()
    /*
    number -> subject Id to updated that subject
    null -> if(null)not updating else updating
     */
    var editingSubject by remember {
        mutableStateOf<Int?>(null)
    }


    if(showLocationSelectionPopUp){
        LocationSelectionPopUp(
            changeLatitude = {
                latitude = it.toString()
            },
            changeLongitude = {
                longitude = it.toString()
            },
            changeLocationSelectionVisibility ={
                showLocationSelectionPopUp = it
            }
        )
    }

    // Alert Dialog -> To add new subject
    if (showAddSubjectDialog.value) {
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                subjectNameTextField = ""
                initialPresent = 0.toString()
                initialAbsent = 0.toString()
                editingSubject = null
                latitude = ""
                longitude = ""
                range = ""
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
                        value = subjectNameTextField,
                        onValueChange = { subjectNameTextField = it },
                        label = {
                            Text(stringResource(R.string.subject_name) + " (Required)")
                        },
                        maxLines = 1,
                        trailingIcon = {
                            if(subjectNameTextField.isNotEmpty()){
                                IconButton(
                                    onClick = {
                                        subjectNameTextField = ""
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
                                modifier = Modifier.defaultMinSize(minWidth = 40.dp),
                                value = initialPresent,
                                onValueChange = {
                                    initialPresent = it
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
                                                initialPresent =
                                                    (initialPresent.toLong() + 1).toString()
                                            } catch (e: NumberFormatException) {

                                            }
                                        },
                                        imageVector = Icons.Filled.ArrowDropUp,
                                        contentDescription = null
                                    )
                                    Icon(
                                        modifier = Modifier.clickable {
                                            try {
                                                if (initialPresent.toLong() > 0) {
                                                    initialPresent =
                                                        (initialPresent.toLong() - 1).toString()
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
                                modifier = Modifier.defaultMinSize(minWidth = 40.dp),
                                value = initialAbsent,
                                onValueChange = {
                                    initialAbsent = it
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
                                                initialAbsent =
                                                    (initialAbsent.toLong() + 1).toString()
                                            } catch (e: NumberFormatException) {

                                            }
                                        },
                                        imageVector = Icons.Filled.ArrowDropUp,
                                        contentDescription = null
                                    )
                                    Icon(
                                        modifier = Modifier.clickable {
                                            try {
                                                if (initialAbsent.toLong() > 0) {
                                                    initialAbsent =
                                                        (initialAbsent.toLong() - 1).toString()
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
                                modifier = Modifier.defaultMinSize(minWidth = 50.dp),
                                value = latitude,
                                onValueChange = {
                                    latitude = it
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
                                if(latitude.isNotEmpty()){
                                    Icon(
                                        modifier = Modifier.clickable {
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
                                modifier = Modifier.defaultMinSize(minWidth = 50.dp),
                                value = longitude,
                                onValueChange = {
                                    longitude = it
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
                                if(longitude.isNotEmpty()){
                                    Icon(
                                        modifier = Modifier.clickable {
                                            longitude = ""
                                        },
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                    OutlinedTextField(

                        value = range,
                        onValueChange = {
                            range = it
                        },
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
                                        range = ""
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
                            showLocationSelectionPopUp = true
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
                                    if (subjectNameTextField.isBlank()) {
                                        Toast.makeText(context,
                                            "Subject Name can't be empty!",
                                            Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                    try {
                                        val daysPresent =
                                            if (initialPresent.isBlank()) 0 else initialPresent.toLong()
                                        val daysAbsent =
                                            if (initialAbsent.isBlank()) 0 else initialAbsent.toLong()
                                        val lat = if(latitude.isBlank()) null else latitude.toDouble()
                                        val lon = if(longitude.isBlank()) null else longitude.toDouble()
                                        val ran = if(range.isBlank()) null else range.toDouble()


                                        if (editingSubject != null) {
                                            val subject = classAttendanceViewModel.getSubjectWithId(
                                                editingSubject!!)!!
                                            classAttendanceViewModel.updateSubject(
                                                Subject(
                                                    _id = subject._id,
                                                    subjectName = subjectNameTextField,
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
                                                    subjectName = subjectNameTextField,
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

                                        subjectNameTextField = ""
                                        initialPresent = 0.toString()
                                        initialAbsent = 0.toString()
                                        editingSubject = null
                                        latitude = ""
                                        longitude = ""
                                        range = ""

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
                                subjectNameTextField = ""
                                initialPresent = 0.toString()
                                initialAbsent = 0.toString()
                                editingSubject = null
                                latitude = ""
                                longitude = ""
                                range = ""
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            }
        )
    }
    if (subjectsList.value.isEmpty() && isInitialSubjectDataRetrievalDone.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(id = R.drawable.subjects),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(5.dp),
                text = stringResource(R.string.no_subjects),
                color = Color.White
            )
        }
    } else if (subjectsList.value.isEmpty() && !isInitialSubjectDataRetrievalDone.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Original Ui
        Box{
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    subjectsList.value.filter {
                        if (searchBarText.value.isNotBlank()) {
                            searchBarText.value.lowercase() in it.subjectName.lowercase()
                        } else {
                            true
                        }
                    },
                    key = {it._id}
                ) { currentSubject ->
                    var showOverFlowMenu by remember { mutableStateOf(false) }
                    var showAdditionalCardDetails by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement()
                            .combinedClickable(
                                onClick = {
                                    showAdditionalCardDetails = !showAdditionalCardDetails
                                },
                                onLongClick = {
                                    showOverFlowMenu = true
                                }
                            )
                    ) {
                        DropdownMenu(
                            expanded = showOverFlowMenu,
                            onDismissRequest = {
                                showOverFlowMenu = false
                            }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    coroutineScope.launch {
                                        classAttendanceViewModel.deleteSubject(
                                            currentSubject._id,
                                            context
                                        )
                                    }
                                    showOverFlowMenu = false
                                }
                            ) {
                                Text(stringResource(R.string.delete))
                            }
                            DropdownMenuItem(
                                onClick = {
                                    showOverFlowMenu = false
                                    subjectNameTextField = currentSubject.subjectName
                                    initialPresent = currentSubject.daysPresent.toString()
                                    initialAbsent = currentSubject.daysAbsent.toString()
                                    editingSubject = currentSubject._id
                                    latitude =
                                        if (currentSubject.latitude == null) "" else currentSubject.latitude.toString()
                                    longitude =
                                        if (currentSubject.longitude == null) "" else currentSubject.longitude.toString()
                                    range = if (currentSubject.range == null) "" else currentSubject.range.toString()
                                    classAttendanceViewModel.changeFloatingButtonClickedState(true)
                                }
                            ) {
                                Text(stringResource(R.string.edit))
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    modifier = Modifier.width(200.dp),
                                    text = currentSubject.subjectName,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {

                                    Box(
                                        modifier = Modifier.size(60.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val target = animateFloatAsState(
                                            targetValue = if (startAttendanceArcAnimation.value) currentSubject.attendancePercentage.toFloat() else 0f,
                                            animationSpec = tween(
                                                durationMillis = 1000,
                                                delayMillis = 50
                                            )
                                        )

                                        val arcColor = animateColorAsState(
                                            targetValue = if (target.value < 75f) Color.Red else Color.Green
                                        )
                                        Canvas(modifier = Modifier.size(60.dp)) {
                                            drawArc(
                                                color = arcColor.value,
                                                startAngle = 270f,
                                                sweepAngle = 360 * target.value / 100,
                                                useCenter = false,
                                                size = this.size,
                                                style = Stroke(15f, cap = StrokeCap.Round)
                                            )
                                        }
                                        Text("${String.format("%.1f", target.value)}%")
                                    }
                                    LaunchedEffect(Unit) {
                                        if (!startAttendanceArcAnimation.value) {
                                            classAttendanceViewModel.startAttendanceArcAnimationInitiate()
                                        }
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = showAdditionalCardDetails
                            ) {

                                Column{

                                    Box{

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.Start,
                                            verticalArrangement = Arrangement.Center
                                        ) {

                                            Text("Latitude :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Longitude :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Range(in meter) :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Days Present :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Days Present Through Log :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Days Absent :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Days Absent Through Log :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Total Presents :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Total Absents :")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("Total Days :")

                                        }
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.End,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                if (currentSubject.latitude == null) {
                                                    "Unknown"
                                                } else {
                                                    "${currentSubject.latitude}"
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text(
                                                if (currentSubject.longitude == null) {
                                                    "Unknown"
                                                } else {
                                                    "${currentSubject.longitude}"
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text(
                                                if (currentSubject.range == null) {
                                                    "Unknown"
                                                } else {
                                                    "${currentSubject.range}"
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("${currentSubject.daysPresent}")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("${currentSubject.daysPresentOfLogs}")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("${currentSubject.daysAbsent}")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("${currentSubject.daysAbsentOfLogs}")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("${currentSubject.totalPresents}")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("${currentSubject.totalAbsents}")
                                            Spacer(modifier = Modifier.height(5.dp))

                                            Text("${currentSubject.totalDays}")
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}