package com.gps.classattendanceapp.presenter.utils

enum class Days(
    val day: String,
    val value: Int,
) {
    SUNDAY("Sunday", 1),
    MONDAY("Monday", 2),
    TUESDAY("Tuesday", 3),
    WEDNESDAY("Wednesday", 4),
    THURSDAY("Thursday", 5),
    FRIDAY("Friday", 6),
    SATURDAY("Saturday", 7);
}

enum class Months(
    val month: String,
    val value: Int
){
    JANUARY("January", 0),
    FEBRUARY("February", 1),
    MARCH("March", 2),
    APRIL("April", 3),
    MAY("May", 4),
    JUNE("June", 5),
    JULY("July", 6),
    AUGUST("August", 7),
    SEPTEMBER("September", 8),
    OCTOBER("October", 9),
    NOVEMBER("November", 10),
    DECEMBER("December", 11);

    // Returns-> Number on success and null on failure
    fun getValueOfMonth(monthInStringFormat: String): Int?{
        for(mon in Months.values()){
            if(mon.month == monthInStringFormat){
                return mon.value
            }
        }
        return null
    }
}