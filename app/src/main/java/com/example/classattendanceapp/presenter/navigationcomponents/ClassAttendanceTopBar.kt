package com.example.classattendanceapp.presenter.navigationcomponents


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.classattendanceapp.R
import com.example.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ClassAttendanceTopBar(
    classAttendanceViewModel: ClassAttendanceViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    listOfSubjectIdsToDelete: List<Int>,
    listOfLogIdsToDelete: List<Int>,
    removeSubjectIdToDelete: (Int)->Unit,
    removeLogIdToDelete: (Int)->Unit,
    clearSubjectIdsToDelete: ()->Unit,
    clearLogIdsToDelete: ()->Unit
){
    val context = LocalContext.current
    var showSearchBar by remember{
        mutableStateOf(false)
    }
    val searchBarText = classAttendanceViewModel.searchBarText.collectAsStateWithLifecycle()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    var isOverflowMenuVisible by remember{
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()

    TopAppBar(
        title = {
            if(!showSearchBar && listOfSubjectIdsToDelete.isEmpty() && listOfLogIdsToDelete.isEmpty()){
                Text(context.getString(R.string.app_name))
            }else if(listOfSubjectIdsToDelete.isNotEmpty()||listOfLogIdsToDelete.isNotEmpty()){

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch{
                                    if (currentBackStackEntry.value?.destination?.route == Screens.SUBJECTSSCREEN.route) {
                                        clearSubjectIdsToDelete()
                                        classAttendanceViewModel.deleteSubjectsInList(context,
                                            classAttendanceViewModel.subjectsList.value.map { it._id })
                                    } else if (currentBackStackEntry.value?.destination?.route == Screens.LOGSSCREEN.route) {
                                        clearLogIdsToDelete()
                                        classAttendanceViewModel.deleteLogsInList(
                                            classAttendanceViewModel.logsList.value.map { it._id })
                                    }
                                }
                            },
                        text = "Delete all"
                    )
                    Text(
                        modifier = Modifier
                            .clickable{
                                coroutineScope.launch{
                                    if (currentBackStackEntry.value?.destination?.route == Screens.SUBJECTSSCREEN.route) {
                                        classAttendanceViewModel.deleteSubjectsInList(context,
                                            listOfSubjectIdsToDelete)
                                        clearSubjectIdsToDelete()
                                    } else if (currentBackStackEntry.value?.destination?.route == Screens.LOGSSCREEN.route) {
                                        classAttendanceViewModel.deleteLogsInList(
                                            listOfLogIdsToDelete)
                                        clearLogIdsToDelete()
                                    }
                                }
                            },
                        text = "Delete"
                    )
                }

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
            if(!showSearchBar && listOfSubjectIdsToDelete.isEmpty() && listOfLogIdsToDelete.isEmpty()){
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



