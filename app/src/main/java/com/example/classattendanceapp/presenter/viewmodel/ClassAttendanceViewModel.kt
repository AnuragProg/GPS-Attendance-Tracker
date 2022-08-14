package com.example.classattendanceapp.presenter.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classattendanceapp.data.models.Log
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.domain.usecases.usecase.ClassAttendanceUseCase
import com.example.classattendanceapp.domain.utils.alarms.ClassAlarmManager
import com.example.classattendanceapp.presenter.utils.DateToSimpleFormat
import com.example.classattendanceapp.presenter.utils.Days
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ClassAttendanceViewModel @Inject constructor(
    private val classAttendanceUseCase: ClassAttendanceUseCase
): ViewModel() {


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

    private fun getAllLogsAdvanced() :Flow<List<ModifiedLogs>>{
        return classAttendanceUseCase.getAllLogsUseCase().map{
            val tempLogList = mutableListOf<ModifiedLogs>()
            it.forEach { log ->

                val tempLog = ModifiedLogs(
                    _id = log._id,
                    subjectName = log.subjectName,
                    subjectId = log.subjectId,
                    hour = DateToSimpleFormat.getHours(log.timestamp),
                    minute = DateToSimpleFormat.getMinutes(log.timestamp),
                    date = DateToSimpleFormat.getDay(log.timestamp),
                    day = DateToSimpleFormat.getDayOfTheWeek(log.timestamp),
                    month = DateToSimpleFormat.getMonthStringFromNumber(log.timestamp),
                    monthNumber = DateToSimpleFormat.getConventionalMonthNumber(log.timestamp),
                    year = DateToSimpleFormat.getYear(log.timestamp),
                    wasPresent = log.wasPresent,
                    latitude = log.latitude,
                    longitude = log.longitude,
                    distance = log.distance
                )
                tempLogList.add(tempLog)
            }
            tempLogList
        }
    }

    private fun getSubjectsAdvanced() : Flow<List<ModifiedSubjects>>{
        return classAttendanceUseCase.getSubjectsUseCase().map {
            val tempSubjectList = mutableListOf<ModifiedSubjects>()
            it.forEach{
                val totalPresents = it.daysPresent + it.daysPresentOfLogs
                val totalAbsents = it.daysAbsent + it.daysAbsentOfLogs
                val percentage = if(totalPresents + totalAbsents == 0.toLong()){
                    0.toDouble()
                }else{
                    (totalPresents.toDouble()/(totalPresents + totalAbsents))*100
                }
                val totalDays = totalPresents + totalAbsents
                tempSubjectList.add(
                    ModifiedSubjects(
                        _id = it._id,
                        subjectName = it.subjectName,
                        attendancePercentage = percentage,
                        daysPresent = it.daysPresent,
                        daysAbsent = it.daysAbsent,
                        daysPresentOfLogs = it.daysPresentOfLogs,
                        daysAbsentOfLogs = it.daysAbsentOfLogs,
                        totalPresents = totalPresents,
                        totalAbsents = totalAbsents,
                        totalDays = totalDays,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        range = it.range
                    )
                )
            }
            tempSubjectList
        }
    }

    fun getTimeTableAdvanced(): Flow<Map<String, List<TimeTable>>>{
        return classAttendanceUseCase.getTimeTableUseCase().map{
            val resultant = mutableMapOf<String, List<TimeTable>>()
            for(day in Days.values()){
                resultant[day.day] = it.filter{
                    it.dayOfTheWeek == day.value
                }
            }
            resultant
        }
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



}