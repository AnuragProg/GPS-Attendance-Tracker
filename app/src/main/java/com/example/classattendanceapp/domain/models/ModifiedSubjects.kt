package com.example.classattendanceapp.domain.models

data class ModifiedSubjects(
    val _id: Int,
    val subjectName: String,
    val attendancePercentage: Double,
    var daysPresent: Long,
    var daysAbsent: Long,
    var daysPresentOfLogs: Long,
    var daysAbsentOfLogs: Long,
)
