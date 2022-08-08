package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ClassAttendanceBottomNavigationBar(
    navController: NavController,
    navigate: (route: String) -> Unit
){

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    var currentCutOutSize by remember{
        mutableStateOf(50.dp)
    }
    val currentCutOutAnimation = animateDpAsState(
        targetValue = currentCutOutSize,
        animationSpec = tween()
    )

    LaunchedEffect(Unit){
        navController.currentBackStackEntryFlow.collectLatest{
            currentCutOutSize = when(it.destination.route){
                Screens.SETTINGSSCREEN.route -> {
                    0.dp
                }
                else -> {
                    50.dp
                }
            }
        }
    }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        cutoutShape = MaterialTheme.shapes.small.copy(
            CornerSize(40)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            for (screen in Screens.values()) {
                Surface(
                    modifier = Modifier
                        .width(90.dp),
                    color = if(currentBackStackEntry.value?.destination?.route == screen.route){
                        MaterialTheme.colors.secondaryVariant
                    }else{
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(
                        40
                    )
                ){
                    BottomNavigationItem(
                        selected = false,
                        onClick = {
                            if (currentBackStackEntry.value?.destination?.route != screen.route) {
                                navigate(screen.route)
                            }
                        },
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.screen_name
                            )
                        },
                        label = {
                            Text(screen.screen_name)
                        }
                    )
                }
            }
        }
    }
}