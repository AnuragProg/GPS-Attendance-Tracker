package com.gps.classattendanceapp.domain.repository

import android.content.Context
import android.net.Uri
import com.gps.classattendanceapp.data.models.Log
import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.models.ModifiedLogs
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import kotlinx.coroutines.flow.Flow


interface ClassAttendanceRepository {

    suspend fun updateSubject(subject: Subject)

    suspend fun updateLog(log: Log)

    suspend fun insertSubject(subject: Subject): Long

    suspend fun insertLogs(logs: Log): Long

    suspend fun insertTimeTable(timeTable: TimeTable): Long

    suspend fun deleteSubject(id: Int)

    suspend fun deleteLogs(id: Int)

    suspend fun deleteTimeTable(id: Int)

    suspend fun deleteTimeTableWithSubjectId(subjectId: Int)

    suspend fun deleteLogsWithSubject(subjectName: String)

    suspend fun deleteLogsWithSubjectId(subjectId: Int)

    fun getAllLogs(): Flow<List<Log>>

    fun getTimeTable(): Flow<List<TimeTable>>

    fun getAllSubjects(): Flow<List<Subject>>

    suspend fun getSubjectWithId(id: Int): Subject?

    suspend fun getLogsWithId(id: Int): Log?

    fun getLogOfSubject(subjectName: String): Flow<List<Log>>

    fun getLogOfSubjectId(subjectId: Int): Flow<List<Log>>

    fun getTimeTableOfDay(day: Int): Flow<List<TimeTable>>

    suspend fun getTimeTableWithId(id: Int): TimeTable?

    fun getTimeTableWithSubjectId(subjectId: Int): Flow<List<TimeTable>>

    fun getPresentThroughLogs(subjectId: Int): Flow<Int>

    fun getAbsentThroughLogs(subjectId: Int): Flow<Int>

}