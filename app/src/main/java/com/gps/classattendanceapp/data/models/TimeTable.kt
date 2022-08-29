package com.gps.classattendanceapp.data.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey


@Keep
@Entity
data class TimeTable(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val subjectId: Int,
    val subjectName: String,
    val hour: Int,
    val minute: Int,
    val dayOfTheWeek: Int // 1->sunday ...
)