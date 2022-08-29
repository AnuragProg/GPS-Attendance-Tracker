package com.gps.classattendanceapp.data.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gps.classattendanceapp.domain.models.ModifiedLogs
import com.gps.classattendanceapp.presenter.utils.DateToSimpleFormat
import java.util.*

@Keep
@Entity(tableName = "log")
data class Log(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val subjectId: Int,
    val subjectName: String,    //TEC201
    val timestamp: Date,             //15360000
    var wasPresent: Boolean,     //true -> present or false -> absent
    var latitude: Double?,
    var longitude: Double?,
    var distance: Double?
)

@Keep
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
        longitude = longitude,
        distance = distance
    )
}

