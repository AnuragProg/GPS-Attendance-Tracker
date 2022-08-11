package com.example.classattendanceapp.presenter.screens.mapsscreen

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.preference.PreferenceManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.R
import com.example.classattendanceapp.domain.utils.location.ClassLocationManager
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.flow.first
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MapsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
){
    val context = LocalContext.current

    val subjectsList = classAttendanceViewModel.subjectsList.collectAsStateWithLifecycle()

    val mapView by remember{
        mutableStateOf(
            MapView(context).apply{

                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                setBuiltInZoomControls(true)
            }
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    var isInternetAlive by remember{
        mutableStateOf(false)
    }

    DisposableEffect(lifecycleOwner){

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when(event){
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                }
                else -> {

                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose{
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    LaunchedEffect(Unit){


        val currentLocation = ClassLocationManager.getLocation(context).first()
        if(currentLocation != null){
            mapView.controller.setCenter(GeoPoint(currentLocation.latitude, currentLocation.longitude))
            mapView.controller.setZoom(12.5)
        }else if(subjectsList.value.isNotEmpty()){
            val randomSubject = subjectsList.value.filter{
                it.latitude!=null && it.longitude!=null
            }.random()
            mapView.controller.setCenter(
                GeoPoint(randomSubject.latitude!!, randomSubject.longitude!!)
            )
            mapView.controller.setZoom(12.5)
        }else{

            mapView.controller.setZoom(12.5)
        }
    }

    DisposableEffect(Unit){
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object: ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                isInternetAlive = true
            }

            override fun onUnavailable() {
                isInternetAlive = false
            }

            override fun onLost(network: Network) {
                isInternetAlive = false
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }




    if(isInternetAlive){
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                Configuration.getInstance()
                    .load(context, PreferenceManager.getDefaultSharedPreferences(context))
                mapView
            }
        ) { map ->
            val mapController = map.controller

            subjectsList.value
                .filter {
                    it.latitude != null && it.longitude != null
                }
                .forEach {
                    val marker = Marker(map)
                    marker.position = GeoPoint(it.latitude!!, it.longitude!!)
                    marker.title = it.subjectName
                    marker.setTextIcon(it.attendancePercentage.toString())
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.showInfoWindow()
                    map.overlays.add(marker)
                }

        }
    }else{
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(id = R.drawable.no_internet),
                contentDescription = null
            )
        }
    }
}