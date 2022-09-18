package com.gps.classattendanceapp.presenter.navigationcomponents


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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.presenter.viewmodel.ClassAttendanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ClassAttendanceTopBar(
    classAttendanceViewModel: ClassAttendanceViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    listOfSubjectIdsToDelete: List<Int>,
    listOfLogIdsToDelete: List<Int>,
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
                    horizontalArrangement = Arrangement.Start
                ){
                    Text(
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch{
                                    if (currentBackStackEntry.value?.destination?.route == Screens.SUBJECTSSCREEN.route) {
                                        clearSubjectIdsToDelete()
                                        classAttendanceViewModel.deleteSubjectsInList(
                                            context,
                                            classAttendanceViewModel.filteredSubjects.value.data!!.map { it._id }
                                        )
                                    } else if (currentBackStackEntry.value?.destination?.route == Screens.LOGSSCREEN.route) {
                                        clearLogIdsToDelete()
                                        classAttendanceViewModel.deleteLogsInList(
                                            classAttendanceViewModel.filteredLogs.value.data!!.map { it._id!! })
                                    }
                                }
                            },
                        text = "Delete all"
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
                if (currentBackStackEntry.value?.destination?.route !in listOf(Screens.TIMETABLESCREEN.route, )
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



