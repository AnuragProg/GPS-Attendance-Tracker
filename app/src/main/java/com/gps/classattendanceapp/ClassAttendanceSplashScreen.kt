package com.gps.classattendanceapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gps.classattendanceapp.components.UserPreferences
import com.gps.classattendanceapp.presenter.theme.ClassAttendanceAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class ClassAttendanceSplashScreen : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(this)
        setContent {
            ClassAttendanceAppTheme {
                SplashScreen(userPreferences)
            }
        }
    }
}


@Composable
fun SplashScreen(
    userPreferences: UserPreferences
) {

    val context = LocalContext.current as Activity
    val animationDuration = remember{3000}
    LaunchedEffect(Unit){
        delay(animationDuration + 2000L)
        val navigateToProminentDisclosure = userPreferences.showProminentDisclosure()
        if(navigateToProminentDisclosure)
            context.startActivity(Intent(context, ProminentDisclosureActivity::class.java))
        else context.startActivity(Intent(context, MainActivity::class.java))

        context.finish()
    }
    var startAnimation by remember{
        mutableStateOf(false)
    }
    val animatedDp by animateFloatAsState(
        targetValue = if(!startAnimation) 0f else 1f,
        animationSpec = tween(
            durationMillis = animationDuration,
        )
    )


    LaunchedEffect(Unit){
        startAnimation = true
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Image(
            modifier = Modifier
                .padding(50.dp)
                .alpha(animatedDp),
            painter = painterResource(R.drawable.calendar),
            contentDescription = null
        )
    }
}