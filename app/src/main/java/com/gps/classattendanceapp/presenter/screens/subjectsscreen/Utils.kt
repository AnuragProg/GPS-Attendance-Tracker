package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import androidx.annotation.RawRes
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.gps.classattendanceapp.R


fun getSubjectStatusLottieCompositionSpec(att: Float): LottieCompositionSpec{
    return LottieCompositionSpec.RawRes(
        if(att >= 75f) R.raw.sunny_weather
        else if(att >= 50f) R.raw.cloudy_weather
        else if(att >= 25f) R.raw.raining
        else R.raw.thunderstorm
    )
}
fun getSubjectStatusLottieCompositionSpec(att: Double): LottieCompositionSpec{
    return LottieCompositionSpec.RawRes(
        if(att >= 75f) R.raw.sunny_weather
        else if(att >= 50f) R.raw.cloudy_weather
        else if(att >= 25f) R.raw.raining
        else R.raw.thunderstorm
    )
}