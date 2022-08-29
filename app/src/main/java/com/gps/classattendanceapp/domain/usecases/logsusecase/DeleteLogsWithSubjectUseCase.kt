package com.gps.classattendanceapp.domain.usecases.logsusecase

import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteLogsWithSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subjectName: String){
        classAttendanceRepository.deleteLogsWithSubject(subjectName)
    }
}