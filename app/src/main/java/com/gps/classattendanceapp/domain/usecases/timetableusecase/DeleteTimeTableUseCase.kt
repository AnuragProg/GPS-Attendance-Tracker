package com.gps.classattendanceapp.domain.usecases.timetableusecase

import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class DeleteTimeTableUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    suspend operator fun invoke(id: Int){
        classAttendanceRepository.deleteTimeTable(id)
    }

}