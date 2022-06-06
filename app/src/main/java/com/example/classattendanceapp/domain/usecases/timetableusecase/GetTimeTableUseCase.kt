package com.example.classattendanceapp.domain.usecases.timetableusecase


import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.*


class GetTimeTableUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke() : Flow<List<TimeTable>>{
        return classAttendanceRepository.getTimeTable()
    }
}