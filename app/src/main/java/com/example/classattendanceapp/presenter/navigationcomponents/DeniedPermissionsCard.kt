package com.example.classattendanceapp.presenter.navigationcomponents

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
    scaffoldPadding: PaddingValues,
    uiState: ClassAttendanceNavigationHostUiState,
    deniedPermissions: List<String>
) {

    if(deniedPermissions.isNotEmpty()){
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(scaffoldPadding)
                .border(5.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "Without following permissions some of the features will show undefined behaviour (if you've changed permissions just now, this message will disappear in some time)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                for (deniedPermission in deniedPermissions) {
                    TextButton(
                        onClick = {
                            Log.d("debugging", "Clicked text button")
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", uiState.context.packageName, null))
                            uiState.context.startActivity(intent)
                        }
                    ){
                        Text(text=deniedPermission)
                    }
                }
            }
        }
    }
}