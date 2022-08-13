package com.example.classattendanceapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.classattendanceapp.domain.models.ModifiedSubjects

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

fun Subject.toModifiedSubjects(): ModifiedSubjects{

    val totalPresents = daysPresent + daysPresentOfLogs
    val totalAbsents = daysAbsent + daysAbsentOfLogs
    val percentage = if(totalPresents+totalAbsents == 0.toLong()){
        0.toDouble()
    }else{
        (totalPresents.toDouble()/(totalPresents + totalAbsents))*100
    }

    val totalDays = totalPresents + totalAbsents

    return ModifiedSubjects(
        _id = _id,
        subjectName = subjectName,
        attendancePercentage = percentage,
        daysPresent = daysPresent,
        daysAbsent = daysAbsent,
        daysPresentOfLogs = daysPresentOfLogs,
        daysAbsentOfLogs = daysAbsentOfLogs,
        totalPresents = totalPresents,
        totalAbsents = totalAbsents,
        totalDays = totalDays,
        latitude = latitude,
        longitude = longitude,
        range = range
    )
}