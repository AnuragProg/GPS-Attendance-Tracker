package com.example.classattendanceapp.domain.usecases.timetableusecase

import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetTimeTableOfDayUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(day: Int): Flow<List<TimeTable>>{
        return classAttendanceRepository.getTimeTableOfDay(day)
    }
}