package com.example.classattendanceapp.presenter.screens.mapsscreen

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.preference.PreferenceManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*


@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun MapsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
){
    val context = LocalContext.current

    val subjectsList = remember{
        mutableStateListOf<ModifiedSubjects>()
    }

    val searchBarText by classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle()

    val mapView by remember{
        mutableStateOf(
            MapView(context).apply{
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                setBuiltInZoomControls(true)
                controller.setZoom(16.5)

            }
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    var isInternetAlive by remember{
        mutableStateOf(false)
    }

    LaunchedEffect(searchBarText){
        classAttendanceViewModel.getSubjectsAdvanced().collect{
            subjectsList.clear()
            subjectsList.addAll(
                    it.filter{ subject ->
                        subject.latitude!=null && subject.longitude!=null
                    }.filter{ subject ->
                        if(searchBarText.isNotBlank()){
                            searchBarText.trim().lowercase() in subject.subjectName.lowercase()
                        }else{
                            true
                        }
                    }
            )
        }
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
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    Configuration.getInstance()
                        .load(context, PreferenceManager.getDefaultSharedPreferences(context))
                    mapView
                }
            ) { map ->

                subjectsList
                    .forEach {
                        val marker = Marker(map)
                        marker.position = GeoPoint(it.latitude!!, it.longitude!!)
                        marker.title = it.subjectName
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        map.overlays.add(marker)
                    }

            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ){
                items(
                    items = subjectsList,
                    key = {subject ->
                        subject._id
                    }
                ){ subject ->
                    Card(
                        modifier= Modifier
                            .padding(5.dp)
                            .border(2.dp, Color.Black),
                        onClick = {
                            mapView.controller.setCenter(GeoPoint(subject.latitude!!, subject.longitude!!))
                        }
                    ){
                        Column(
                            modifier = Modifier.padding(5.dp)
                        ){
                            Text(subject.subjectName)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text("Latitude: " + String.format("%.6f",subject.latitude))
                            Text("Longitude: " + String.format("%.6f",subject.longitude))
                            Text("Range: " + String.format("%.6f",subject.range))
                        }
                    }
                }
            }
        }
    }else{
        NoInternetScreen()
    }
}