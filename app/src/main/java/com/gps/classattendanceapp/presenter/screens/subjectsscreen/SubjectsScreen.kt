@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.presenter.screens.DeleteConfirmationDialog
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import com.gps.classattendanceapp.ui.theme.Dimens
import com.gps.classattendanceapp.ui.theme.VeryLightGray
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubjectsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
    addSubjectIdtoDelete: (Int)->Unit,
    removeSubjectIdToDelete: (Int)->Unit,
    snackbarHostState: SnackbarHostState,
    showModalBottomSheet: suspend (ModifiedSubjects) -> Unit,
) {
    
    val uiState = rememberSubjectScreenUiState(
        classAttendanceViewModel = classAttendanceViewModel
    )

    val configuration = LocalConfiguration.current

    val coroutineScope = rememberCoroutineScope()

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
                modifier = Modifier
                    .fillMaxSize()
                    .background(VeryLightGray),
                contentAlignment = Alignment.Center
            ) {
//                CircularProgressIndicator()
                val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.hourglass_loading))
                val progress by animateLottieCompositionAsState(composition = lottieComposition)
                LottieAnimation(
                    modifier = Modifier.size(Dimens.dimen.loading_lottie_size),
                    composition = lottieComposition,
                    progress = { progress }
                )
            }
        }
        is Resource.Success -> {
            Log.d("debugging", "Subject Screen Success UI invoked")

            if((uiState.subjectsList.value.data?.size ?: 0) == 0){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VeryLightGray),

                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

//                    Image(
//                        modifier = Modifier
//                            .size(
//                                height = (configuration.screenHeightDp* boxSizePercentage).dp,
//                                width = (configuration.screenWidthDp* boxSizePercentage).dp
//                            ),
//                        painter = painterResource(id = R.drawable.box),
//                        contentDescription = null
//                    )

                    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.empty_lottie))
                    val progress by animateLottieCompositionAsState(
                        composition = lottieComposition,
                        iterations = LottieConstants.IterateForever
                    )

                    LottieAnimation(
                        modifier = Modifier.size(Dimens.dimen.no_subject_lottie_size),
                        composition = lottieComposition,
                        progress = { progress }
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
                Box(
                    modifier = Modifier
                        .background(VeryLightGray),

                    ){
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            uiState.subjectsList.value.data ?: emptyList(),
                            key = {subject -> subject._id}
                        ) { subject ->
                            var isCardVisible by remember{mutableStateOf(false)}
                            var isSubjectSelected by remember{mutableStateOf(false)}
                            var showDeleteConfirmationDialog by remember{mutableStateOf(false)}
                            val dismissState = rememberDismissState(
                                confirmStateChange = {
                                    if(DismissValue.DismissedToEnd == it){
                                        showDeleteConfirmationDialog = true
                                        false
                                    }else if(DismissValue.DismissedToStart == it){
                                        uiState.subjectToEdit.value = subject
                                        uiState.fillFieldsWithSubjectToEditFields()
                                        classAttendanceViewModel.changeFloatingButtonClickedState(state = true)
//                                        coroutineScope.launch{
//                                        }
                                        false
                                    }else false
                                }
                            )

                            LaunchedEffect(Unit){
                                isCardVisible = true
                            }

                            if(showDeleteConfirmationDialog){
                                Log.d("deleteConfirmation", "Inside showDeleteConfirmationDialog with value $showDeleteConfirmationDialog")
                                DeleteConfirmationDialog(
                                    onConfirm = {
                                        classAttendanceViewModel.deleteSubject(subject._id, uiState.context)
                                    },
                                    onReject = {
                                           coroutineScope.launch{
                                               dismissState.reset()
                                           }
                                    },
                                    hide = {showDeleteConfirmationDialog=false}
                                )
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
                                            },
                                            onClick = {
                                                coroutineScope.launch{
                                                    showModalBottomSheet(subject)
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