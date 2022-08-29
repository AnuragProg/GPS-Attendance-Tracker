package com.gps.classattendanceapp.domain.usecases.logsusecase

import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteLogsWithSubjectIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subjectId: Int){
        classAttendanceRepository.deleteLogsWithSubjectId(subjectId)
    }
}