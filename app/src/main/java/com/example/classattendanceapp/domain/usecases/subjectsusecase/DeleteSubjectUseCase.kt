package com.example.classattendanceapp.domain.usecases.subjectsusecase

import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int){
        classAttendanceRepository.deleteSubject(id)
    }
}