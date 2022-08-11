package com.example.classattendanceapp.presenter.navigationcomponents

import android.Manifest
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    addNonGrantedPermissionToList: (String) -> Unit,
    removeNonGrantedPermissionFromList: (String) -> Unit
){

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver{ _ , event ->
            if(event == Lifecycle.Event.ON_START){
                permissions.permissions.forEach{
                    if(it.status is PermissionStatus.Denied || it.status != PermissionStatus.Granted){

                        it.launchPermissionRequest()
                    }
                }
                permissions.revokedPermissions.forEach{
                    val permission = when(it.permission){
                        Manifest.permission.ACCESS_COARSE_LOCATION ->{
                            "Location Permission"
                        }
                        Manifest.permission.ACCESS_FINE_LOCATION ->{
                            "Location Permission"
                        }
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION ->{
                            "Location Permission"
                        }
                        Manifest.permission.ACCESS_NETWORK_STATE ->{
                            "Access Network State Permission"
                        }
                        Manifest.permission.WRITE_EXTERNAL_STORAGE->{
                            "Storage Permission"
                        }
                        else -> {
                            null
                        }
                    }
                    permission?.let{
                        addNonGrantedPermissionToList(permission)
                    }
                }
                permissions.permissions.filter{
                    it.status.isGranted
                }.forEach{
                    val permission = when(it.permission) {
                        Manifest.permission.ACCESS_COARSE_LOCATION -> {
                            "Location Permission"
                        }
                        Manifest.permission.ACCESS_FINE_LOCATION -> {
                            "Location Permission"
                        }
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                            "Location Permission"
                        }
                        Manifest.permission.ACCESS_NETWORK_STATE -> {
                            "Access Network State Permission"
                        }
                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                            "Storage Permission"
                        }
                        else -> {
                            null
                        }
                    }

                    permission?.let{
                        removeNonGrantedPermissionFromList(permission)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })


}
