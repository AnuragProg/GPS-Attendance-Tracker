package com.gps.classattendanceapp.data.db

import androidx.room.TypeConverter
import java.util.*

object ClassAttendanceTypeConverter {

    @TypeConverter
    fun fromDateToLong(date: Date): Long{
        return date.time
    }

    @TypeConverter
    fun fromLongToDate(long: Long): Date{
        return Date(long)
    }
}