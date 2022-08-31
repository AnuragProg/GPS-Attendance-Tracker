package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.CoroutineScope

data class SubjectScreenUiState(
    val context: Context,
    val coroutineScope: CoroutineScope,
    val classAttendanceViewModel: ClassAttendanceViewModel,
    val subjectsList: SnapshotStateList<ModifiedSubjects>,
    val searchBarText: State<String>,
    val showAddSubjectDialog: State<Boolean>,
    val subjectToEdit: MutableState<ModifiedSubjects?> = mutableStateOf(null),
    val isInitialSubjectDataRetrievalDone: State<Boolean>,
    val latitude : MutableState<String>,
    val longitude : MutableState<String>,
    val range : MutableState<String>,
)

fun SubjectScreenUiState.setLatitude(latitude: String){

}


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun rememberSubjectScreenUiState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    classAttendanceViewModel: ClassAttendanceViewModel,
    subjectsList: SnapshotStateList<ModifiedSubjects> = mutableStateListOf(),
    searchBarText: State<String> = classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle(),
    showAddSubjectDialog: State<Boolean> = classAttendanceViewModel.floatingButtonClicked.collectAsStateWithLifecycle(),
    subjectToEdit: MutableState<ModifiedSubjects?> = mutableStateOf(null),
    isInitialSubjectDataRetrievalDone: State<Boolean> = classAttendanceViewModel.isInitialSubjectDataRetrievalDone.collectAsStateWithLifecycle(),
    latitude: MutableState<String> = mutableStateOf(""),
    longitude: MutableState<String> = mutableStateOf(""),
    range: MutableState<String> = mutableStateOf(""),
)= remember {
    SubjectScreenUiState(
        context = context,
        coroutineScope = coroutineScope,
        classAttendanceViewModel = classAttendanceViewModel,
        subjectsList = subjectsList,
        searchBarText = searchBarText,
        showAddSubjectDialog = showAddSubjectDialog,
        subjectToEdit = subjectToEdit,
        isInitialSubjectDataRetrievalDone = isInitialSubjectDataRetrievalDone,
        latitude = latitude,
        longitude = longitude,
        range = range
    )
}
