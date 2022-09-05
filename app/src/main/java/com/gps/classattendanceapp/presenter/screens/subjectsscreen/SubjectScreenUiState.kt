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


    // AddSubjectDialogFields
    val latitude : MutableState<String>,
    val longitude : MutableState<String>,
    val range : MutableState<String>,
    val subjectName: MutableState<String>,
    val presents: MutableState<String>,
    val absents: MutableState<String>,
)

fun SubjectScreenUiState.clearDialogFields(){
    subjectToEdit.value = null
    latitude.value = ""
    longitude.value = ""
    range.value = ""
    subjectName.value = ""
    presents.value = ""
    absents.value = ""
}

fun SubjectScreenUiState.fillFieldsWithSubjectToEditFields(){
    subjectName.value = subjectToEdit.value!!.subjectName
    presents.value = subjectToEdit.value!!.daysPresent.toString()
    absents.value = subjectToEdit.value!!.daysAbsent.toString()
    latitude.value = subjectToEdit.value!!.latitude.toString()
    longitude.value = subjectToEdit.value!!.longitude.toString()
    range.value = subjectToEdit.value!!.range.toString()
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
    subjectName : MutableState<String> = mutableStateOf(""),
    presents: MutableState<String> = mutableStateOf(""),
    absents: MutableState<String> = mutableStateOf("")
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
        range = range,
        subjectName = subjectName,
        presents = presents,
        absents = absents
    )
}
