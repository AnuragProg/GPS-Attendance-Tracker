package com.example.classattendanceapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ClassAttendanceRepositoryImpl(
    private val dao: ClassAttendanceDao,
    private val dataStore: DataStore<Preferences>
) : ClassAttendanceRepository{
    override suspend fun updateSubject(subject: Subject) {
        dao.updateSubject(subject)
    }

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



    override suspend fun writeOrUpdateCoordinateInDataStore(key: Preferences.Key<Double>, value: Double) {
        dataStore.edit{ pref ->
            pref[key] = value
        }
    }

    override suspend fun getCoordinateInDataStore(key: Preferences.Key<Double>): Flow<Double?> {
        return dataStore.data.map { pref->
            pref[key]
        }
    }

    override suspend fun deleteCoordinateInDataStore(key: Preferences.Key<Double>) {
        dataStore.edit{ pref ->
            pref.remove(key)
        }
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return dao.getAllSubjects()
    }

    override suspend fun getSubjectWithId(id: Int): Subject {
        return dao.getSubjectWithId(id)
    }

    override suspend fun getLogsWithId(id: Int): Logs {
        return dao.getLogsWithId(id)
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