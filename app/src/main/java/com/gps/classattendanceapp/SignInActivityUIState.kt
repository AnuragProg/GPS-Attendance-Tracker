package com.gps.classattendanceapp

import android.widget.ProgressBar
import androidx.compose.runtime.*

data class SignInActivityUIState(
    val showProgressBar: MutableState<Boolean>
)

@Composable
fun rememberSignInActivityUIState() = remember{
    SignInActivityUIState(
        showProgressBar = mutableStateOf(false)
    )
}