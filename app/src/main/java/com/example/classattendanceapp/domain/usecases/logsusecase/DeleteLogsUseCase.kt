package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteLogsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int){
        classAttendanceRepository.deleteLogs(id)
    }
}