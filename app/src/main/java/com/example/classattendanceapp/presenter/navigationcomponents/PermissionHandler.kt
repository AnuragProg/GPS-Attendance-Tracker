package com.example.classattendanceapp.presenter.navigationcomponents

import android.Manifest
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(){

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver{ _ , event ->
            if(event == Lifecycle.Event.ON_START){
                permissions.launchMultiplePermissionRequest()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        permissions.permissions.forEach{ perm ->
            when(perm.permission){
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    when{
                        perm.status.isGranted -> {
                        }
                        perm.status.shouldShowRationale -> {
                            perm.launchPermissionRequest()
                        }
                        perm.isPermanentlyDenied() -> {
                        }
                    }
                }
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    when{
                        perm.status.isGranted -> {
                        }
                        perm.status.shouldShowRationale -> {
                            perm.launchPermissionRequest()
                        }
                        perm.isPermanentlyDenied() -> {
                        }
                    }
                }
                Manifest.permission.INTERNET -> {
                    when{
                        perm.status.isGranted -> {
                        }
                        perm.status.shouldShowRationale -> {
                            perm.launchPermissionRequest()
                        }
                        perm.isPermanentlyDenied() -> {
                        }
                    }
                }
                Manifest.permission.ACCESS_NETWORK_STATE -> {
                    when{
                        perm.status.isGranted -> {
                        }
                        perm.status.shouldShowRationale -> {
                            perm.launchPermissionRequest()
                        }
                        perm.isPermanentlyDenied() -> {
                        }
                    }
                }
                Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                    when{
                        perm.status.isGranted->{

                        }
                        perm.status.shouldShowRationale ->{
                            perm.launchPermissionRequest()
                        }
                        perm.isPermanentlyDenied() ->{

                        }
                    }
                }
            }

        }
    } )
}

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.isPermanentlyDenied(): Boolean{
    return !status.isGranted && !status.shouldShowRationale
}
