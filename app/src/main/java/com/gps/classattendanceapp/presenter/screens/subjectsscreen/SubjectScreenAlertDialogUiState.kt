package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope

class SubjectScreenAlertDialogUiState(
    subName: String,
    present: String,
    absent: String,
    subId: Int?,
    ran: String,
    val context: Context,
    val coroutineScope: CoroutineScope
){
    val subjectName: MutableState<String> = mutableStateOf(subName)
    val presents : MutableState<String> = mutableStateOf(present)
    val absents : MutableState<String> = mutableStateOf(absent)
    val subjectId: MutableState<Int?> = mutableStateOf(subId)
    val latitude : MutableState<String> = mutableStateOf("")
    val longitude : MutableState<String> = mutableStateOf("")
    val range : MutableState<String> = mutableStateOf(ran)
    val showPresentTextFieldError : MutableState<Boolean> = mutableStateOf(false)
    val showAbsentTextFieldError : MutableState<Boolean> = mutableStateOf(false)
    val showLatitudeTextFieldError: MutableState<Boolean> = mutableStateOf(false)
    val showLongitudeTextFieldError: MutableState<Boolean> = mutableStateOf(false)
    val showRangeTextFieldError: MutableState<Boolean> = mutableStateOf(false)
}

@Composable
fun rememberSubjectScreenAlertDialogUiState(
    subjectName: String = "",
    presents: String = "0",
    absents: String = "0",
    subjectId: Int? = null,
    range: String = "",
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),

    ) : SubjectScreenAlertDialogUiState{
    return remember{
        SubjectScreenAlertDialogUiState(
            subjectName, presents, absents, subjectId, range, context, coroutineScope
        )
    }
}