package com.example.classattendanceapp.domain.usecases.timetableusecase

import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class InsertTimeTableUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(timeTable: TimeTable): Long{
        return classAttendanceRepository.insertTimeTable(timeTable = timeTable)
    }
}