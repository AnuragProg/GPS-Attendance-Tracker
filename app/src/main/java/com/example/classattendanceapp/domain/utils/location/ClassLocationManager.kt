package com.example.classattendanceapp.domain.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlin.coroutines.cancellation.CancellationException


object ClassLocationManager {

    lateinit var locationManager: LocationManager

    private fun getLocationManager(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }



    @SuppressLint("MissingPermission")
    fun getLocation(context: Context) = flow {
        getLocationManager(context)

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        val locationByGps = MutableStateFlow<Location?>(null)

        val gpsListener = object : LocationListener {

            override fun onLocationChanged(location: Location) {
                locationByGps.value = location
            }

            // For older versions than sdk 30 these should be defined
            // as per old code convention
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onFlushComplete(requestCode: Int) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsListener,
                Looper.getMainLooper()
            )
        }else{
            return@flow
        }


        locationByGps.collect {
            emit(it)
        }
    }
}