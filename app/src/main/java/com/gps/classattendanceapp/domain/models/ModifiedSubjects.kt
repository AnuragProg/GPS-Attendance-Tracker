package com.gps.classattendanceapp.domain.models

import androidx.annotation.Keep

@Keep
data class ModifiedSubjects(
    val _id: Int,
    val subjectName: String,
    val attendancePercentage: Double,
    var daysPresent: Long,
    var daysAbsent: Long,
    var daysPresentOfLogs: Long,
    var daysAbsentOfLogs: Long,
    var totalPresents: Long,
    var totalAbsents: Long,
    var totalDays: Long,
    val latitude: Double?,
    val longitude: Double?,
    val range: Double?,
)
