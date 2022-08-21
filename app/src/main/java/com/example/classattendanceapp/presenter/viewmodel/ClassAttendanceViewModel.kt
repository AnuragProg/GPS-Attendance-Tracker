package com.example.classattendanceapp.presenter.viewmodel

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classattendanceapp.data.models.Log
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.domain.usecases.usecase.ClassAttendanceUseCase
import com.example.classattendanceapp.domain.utils.alarms.ClassAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shreyaspatil.permissionFlow.MultiplePermissionState
import dev.shreyaspatil.permissionFlow.PermissionFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ClassAttendanceViewModel @Inject constructor(
    private val classAttendanceUseCase: ClassAttendanceUseCase,
    private val permissionFlow: PermissionFlow
): ViewModel() {



    private var _deniedPermissions = MutableStateFlow<List<String>>(emptyList())
    val deniedPermissions : StateFlow<List<String>> get() = _deniedPermissions


    init{
        viewModelScope.launch {
            getSubjectsAdvanced().collectLatest { retrievedSubjectsList ->
                if(!_isInitialSubjectDataRetrievalDone.value){
                    _isInitialSubjectDataRetrievalDone.value = true
                }
                _subjectsList.value = retrievedSubjectsList
            }
        }
        viewModelScope.launch {
            getAllLogsAdvanced().collectLatest { retrievedLogsList ->
                if(!_isInitialLogDataRetrievalDone.value){
                    _isInitialLogDataRetrievalDone.value = true
                }
                _logsList.value = retrievedLogsList
            }
        }

        viewModelScope.launch{
            permissionsStateFlow().collectLatest{
                d("debugging", "Granted Permissions: ${it.grantedPermissions}")
                d("debugging", "Denied Permissions: ${it.deniedPermissions}")
                val deniedPermissions = mutableListOf<String>()
                it.deniedPermissions.forEach{
                    val permission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        when (it) {
                            Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
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
                    }else{
                        when (it) {
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                d("debugging", "Added location for fine or coarse location")
                                "Location"
                            }
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET -> {
                                d("debugging", "Added Network for network state and internet")
                                "Network"
                            }
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE -> {
                                d("debugging", "Added Storage for read/write external storage")
                                "Storage"
                            }
                            else -> ""
                        }
                    }
                    if(permission.isNotBlank() && permission !in deniedPermissions){
                        deniedPermissions.add(permission)
                    }
                }
                _deniedPermissions.value = deniedPermissions
            }
        }
    }

    fun permissionsStateFlow():StateFlow<MultiplePermissionState>{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionFlow.getMultiplePermissionState(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        } else {
            permissionFlow.getMultiplePermissionState(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }
    }


    private var _searchBarText = MutableStateFlow("")
    val searchBarText: StateFlow<String> get() = _searchBarText

    fun changeSearchBarText(text: String){
        _searchBarText.value = text
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

    private var _isInitialSubjectDataRetrievalDone = MutableStateFlow(false)
    val isInitialSubjectDataRetrievalDone : StateFlow<Boolean> get() = _isInitialSubjectDataRetrievalDone

    private var _isInitialLogDataRetrievalDone = MutableStateFlow(false)
    val isInitialLogDataRetrievalDone : StateFlow<Boolean> get() = _isInitialLogDataRetrievalDone

    private var _subjectsList = MutableStateFlow<List<ModifiedSubjects>>(emptyList())
    val subjectsList : StateFlow<List<ModifiedSubjects>> get() = _subjectsList

    private var _logsList = MutableStateFlow<List<ModifiedLogs>>(emptyList())
    val logsList : StateFlow<List<ModifiedLogs>> get() = _logsList


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

    fun getAllLogsAdvanced() :Flow<List<ModifiedLogs>>{
        return classAttendanceUseCase.getAllLogsUseCase()
    }

    fun getSubjectsAdvanced() : Flow<List<ModifiedSubjects>>{
        return classAttendanceUseCase.getSubjectsUseCase()
    }

    fun getTimeTableAdvanced(): Flow<Map<String, List<TimeTable>>>{
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

    fun writeSubjectsStatsToExcel(context: Context, subjectsList: List<ModifiedSubjects>): Uri{
        return classAttendanceUseCase.writeSubjectsStatsToExcelUseCase(context, subjectsList)
    }
    fun writeLogsStatsToExcel(context: Context, logsList: List<ModifiedLogs>): Uri{
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