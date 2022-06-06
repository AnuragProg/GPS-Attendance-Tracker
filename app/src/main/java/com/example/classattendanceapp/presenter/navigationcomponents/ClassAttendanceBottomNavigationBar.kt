package com.example.classattendanceapp.presenter.navigationcomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ClassAttendanceBottomNavigationBar(
    navController: NavController,
    callback: (String?, String?, Boolean) -> Unit
){

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val coroutineScope = rememberCoroutineScope()
    BottomNavigation(
        modifier = Modifier.fillMaxWidth()
    ) {
        for(screen in Screens.values()){
            BottomNavigationItem(
                selected = false,
                onClick = {
                          if(currentBackStackEntry.value?.destination?.route != screen.route){
                              callback(currentBackStackEntry.value?.destination?.route, screen.route, true)
                              coroutineScope.launch {
                                  delay(100)
                                  navController.navigate(screen.route){
                                      popUpToRoute
                                  }
                              }
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