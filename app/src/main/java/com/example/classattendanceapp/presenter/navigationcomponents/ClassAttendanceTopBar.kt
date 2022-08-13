package com.example.classattendanceapp.presenter.navigationcomponents


import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel

@Composable
fun ClassAttendanceTopBar(
    classAttendanceViewModel: ClassAttendanceViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState
){
    val context = LocalContext.current
    var showSearchBar by remember{
        mutableStateOf(false)
    }
    val searchBarText = classAttendanceViewModel.searchBarText.collectAsState()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    var isOverflowMenuVisible by remember{
        mutableStateOf(false)
    }

    TopAppBar(
        title = {
            if(!showSearchBar){
                Text(context.getString(R.string.app_name))
            }else{
                /*
                Text Field for filtering through searching
                 */
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
            if(!showSearchBar){
                if (currentBackStackEntry.value?.destination?.route !in listOf(Screens.TIMETABLESCREEN.route,
                        Screens.MAPSSCREEN.route)
                ) {
                    IconButton(
                        onClick = {
                            showSearchBar = true
                        }
                    ){
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    }
                }

                IconButton(
                    onClick = {
                        isOverflowMenuVisible = true
                    }
                ){
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null
                    )
                }
            }
            OverflowMenu(
                classAttendanceViewModel = classAttendanceViewModel,
                isOverflowMenuVisible = isOverflowMenuVisible,
                changeOverflowMenuVisibility = {isOverflowMenuVisible=it},
                snackbarHostState = snackbarHostState
            )

        }
    )
}