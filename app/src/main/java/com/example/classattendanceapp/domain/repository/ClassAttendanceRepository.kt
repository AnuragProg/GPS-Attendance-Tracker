package com.example.classattendanceapp.domain.repository

import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import kotlinx.coroutines.flow.Flow

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



}