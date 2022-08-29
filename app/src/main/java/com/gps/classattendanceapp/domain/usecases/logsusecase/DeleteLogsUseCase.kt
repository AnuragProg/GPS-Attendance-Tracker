package com.gps.classattendanceapp.domain.usecases.logsusecase

import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteLogsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int){
        classAttendanceRepository.deleteLogs(id)
    }
}