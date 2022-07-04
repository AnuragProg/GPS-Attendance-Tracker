package com.example.classattendanceapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val subjectName: String,
    var daysPresentOfLogs: Long = 0,
    var daysAbsentOfLogs: Long = 0,
    var daysPresent: Long,
    var daysAbsent: Long
)
