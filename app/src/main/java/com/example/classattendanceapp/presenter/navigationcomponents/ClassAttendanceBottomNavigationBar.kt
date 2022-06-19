package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.navigation.NavController
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ClassAttendanceBottomNavigationBar(
    navController: NavController,
    navigate: (route: String) -> Unit
){

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        for(screen in Screens.values()){
            BottomNavigationItem(
                selected = false,
                onClick = {
                          if(currentBackStackEntry.value?.destination?.route != screen.route){
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