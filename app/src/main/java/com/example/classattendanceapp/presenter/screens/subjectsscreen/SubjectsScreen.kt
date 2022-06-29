package com.example.classattendanceapp.presenter.screens.subjectsscreen

import android.util.Log
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classattendanceapp.R
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.presenter.utils.ProcessState
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){


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


    // Alert Dialog -> To add new subject
    if(showAddSubjectDialog.value){
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                subjectNameTextField = ""
                initialPresent = 0.toString()
                initialAbsent = 0.toString()
            },
            text = {
                Column{
                    Text(
                        text = "Add new Subject",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = subjectNameTextField,
                        onValueChange = { subjectNameTextField = it },
                        label = {
                            Text("Subject Name")
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
                            Text("Initial Presents")
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
                                      Text("Initial Absents")
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
                                    classAttendanceViewModel.insertSubject(
                                        Subject(
                                            0,
                                            subjectNameTextField,
                                            initialPresent.toLong(),
                                            initialAbsent.toLong()
                                        )
                                    )
                                    subjectNameTextField = ""
                                    initialPresent = 0.toString()
                                    initialAbsent = 0.toString()
                                }
                                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                            }
                        ) {
                            Text("Add")
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        TextButton(
                            onClick = {
                                classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                                subjectNameTextField = ""
                            }
                        ) {
                            Text("Cancel")
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
                modifier = Modifier.size(70.dp),
                painter = painterResource(id = R.drawable.subjects),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(5.dp),
                text = "No Subjects"
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(10.dp)
                            .animateItemPlacement()
                            .combinedClickable(
                                onClick = {
                                    Log.d("debugging", "clicked once")
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
                                        classAttendanceViewModel.deleteSubject(it._id)
                                    }
                                    showOverFlowMenu = false
                                }
                            ) {
                                Text("Delete")
                            }
                            DropdownMenuItem(
                                onClick = {
                                    showOverFlowMenu = false
                                    subjectNameTextField = it.subjectName
                                    initialPresent = it.daysPresent.toString()
                                    initialAbsent = it.daysAbsent.toString()
                                    classAttendanceViewModel.changeFloatingButtonClickedState(true)
                                }
                            ) {
                                Text("Edit")
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                ,
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
                                    var startAnimation by remember{mutableStateOf(false)}
                                    val target = animateFloatAsState(
                                        targetValue = if(startAnimation) it.attendancePercentage.toFloat() else 0f,
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

                                    LaunchedEffect(key1 = Unit){
                                        startAnimation = true
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

