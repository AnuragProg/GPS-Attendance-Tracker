@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.annotation.SuppressLint
import androidx.compose.animation.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun SubjectsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
    addSubjectIdtoDelete: (Int)->Unit,
    removeSubjectIdToDelete: (Int)->Unit,
) {
    
    val uiState = rememberSubjectScreenUiState(
        classAttendanceViewModel = classAttendanceViewModel
    )
    
    LaunchedEffect(uiState.searchBarText.value){
        classAttendanceViewModel.getSubjectsAdvanced().collect{
            uiState.subjectsList.clear()
            uiState.subjectsList.addAll(
                it.filter { subject ->
                    if(uiState.searchBarText.value.isNotBlank()){
                        uiState.searchBarText.value.lowercase() in subject.subjectName.lowercase()
                    }else{
                        true
                    }
                }
            )
        }
    }



    // Alert Dialog -> To add new subject
    if (uiState.showAddSubjectDialog.value) {
        
    }
    if (uiState.subjectsList.isEmpty() && uiState.isInitialSubjectDataRetrievalDone.value) {
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
    } else if (uiState.subjectsList.isEmpty() && !uiState.isInitialSubjectDataRetrievalDone.value) {
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
                    uiState.subjectsList,
                    key = {subject -> subject._id}
                ) { subject ->
                    var isCardVisible by remember{mutableStateOf(false)}
                    var isSubjectSelected by remember{mutableStateOf(false)}
                    val dismissState = rememberDismissState()

                    LaunchedEffect(Unit){
                        isCardVisible = true
                    }

                    if(dismissState.isDismissed(DismissDirection.StartToEnd)){
                        uiState.coroutineScope.launch{
                            classAttendanceViewModel.deleteSubject(subject._id, uiState.context)
                        }
                    }else if(dismissState.isDismissed(DismissDirection.EndToStart)){
                        uiState.subjectToEdit.value = subject
                        classAttendanceViewModel.changeFloatingButtonClickedState(state = true)
                        uiState.coroutineScope.launch{ dismissState.reset() }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ){

                        AnimatedVisibility(
                            visible = isCardVisible,
                            enter = slideInHorizontally{ it/10} + fadeIn(),
                            exit = slideOutHorizontally{-it/10} + fadeOut()
                        ) {
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