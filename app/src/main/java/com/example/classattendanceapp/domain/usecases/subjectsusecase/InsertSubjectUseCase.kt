package com.example.classattendanceapp.domain.usecases.subjectsusecase

import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class InsertSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subject: Subject): Long{
        return classAttendanceRepository.insertSubject(subject = subject)
    }
}