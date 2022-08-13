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
    grantedPermissions: (List<String>) -> Unit,
    nonGrantedPermissions: (List<String>)->Unit
){

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                Manifest.permission.RECEIVE_BOOT_COMPLETED
            )
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                Manifest.permission.RECEIVE_BOOT_COMPLETED

            )
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner){
        val lifecycleObserver = LifecycleEventObserver{_, event->

            when(event){
                Lifecycle.Event.ON_START -> {
                    // Launching all permissions at once
                    permissions.launchMultiplePermissionRequest()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose{
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    // Adding non granted permissions
    if(!permissions.allPermissionsGranted){
        nonGrantedPermissions(permissions.revokedPermissions.map{it.permission})
    }

    // Adding granted Permissions
    val allowedPermissions = mutableListOf<String>()
    for(permission in permissions.permissions){
        if(permission.status.isGranted){
            allowedPermissions.add(permission.permission)
        }
    }
    grantedPermissions(allowedPermissions)


}
