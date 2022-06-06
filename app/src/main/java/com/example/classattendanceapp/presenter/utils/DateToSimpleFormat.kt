package com.example.classattendanceapp.presenter.utils

import android.text.format.DateFormat
import androidx.compose.ui.text.capitalize
import java.lang.IllegalStateException
import java.util.*

object DateToSimpleFormat {

    fun getHours(date: Date): Int{
        return DateFormat.format("HH", date).toString().toInt()
    }

    fun getMinutes(date: Date): Int{
        return DateFormat.format("mm", date).toString().toInt()
    }

    fun getDay(date: Date): Int{
        return DateFormat.format("dd", date).toString().toInt()
    }

    fun getMonthNumber(date: Date): Int{
        return DateFormat.format("MM", date).toString().toInt()
    }

    fun getMonthThreeWord(date: Date): String{
        return DateFormat.format("MMM", date).toString()
    }

    fun getYear(date: Date): Int{
        return DateFormat.format("yyyy", date).toString().toInt()
    }

    fun getDayOfTheWeek(date: Date): String{
        return DateFormat.format("EEEE", date).toString()
    }

    fun getMonthStringFromNumber(date: Date): String{
        return when(getMonthNumber(date)){
            0 -> "JANUARY"
            1 -> "FEBRUARY"
            2 -> "MARCH"
            3 -> "APRIL"
            4 -> "MAY"
            5 -> "JUNE"
            6 -> "JULY"
            7 -> "AUGUST"
            8 -> "SEPTEMBER"
            9 -> "OCTOBER"
            10 -> "NOVEMBER"
            else -> "DECEMBER"
        }
    }

    fun getDayOfTheWeekFromNumber(day: Int): String{
        return when(day){
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> {
                throw IllegalStateException("No Such Day")
            }
        }
    }

    fun getNumberFromDayOfTheWeek(day: String): Int{
        return when(day){
            "Sunday" -> 1
            "Monday" -> 2
            "Tuesday" -> 3
            "Wednesday" -> 4
            "Thursday" -> 5
            "Friday" -> 6
            "Saturday" -> 7
            else -> {
                throw IllegalStateException("No Such Day")
            }
        }
    }
}

