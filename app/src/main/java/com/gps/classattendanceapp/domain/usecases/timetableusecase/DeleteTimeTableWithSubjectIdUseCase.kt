package com.gps.classattendanceapp.domain.usecases.timetableusecase

import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteTimeTableWithSubjectIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subjectId: Int){
        classAttendanceRepository.deleteTimeTableWithSubjectId(subjectId)
    }
}