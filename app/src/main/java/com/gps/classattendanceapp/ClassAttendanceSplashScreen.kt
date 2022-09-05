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
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.gps.classattendanceapp.ui.theme.ClassAttendanceAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class ClassAttendanceSplashScreen : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClassAttendanceAppTheme {
                SplashScreen(auth)
            }
        }
    }
}


@Composable
fun SplashScreen(
    auth: FirebaseAuth
) {

    val context = LocalContext.current as Activity
    val animationDuration = remember{5000}
    LaunchedEffect(Unit){
        delay(animationDuration + 2000L)
        context.startActivity(Intent(context, MainActivity::class.java))
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
            .fillMaxSize()
            .background(Color(0xFFB3E5FC)),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Card(
                modifier = Modifier
                    .padding(50.dp)
                    .alpha(animatedDp),
                shape = CircleShape,
                backgroundColor = Color(0xFF4FC3F7)
            ) {
                Image(
                    painter = painterResource(R.drawable.gps_attendance_tracker_icon),
                    contentDescription = null
                )
            }

            Text(
                text = "Bring Discipline To Life!",
                fontFamily = FontFamily(
                    Font(R.font.notoserif_regular)
                ),
                fontSize = 30.sp,
                color = Color.White
            )
        }
    }
}