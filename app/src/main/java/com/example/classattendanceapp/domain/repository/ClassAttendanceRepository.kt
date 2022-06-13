package com.example.classattendanceapp.domain.repository

import androidx.datastore.preferences.core.Preferences
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface ClassAttendanceRepository {

    suspend fun insertSubject(subject: Subject)

    suspend fun insertLogs(logs: Logs)

    suspend fun insertTimeTable(timeTable: TimeTable): Long

    suspend fun deleteSubject(id: Int)

    suspend fun deleteLogs(id: Int)

    suspend fun deleteTimeTable(id: Int)

    suspend fun deleteLogsWithSubject(subjectName: String)

    suspend fun deleteLogsWithSubjectId(subjectId: Int)

    fun getAllLogs(): Flow<List<Logs>>

    fun getTimeTable(): Flow<List<TimeTable>>

    fun getAllSubjects(): Flow<List<Subject>>

    fun getLogOfSubject(subjectName: String): Flow<List<Logs>>

    fun getLogOfSubjectId(subjectId: Int): Flow<List<Logs>>

    fun getTimeTableOfDay(day: Int): Flow<List<TimeTable>>

    suspend fun getTimeTableWithId(id: Int): TimeTable

    suspend fun writeOrUpdateCoordinateInDataStore(key: Preferences.Key<Double>, value: Double)

    suspend fun getCoordinateInDataStore(key: Preferences.Key<Double>): Flow<Double?>

    suspend fun deleteCoordinateInDataStore(key: Preferences.Key<Double>)

}