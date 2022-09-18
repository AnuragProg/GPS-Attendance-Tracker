@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.presenter.theme.boxSizePercentage
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubjectsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
    addSubjectIdtoDelete: (Int)->Unit,
    removeSubjectIdToDelete: (Int)->Unit,
    snackbarHostState: SnackbarHostState
) {
    
    val uiState = rememberSubjectScreenUiState(
        classAttendanceViewModel = classAttendanceViewModel
    )

    val configuration = LocalConfiguration.current

    // Alert Dialog -> To add new subject
    if (uiState.showAddSubjectDialog.value) {
        SubjectScreenAlertDialog(
            subjectScreenUiState = uiState
        )
    }

    when(uiState.subjectsList.value){
        // No using passed Error message
        is Resource.Error -> {}
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            Log.d("debugging", "Subject Screen Success UI invoked")

            if((uiState.subjectsList.value.data?.size ?: 0) == 0){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        modifier = Modifier
                            .size(
                                height = (configuration.screenHeightDp* boxSizePercentage).dp,
                                width = (configuration.screenWidthDp* boxSizePercentage).dp
                            ),
                        painter = painterResource(id = R.drawable.box),
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = stringResource(R.string.no_subjects),
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }else{
                Box{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            uiState.subjectsList.value.data!!,
                            key = {subject -> subject._id}
                        ) { subject ->
                            var isCardVisible by remember{mutableStateOf(false)}
                            var isSubjectSelected by remember{mutableStateOf(false)}
                            val dismissState = rememberDismissState()

                            LaunchedEffect(Unit){
                                isCardVisible = true
                            }

                            if(dismissState.isDismissed(DismissDirection.StartToEnd)){
                                LaunchedEffect(Unit){
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Confirm delete",
                                        actionLabel = "Confirm",
                                        duration = SnackbarDuration.Short
                                    )

                                    if(result.name == SnackbarResult.ActionPerformed.name){
                                        classAttendanceViewModel.deleteSubject(subject._id, uiState.context)
                                    }else{
                                        dismissState.reset()
                                    }
                                }
                            }else if(dismissState.isDismissed(DismissDirection.EndToStart)){
                                uiState.subjectToEdit.value = subject
                                uiState.fillFieldsWithSubjectToEditFields()
                                classAttendanceViewModel.changeFloatingButtonClickedState(state = true)
                                uiState.coroutineScope.launch{ dismissState.reset() }
                            }

                            Box{
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
                                                        tint = Color.Black
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
                                                        tint = Color.Black
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
    }
}