package com.example.classattendanceapp.data.repository

import android.content.Context
import android.net.Uri
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.data.excel.Excel
import com.example.classattendanceapp.data.models.Log
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow


class ClassAttendanceRepositoryImpl(
    private val dao: ClassAttendanceDao,
    private val excel: Excel
) : ClassAttendanceRepository{
    override suspend fun updateSubject(subject: Subject) {
        dao.updateSubject(subject)
    }

    override suspend fun updateLog(log: Log) {
        dao.updateLog(log)
    }

    override suspend fun insertSubject(subject: Subject): Long {
        return dao.insertSubject(subject)
    }

    override suspend fun insertLogs(log: Log): Long {
        return dao.insertLogs(log)
    }

    override suspend fun insertTimeTable(timeTable: TimeTable): Long {
        return dao.insertTimeTable(timeTable)
    }

    override suspend fun deleteSubject(id: Int) {
        dao.deleteSubject(id)
    }

    override suspend fun deleteLogs(id: Int) {
        dao.deleteLogs(id)
    }

    override suspend fun deleteTimeTable(id: Int) {
        dao.deleteTimeTable(id)
    }

    override suspend fun deleteTimeTableWithSubjectId(subjectId: Int) {
        dao.deleteTimeTableWithSubjectId(subjectId)
    }

    override suspend fun deleteLogsWithSubject(subjectName: String) {
        dao.deleteLogsWithSubject(subjectName)
    }

    override suspend fun deleteLogsWithSubjectId(subjectId: Int) {
        dao.deleteLogsWithSubjectId(subjectId)
    }

    override fun getAllLogs(): Flow<List<Log>> {
        return dao.getAllLogs()
    }

    override fun getTimeTable(): Flow<List<TimeTable>> {
        return dao.getTimeTable()
    }

    override suspend fun getTimeTableWithId(id: Int): TimeTable?{
        return dao.getTimeTableWithId(id)
    }

    override fun getTimeTableWithSubjectId(subjectId: Int): Flow<List<TimeTable>> {
        return dao.getTimeTableWithSubjectId(subjectId)
    }

    override fun writeSubjectsStatsToExcel(context: Context, subjectsList: List<ModifiedSubjects>): Uri {
        return excel.writeSubjectsStatsToExcel(context, subjectsList)
    }

    override fun writeLogsStatsToExcel(context: Context, logsList: List<ModifiedLogs>): Uri {
        return excel.writeLogsStatsToExcel(context, logsList)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return dao.getAllSubjects()
    }

    override suspend fun getSubjectWithId(id: Int): Subject? {
        return dao.getSubjectWithId(id)
    }

    override suspend fun getLogsWithId(id: Int): Log? {
        return dao.getLogsWithId(id)
    }

    override fun getLogOfSubject(subjectName: String): Flow<List<Log>> {
        return dao.getLogOfSubject(subjectName)
    }

    override fun getLogOfSubjectId(subjectId: Int): Flow<List<Log>> {
        return dao.getLogOfSubjectId(subjectId)
    }

    override fun getTimeTableOfDay(day: Int): Flow<List<TimeTable>> {
        return dao.getTimeTableOfDay(day)
    }

}