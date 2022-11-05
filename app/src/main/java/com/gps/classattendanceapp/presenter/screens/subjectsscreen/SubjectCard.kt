package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import com.gps.classattendanceapp.ui.theme.Dimens

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
            .padding(10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val lottieComposition by rememberLottieComposition(
                    spec = getSubjectStatusLottieCompositionSpec(subject.attendancePercentage)
                )
                val progress by animateLottieCompositionAsState(
                    composition = lottieComposition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    modifier = Modifier.size(Dimens.dimen.subject_attendance_status_lottie_size),
                    composition = lottieComposition,
                    progress = { progress }
                )

            }
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
                        if(target.value == 0f){
                            Text("--:- %")
                        }else{
                            Text("${String.format("%.1f", target.value)} %")
                        }
                    }
                    LaunchedEffect(Unit) {
                        if (!startAttendanceArcAnimation.value) {
                            classAttendanceViewModel.startAttendanceArcAnimationInitiate()
                        }
                    }
                }
            }
        }




    }
}