package com.gps.classattendanceapp.domain.usecases.logsusecase

import com.gps.classattendanceapp.data.models.Log
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class InsertLogsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(log: Log): Long{
        return classAttendanceRepository.insertLogs(logs = log)
    }
}