package com.example.classattendanceapp.di

import android.content.Context
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.data.db.ClassAttendanceDatabase
import com.example.classattendanceapp.data.repository.ClassAttendanceRepositoryImpl
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import com.example.classattendanceapp.domain.usecases.logsusecase.*
import com.example.classattendanceapp.domain.usecases.subjectsusecase.DeleteSubjectUseCase
import com.example.classattendanceapp.domain.usecases.subjectsusecase.GetSubjectsUseCase
import com.example.classattendanceapp.domain.usecases.subjectsusecase.InsertSubjectUseCase
import com.example.classattendanceapp.domain.usecases.timetableusecase.*
import com.example.classattendanceapp.domain.usecases.usecase.ClassAttendanceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClassAttendanceModule {

    @Provides
    @Singleton
    fun providesClassAttendanceDao(@ApplicationContext context: Context): ClassAttendanceDao{
        return ClassAttendanceDatabase.getInstance(context).classAttendanceDao
    }

    @Provides
    @Singleton
    fun providesClassAttendanceRepository(
        classAttendanceDao: ClassAttendanceDao
    ): ClassAttendanceRepository{
        return  ClassAttendanceRepositoryImpl(
            classAttendanceDao
        )
    }

    @Provides
    @Singleton
    fun providesClassAttendanceUseCase(
        classAttendanceRepository: ClassAttendanceRepository
    ): ClassAttendanceUseCase{
        return ClassAttendanceUseCase(
            deleteLogsUseCase = DeleteLogsUseCase(classAttendanceRepository),
            deleteSubjectUseCase = DeleteSubjectUseCase(classAttendanceRepository),
            deleteTimeTableUseCase = DeleteTimeTableUseCase(classAttendanceRepository),
            deleteLogsWithSubjectUseCase = DeleteLogsWithSubjectUseCase(classAttendanceRepository),
            deleteLogsWithSubjectIdUseCase = DeleteLogsWithSubjectIdUseCase(classAttendanceRepository),
            getAllLogsUseCase = GetAllLogsUseCase(classAttendanceRepository),
            getSubjectsUseCase = GetSubjectsUseCase(classAttendanceRepository),
            getTimeTableUseCase = GetTimeTableUseCase(classAttendanceRepository),
            getTimeTableWithIdUseCase = GetTimeTableWithIdUseCase(classAttendanceRepository),
            insertLogsUseCase = InsertLogsUseCase(classAttendanceRepository),
            insertSubjectUseCase = InsertSubjectUseCase(classAttendanceRepository),
            insertTimeTableUseCase = InsertTimeTableUseCase(classAttendanceRepository),
            getLogOfSubjectUseCase = GetLogOfSubjectUseCase(classAttendanceRepository),
            getLogOfSubjectIdUseCase = GetLogOfSubjectIdUseCase(classAttendanceRepository),
            getTimeTableOfDayUseCase = GetTimeTableOfDayUseCase(classAttendanceRepository)
        )
    }

}