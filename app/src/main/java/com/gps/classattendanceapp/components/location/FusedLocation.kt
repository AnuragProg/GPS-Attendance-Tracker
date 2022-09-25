package com.gps.classattendanceapp.components.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

object FusedLocation {

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context) = callbackFlow{
        val fusedLocationClient = FusedLocationProviderClient(context)
        val locationRequest = LocationRequest.create()
        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY

        val locationCallback = object: LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
                location.lastLocation?.let{
                    trySendBlocking(it)
                } ?: trySendBlocking(null)
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