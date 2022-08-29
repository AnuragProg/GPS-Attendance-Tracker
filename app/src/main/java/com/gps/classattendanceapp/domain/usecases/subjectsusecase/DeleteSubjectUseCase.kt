package com.gps.classattendanceapp.domain.usecases.subjectsusecase

import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int){
        classAttendanceRepository.deleteSubject(id)
    }
}