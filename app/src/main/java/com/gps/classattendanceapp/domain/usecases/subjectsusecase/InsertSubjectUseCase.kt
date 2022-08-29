package com.gps.classattendanceapp.domain.usecases.subjectsusecase

import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class InsertSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subject: Subject): Long{
        return classAttendanceRepository.insertSubject(subject = subject)
    }
}