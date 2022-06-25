package com.example.classattendanceapp.domain.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.*


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


//        locationByGps.combine(locationByNetwork){ gpsLocation, networkLocation ->
//            Pair(gpsLocation, networkLocation)
//        }.collect{ coordinates ->
//            Log.d("worker", "Getting combined coordinates $coordinates")
//            if(coordinates.first!=null && coordinates.second!=null){
//                if(coordinates.first!!.accuracy >= coordinates.second!!.accuracy){
//                    Log.d("worker", "Emitting gps location with accuracy ${coordinates.first!!.accuracy}")
//                    emit(coordinates.first)
//                }else{
//                    Log.d("worker", "Emitting network location with accuracy ${coordinates.second!!.accuracy}")
//                    emit(coordinates.second)
//                }
//            }else if(coordinates.first!=null){
//                Log.d("worker", "Gps is not null so emitting gps fetched coordinates")
//                emit(coordinates.first)
//
//            }else if(coordinates.second!=null){
//                Log.d("worker", "Network is not null so emitting network fetched coordinates")
//                emit(coordinates.second)
//            }
//        }
        if(!hasGps && !hasNetwork){
            Log.d("worker", "Don't have gps and network -> emitting null")
            emit(null)
        }else if(hasGps && hasNetwork){
            Log.d("worker", "Have both gps and network")
            val gpsAndNetworkCombinedLocation = combine(
                locationByGps, locationByNetwork
            ){ gps, network ->
                Pair(gps, network)
            }.first{
                it.first!=null && it.second!=null
            }
            Log.d("worker", "retrieved location from both gps and network = $gpsAndNetworkCombinedLocation")
            val highestAccuracyLocation = if(
                gpsAndNetworkCombinedLocation.first!!.accuracy >= gpsAndNetworkCombinedLocation.second!!.accuracy
            ){
                gpsAndNetworkCombinedLocation.first!!
            }else{
                gpsAndNetworkCombinedLocation.second!!
            }
            Log.d("worker", "Location with higher accuracy is $highestAccuracyLocation")
            Log.d("worker", "Emitting this location")
            emit(highestAccuracyLocation)
        }else if(hasGps){
            Log.d("worker", "Has Gps so emitting first non null location from gps")
            emit(
                locationByGps.first{
                    it!=null
                }
            )
        }else{
            Log.d("worker", "Has Network so emitting first non null location from network")
            emit(
                locationByNetwork.first{
                    it!=null
                }
            )
        }
    }
}