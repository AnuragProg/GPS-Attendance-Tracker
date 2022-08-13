package com.example.classattendanceapp.di

import android.content.Context
import com.example.classattendanceapp.data.db.ClassAttendanceDao
import com.example.classattendanceapp.data.db.ClassAttendanceDatabase
import com.example.classattendanceapp.data.excel.Excel
import com.example.classattendanceapp.data.repository.ClassAttendanceRepositoryImpl
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import com.example.classattendanceapp.domain.usecases.excelusecase.WriteLogsStatsToExcelUseCase
import com.example.classattendanceapp.domain.usecases.excelusecase.WriteSubjectsStatsToExcelUseCase
import com.example.classattendanceapp.domain.usecases.logsusecase.*
import com.example.classattendanceapp.domain.usecases.subjectsusecase.*
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
    fun providesExcel():Excel{
        return Excel()
    }


    @Provides
    @Singleton
    fun providesClassAttendanceDao(@ApplicationContext context: Context): ClassAttendanceDao{
        return ClassAttendanceDatabase.getInstance(context).classAttendanceDao
    }

    @Provides
    @Singleton
    fun providesClassAttendanceRepository(
        classAttendanceDao: ClassAttendanceDao,
        excel: Excel
    ): ClassAttendanceRepository{
        return  ClassAttendanceRepositoryImpl(
            classAttendanceDao,
            excel
        )
    }

    @Provides
    @Singleton
    fun providesClassAttendanceUseCase(
        classAttendanceRepository: ClassAttendanceRepository
    ): ClassAttendanceUseCase{
        return ClassAttendanceUseCase(
            updateSubjectUseCase = UpdateSubjectUseCase(classAttendanceRepository),
            updateLogUseCase = UpdateLogUseCase(classAttendanceRepository),
            deleteLogsUseCase = DeleteLogsUseCase(classAttendanceRepository),
            deleteSubjectUseCase = DeleteSubjectUseCase(classAttendanceRepository),
            deleteTimeTableUseCase = DeleteTimeTableUseCase(classAttendanceRepository),
            deleteTimeTableWithSubjectIdUseCase = DeleteTimeTableWithSubjectIdUseCase(classAttendanceRepository),
            deleteLogsWithSubjectUseCase = DeleteLogsWithSubjectUseCase(classAttendanceRepository),
            deleteLogsWithSubjectIdUseCase = DeleteLogsWithSubjectIdUseCase(classAttendanceRepository),
            getAllLogsUseCase = GetAllLogsUseCase(classAttendanceRepository),
            getSubjectsUseCase = GetSubjectsUseCase(classAttendanceRepository),
            getSubjectWithIdWithUseCase = GetSubjectWithIdWithUseCase(classAttendanceRepository),
            getLogsWithIdUseCase = GetLogsWithIdUseCase(classAttendanceRepository),
            getTimeTableUseCase = GetTimeTableUseCase(classAttendanceRepository),
            getTimeTableWithIdUseCase = GetTimeTableWithIdUseCase(classAttendanceRepository),
            getTimeTableWithSubjectIdUseCase = GetTimeTableWithSubjectIdUseCase(classAttendanceRepository),
            insertLogsUseCase = InsertLogsUseCase(classAttendanceRepository),
            insertSubjectUseCase = InsertSubjectUseCase(classAttendanceRepository),
            insertTimeTableUseCase = InsertTimeTableUseCase(classAttendanceRepository),
            getLogOfSubjectUseCase = GetLogOfSubjectUseCase(classAttendanceRepository),
            getLogOfSubjectIdUseCase = GetLogOfSubjectIdUseCase(classAttendanceRepository),
            getTimeTableOfDayUseCase = GetTimeTableOfDayUseCase(classAttendanceRepository),
            writeSubjectsStatsToExcelUseCase = WriteSubjectsStatsToExcelUseCase(classAttendanceRepository),
            writeLogsStatsToExcelUseCase = WriteLogsStatsToExcelUseCase(classAttendanceRepository)
        )
    }
}