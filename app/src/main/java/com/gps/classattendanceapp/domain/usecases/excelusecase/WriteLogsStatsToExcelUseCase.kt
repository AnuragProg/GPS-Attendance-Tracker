package com.gps.classattendanceapp.domain.usecases.excelusecase

import android.content.Context
import android.net.Uri
import com.gps.classattendanceapp.domain.models.ModifiedLogs
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class WriteLogsStatsToExcelUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(context: Context, logsList: List<com.gps.classattendanceapp.domain.models.ModifiedLogs>): Uri {
        return classAttendanceRepository.writeLogsStatsToExcel(context, logsList)
    }
}