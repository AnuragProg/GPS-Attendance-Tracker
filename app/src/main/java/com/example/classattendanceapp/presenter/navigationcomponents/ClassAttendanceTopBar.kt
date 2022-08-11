package com.example.classattendanceapp.presenter.navigationcomponents


import androidx.compose.animation.*
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClassAttendanceTopBar(
    classAttendanceViewModel: ClassAttendanceViewModel,
    navController: NavController
){
    val context = LocalContext.current
    var showSearchBar by remember{
        mutableStateOf(false)
    }
    val searchBarText = classAttendanceViewModel.searchBarText.collectAsState()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    TopAppBar(
        title = {
            if(!showSearchBar){
                Text(context.getString(R.string.app_name))
            }else{

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchBarText.value,
                    onValueChange = { classAttendanceViewModel.changeSearchBarText(it) },
                    label = null,
                    placeholder = {
                        Text(stringResource(R.string.subject_name))
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        placeholderColor = Color.White
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                classAttendanceViewModel.changeSearchBarText("")
                                showSearchBar = false
                            }
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        },
        actions = {

            if(currentBackStackEntry.value?.destination?.route in listOf(
                    Screens.TIMETABLESCREEN.route,
                    Screens.MAPSSCREEN.route
            )){

            }
            else if (!showSearchBar) {
                IconButton(
                    onClick = {
                        showSearchBar = true
                    }
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null
                    )
                }
            }
        }
    )
}