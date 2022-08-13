package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.data.models.Log
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.*



class GetAllLogsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke() : Flow<List<Log>>{
        return classAttendanceRepository.getAllLogs()
    }
}