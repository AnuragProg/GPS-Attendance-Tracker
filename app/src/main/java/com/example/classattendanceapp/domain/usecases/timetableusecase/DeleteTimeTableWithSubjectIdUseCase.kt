package com.example.classattendanceapp.domain.usecases.timetableusecase

import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteTimeTableWithSubjectIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subjectId: Int){
        classAttendanceRepository.deleteTimeTableWithSubjectId(subjectId)
    }
}