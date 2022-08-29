package com.gps.classattendanceapp.domain.usecases.subjectsusecase

import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class UpdateSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(subject: Subject){
        return classAttendanceRepository.updateSubject(subject)
    }
}
