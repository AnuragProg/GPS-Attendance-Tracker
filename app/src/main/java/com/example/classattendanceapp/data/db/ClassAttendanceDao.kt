package com.example.classattendanceapp.data.db

import androidx.room.*
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import kotlinx.coroutines.flow.Flow


@Dao
interface ClassAttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: Logs)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeTable(timeTable: TimeTable): Long

    @Query("delete from subject where _id = :id")
    suspend fun deleteSubject(id: Int)

    @Query("delete from logs where _id = :id")
    suspend fun deleteLogs(id: Int)

    @Query("delete from timetable where _id = :id")
    suspend fun deleteTimeTable(id: Int)

    @Query("delete from logs where subjectName = :subjectName")
    suspend fun deleteLogsWithSubject(subjectName: String)

    @Query("delete from logs where subjectId = :subjectId")
    suspend fun deleteLogsWithSubjectId(subjectId: Int)

    @Transaction
    @Query("select * from subject where _id = :id")
    suspend fun getSubjectWithId(id: Int): Subject

    @Transaction
    @Query("select * from logs where _id = :id")
    suspend fun getLogsWithId(id: Int): Logs

    @Transaction
    @Query("select * from logs")
    fun getAllLogs(): Flow<List<Logs>>

    @Transaction
    @Query("select * from timetable")
    fun getTimeTable(): Flow<List<TimeTable>>

    @Transaction
    @Query("select * from timetable where _id = :id")
    suspend fun getTimeTableWithId(id: Int): TimeTable

    @Transaction
    @Query("select * from subject")
    fun getAllSubjects(): Flow<List<Subject>>

    @Transaction
    @Query("select * from logs where subjectName = :subjectName")
    fun getLogOfSubject(subjectName: String): Flow<List<Logs>>

    @Transaction
    @Query("select * from logs where subjectId = :subjectId")
    fun getLogOfSubjectId(subjectId: Int): Flow<List<Logs>>

    @Transaction
    @Query("select * from timetable where dayOfTheWeek = :day")
    fun getTimeTableOfDay(day: Int): Flow<List<TimeTable>>
}