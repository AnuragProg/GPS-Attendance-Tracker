package com.example.classattendanceapp.presenter.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.estimateAnimationDurationMillis
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.presenter.utils.DateToSimpleFormat
import com.example.classattendanceapp.presenter.utils.Days
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class)
@Composable
fun TimeTableScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    var showAddTimeTableDialog = classAttendanceViewModel.floatingButtonClicked.collectAsState()

    var showSelectDayDropDownMenu by remember{
        mutableStateOf(false)
    }

    var dayInDialog by remember{
        mutableStateOf("")
    }

    val selectedTime = remember{
        mutableStateListOf(
            0,
            0
        )
    }

    var showNoSubjectsFound by remember{
        mutableStateOf(false)
    }

    var subjectInAlertDialog by remember{
        mutableStateOf<ModifiedSubjects?>(null)
    }

    var showAddTimeTableSubjectNameAlertDialog by remember{
        mutableStateOf(false)
    }

    val datetimeDialogState = rememberMaterialDialogState()

    val timetableList = remember{
        mutableStateMapOf<String, List<TimeTable>>()
    }



    LaunchedEffect(Unit){

        classAttendanceViewModel.getTimeTable().collectLatest { timetables ->
            timetableList.clear()
            var i = 0;
            timetables.keys.forEach{
                Log.d("debugging" , "${++i}th value is $it")
                timetableList[it] = timetables[it]!!
            }
        }
    }



    if(showAddTimeTableDialog.value) {
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeFloatingButtonClickedState(false)
                dayInDialog = ""
                selectedTime[0] = 0 // hour
                selectedTime[1] = 0 // minute
            },
            text = {
                Column{
                    Text("Add To TimeTable")
                    Spacer(modifier = Modifier.height(10.dp))
                    Row{
                        OutlinedButton(onClick = {
                            showAddTimeTableSubjectNameAlertDialog = true
                        }) {
                            Text(subjectInAlertDialog?.subjectName ?: "Subject")
                        }
                        DropdownMenu(
                            expanded = showAddTimeTableSubjectNameAlertDialog,
                            onDismissRequest = {
                                showAddTimeTableSubjectNameAlertDialog = false
                            }
                        ) {
                            val subjectsList = remember{ mutableStateListOf<ModifiedSubjects>() }
                            coroutineScope.launch {
                                classAttendanceViewModel.getSubjects().collectLatest{
                                    subjectsList.clear()
                                    subjectsList.addAll(it)
                                }
                            }
                            if(subjectsList.isNotEmpty()){
                                subjectsList.forEach{
                                    DropdownMenuItem(
                                        onClick = {
                                            subjectInAlertDialog = it
                                            showAddTimeTableSubjectNameAlertDialog = false
                                        }
                                    ) {
                                        Text(it.subjectName)
                                    }
                                }
                            }else{
                                Text("No Subjects to select from!!")
                            }
                        }
                    }
                    Row{
                        OutlinedButton(
                            onClick = {
                                showSelectDayDropDownMenu = true
                            }
                        ) {
                            Text(dayInDialog.ifBlank { "Select Day" })
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ){
                                Icon(
                                    Icons.Filled.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showSelectDayDropDownMenu,
                            onDismissRequest = {
                                showSelectDayDropDownMenu = false
                            }
                        ) {
                            Days.values().forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        dayInDialog = it.day
                                        showSelectDayDropDownMenu = false
                                    }
                                ) {
                                    Text(it.day)
                                }
                            }
                        }
                    }
                    Row{
                        OutlinedButton(
                            onClick = {
                                datetimeDialogState.show()
                            }
                        ) {
                            Text(if(selectedTime[0]==0 && selectedTime[1]==0) "Select Time" else "${selectedTime[0]}:${selectedTime[1]}")
                        }
                        MaterialDialog(
                            dialogState = datetimeDialogState,
                            buttons = {
                                positiveButton("Ok"){

                                }
                                negativeButton("Cancel"){
                                    datetimeDialogState.hide()
                                }
                            }
                        ) {
                            timepicker(
                                is24HourClock = true
                            ){
                                Log.d("debugging", "Time is ${selectedTime[0]}:${selectedTime[1]}")
                                selectedTime[0] = it.hour
                                selectedTime[1] = it.minute
                            }
                        }
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    TextButton(
                        onClick = {
                            if(subjectInAlertDialog!=null && dayInDialog.isNotBlank()){
                                coroutineScope.launch{
                                    classAttendanceViewModel.insertTimeTable(
                                        TimeTable(
                                            0,
                                            subjectInAlertDialog!!._id,
                                            subjectInAlertDialog!!.subjectName,
                                            selectedTime[0],
                                            selectedTime[1],
                                            DateToSimpleFormat.getNumberFromDayOfTheWeek(dayInDialog)
                                        ),
                                        context
                                    )
                                    classAttendanceViewModel.changeFloatingButtonClickedState(false)
                                    subjectInAlertDialog = null
                                    selectedTime[0] = 0
                                    selectedTime[1] = 0
                                    dayInDialog = ""
                                }
                            }

                        }
                    ) {
                        Text("Add")
                    }
                    TextButton(
                        onClick = {
                            classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                            subjectInAlertDialog = null
                            selectedTime[0] = 0
                            selectedTime[1] = 0
                            dayInDialog = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }



    Box(){
        Column(){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                items(Days.values()){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .animateItemPlacement(),
                        elevation = 5.dp,
                        border = BorderStroke(2.dp, Color.Black)
                    ){
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .animateContentSize()
                        ){
                            Text(
                                text = it.day,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            if(timetableList[it.day]?.isEmpty() == true){
                                Text("Nothing Here...")
                            }else{
                                for(timetable in timetableList[it.day] ?: emptyList()){
                                    Box{
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.CenterEnd
                                        ){
                                            IconButton(onClick = {
                                                coroutineScope.launch{
                                                    classAttendanceViewModel.deleteTimeTable(timetable._id, context)
                                                }
                                            }) {
                                                Icon(
                                                    Icons.Filled.Delete,
                                                    contentDescription = null
                                                )
                                            }
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.CenterStart
                                            ){
                                                Text(
                                                    timetable.subjectName
                                                )

                                            }
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ){
                                                Text(
                                                    "${timetable.hour}:${timetable.minute}"
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
    }
}
