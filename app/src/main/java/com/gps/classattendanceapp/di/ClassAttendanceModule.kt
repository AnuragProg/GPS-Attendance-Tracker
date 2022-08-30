package com.gps.classattendanceapp.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gps.classattendanceapp.data.db.ClassAttendanceDao
import com.gps.classattendanceapp.data.db.ClassAttendanceDatabase
import com.gps.classattendanceapp.data.excel.Excel
import com.gps.classattendanceapp.data.repository.ClassAttendanceRepositoryImpl
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import com.gps.classattendanceapp.domain.usecases.excelusecase.WriteLogsStatsToExcelUseCase
import com.gps.classattendanceapp.domain.usecases.excelusecase.WriteSubjectsStatsToExcelUseCase
import com.gps.classattendanceapp.domain.usecases.logsusecase.*
import com.gps.classattendanceapp.domain.usecases.subjectsusecase.*
import com.gps.classattendanceapp.domain.usecases.timetableusecase.*
import com.gps.classattendanceapp.domain.usecases.usecase.ClassAttendanceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shreyaspatil.permissionFlow.PermissionFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClassAttendanceModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun providesPermissionFlow(): PermissionFlow{
        return PermissionFlow.getInstance()
    }

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