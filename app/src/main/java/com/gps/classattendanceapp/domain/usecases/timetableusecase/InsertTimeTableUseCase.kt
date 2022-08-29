package com.gps.classattendanceapp.domain.usecases.timetableusecase

import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class InsertTimeTableUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(timeTable: TimeTable): Long{
        return classAttendanceRepository.insertTimeTable(timeTable = timeTable)
    }
}