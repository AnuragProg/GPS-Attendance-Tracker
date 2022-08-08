package com.example.classattendanceapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val subjectName: String,
    var daysPresentOfLogs: Long ,
    var daysAbsentOfLogs: Long,
    var daysPresent: Long,
    var daysAbsent: Long,
    val latitude: Double?,
    val longitude: Double?,
    val range: Double?
)
