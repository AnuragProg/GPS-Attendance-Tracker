package com.example.classattendanceapp.domain.usecases.timetableusecase

import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetTimeTableWithSubjectIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(subjectId: Int): Flow<List<TimeTable>> {
        return classAttendanceRepository.getTimeTableWithSubjectId(subjectId)
    }
}