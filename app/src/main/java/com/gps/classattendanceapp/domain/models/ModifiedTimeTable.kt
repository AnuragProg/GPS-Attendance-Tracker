package com.gps.classattendanceapp.domain.models


// only to be used for providing to ui layer
data class ModifiedTimeTable(
    val _id: Int,
    val day: String,
    val subjectNameAndTime: Map<String, List<Int>> //here at 0th -> hour && 1st -> minutes
)
