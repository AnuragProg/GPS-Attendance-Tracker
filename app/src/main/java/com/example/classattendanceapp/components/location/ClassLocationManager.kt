package com.example.classattendanceapp.components.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout


object ClassLocationManager {


    @SuppressLint("MissingPermission")
    fun getLocation(context: Context) = flow {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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


        if(!hasGps && !hasNetwork){
            /*
            * If there is neither GPS nor NETWORK, sending null to the caller
            * */
            locationManager.removeUpdates(gpsListener)
            locationManager.removeUpdates(networkListener)
            emit(null)
        }else if(hasGps && hasNetwork){
            /*
            * If Device has both GPS and NETWORK,
            * We are setting Listener for both GPS and NETWORK, We will choose the highest accuracy location from results
            * */
            try{
                withTimeout(5000) {
                    /*
                    * Setting a Timeout of 5sec, if no coordinates is received,
                    * Then setting a Timeout of 5sec for GPS, if no coordinates is received,
                    * Then setting a Timeout of 5sec for NETWORK, if no coordinates is received,
                    * Sending null to the caller
                    * */
                    val gpsAndNetworkCombinedLocation = combine(
                        locationByGps, locationByNetwork
                    ) { gps, network ->
                        Pair(gps, network)
                    }.first {
                        it.first != null && it.second != null
                    }
                    val highestAccuracyLocation = if (
                        gpsAndNetworkCombinedLocation.first!!.accuracy >= gpsAndNetworkCombinedLocation.second!!.accuracy
                    ) {
                        gpsAndNetworkCombinedLocation.first!!
                    } else {
                        gpsAndNetworkCombinedLocation.second!!
                    }

                    locationManager.removeUpdates(gpsListener)
                    locationManager.removeUpdates(networkListener)
                    emit(highestAccuracyLocation)
                }
            }catch(timeout: TimeoutCancellationException){
                try{
                    withTimeout(5000){
                        /*
                        * Listening for GPS Location
                        * */
                        val gpsLocation = locationByGps.first{
                            it!=null
                        }
                        locationManager.removeUpdates(gpsListener)
                        locationManager.removeUpdates(networkListener)
                        emit(gpsLocation)
                    }
                }catch (timeout: TimeoutCancellationException){
                    try{
                        withTimeout(5000) {
                            /*
                            * Listening for NETWORK Location
                            * */
                            val networkLocation = locationByNetwork.first {
                                it != null
                            }
                            locationManager.removeUpdates(gpsListener)
                            locationManager.removeUpdates(networkListener)
                            emit(networkLocation)
                        }
                    }catch(e: TimeoutCancellationException){
                        /*
                        * After failing to get location from both GPS and NETWORK within allocated time
                        * sending null to caller
                        * */
                        locationManager.removeUpdates(gpsListener)
                        locationManager.removeUpdates(networkListener)
                        emit(null)
                    }
                }
            }
        }else if(hasGps){
            /*
            * If Device has only GPS,
            * We are setting Listener for GPS
            * */
            try{
                withTimeout(5000){
                    /*
                    * Listening for GPS Location
                    * */
                    val gpsLocation = locationByGps.first {
                        it != null
                    }
                    locationManager.removeUpdates(gpsListener)
                    locationManager.removeUpdates(networkListener)
                    emit(gpsLocation)
                }
            }catch(timeout: TimeoutCancellationException){
                /*
                * Failed to get GPS Location,
                * sending null to caller
                * */
                locationManager.removeUpdates(gpsListener)
                locationManager.removeUpdates(networkListener)

                emit(null)
            }
        }else{
            /*
            * If Device has only NETWORK,
            * We are setting Listener for NETWORK
            * */
            try{
                withTimeout(5000){
                    /*
                    * Listening for NETWORK Location
                    * */
                    val networkLocation = locationByNetwork.first {
                        it != null
                    }
                    locationManager.removeUpdates(gpsListener)
                    locationManager.removeUpdates(networkListener)
                    emit(networkLocation)
                }
            }catch(timeout: TimeoutCancellationException){
                /*
                * Failed to get NETWORK Location, within allocated time
                * sending null to caller
                * */
                locationManager.removeUpdates(gpsListener)
                locationManager.removeUpdates(networkListener)
                emit(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationFlow(context: Context) = callbackFlow<Location> {
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