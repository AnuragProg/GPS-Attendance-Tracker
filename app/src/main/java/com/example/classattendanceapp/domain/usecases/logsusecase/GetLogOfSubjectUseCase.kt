package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.data.models.Log
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetLogOfSubjectUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(subjectName: String): Flow<List<Log>> {
        return classAttendanceRepository.getLogOfSubject(subjectName)
    }
}