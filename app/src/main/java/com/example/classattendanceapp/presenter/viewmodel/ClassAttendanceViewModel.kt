package com.example.classattendanceapp.presenter.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.domain.models.ModifiedTimeTable
import com.example.classattendanceapp.domain.usecases.usecase.ClassAttendanceUseCase
import com.example.classattendanceapp.domain.utils.alarms.ClassAlarmBroadcastReceiver
import com.example.classattendanceapp.domain.utils.alarms.ClassAlarmManager
import com.example.classattendanceapp.presenter.utils.DateToSimpleFormat
import com.example.classattendanceapp.presenter.utils.Days
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ClassAttendanceViewModel @Inject constructor(
    private val classAttendanceUseCase: ClassAttendanceUseCase
): ViewModel() {

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

    suspend fun getAllLogs() = flow{
        classAttendanceUseCase.getAllLogsUseCase().collect{
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
            emit(tempLogList.toList())
        }
    }

    suspend fun getSubjects() = flow {
        classAttendanceUseCase.getSubjectsUseCase().collect {
            val tempSubjectList = mutableListOf<ModifiedSubjects>()
            it.forEach{
                val tempLogOfSubject = classAttendanceUseCase.getLogOfSubjectIdUseCase(it._id).first()
                val percentage = if(tempLogOfSubject.isEmpty()) 0.toDouble() else String.format("%.2f",((tempLogOfSubject.filter{ it.wasPresent }.size.toDouble())/tempLogOfSubject.size.toDouble())*100).toDouble()

                tempSubjectList.add(
                    ModifiedSubjects(
                        it._id,
                        it.subjectName,
                        percentage
                    )
                )
            }
            emit(tempSubjectList)
        }
    }

    suspend fun getTimeTable() = flow<Map<String, List<TimeTable>>>{
        val resultant = mutableMapOf<String, List<TimeTable>>()
        classAttendanceUseCase.getTimeTableUseCase().collect{
            for(day in Days.values()){
                resultant[day.day] = classAttendanceUseCase.getTimeTableOfDayUseCase(day.value).first().ifEmpty { emptyList() }
            }
            emit(resultant)
        }
    }

    suspend fun insertLogs(logs: Logs){
        classAttendanceUseCase.insertLogsUseCase(logs)
    }

    suspend fun insertSubject(subject: Subject){
        classAttendanceUseCase.insertSubjectUseCase(
            Subject(
                subject._id,
                subject.subjectName.trim()
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
        classAttendanceUseCase.deleteLogsUseCase(id)
    }

    suspend fun deleteSubject(id: Int){
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

    suspend fun deleteLogsWithSubject(subjectName: String){
        classAttendanceUseCase.deleteLogsWithSubjectUseCase(subjectName)
    }

    suspend fun deleteLogsWithSubjectId(subjectId: Int){
        classAttendanceUseCase.deleteLogsWithSubjectIdUseCase(subjectId)
    }

}