package com.gps.classattendanceapp.presenter.screens.mapsscreen

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.preference.PreferenceManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(
    classAttendanceViewModel: ClassAttendanceViewModel,
){
    val uiState = rememberMapsScreenUiState(
        classAttendanceViewModel = classAttendanceViewModel
    )

    val subjectsList = remember{
        mutableStateListOf<ModifiedSubjects>()
    }

    val mapView by remember{
        mutableStateOf(
            MapView(uiState.context).apply{
                setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                setMultiTouchControls(true)
                setBuiltInZoomControls(false)
                controller.setZoom(16.5)

            }
        )
    }

    /*
    Searchbar filtering
     */
    LaunchedEffect(uiState.searchBarText.value){
        classAttendanceViewModel.getSubjectsAdvanced().collect{
            subjectsList.clear()
            subjectsList.addAll(
                it.filter{ subject ->
                    subject.latitude!=null && subject.longitude!=null
                }.filter{ subject ->
                    if(uiState.searchBarText.value.isNotBlank()){
                        uiState.searchBarText.value.trim().lowercase() in subject.subjectName.lowercase()
                    }else{
                        true
                    }
                }
            )
        }
    }


    /*
    MapView lifecycle adjustments
     */
    DisposableEffect(uiState.lifecycleOwner){

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when(event){
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                }
                else -> {}
            }
        }

        uiState.lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose{
            uiState.lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }


    /*
    Callback for checking network status
     */
    DisposableEffect(Unit){
        val connectivityManager = uiState.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object: ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                uiState.isInternetAlive.value = true
            }

            override fun onUnavailable() {
                uiState.isInternetAlive.value = false
            }

            override fun onLost(network: Network) {
                uiState.isInternetAlive.value = false
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }



    if(uiState.isInternetAlive.value){
        if(uiState.currentLocation.value!=null){
            Box(
                modifier = Modifier.fillMaxSize()
            ){
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        Configuration.getInstance()
                            .load(uiState.context, PreferenceManager.getDefaultSharedPreferences(uiState.context))
                        mapView.apply{
                            controller.setCenter(GeoPoint(uiState.currentLocation.value))
                        }
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp),
                    contentAlignment = Alignment.BottomCenter
                ){
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        value = uiState.searchLocation.value,
                        onValueChange = {
                            uiState.searchLocation.value = it
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            backgroundColor = Color.White,
                            textColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = null
                                )

                            }
                        }
                    )
                }
            }
        }else{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                    Text("Looking for your current location...")
                }
            }
        }

    }else{
        NoInternetScreen()
    }
}