package com.example.classattendanceapp.domain.usecases.timetableusecase

import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class GetTimeTableWithIdUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int): TimeTable{
        return classAttendanceRepository.getTimeTableWithId(id)
    }
}