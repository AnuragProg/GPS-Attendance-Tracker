package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.data.models.Log
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetLogOfSubjectIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(subjectId: Int): Flow<List<Log>>{
        return classAttendanceRepository.getLogOfSubjectId(subjectId)
    }
}