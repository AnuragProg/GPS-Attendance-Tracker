package com.example.classattendanceapp.domain.usecases.excelusecase

import android.content.Context
import android.net.Uri
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class WriteLogsStatsToExcelUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(context: Context, logsList: List<ModifiedLogs>): Uri {
        return classAttendanceRepository.writeLogsStatsToExcel(context, logsList)
    }
}