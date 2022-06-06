package com.example.classattendanceapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


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