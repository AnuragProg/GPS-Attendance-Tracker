package com.example.classattendanceapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.presenter.utils.DateToSimpleFormat
import java.util.*


@Entity(tableName = "log")
data class Log(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val subjectId: Int,
    val subjectName: String,    //TEC201
    val timestamp: Date,             //15360000
    var wasPresent: Boolean,     //true -> present or false -> absent
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun Log.toModifiedLogs(): ModifiedLogs{
    return ModifiedLogs(
        _id = _id,
        subjectId = subjectId,
        subjectName = subjectName,
        hour = DateToSimpleFormat.getHours(timestamp),
        minute = DateToSimpleFormat.getMinutes(timestamp),
        date = DateToSimpleFormat.getDay(timestamp),
        day = DateToSimpleFormat.getDayOfTheWeek(timestamp),
        month = DateToSimpleFormat.getMonthStringFromNumber(timestamp),
        monthNumber = DateToSimpleFormat.getConventionalMonthNumber(timestamp),
        year = DateToSimpleFormat.getYear(timestamp),
        wasPresent = wasPresent,
        latitude = latitude,
        longitude = longitude
    )
}

