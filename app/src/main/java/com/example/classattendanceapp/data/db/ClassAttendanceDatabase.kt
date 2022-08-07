package com.example.classattendanceapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.data.models.TimeTable
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized


@Database(
    entities = [
        Logs::class,
        Subject::class,
        TimeTable::class,
    ],
    version = 1
)
@TypeConverters(ClassAttendanceTypeConverter::class)
abstract class ClassAttendanceDatabase : RoomDatabase(){

    abstract val classAttendanceDao: ClassAttendanceDao

    companion object{
        @Volatile
        private var INSTANCE : ClassAttendanceDatabase? = null
        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(context: Context): ClassAttendanceDatabase{
            return synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    ClassAttendanceDatabase::class.java,
                    "class_attendance_db"
                ).build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}