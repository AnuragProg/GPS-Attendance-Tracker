package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.data.models.Log
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class InsertLogsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(log: Log): Long{
        return classAttendanceRepository.insertLogs(logs = log)
    }
}