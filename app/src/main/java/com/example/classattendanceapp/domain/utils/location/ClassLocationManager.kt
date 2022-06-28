package com.example.classattendanceapp.domain.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout


object ClassLocationManager {

    private lateinit var locationManager: LocationManager

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

        Log.d("worker", "The gps is $hasGps")
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsListener,
                Looper.getMainLooper()
            )
        }
        Log.d("worker", "The network is $hasNetwork")
        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkListener,
                Looper.getMainLooper()
            )
        }

        Log.d("worker", "Starting combine locationCoroutine")

        if(!hasGps && !hasNetwork){
            Log.d("worker", "Don't have gps and network -> emitting null")
            emit(null)
        }else if(hasGps && hasNetwork){
            Log.d("worker", "Have both gps and network")
            try{
                withTimeout(5000) {
                    val gpsAndNetworkCombinedLocation = combine(
                        locationByGps, locationByNetwork
                    ) { gps, network ->
                        Pair(gps, network)
                    }.first {
                        it.first != null && it.second != null
                    }
                    Log.d("worker",
                        "retrieved location from both gps and network = $gpsAndNetworkCombinedLocation")
                    val highestAccuracyLocation = if (
                        gpsAndNetworkCombinedLocation.first!!.accuracy >= gpsAndNetworkCombinedLocation.second!!.accuracy
                    ) {
                        gpsAndNetworkCombinedLocation.first!!
                    } else {
                        gpsAndNetworkCombinedLocation.second!!
                    }

                    Log.d("worker", "Location with higher accuracy is $highestAccuracyLocation")
                    Log.d("worker", "Emitting this location")
                    emit(highestAccuracyLocation)
                }
            }catch(timeout: TimeoutCancellationException){
                Log.d("worker", "Timeout Exception raised")
                try{
                    Log.d("worker", "Trying to get gps location instead")
                    withTimeout(5000){
                        val gpsLocation = locationByGps.first{
                            it!=null
                        }
                        emit(gpsLocation)
                    }
                }catch (timeout: TimeoutCancellationException){
                    Log.d("worker", "Timeout Exception raised")
                    withTimeout(5000){
                        Log.d("worker", "Trying to get network location instead")

                        val networkLocation = locationByNetwork.first{
                            it!=null
                        }
                        emit(networkLocation)
                    }
                }
            }
        }else if(hasGps){
            try{
                withTimeout(5000){
                    Log.d("worker", "Has Gps so emitting first non null location from gps")
                    val gpsLocation = locationByGps.first {
                        it != null
                    }
                    emit(gpsLocation)
                }
            }catch(timeout: TimeoutCancellationException){
                Log.d("worker", "Timeout Exception raised emitting null")

                emit(null)
            }
        }else{
            try{
                withTimeout(5000){
                    Log.d("worker", "Has Network so emitting first non null location from network")
                    val networkLocation = locationByNetwork.first {
                        it != null
                    }
                    emit(networkLocation)
                }
            }catch(timeout: TimeoutCancellationException){
                Log.d("worker", "Timeout Exception raised emitting null")

                emit(null)
            }
        }
    }
}