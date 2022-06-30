package com.example.classattendanceapp.presenter.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.domain.usecases.usecase.ClassAttendanceUseCase
import com.example.classattendanceapp.domain.utils.alarms.ClassAlarmManager
import com.example.classattendanceapp.presenter.utils.DateToSimpleFormat
import com.example.classattendanceapp.presenter.utils.Days
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ClassAttendanceViewModel @Inject constructor(
    private val classAttendanceUseCase: ClassAttendanceUseCase
): ViewModel() {

    init{
        viewModelScope.launch {
            Log.d("viewmodel", "Subjects retrieval started in viewmodel")
            getSubjectsAdvanced().collectLatest { retrievedSubjectsList ->
                if(!_isInitialSubjectDataRetrievalDone.value){
                    _isInitialSubjectDataRetrievalDone.value = true
                }
                _subjectsList.value = retrievedSubjectsList
            }
        }
        viewModelScope.launch {
            Log.d("viewmodel", "Logs retrieval started in viewmodel")
            getAllLogsAdvanced().collectLatest { retrievedLogsList ->
                if(!_isInitialLogDataRetrievalDone.value){
                    _isInitialLogDataRetrievalDone.value = true
                }
                _logsList.value = retrievedLogsList
            }
        }
    }

    private var _isInitialSubjectDataRetrievalDone = MutableStateFlow(false)
    val isInitialSubjectDataRetrievalDone : StateFlow<Boolean> get() = _isInitialSubjectDataRetrievalDone

    private var _isInitialLogDataRetrievalDone = MutableStateFlow(false)
    val isInitialLogDataRetrievalDone : StateFlow<Boolean> get() = _isInitialLogDataRetrievalDone

    private var _subjectsList = MutableStateFlow<List<ModifiedSubjects>>(emptyList())
    val subjectsList : StateFlow<List<ModifiedSubjects>> get() = _subjectsList

    private var _logsList = MutableStateFlow<List<ModifiedLogs>>(emptyList())
    val logsList : StateFlow<List<ModifiedLogs>> get() = _logsList


    private val longitudeDataStoreKey = doublePreferencesKey("userLongitude")
    private val latitudeDataStoreKey = doublePreferencesKey("userLatitude")
    private val rangeDataStoreKey = doublePreferencesKey("userRange")

    private var _floatingButtonClicked = MutableStateFlow(false)
    val floatingButtonClicked: StateFlow<Boolean> get() = _floatingButtonClicked

    fun changeFloatingButtonClickedState(state: Boolean){
        _floatingButtonClicked.value = state
    }

    private var _showAddLocationCoordinateDialog = MutableStateFlow(false)
    val showAddLocationCoordinateDialog:StateFlow<Boolean> get() = _showAddLocationCoordinateDialog

    fun changeAddLocationCoordinateState(state: Boolean){
        _showAddLocationCoordinateDialog.value = state
    }

    private var _showOverFlowMenu = MutableStateFlow(false)
    val showOverFlowMenu : StateFlow<Boolean> get() = _showOverFlowMenu

    fun changeOverFlowMenuState(state: Boolean){
        _showOverFlowMenu.value = state
    }

    suspend fun updateSubject(subject: Subject){
        classAttendanceUseCase.updateSubjectUseCase(subject)
    }

    fun getAllLogsAdvanced() :Flow<List<ModifiedLogs>>{
        return classAttendanceUseCase.getAllLogsUseCase().map{
            val tempLogList = mutableListOf<ModifiedLogs>()
            it.forEach {
                val tempLog = ModifiedLogs(
                    _id = it._id,
                    subjectName = it.subjectName,
                    subjectId = it.subjectId,
                    date = DateToSimpleFormat.getDay(it.timestamp),
                    day = DateToSimpleFormat.getDayOfTheWeek(it.timestamp),
                    month = DateToSimpleFormat.getMonthStringFromNumber(it.timestamp),
                    monthNumber = DateToSimpleFormat.getMonthNumber(it.timestamp),
                    year = DateToSimpleFormat.getYear(it.timestamp),
                    wasPresent = it.wasPresent
                )
                tempLogList.add(tempLog)
            }
            tempLogList
        }
    }

    fun getSubjectsAdvanced() : Flow<List<ModifiedSubjects>>{
        return classAttendanceUseCase.getSubjectsUseCase().map {
            val tempSubjectList = mutableListOf<ModifiedSubjects>()
            it.forEach{
                val percentage = if((it.daysPresent+it.daysAbsent)!=0.toLong()){
                    (it.daysPresent.toDouble()/(it.daysPresent+it.daysAbsent).toDouble())*100
                }else{
                    0.00
                }
                tempSubjectList.add(
                    ModifiedSubjects(
                        it._id,
                        it.subjectName,
                        percentage,
                        it.daysPresent,
                        it.daysAbsent
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

    suspend fun insertLogs(logs: Logs){
        val subjectWithId = classAttendanceUseCase.getSubjectWithIdWithUseCase(logs.subjectId)
        if(logs.wasPresent){
            subjectWithId.daysPresent++
        }else{
            subjectWithId.daysAbsent++
        }
        classAttendanceUseCase.insertSubjectUseCase(subjectWithId)
        classAttendanceUseCase.insertLogsUseCase(logs)
    }

    suspend fun insertSubject(subject: Subject){
        classAttendanceUseCase.insertSubjectUseCase(
            Subject(
                subject._id,
                subject.subjectName.trim(),
                subject.daysPresent,
                subject.daysAbsent
            )
        )
    }

    suspend fun insertTimeTable(
        timeTable: TimeTable,
        context: Context,

    ){
        val id = classAttendanceUseCase.insertTimeTableUseCase(timeTable)
        ClassAlarmManager.registerAlarm(
            context = context,
            timeTableId = id.toInt(),
            timeTable = timeTable
        )
    }

    suspend fun deleteLogs(id: Int){
        val logWithId = classAttendanceUseCase.getLogsWithIdUseCase(id)
        val subjectWithId = classAttendanceUseCase.getSubjectWithIdWithUseCase(logWithId.subjectId)
        if(logWithId.wasPresent){
            subjectWithId.daysPresent--
        }else{
            subjectWithId.daysAbsent--
        }
        classAttendanceUseCase.insertSubjectUseCase(
            subjectWithId
        )
        classAttendanceUseCase.deleteLogsUseCase(id)
    }

    suspend fun deleteSubject(id: Int){
        classAttendanceUseCase.deleteLogsWithSubjectIdUseCase(id)
        classAttendanceUseCase.deleteSubjectUseCase(id)
    }

    suspend fun deleteTimeTable(id: Int, context: Context){
        val tempTimeTable = classAttendanceUseCase.getTimeTableWithIdUseCase(id)
        Log.d("tempTimeTable", "tempTimeTable is $tempTimeTable")
        classAttendanceUseCase.deleteTimeTableUseCase(id)
        ClassAlarmManager.cancelAlarm(
            context = context,
            timeTable = tempTimeTable
        )
    }

    private var _currentLatitudeInDataStore = MutableStateFlow<Double?>(null)
    private var _currentLongitudeInDataStore = MutableStateFlow<Double?>(null)
    private var _currentRangeInDataStore = MutableStateFlow<Double?>(null)

    val currentLatitudeInDataStore : StateFlow<Double?> get() = _currentLatitudeInDataStore
    val currentLongitudeInDataStore: StateFlow<Double?> get() = _currentLongitudeInDataStore
    val currentRangeInDataStore : StateFlow<Double?> get() = _currentRangeInDataStore

    fun changeUserLatitude(latitude: Double?){
        _currentLatitudeInDataStore.value = latitude
    }
    fun changeUserLongitude(longitude: Double?){
        _currentLongitudeInDataStore.value = longitude
    }
    fun changeUserRange(range: Double?){
        _currentRangeInDataStore.value = range
    }

    fun getCoordinateInDataStore(
        coroutineScope: CoroutineScope
    ){
        coroutineScope.launch{
            val latitudeDataStoreFlow = classAttendanceUseCase.getCoordinateInDataStoreUseCase(latitudeDataStoreKey)
            val longitudeDataStoreFlow = classAttendanceUseCase.getCoordinateInDataStoreUseCase(longitudeDataStoreKey)
            val rangeDataStoreFlow = classAttendanceUseCase.getCoordinateInDataStoreUseCase(rangeDataStoreKey)
            combine(latitudeDataStoreFlow,longitudeDataStoreFlow,rangeDataStoreFlow){ latitude, longitude, range ->
                Triple(latitude, longitude, range)
            }.collectLatest { coordinates ->
                changeUserLatitude(coordinates.first)
                changeUserLongitude(coordinates.second)
                changeUserRange(coordinates.third)
            }
        }
    }

    suspend fun writeOrUpdateCoordinateInDataStore(latitude: Double, longitude: Double, range: Double){
        classAttendanceUseCase.writeOrUpdateCoordinateInDataStoreUseCase(latitudeDataStoreKey, latitude)
        classAttendanceUseCase.writeOrUpdateCoordinateInDataStoreUseCase(longitudeDataStoreKey, longitude)
        classAttendanceUseCase.writeOrUpdateCoordinateInDataStoreUseCase(rangeDataStoreKey, range)
    }

    fun deleteCoordinateInDataStore(){
        viewModelScope.launch{
            classAttendanceUseCase.deleteCoordinateInDataStoreUseCase(latitudeDataStoreKey)
            classAttendanceUseCase.deleteCoordinateInDataStoreUseCase(longitudeDataStoreKey)
        }
    }
}