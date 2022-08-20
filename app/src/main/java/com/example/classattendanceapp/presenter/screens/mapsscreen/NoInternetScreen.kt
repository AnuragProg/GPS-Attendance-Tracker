package com.example.classattendanceapp.presenter.screens.mapsscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.classattendanceapp.R

@Composable
fun NoInternetScreen() {

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