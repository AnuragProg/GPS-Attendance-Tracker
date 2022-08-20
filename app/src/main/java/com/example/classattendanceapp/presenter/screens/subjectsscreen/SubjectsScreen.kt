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
import androidx.compose.ui.text.font.FontWeight
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


    var showLocationSelectionPopUp by remember{
        mutableStateOf(false)
    }

    var subjectToEdit by remember{
        mutableStateOf<ModifiedSubjects?>(null)
    }

    var latitudeFromMap by remember{
        mutableStateOf<String?>(null)
    }
    var longitudeFromMap by remember{
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(searchBarText){
        classAttendanceViewModel.getSubjectsAdvanced().collect{
            subjectsList.clear()
            subjectsList.addAll(
                it.filter { subject ->
                    if(searchBarText.isNotBlank()){
                        searchBarText.lowercase() in subject.subjectName.lowercase()
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
                latitudeFromMap = it.toString()
            },
            changeLongitude = {
                longitudeFromMap = it.toString()
            },
            changeLocationSelectionVisibility ={
                showLocationSelectionPopUp = it
            }
        )
    }

    // Alert Dialog -> To add new subject
    if (showAddSubjectDialog.value) {
        SubjectScreenAlertDialog(
            subjectToEdit = subjectToEdit,
            resetSubjectToEdit = {
                subjectToEdit=null
                latitudeFromMap = null
                longitudeFromMap = null
                                 },
            changeShowLocationSelectionPopup = {showLocationSelectionPopUp=it},
            classAttendanceViewModel = classAttendanceViewModel,
            latitudeFromMap = latitudeFromMap,
            longitudeFromMap = longitudeFromMap,
            changeLatitudeFromMap = {latitudeFromMap=it},
            changeLongitudeFromMap = {longitudeFromMap=it}
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
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold
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
                        subjectToEdit = subject
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
                                onSubjectSelected = { isSelected ->
                                    isSubjectSelected = isSelected
                                    if(isSelected){
                                        addSubjectIdtoDelete(subject._id)
                                    }else{
                                        removeSubjectIdToDelete(subject._id)
                                    }
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