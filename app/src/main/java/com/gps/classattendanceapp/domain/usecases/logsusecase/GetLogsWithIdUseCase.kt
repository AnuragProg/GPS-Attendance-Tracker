package com.gps.classattendanceapp.domain.usecases.logsusecase

import com.gps.classattendanceapp.data.models.Log
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class GetLogsWithIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int): Log? {
        return classAttendanceRepository.getLogsWithId(id)
    }
}