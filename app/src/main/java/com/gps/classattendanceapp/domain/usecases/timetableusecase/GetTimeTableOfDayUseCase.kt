package com.gps.classattendanceapp.domain.usecases.timetableusecase

import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetTimeTableOfDayUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(day: Int): Flow<List<TimeTable>>{
        return classAttendanceRepository.getTimeTableOfDay(day)
    }
}