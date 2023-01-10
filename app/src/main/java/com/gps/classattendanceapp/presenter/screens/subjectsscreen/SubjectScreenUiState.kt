package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.CoroutineScope

data class SubjectScreenUiState(
    val context: Context,
    val coroutineScope: CoroutineScope,
    val classAttendanceViewModel: ClassAttendanceViewModel,
    val subjectsList: State<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>>>,
    val searchBarText: State<String>,
    val showAddSubjectDialog: State<Boolean>,
    var subjectToEditId: Int? = null
)

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun rememberSubjectScreenUiState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    classAttendanceViewModel: ClassAttendanceViewModel,
    subjectsList: State<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>>> = classAttendanceViewModel.filteredSubjects.collectAsStateWithLifecycle(),
    searchBarText: State<String> = classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle(),
    showAddSubjectDialog: State<Boolean> = classAttendanceViewModel.floatingButtonClicked.collectAsStateWithLifecycle(),
)= remember {
    SubjectScreenUiState(
        context = context,
        coroutineScope = coroutineScope,
        classAttendanceViewModel = classAttendanceViewModel,
        subjectsList = subjectsList,
        searchBarText = searchBarText,
        showAddSubjectDialog = showAddSubjectDialog,
    )
}
