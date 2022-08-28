package com.example.classattendanceapp.presenter.screens.subjectsscreen

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.Network
import android.preference.PreferenceManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.classattendanceapp.R
import com.example.classattendanceapp.components.location.ClassLocationManager
import kotlinx.coroutines.flow.first
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

@Composable
fun LocationSelectionPopUp(
    changeLatitude: (Double)->Unit,
    changeLongitude: (Double)->Unit,
    changeLocationSelectionVisibility: (Boolean)->Unit,
){

    val context = LocalContext.current

    var currentLocation by remember{
        mutableStateOf<Location?>(null)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    var isConfirmLocationDialogVisible by remember{
        mutableStateOf(false)
    }

    var localLatitude by remember{
        mutableStateOf("")
    }
    var localLongitude by remember{
        mutableStateOf("")
    }

    var isInternetAlive by remember{
        mutableStateOf(false)
    }

    val mapView by remember{
        mutableStateOf(
            MapView(context).apply{
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                setBuiltInZoomControls(true)
            }
        )
    }

    DisposableEffect(lifecycleOwner){
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when(event){
                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                }
                else -> {

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    LaunchedEffect(Unit){
        val location = ClassLocationManager.getLocation(context).first()
        currentLocation = location
        if(currentLocation != null){
            mapView.controller.setCenter(GeoPoint(currentLocation!!.latitude, currentLocation!!.longitude))
            mapView.controller.setZoom(12.5)
        }else{
            mapView.controller.setCenter(GeoPoint(30.0, 70.0))
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
        onDispose{
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    // alert dialog to confirm location change
    Dialog(
        onDismissRequest = {
            changeLocationSelectionVisibility(false)
        }

    ) {

        if(isInternetAlive){
            AndroidView(
                modifier = Modifier.size(500.dp),
                factory = {
                    Configuration.getInstance()
                        .load(context, PreferenceManager.getDefaultSharedPreferences(context))
                    mapView
                }
            ) { map ->
                val controller = map.controller
                if (currentLocation != null) {
                    controller.setCenter(GeoPoint(currentLocation!!.latitude,
                        currentLocation!!.longitude))
                }
                controller.setZoom(16.5)


                val mapClickOverlay = MapEventsOverlay(
                    object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(location: GeoPoint?): Boolean {
                            if(location!=null){
                                localLatitude = location.latitude.toString()
                                localLongitude = location.longitude.toString()
                                isConfirmLocationDialogVisible = true
                            }
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint?): Boolean {
                            return false
                        }

                    }
                )

                map.overlays.add(mapClickOverlay)
            }
        }else{
            Box(
                modifier = Modifier.size(500.dp),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.no_internet),
                    contentDescription = null
                )
            }
        }

        Box(
            modifier = Modifier.size(500.dp),
            contentAlignment = Alignment.TopEnd
        ){
            IconButton(onClick = { changeLocationSelectionVisibility(false) }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null
                )
            }
        }
    }
    if(isConfirmLocationDialogVisible){
        AlertDialog(
            onDismissRequest = {
                isConfirmLocationDialogVisible = false
            },
            title = {
                Text("Confirm Location Selection")
            },
            text = {
                Text("Do you want to confirm the selected Location?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        changeLatitude(localLatitude.toDouble())
                        changeLongitude(localLongitude.toDouble())
                        isConfirmLocationDialogVisible = false
                        changeLocationSelectionVisibility(false)
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isConfirmLocationDialogVisible = false
                    }
                ){
                    Text("Cancel")
                }
            }
        )
    }
}