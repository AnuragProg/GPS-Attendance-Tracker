@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.classattendanceapp.presenter.screens.subjectsscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Book
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun SubjectsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
    addSubjectIdtoDelete: (Int)->Unit,
    removeSubjectIdToDelete: (Int)->Unit,
) {
    val subjectsList = remember{
        mutableStateListOf<ModifiedSubjects>()
    }

    val searchBarText by classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
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
    val snackbarHostState = rememberScaffoldState()

    /*
    number -> subject Id to updated that subject
    null -> if(null)not updating else updating
     */
    var editingSubject by remember {
        mutableStateOf<Int?>(null)
    }

    LaunchedEffect(searchBarText){
        classAttendanceViewModel.getSubjectsAdvanced().collect{
            subjectsList.clear()
            subjectsList.addAll(
                it.filter {
                    if(searchBarText.isNotBlank()){
                        searchBarText.lowercase() in it.subjectName.lowercase()
                    }else{
                        true
                    }
                }
            )
        }
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
            classAttendanceViewModel = classAttendanceViewModel,
            changeEditingSubject = {editingSubject=it},
            changeSubjectNameTextField = {subjectNameTextField=it},
            changeInitialAbsent = {initialAbsent=it},
            changeInitialPresent = {initialPresent=it},
            changeLatitude = {latitude=it},
            changeLongitude = {longitude=it},
            changeRange = {range=it}
        )
    }
    if (subjectsList.isEmpty() && isInitialSubjectDataRetrievalDone.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                modifier = Modifier.size(200.dp),
                tint = Color.White,
                imageVector = Icons.Outlined.Book,
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(5.dp),
                text = stringResource(R.string.no_subjects),
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    } else if (subjectsList.isEmpty() && !isInitialSubjectDataRetrievalDone.value) {
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
                    subjectsList,
                    key = {subject -> subject._id}
                ) { subject ->
                    var isSubjectSelected by remember{mutableStateOf(false)}
                    val dismissState = rememberDismissState()

                    if(dismissState.isDismissed(DismissDirection.StartToEnd)){
                        coroutineScope.launch{
                            classAttendanceViewModel.deleteSubject(subject._id, context)
                        }
                    }else if(dismissState.isDismissed(DismissDirection.EndToStart)){
                        editingSubject = subject._id
                        subjectNameTextField = subject.subjectName
                        initialPresent = subject.daysPresent.toString()
                        initialAbsent = subject.daysAbsent.toString()
                        latitude = if(subject.latitude!=null)subject.latitude.toString() else ""
                        longitude = if(subject.longitude!=null)subject.longitude.toString() else ""
                        range = if(subject.range!=null)subject.range.toString() else ""
                        classAttendanceViewModel.changeFloatingButtonClickedState(state = true)
                        coroutineScope.launch{ dismissState.reset() }

                    }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        SwipeToDismiss(
                            state = dismissState,
                            dismissThresholds = { FractionalThreshold(0.8f) },
                            background = {

                                if(dismissState.dismissDirection == DismissDirection.StartToEnd){
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .requiredHeightIn(min = 80.dp)
                                            .padding(start = 10.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ){
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }else{
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .requiredHeightIn(min = 80.dp)
                                            .padding(end = 10.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ){
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        ){
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
                                changeIsSubjectSelected = { selected ->
                                    if (selected) {
                                        addSubjectIdtoDelete(subject._id)
                                    } else {
                                        removeSubjectIdToDelete(subject._id)
                                    }
                                    isSubjectSelected = selected
                                }
                            )
                        }
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