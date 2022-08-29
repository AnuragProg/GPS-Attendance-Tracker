package com.gps.classattendanceapp.domain.usecases.subjectsusecase

import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class GetSubjectWithIdWithUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int): Subject?{
        return classAttendanceRepository.getSubjectWithId(id)
    }
}