package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteLogsWithSubjectIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subjectId: Int){
        classAttendanceRepository.deleteLogsWithSubjectId(subjectId)
    }
}