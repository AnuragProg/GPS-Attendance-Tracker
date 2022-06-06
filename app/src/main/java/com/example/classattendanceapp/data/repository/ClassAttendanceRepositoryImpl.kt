package com.example.classattendanceapp.data.repository

import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class ClassAttendanceRepositoryImpl(
    private val dao: ClassAttendanceDao
) : ClassAttendanceRepository{

    override suspend fun insertSubject(subject: Subject) {
        dao.insertSubject(subject)
    }

    override suspend fun insertLogs(logs: Logs) {
        dao.insertLogs(logs)
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

    override suspend fun deleteLogsWithSubject(subjectName: String) {
        dao.deleteLogsWithSubject(subjectName)
    }

    override suspend fun deleteLogsWithSubjectId(subjectId: Int) {
        dao.deleteLogsWithSubjectId(subjectId)
    }

    override fun getAllLogs(): Flow<List<Logs>> {
        return dao.getAllLogs()
    }

    override fun getTimeTable(): Flow<List<TimeTable>> {
        return dao.getTimeTable()
    }

    override suspend fun getTimeTableWithId(id: Int): TimeTable{
        return dao.getTimeTableWithId(id)
    }


    override fun getAllSubjects(): Flow<List<Subject>> {
        return dao.getAllSubjects()
    }

    override fun getLogOfSubject(subjectName: String): Flow<List<Logs>> {
        return dao.getLogOfSubject(subjectName)
    }

    override fun getLogOfSubjectId(subjectId: Int): Flow<List<Logs>> {
        return dao.getLogOfSubjectId(subjectId)
    }

    override fun getTimeTableOfDay(day: Int): Flow<List<TimeTable>> {
        return dao.getTimeTableOfDay(day)
    }

}