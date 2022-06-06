package com.example.classattendanceapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Logs(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val subjectId: Int,
    val subjectName: String,    //TEC201
    val timestamp: Date,             //15360000
    val wasPresent: Boolean     //true -> present or false -> absent
)
