package com.example.classattendanceapp.components.maths

import kotlin.math.*

object CoordinateCalculations {

    private fun toRadians(degree: Double): Double{
        return degree*PI/180
    }


    /*
    Haversine Formula
     */
    fun distanceBetweenPointsInKm(
        long1: Double,
        long2: Double,
        lat1: Double,
        lat2: Double,
    ): Double{
        val dlat = toRadians(lat2-lat1)
        val dlon = toRadians(long2-long1)
        val a = sin(dlat / 2).pow(2) + sin(dlon/2).pow(2)*
                cos(toRadians(lat1)) * cos(toRadians(lat2))

        return  2* atan2(sqrt(a), sqrt(1-a)) * 6371
    }

    fun distanceBetweenPointsInM(
        long1: Double,
        long2: Double,
        lat1: Double,
        lat2: Double,
    ):Double{
        return distanceBetweenPointsInKm(
            long1,
            long2,
            lat1,
            lat2
        ) * 1000
    }
}