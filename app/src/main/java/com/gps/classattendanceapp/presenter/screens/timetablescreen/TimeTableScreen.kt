@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.gps.classattendanceapp.presenter.screens.timetablescreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.presenter.utils.DateToSimpleFormat
import com.gps.classattendanceapp.presenter.utils.Days
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun TimeTableScreen(
    classAttendanceViewModel: ClassAttendanceViewModel
){

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val showAddTimeTableDialog = classAttendanceViewModel.floatingButtonClicked.collectAsStateWithLifecycle()

    var showSelectDayDropDownMenu by remember{
        mutableStateOf(false)
    }

    var dayInDialog by remember{
        mutableStateOf("")
    }

    val selectedTime = remember{
        mutableStateListOf<Int?>(
            null, // hour
            null  // minute
        )
    }


    var subjectInAlertDialog by remember{
        mutableStateOf<com.gps.classattendanceapp.domain.models.ModifiedSubjects?>(null)
    }

    var showAddTimeTableSubjectNameAlertDialog by remember{
        mutableStateOf(false)
    }

    val datetimeDialogState = rememberMaterialDialogState()

    val timetableList = remember{
        mutableStateMapOf<String, MutableList<TimeTable>>()
    }



    LaunchedEffect(Unit){
        classAttendanceViewModel.getTimeTable().collectLatest { timetables ->
            timetables.data?.let{
                timetableList.clear()
                it.keys.forEach { day ->
                    timetableList[day] = mutableListOf()
                    timetableList[day]?.addAll(
                        it[day]!!.sortedBy {
                            it.hour * 60 + it.minute
                        }
                    )
                }
            }
        }
    }



    if(showAddTimeTableDialog.value) {
        AlertDialog(
            onDismissRequest = {
                classAttendanceViewModel.changeFloatingButtonClickedState(false)
                dayInDialog = ""
                selectedTime[0] = null // hour
                selectedTime[1] = null // minute
            },
            text = {
                Column{
                    Text(
                        text = stringResource(R.string.add_to_timetable),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box{
                        OutlinedButton(
                            onClick = {
                                showAddTimeTableSubjectNameAlertDialog = true
                            }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(subjectInAlertDialog?.subjectName
                                    ?: stringResource(R.string.subject))
                                IconButton(
                                    onClick = {
                                        showAddTimeTableSubjectNameAlertDialog = true
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.rotate(
                                            if(showAddTimeTableSubjectNameAlertDialog) 180f
                                            else 0f
                                        ),
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = null
                                    )

                                }
                            }
                        }
                        DropdownMenu(
                            modifier = Modifier.requiredHeightIn(max=300.dp),
                            expanded = showAddTimeTableSubjectNameAlertDialog,
                            onDismissRequest = {
                                showAddTimeTableSubjectNameAlertDialog = false
                            }
                        ) {
                            val subjectsList = classAttendanceViewModel.subjects.collectAsStateWithLifecycle()
                            if(subjectsList.value.data?.size != 0){
                                subjectsList.value.data!!.forEach{
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
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = stringResource(R.string.no_subject_to_select_from)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Box{
                        OutlinedButton(
                            onClick = {
                                showSelectDayDropDownMenu = true
                            }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(dayInDialog.ifBlank { stringResource(R.string.select_day) })
                                IconButton(
                                    onClick = {
                                        showSelectDayDropDownMenu = true
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.rotate(
                                            if(showSelectDayDropDownMenu) 180f
                                            else 0f
                                        ),
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                        DropdownMenu(
                            modifier = Modifier.height(300.dp),
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
                    Spacer(modifier = Modifier.height(5.dp))

                    Row{
                        OutlinedButton(
                            onClick = {
                                datetimeDialogState.show()
                            }
                        ) {
                            Text(
                                if(selectedTime[0]==null || selectedTime[1]==null) stringResource(R.string.select_time)
                                else "${
                                    if(selectedTime[0]!!<10) "0${selectedTime[0]}"
                                    else selectedTime[0]
                                }:${
                                    if(selectedTime[1]!!<10) "0${selectedTime[1]}"
                                    else selectedTime[1]
                                }"
                            )
                        }
                        MaterialDialog(
                            dialogState = datetimeDialogState,
                            buttons = {
                                positiveButton(stringResource(R.string.ok)){

                                }
                                negativeButton(stringResource(R.string.cancel)){
                                    datetimeDialogState.hide()
                                }
                            }
                        ) {
                            timepicker(
                                is24HourClock = true
                            ){
                                selectedTime[0] = it.hour
                                selectedTime[1] = it.minute
                            }
                        }
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.End
                ){
                    TextButton(
                        onClick = {
                            if(
                                subjectInAlertDialog!=null && dayInDialog.isNotBlank() && selectedTime[0]!=null && selectedTime[1]!=null
                            ){
                                coroutineScope.launch{
                                    classAttendanceViewModel.insertTimeTable(
                                        TimeTable(
                                            0,
                                            subjectInAlertDialog!!._id,
                                            subjectInAlertDialog!!.subjectName,
                                            selectedTime[0]!!,
                                            selectedTime[1]!!,
                                            DateToSimpleFormat.getNumberFromDayOfTheWeek(dayInDialog)
                                        ),
                                        context
                                    )
                                    classAttendanceViewModel.changeFloatingButtonClickedState(false)
                                    subjectInAlertDialog = null
                                    selectedTime[0] = null
                                    selectedTime[1] = null
                                    dayInDialog = ""
                                }
                            }

                        }
                    ) {
                        Text(stringResource(R.string.add))
                    }
                    TextButton(
                        onClick = {
                            classAttendanceViewModel.changeFloatingButtonClickedState(state = false)
                            subjectInAlertDialog = null
                            selectedTime[0] = null
                            selectedTime[1] = null
                            dayInDialog = ""
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        )
    }



    Box{
        Column{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                items(Days.values()){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .animateItemPlacement(),
                        shape = RoundedCornerShape(10.dp)
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
                                Text(stringResource(R.string.nothing_here))
                            }else{
                                for(timetable in timetableList[it.day] ?: mutableListOf()){
                                    Box{
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.CenterEnd
                                        ){

                                            IconButton(onClick = {
                                                timetableList[it.day]?.remove(timetable)
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
                                                    "${
                                                        if(timetable.hour<10){
                                                            "0${timetable.hour}"   
                                                        }else{
                                                            timetable.hour
                                                        }
                                                    }:${
                                                        if(timetable.minute<10){
                                                            "0${timetable.minute}"
                                                        }else{
                                                            timetable.minute
                                                        }
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
    }
}