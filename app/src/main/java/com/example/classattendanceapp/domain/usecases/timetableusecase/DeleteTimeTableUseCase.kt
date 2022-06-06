package com.example.classattendanceapp.domain.usecases.timetableusecase

import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteTimeTableUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int){
        classAttendanceRepository.deleteTimeTable(id)
    }

}