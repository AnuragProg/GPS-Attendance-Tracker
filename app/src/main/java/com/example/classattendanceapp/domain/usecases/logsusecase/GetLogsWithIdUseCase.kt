package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.data.models.Log
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class GetLogsWithIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int): Log? {
        return classAttendanceRepository.getLogsWithId(id)
    }
}