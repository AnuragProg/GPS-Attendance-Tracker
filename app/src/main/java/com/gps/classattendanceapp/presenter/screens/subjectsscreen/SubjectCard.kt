package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun SubjectCard(
    subject: com.gps.classattendanceapp.domain.models.ModifiedSubjects,
    classAttendanceViewModel: ClassAttendanceViewModel,
    onSubjectSelected: (Boolean)->Unit,
    onClick : () -> Unit

){
    var showOverFlowMenu by remember { mutableStateOf(false) }
    var showAdditionalCardDetails by remember { mutableStateOf(false) }

    val startAttendanceArcAnimation =
        classAttendanceViewModel.startAttendanceArcAnimation.collectAsStateWithLifecycle()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    showAdditionalCardDetails = !showAdditionalCardDetails
                    onSubjectSelected(false)
                    onClick()
                },
                onLongClick = {
                    onSubjectSelected(true)
                }
            )
            .padding(10.dp)
    ) {
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
                    text = subject.subjectName,
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
                            targetValue = if (startAttendanceArcAnimation.value) subject.attendancePercentage.toFloat() else 0f,
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

//            AnimatedVisibility(
//                visible = showAdditionalCardDetails
//            ) {
//
//                val modifier = Modifier.fillMaxWidth()
//                val horizontalArrangement = Arrangement.Center
//
//                Box{
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(10.dp),
//                        horizontalAlignment = Alignment.Start,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Latitude :")
//                            Text(
//                                if (subject.latitude == null) {
//                                    "Unknown"
//                                } else {
//                                    "${subject.latitude}"
//                                }
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Longitude :")
//                            Text(
//                                if (subject.longitude == null) {
//                                    "Unknown"
//                                } else {
//                                    "${subject.longitude}"
//                                }
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Range(in meter) :")
//                            Text(
//                                if (subject.range == null) {
//                                    "Unknown"
//                                } else {
//                                    "${subject.range}"
//                                }
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Days present :")
//                            Text("${subject.daysPresent}")
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Days present through logs :")
//                            Text("${subject.daysPresentOfLogs}")
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Days absent :")
//                            Text("${subject.daysAbsent}")
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Days absent through logs :")
//                            Text("${subject.daysAbsentOfLogs}")
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Total presents :")
//                            Text("${subject.totalPresents}")
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Total absents :")
//                            Text("${subject.totalAbsents}")
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//
//                        Row(
//                            modifier = modifier,
//                            horizontalArrangement = horizontalArrangement
//                        ){
//                            Text("Total days :")
//                            Text("${subject.totalDays}")
//                        }
//                    }
//                }
//
//            }
        }
    }
}