package com.gps.classattendanceapp.presenter.navigationcomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun ClassAttendanceBottomNavigationBar(
    navController: NavController,
    navigate: (route: String) -> Unit
){

    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        cutoutShape = MaterialTheme.shapes.small.copy(
            CornerSize(40)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
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