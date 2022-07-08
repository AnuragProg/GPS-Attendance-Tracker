package com.example.classattendanceapp.domain.models


// only to be used for providing to ui layer
data class ModifiedLogs(
    val _id: Int,
    val subjectId: Int,
    val subjectName: String,
    val hour: Int,
    val minute: Int,
    val date: Int,
    val day: String,
    val month: String,
    val monthNumber: Int,
    val year: Int,
    val wasPresent: Boolean,
    val latitude: Double?,
    val longitude: Double?
)