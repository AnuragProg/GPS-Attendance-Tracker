package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.CoroutineScope

data class SubjectScreenUiState(
    val context: Context,
    val coroutineScope: CoroutineScope,
    val classAttendanceViewModel: ClassAttendanceViewModel,
    val subjectsList: State<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>>>,
    val searchBarText: State<String>,
    val showAddSubjectDialog: State<Boolean>,
    val subjectToEdit: MutableState<com.gps.classattendanceapp.domain.models.ModifiedSubjects?> = mutableStateOf(null),


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
    presents.value = "0"
    absents.value = "0"
}

fun SubjectScreenUiState.fillFieldsWithSubjectToEditFields(){
    subjectName.value = subjectToEdit.value!!.subjectName
    presents.value = subjectToEdit.value!!.daysPresent.toString()
    absents.value = subjectToEdit.value!!.daysAbsent.toString()
    latitude.value = subjectToEdit.value!!.latitude?.toString() ?: ""
    longitude.value = subjectToEdit.value!!.longitude?.toString() ?: ""
    range.value = subjectToEdit.value!!.range?.toString() ?: ""
}


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun rememberSubjectScreenUiState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    classAttendanceViewModel: ClassAttendanceViewModel,
    subjectsList: State<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>>> = classAttendanceViewModel.filteredSubjects.collectAsStateWithLifecycle(),
    searchBarText: State<String> = classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle(),
    showAddSubjectDialog: State<Boolean> = classAttendanceViewModel.floatingButtonClicked.collectAsStateWithLifecycle(),
    subjectToEdit: MutableState<com.gps.classattendanceapp.domain.models.ModifiedSubjects?> = mutableStateOf(null),
    latitude: MutableState<String> = mutableStateOf(""),
    longitude: MutableState<String> = mutableStateOf(""),
    range: MutableState<String> = mutableStateOf(""),
    subjectName: MutableState<String> = mutableStateOf(""),
    presents: MutableState<String> = mutableStateOf("0"),
    absents: MutableState<String> = mutableStateOf("0"),
)= remember {
    SubjectScreenUiState(
        context = context,
        coroutineScope = coroutineScope,
        classAttendanceViewModel = classAttendanceViewModel,
        subjectsList = subjectsList,
        searchBarText = searchBarText,
        showAddSubjectDialog = showAddSubjectDialog,
        subjectToEdit = subjectToEdit,
        latitude = latitude,
        longitude = longitude,
        range = range,
        subjectName = subjectName,
        presents = presents,
        absents = absents
    )
}
