package com.gps.classattendanceapp.presenter.screens.logsscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LogsScreenAlertDialogUiState{
    var subjectName by mutableStateOf<String?>("")
    var isPresent by mutableStateOf(true)
    var showSubjectListOverflowMenu by mutableStateOf(false)
}