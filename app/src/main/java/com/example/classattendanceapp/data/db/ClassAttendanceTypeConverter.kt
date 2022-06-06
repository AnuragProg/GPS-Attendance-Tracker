package com.example.classattendanceapp.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    @TypeConverter
    fun fromMapToString(dayAndTime: Map<String, Date>): String{
        return Gson().toJson(dayAndTime)
    }

    @TypeConverter
    fun fromStringToMap(dayAndTime: String): Map<String, Date>{
        return Gson().fromJson(dayAndTime, object: TypeToken<Map<String, Date>>(){}.type)
    }


}