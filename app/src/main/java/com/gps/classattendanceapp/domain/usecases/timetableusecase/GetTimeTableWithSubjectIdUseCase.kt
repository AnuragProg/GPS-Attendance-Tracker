package com.gps.classattendanceapp.domain.usecases.timetableusecase

import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetTimeTableWithSubjectIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(subjectId: Int): Flow<List<TimeTable>> {
        return classAttendanceRepository.getTimeTableWithSubjectId(subjectId)
    }
}