package com.example.classattendanceapp.domain.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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

    private fun getLocationManager(context: Context): LocationManager {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager
    }



    @SuppressLint("MissingPermission")
    fun getLocation(context: Context) = flow {
        getLocationManager(context)
//        var currentLocation: Location?

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val locationByGps = MutableStateFlow<Location?>(null)
//        val locationByNetwork = MutableStateFlow<Location?>(null)
        val gpsListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d("location", "gps location change detected with value $location")
                locationByGps.value = location
            }
        }

//        val networkListener = object: LocationListener{
//            override fun onLocationChanged(location: Location) {
//                Log.d("location", "network location change detected with value $location")
//                locationByNetwork.value = location
//            }
//        }

//        Log.d("location", "hasGps -> $hasGps | hasNetwork -> $hasNetwork")
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsListener,
                Looper.getMainLooper()
            )

            // For requesting single updates
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
//                // Use getCurrentLocation
//                locationManager.getCurrentLocation(
//                    LocationManager.GPS_PROVIDER,
//                    null,
//                    ContextCompat.getMainExecutor(context),
//                ){
//                    locationByGps = it
//                }
//            }else{
//                // Use requestSingleUpdate
//                locationManager.requestSingleUpdate(
//                    LocationManager.GPS_PROVIDER,
//                    { location -> locationByGps = location },
//                    context.mainLooper
//                )
//            }
        }
//        if(hasNetwork){
//            locationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER,
//                5000,
//                0f,
//                networkListener,
//                Looper.getMainLooper()
//
//            )

        // for requesting single updates
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
//                // Use getCurrentLocation
//                locationManager.getCurrentLocation(
//                    LocationManager.NETWORK_PROVIDER,
//                    null,
//                    ContextCompat.getMainExecutor(context),
//                ){
//                    locationByNetwork = it
//                }
//            }else{
//                // Use requestSingleUpdate
//                locationManager.requestSingleUpdate(
//                    LocationManager.GPS_PROVIDER,
//                    { location -> locationByNetwork = location },
//                    context.mainLooper
//                )
//            }





        locationByGps.collect {
//            currentLocation = if(locationByGps.value!=null){
//                Log.d("broadcast", "locationByGps and locationByNetwork both are available")
//                if(locationByGps.value!!.accuracy > locationByNetwork.value!!.accuracy){
//                    locationByGps.value
//                }else{
//                    locationByNetwork.value
//                }
//            }else if(locationByGps!=null){
//                Log.d("broadcast", "locationByGps is available")
//                locationByGps.value
//            }else if(locationByNetwork!=null){
//                Log.d("broadcast", "locationByNetwork is available")
//                locationByNetwork.value
//            }else{
//                Log.d("location", "Returning because network and gps are not retrieved successfully")
//                null
//            }
//            Log.d("broadcast", "emitting value $currentLocation")
//            emit(currentLocation)
            emit(locationByGps.value)

        }
    }
}