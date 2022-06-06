package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.data.models.Logs
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class InsertLogsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(logs: Logs){
        classAttendanceRepository.insertLogs(logs = logs)
    }
}