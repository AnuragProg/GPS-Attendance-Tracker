package com.gps.classattendanceapp.ui.theme

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Dimensions(
    val disclaimer_title: TextUnit,
    val disclaimer_content: TextUnit,
    val map_image: Dp = 150.dp,
    val splash_screen_image: Dp = 150.dp,
    val no_subject_lottie_size: Dp = 300.dp,
    val loading_lottie_size: Dp = 300.dp,
    val subject_attendance_status_lottie_size : Dp = 70.dp
)

val mdpi = Dimensions(
    disclaimer_title = 15.sp,
    disclaimer_content = 20.sp,
)

val hdpi = Dimensions(
    disclaimer_title = 15.sp,
    disclaimer_content = 23.sp,
    map_image = 150.dp
)

val xhdpi = Dimensions(
    disclaimer_title = 20.sp,
    disclaimer_content = 30.sp,
    map_image = 150.dp,
    no_subject_lottie_size = 270.dp,
    loading_lottie_size = 270.dp,
    subject_attendance_status_lottie_size = 70.dp
)

val xxhdpi = Dimensions(
    disclaimer_title = 23.sp,
    disclaimer_content = 33.sp,
    map_image = 150.dp,
    no_subject_lottie_size = 300.dp,
    loading_lottie_size = 300.dp,
    subject_attendance_status_lottie_size = 70.dp
)

object Dimens {
    val dimen: Dimensions
        @Composable
        get() {
            val context = LocalContext.current
            val density = context.resources.displayMetrics.density
            val densityDpi = context.resources.displayMetrics.densityDpi
            Log.d("density", "density: $density")
            Log.d("density", "densityDpi: $densityDpi")
            val size = if(density >= 3.0f) xxhdpi
            else if(density >= 2.0f) xhdpi
            else if(density >= 1.5f) hdpi
            else mdpi
            Log.d("density", "Size is $size")
            return size
        }
}