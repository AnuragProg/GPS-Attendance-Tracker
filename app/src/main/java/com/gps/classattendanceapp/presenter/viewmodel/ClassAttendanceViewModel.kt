package com.gps.classattendanceapp.presenter.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.components.alarms.ClassAlarmManager
import com.gps.classattendanceapp.data.models.Log
import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.models.ModifiedLogs
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.domain.usecases.usecase.ClassAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ClassAttendanceViewModel @Inject constructor(
    private val classAttendanceUseCase: ClassAttendanceUseCase,
): ViewModel() {

    private val _deniedPermissions = MutableStateFlow<Set<String>>(emptySet())
    val deniedPermissions : StateFlow<Set<String>> get() = _deniedPermissions


    private val requiredPermissions =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }else{
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }

    private var _searchBarText = MutableStateFlow("")
    val searchBarText: StateFlow<String> get() = _searchBarText

    fun changeSearchBarText(text: String){
        _searchBarText.value = text
    }

    private val _subjects = MutableStateFlow<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>>>(Resource.Loading())
    val subjects get() = _subjects.asStateFlow()

    private val _logs = MutableStateFlow<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedLogs>>>(Resource.Loading())
    val logs get() = _logs.asStateFlow()

    private val _filteredSubjects = MutableStateFlow<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>>>(Resource.Loading())
    val filteredSubjects get() = _filteredSubjects.asStateFlow()

    private val _filteredLogs = MutableStateFlow<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedLogs>>>(Resource.Loading())
    val filteredLogs get() = _filteredLogs.asStateFlow()

    fun getAllLogs() :Flow<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedLogs>>>{
        return classAttendanceUseCase.getAllLogsUseCase()
    }

    fun getAllSubjects() : Flow<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>>>{
        return classAttendanceUseCase.getSubjectsUseCase()
    }

    init{
        viewModelScope.launch{
            getAllLogs().collectLatest{
                _logs.value = it
            }
        }

        viewModelScope.launch{
            getAllSubjects().collectLatest{
                _subjects.value = it
            }
        }

        viewModelScope.launch{
            combine(_searchBarText, _subjects){ searchQuery, subjectsList ->
                Pair(searchQuery, subjectsList)
            }.collectLatest{ searchAndSubjectList ->
                when(searchAndSubjectList.second){
                    is Resource.Error -> {
                        d("debugging", "Sending Error Signal to UI")
                        _filteredSubjects.value = searchAndSubjectList.second
                    }
                    is Resource.Loading -> {
                        d("debugging", "Sending Loading Signal to UI")
                        _filteredSubjects.value = Resource.Loading()
                    }
                    is Resource.Success -> {
                        _filteredSubjects.value = Resource.Success(
                            searchAndSubjectList.second.data!!.filter{
                                searchAndSubjectList.first.lowercase() in it.subjectName.lowercase()
                            }
                        )
                    }
                }

                d("debugging", "${filteredSubjects.value} new subjects")
            }
        }

        viewModelScope.launch{
            combine(_searchBarText, _logs){ searchQuery, logsList ->
                Pair(searchQuery, logsList)
            }.collectLatest{ searchAndLogsList ->
                when(searchAndLogsList.second){
                    is Resource.Error -> {
                        _filteredLogs.value = searchAndLogsList.second
                    }
                    is Resource.Loading -> {
                        _filteredLogs.value = Resource.Loading()
                    }
                    is Resource.Success -> {
                        _filteredLogs.value = Resource.Success(
                            searchAndLogsList.second.data!!.filter{
                                searchAndLogsList.first.lowercase() in it.subjectName!!.lowercase()
                            }
                        )
                    }
                }
                d("debugging", "${filteredLogs.value} new logs")
            }
        }


        // Send Resource.Success only if the List of data is non empty
        // Otherwise send error with appropriate message

    }


    fun refreshPermissions(context: Context){
        val deniedPermissions = mutableSetOf<String>()
        requiredPermissions.forEach{ permission ->
            val result = context.checkSelfPermission(permission)

            if(result != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(
                    when (permission) {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                            "Location"
                        }
                        Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET -> {
                            "Network"
                        }
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            "Storage"
                        }
                        else -> ""
                    }
                )
            }
        }
        _deniedPermissions.value = deniedPermissions
    }


    private var _startAttendanceArcAnimation = MutableStateFlow(false)
    val startAttendanceArcAnimation : StateFlow<Boolean> get() = _startAttendanceArcAnimation

    fun startAttendanceArcAnimationInitiate(){
        _startAttendanceArcAnimation.value = true
    }

    private var _currentHour = MutableStateFlow(0)
    val currentHour : StateFlow<Int> get() = _currentHour

    private var _currentMinute = MutableStateFlow(0)
    val currentMinute : StateFlow<Int> get() = _currentMinute

    private var _currentYear = MutableStateFlow(0)
    val currentYear : StateFlow<Int> get() = _currentYear

    private var _currentMonth = MutableStateFlow(0)
    val currentMonth : StateFlow<Int> get() = _currentMonth

    private var _currentDay = MutableStateFlow(0)
    val currentDay : StateFlow<Int> get() = _currentDay

    fun changeCurrentHour(hour: Int){
        _currentHour.value = hour
    }

    fun changeCurrentMinute(minute: Int){
        _currentMinute.value = minute
    }

    fun changeCurrentYear(year: Int){
        _currentYear.value = year
    }

    fun changeCurrentMonth(month: Int){
        _currentMonth.value = month
    }

    fun changeCurrentDay(day: Int){
        _currentDay.value = day
    }


    private var _floatingButtonClicked = MutableStateFlow(false)
    val floatingButtonClicked: StateFlow<Boolean> get() = _floatingButtonClicked

    fun changeFloatingButtonClickedState(
        state: Boolean,
        doNotMakeChangesToTime: Boolean? = null
    ){
        if(doNotMakeChangesToTime==null && state){
            updateHourMinuteYearMonthDay()
        }
        _floatingButtonClicked.value = state
    }

    private fun updateHourMinuteYearMonthDay(){
        _currentHour.value = getCurrentHour()
        _currentMinute.value = getCurrentMinute()
        _currentYear.value = getCurrentYear()
        _currentMonth.value = getCurrentMonth()
        _currentDay.value = getCurrentDay()
    }


    suspend fun getSubjectWithId(subjectId: Int):Subject?{
        return classAttendanceUseCase.getSubjectWithIdWithUseCase(subjectId)
    }


    suspend fun updateSubject(subject: Subject){
        classAttendanceUseCase.updateSubjectUseCase(subject)
    }

    suspend fun updateLog(log: Log){
        classAttendanceUseCase.updateLogUseCase(log)
    }

    fun getTimeTable(): Flow<Resource<Map<String, List<TimeTable>>>>{
        return classAttendanceUseCase.getTimeTableUseCase()
    }

    suspend fun insertLogs(log: Log): Long{
        val subjectWithId = classAttendanceUseCase.getSubjectWithIdWithUseCase(log.subjectId)

        val subject = subjectWithId?.let{
            if (log.wasPresent) {
                it.daysPresentOfLogs++
            } else {
                it.daysAbsentOfLogs++
            }
            it
        }
        subject?.let{
            classAttendanceUseCase.insertSubjectUseCase(subject)
        }
        return classAttendanceUseCase.insertLogsUseCase(log)
    }

    suspend fun insertSubject(subject: Subject): Long{
        return classAttendanceUseCase.insertSubjectUseCase(
            subject
        )
    }

    suspend fun insertTimeTable(
        timeTable: TimeTable,
        context: Context,

    ): Long{
        val id = classAttendanceUseCase.insertTimeTableUseCase(timeTable)
        ClassAlarmManager.registerAlarm(
            context = context,
            timeTableId = id.toInt(),
            timeTable = timeTable
        )
        return id
    }

    suspend fun deleteLogs(id: Int){
        val logWithId = classAttendanceUseCase.getLogsWithIdUseCase(id) ?: return

        val subjectWithId = classAttendanceUseCase.getSubjectWithIdWithUseCase(logWithId.subjectId) ?: return
        if(logWithId.wasPresent){
            if(subjectWithId.daysPresentOfLogs>0){ subjectWithId.daysPresentOfLogs-- }
        }else{
            if(subjectWithId.daysAbsentOfLogs>0){ subjectWithId.daysAbsentOfLogs-- }
        }
        classAttendanceUseCase.insertSubjectUseCase(
            subjectWithId
        )
        classAttendanceUseCase.deleteLogsUseCase(id)
    }

    suspend fun deleteSubject(id: Int, context: Context){
        val timeTableWithSubjectId = classAttendanceUseCase.getTimeTableWithSubjectIdUseCase(id).first()
        for(timeTable in timeTableWithSubjectId){
            deleteTimeTable(
                timeTable._id,
                context
            )
        }
        classAttendanceUseCase.deleteLogsWithSubjectIdUseCase(id)
        classAttendanceUseCase.deleteSubjectUseCase(id)
    }

    suspend fun deleteTimeTable(id: Int, context: Context){
        val tempTimeTable = classAttendanceUseCase.getTimeTableWithIdUseCase(id) ?: return
        classAttendanceUseCase.deleteTimeTableUseCase(id)
        ClassAlarmManager.cancelAlarm(
            context = context,
            timeTable = tempTimeTable
        )
    }

    private fun getCurrentYear():Int{
        val cal = Calendar.getInstance()
        return cal.get(Calendar.YEAR)
    }
    private fun getCurrentMonth():Int{
        val cal = Calendar.getInstance()
        return cal.get(Calendar.MONTH)
    }
    private fun getCurrentDay():Int{
        val cal = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_MONTH)
    }
    private fun getCurrentHour():Int{
        val cal = Calendar.getInstance()
        return cal.get(Calendar.HOUR_OF_DAY)
    }
    private fun getCurrentMinute():Int{
        val cal = Calendar.getInstance()
        return cal.get(Calendar.MINUTE)
    }

    fun writeSubjectsStatsToExcel(context: Context, subjectsList: List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>): Uri{
        return classAttendanceUseCase.writeSubjectsStatsToExcelUseCase(context, subjectsList)
    }
    fun writeLogsStatsToExcel(context: Context, logsList: List<com.gps.classattendanceapp.domain.models.ModifiedLogs>): Uri{
        return classAttendanceUseCase.writeLogsStatsToExcelUseCase(context, logsList)
    }

    suspend fun deleteSubjectsInList(context: Context,subjectIds: List<Int>){
        subjectIds.forEach { subjectId ->
            deleteSubject(subjectId, context)
        }
    }
    suspend fun deleteLogsInList(logIds: List<Int>){
        logIds.forEach { logId ->
            deleteLogs(logId)
        }
    }
}