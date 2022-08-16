@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.subjectsscreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel


@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun SubjectsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
    addSubjectIdtoDelete: (Int)->Unit,
    removeSubjectIdToDelete: (Int)->Unit,
) {
    val subjectsList = classAttendanceViewModel.subjectsList.collectAsStateWithLifecycle()

    val searchBarText = classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle()

    val isInitialSubjectDataRetrievalDone =
        classAttendanceViewModel.isInitialSubjectDataRetrievalDone.collectAsStateWithLifecycle()

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
                ) { subject ->
                    var isSubjectSelected by remember{mutableStateOf(false)}
                    Box{
                        SubjectCard(
                            subject = subject,
                            classAttendanceViewModel = classAttendanceViewModel,
                            changeSubjectNameTextField = { subjectNameTextField = it },
                            changeInitialPresent = { initialPresent = it },
                            changeInitialAbsent = { initialAbsent = it },
                            changeEditingSubject = { editingSubject = it },
                            changeLatitude = { latitude = it },
                            changeLongitude = { longitude = it },
                            changeRange = { range = it },
                            changeIsSubjectSelected = { selected->
                                if(selected){
                                    addSubjectIdtoDelete(subject._id)
                                }else{
                                    removeSubjectIdToDelete(subject._id)
                                }
                                isSubjectSelected = selected
                            }
                        )
                        if(isSubjectSelected){
                            Surface(
                                onClick = {
                                    removeSubjectIdToDelete(subject._id)
                                    isSubjectSelected = false
                                },
                                modifier = Modifier.matchParentSize(),
                                color = Color.Blue.copy(alpha=0.2f)
                            ) {}
                        }
                    }
                }
            }
        }
    }
}