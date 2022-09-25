package com.gps.classattendanceapp.data.db

import androidx.room.*
import com.gps.classattendanceapp.data.models.Log
import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.data.models.TimeTable
import kotlinx.coroutines.flow.Flow


@Dao
interface ClassAttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(log: Log): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeTable(timeTable: TimeTable): Long

    @Update
    suspend fun updateSubject(subject: Subject)

    @Update
    suspend fun updateLog(log: Log)

    @Query("delete from subject where _id = :id")
    suspend fun deleteSubject(id: Int)

    @Query("delete from log where _id = :id")
    suspend fun deleteLogs(id: Int)

    @Query("delete from timetable where _id = :id")
    suspend fun deleteTimeTable(id: Int)

    @Query("delete from timetable where subjectId = :subjectId")
    suspend fun deleteTimeTableWithSubjectId(subjectId: Int)

    @Query("delete from log where subjectName = :subjectName")
    suspend fun deleteLogsWithSubject(subjectName: String)

    @Query("delete from log where subjectId = :subjectId")
    suspend fun deleteLogsWithSubjectId(subjectId: Int)

    @Transaction
    @Query("select * from subject where _id = :id")
    suspend fun getSubjectWithId(id: Int): Subject?

    @Transaction
    @Query("select * from log where _id = :id")
    suspend fun getLogsWithId(id: Int): Log?

    @Transaction
    @Query("select * from log")
    fun getAllLogs(): Flow<List<Log>>

    @Transaction
    @Query("select count(wasPresent) from log where wasPresent and subjectId = :subjectId")
    fun getPresentThroughLogs(subjectId: Int): Flow<Int>

    @Transaction
    @Query("select count(wasPresent) from log where not wasPresent and subjectId = :subjectId")
    fun getAbsentThroughLogs(subjectId: Int): Flow<Int>

    @Transaction
    @Query("select * from timetable")
    fun getTimeTable(): Flow<List<TimeTable>>

    @Transaction
    @Query("select * from timetable where _id = :id")
    suspend fun getTimeTableWithId(id: Int): TimeTable?

    @Transaction
    @Query("select * from timetable where subjectId = :subjectId")
    fun getTimeTableWithSubjectId(subjectId: Int): Flow<List<TimeTable>>

    @Transaction
    @Query("select * from subject")
    fun getAllSubjects(): Flow<List<Subject>>

    @Transaction
    @Query("select * from log where subjectName = :subjectName")
    fun getLogOfSubject(subjectName: String): Flow<List<Log>>

    @Transaction
    @Query("select * from log where subjectId = :subjectId")
    fun getLogOfSubjectId(subjectId: Int): Flow<List<Log>>

    @Transaction
    @Query("select * from timetable where dayOfTheWeek = :day")
    fun getTimeTableOfDay(day: Int): Flow<List<TimeTable>>
}