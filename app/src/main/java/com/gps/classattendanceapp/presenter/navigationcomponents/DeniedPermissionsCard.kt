package com.gps.classattendanceapp.presenter.navigationcomponents

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeniedPermissionsCard(
    uiState: ClassAttendanceNavigationHostUiState,
    deniedPermissions: List<String>
) {

    if(deniedPermissions.isNotEmpty()){
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(5.dp, Color.Transparent, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
            ) {
                Text(
                    text = "Without following permissions some of the features will not work properly",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                for (deniedPermissionIndex in deniedPermissions.indices) {
                    if(deniedPermissionIndex == deniedPermissions.size - 1){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            TextButton(
                                onClick = {
                                    Log.d("debugging", "Clicked text button")
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", uiState.context.packageName, null))
                                    uiState.context.startActivity(intent)
                                }
                            ) {
                                Text(text = deniedPermissions[deniedPermissionIndex])
                            }

                            TextButton(
                                onClick = {
                                    uiState.classAttendanceViewModel.refreshPermissions(uiState.context)
                                }
                            ) {
                                Text(
                                    text = "Refresh"
                                )
                            }
                        }
                    }
                    else{
                        TextButton(
                            onClick = {
                                Log.d("debugging", "Clicked text button")
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", uiState.context.packageName, null))
                                uiState.context.startActivity(intent)
                            }
                        ) {
                            Text(text = deniedPermissions[deniedPermissionIndex])
                        }
                    }
                }
            }
        }
    }
}