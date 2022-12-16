package com.gps.classattendanceapp.components.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

object FusedLocation {

    fun isGpsEnabled(context: Context): Boolean{
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @SuppressLint("MissingPermission", "VisibleForTests")
    fun getLocationFlow(context: Context) = callbackFlow{
        val fusedLocationClient = FusedLocationProviderClient(context)
        val locationRequest = LocationRequest.create().apply{
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        val locationCallback = object: LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
                trySendBlocking(location.lastLocation)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose{
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}