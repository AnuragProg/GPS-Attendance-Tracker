@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.subjectsscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
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
        SubjectScreenAlertDialog(
            editingSubject = editingSubject,
            subjectNameTextField = subjectNameTextField,
            initialPresent = initialPresent,
            initialAbsent = initialAbsent,
            latitude = latitude,
            longitude = longitude,
            range = range,
            changeShowLocationSelectionPopup = {showLocationSelectionPopUp=it},
            classAttendanceViewModel = classAttendanceViewModel
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