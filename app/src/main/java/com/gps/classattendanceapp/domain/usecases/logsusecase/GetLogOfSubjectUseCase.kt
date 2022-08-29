package com.gps.classattendanceapp.domain.usecases.logsusecase

import com.gps.classattendanceapp.data.models.Log
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetLogOfSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(subjectName: String): Flow<List<Log>> {
        return classAttendanceRepository.getLogOfSubject(subjectName)
    }
}