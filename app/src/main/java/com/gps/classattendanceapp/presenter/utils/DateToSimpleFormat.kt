package com.gps.classattendanceapp.presenter.utils

import android.text.format.DateFormat
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

    fun getConventionalMonthNumber(date: Date):Int{
        return getMonthNumber(date) -1
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
        return when(getConventionalMonthNumber(date)){
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            else -> "December"
        }
    }

    fun getMonthStringFromNumber(month: Int): String{
        return when(month){
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            else -> "December"
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

