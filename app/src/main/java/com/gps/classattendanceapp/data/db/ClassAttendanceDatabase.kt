package com.gps.classattendanceapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gps.classattendanceapp.data.models.Log
import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.data.models.TimeTable
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized


@Database(
    entities = [
        Log::class,
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