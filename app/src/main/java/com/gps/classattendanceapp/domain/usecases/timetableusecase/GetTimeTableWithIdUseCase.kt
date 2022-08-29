package com.gps.classattendanceapp.domain.usecases.timetableusecase

import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class GetTimeTableWithIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int): TimeTable?{
        return classAttendanceRepository.getTimeTableWithId(id)
    }
}