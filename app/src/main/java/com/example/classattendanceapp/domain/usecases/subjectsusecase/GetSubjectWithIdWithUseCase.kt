package com.example.classattendanceapp.domain.usecases.subjectsusecase

import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class GetSubjectWithIdWithUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int): Subject{
        return classAttendanceRepository.getSubjectWithId(id)
    }
}