package com.example.classattendanceapp.presenter.screens.subjectsscreen

import android.util.Log
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
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    val context = LocalContext.current

    val subjectsList = classAttendanceViewModel.subjectsList.collectAsState()

    val isInitialSubjectDataRetrievalDone = classAttendanceViewModel.isInitialSubjectDataRetrievalDone.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val showAddSubjectDialog = classAttendanceViewModel.floatingButtonClicked.collectAsState()

    var subjectNameTextField by remember{
        mutableStateOf("")
    }

    var initialPresent by remember{
        mutableStateOf("0")
    }
    var initialAbsent by remember{
        mutableStateOf("0")
    }

    val startAttedanceArcAnimation = classAttendanceViewModel.startAttendanceArcAnimation.collectAsState()
    /*
    number -> subject Id to updated that subject
    null -> if(null)not updating else updating
     */
    var editingSubject by remember{
        mutableStateOf<Int?>(null)
    }




    // Alert Dialog -> To add new subject
    if(showAddSubjectDialog.value){
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                subjectNameTextField = ""
                initialPresent = 0.toString()
                initialAbsent = 0.toString()
                editingSubject = null
            },
            text = {
                Column{
                    Text(
                        text = stringResource(R.string.add_new_subject),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = subjectNameTextField,
                        onValueChange = { subjectNameTextField = it },
                        label = {
                            Text(stringResource(R.string.subject_name))
                        },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
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
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
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
                        singleLine = true
                    )
                }
            },
            buttons = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterEnd
                ){
                    Row{
                        TextButton(
                            onClick = {
                                coroutineScope.launch{
                                    if(editingSubject!=null){
                                        classAttendanceViewModel.updateSubject(
                                            Subject(
                                                _id = editingSubject!!,
                                                subjectName = subjectNameTextField,
                                                daysPresent = initialPresent.toLong(),
                                                daysAbsent = initialAbsent.toLong()
                                            )
                                        )
                                    }else{
                                        classAttendanceViewModel.insertSubject(
                                            Subject(
                                                _id = 0,
                                                subjectName = subjectNameTextField,
                                                daysPresent = initialPresent.toLong(),
                                                daysAbsent = initialAbsent.toLong()
                                            )
                                        )
                                    }
                                    subjectNameTextField = ""
                                    initialPresent = 0.toString()
                                    initialAbsent = 0.toString()
                                    editingSubject = null
                                }
                                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
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
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            }
        )
    }
    
    if(subjectsList.value.isEmpty() && isInitialSubjectDataRetrievalDone.value){
        Log.d("subjects", "Showing no subjects icon")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(id = R.drawable.subjects),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(5.dp),
                text = stringResource(R.string.no_subjects)
            )
        }
    } else if(subjectsList.value.isEmpty() && !isInitialSubjectDataRetrievalDone.value){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }else{
        Log.d("subjects", "Showing subjects list")
        // Original Ui
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                items(subjectsList.value){
                    var showOverFlowMenu by remember{ mutableStateOf(false) }
                    var showAdditionalCardDetails by remember{ mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .animateItemPlacement()
                            .combinedClickable(
                                onClick = {
                                    showAdditionalCardDetails = !showAdditionalCardDetails
                                    Log.d("debugging",
                                        "showing additional carddetails = $showAdditionalCardDetails")

                                },
                                onLongClick = {
                                    Log.d("debugging", "Clicked for long")
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
                                    coroutineScope.launch{
                                        classAttendanceViewModel.deleteSubject(
                                            it._id,
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
                                    subjectNameTextField = it.subjectName
                                    initialPresent = it.daysPresent.toString()
                                    initialAbsent = it.daysAbsent.toString()
                                    editingSubject = it._id
                                    classAttendanceViewModel.changeFloatingButtonClickedState(true)
                                }
                            ) {
                                Text(stringResource(R.string.edit))
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                            ,
                        ){
                            Box(
                                contentAlignment = Alignment.CenterStart
                            ){
                                Text(
                                    modifier = Modifier.width(200.dp),
                                    text = it.subjectName,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ){

                                    Box(
                                        modifier = Modifier.size(60.dp),
                                        contentAlignment = Alignment.Center
                                    ){
                                        val target = animateFloatAsState(
                                            targetValue = if(startAttedanceArcAnimation.value) it.attendancePercentage.toFloat() else 0f,
                                            animationSpec = tween(
                                                durationMillis = 1000,
                                                delayMillis = 50
                                            )
                                        )

                                        val arcColor = animateColorAsState(
                                            targetValue = if(target.value < 75f) Color.Red else Color.Green
                                        )
                                        Canvas(modifier = Modifier.size(60.dp)){
                                            drawArc(
                                                color = arcColor.value,
                                                startAngle = 270f,
                                                sweepAngle = 360 * target.value/100,
                                                useCenter = false,
                                                size = this.size,
                                                style = Stroke(15f, cap = StrokeCap.Round)
                                            )
                                        }
                                        Text("${String.format("%.2f", target.value)}%")
                                    }
                                    LaunchedEffect(Unit){
                                        if(!startAttedanceArcAnimation.value){
                                            classAttendanceViewModel.startAttendanceArcAnimationInitiate()
                                        }
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = showAdditionalCardDetails
                            ) {
                                Box(
                                ){
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("Days Present :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Days Present Through Logs :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Days Absent :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Days Absent Through Logs :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Total Presents :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Total Absents :")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Total Days :")

                                    }
                                }
                                Box(
                                ){
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.Center
                                    ){
                                        Text("${it.daysPresent}")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("${ it.daysPresentOfLogs }")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("${it.daysAbsent}")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("${it.daysAbsentOfLogs}")
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            "${
                                                it.daysPresent + it.daysPresentOfLogs
                                            }"
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            "${
                                                it.daysAbsent + it.daysAbsentOfLogs
                                            }"
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            "${
                                                it.daysPresent + it.daysPresentOfLogs + it.daysAbsent + it.daysAbsentOfLogs
                                            }"
                                        )
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

