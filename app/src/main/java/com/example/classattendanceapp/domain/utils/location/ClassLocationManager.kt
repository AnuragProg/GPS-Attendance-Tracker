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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
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
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val locationByGps = MutableStateFlow<Location?>(null)
        val locationByNetwork = MutableStateFlow<Location?>(null)
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

        val networkListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByNetwork.value = location
            }
            // For older versions than sdk 30 these should be defined
            // as per old code convention
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onFlushComplete(requestCode: Int) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        Log.d("broadcast", "The gps is $hasGps")
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsListener,
                Looper.getMainLooper()
            )
        }
        Log.d("broadcast", "The network is $hasNetwork")
        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkListener,
                Looper.getMainLooper()
            )
        }

        Log.d("broadcast", "Starting combine locationCoroutine")


        locationByGps.combine(locationByNetwork){ gpsLocation, networkLocation ->
            Pair(gpsLocation, networkLocation)
        }.collect{ coordinates ->
            Log.d("broadcast", "Getting combined coordinates $coordinates")
            if(coordinates.first!=null && coordinates.second!=null){
                if(coordinates.first!!.accuracy >= coordinates.second!!.accuracy){
                    Log.d("broadcast", "Emitting gps location with accuracy ${coordinates.first!!.accuracy}")
                    emit(coordinates.first)
                }else{
                    Log.d("broadcast", "Emitting network location with accuracy ${coordinates.second!!.accuracy}")
                    emit(coordinates.second)
                }
            }else if(coordinates.first!=null){
                Log.d("broadcast", "Gps is not null so emitting gps fetched coordinates")
                emit(coordinates.first)

            }else if(coordinates.second!=null){
                Log.d("broadcast", "Network is not null so emitting network fetched coordinates")
                emit(coordinates.second)
            }
        }
    }
}