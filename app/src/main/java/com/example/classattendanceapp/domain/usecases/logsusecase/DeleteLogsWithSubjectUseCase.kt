package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteLogsWithSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subjectName: String){
        classAttendanceRepository.deleteLogsWithSubject(subjectName)
    }
}