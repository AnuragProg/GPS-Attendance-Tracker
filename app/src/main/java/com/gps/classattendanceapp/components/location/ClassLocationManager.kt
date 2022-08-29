package com.gps.classattendanceapp.components.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow


object ClassLocationManager {

    @SuppressLint("MissingPermission")
    fun getLocationFlow(context: Context) = callbackFlow{
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val gpsListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySendBlocking(location)
            }
            // For older versions than sdk 30 these should be defined
            // as per old code convention
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onFlushComplete(requestCode: Int) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        val networkListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySendBlocking(location)
            }
            // For older versions than sdk 30 these should be defined
            // as per old code convention
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onFlushComplete(requestCode: Int) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }


        if (hasGps) {
            /*
               If device has GPS then requesting coordinates from GPS
            */
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsListener,
                Looper.getMainLooper()
            )
        }
        if (hasNetwork) {
            /*
                If device has Network then requesting coordinates from Network
            */
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkListener,
                Looper.getMainLooper()
            )
        }

        awaitClose {
            locationManager.removeUpdates(gpsListener)
            locationManager.removeUpdates(networkListener)
        }
    }
}