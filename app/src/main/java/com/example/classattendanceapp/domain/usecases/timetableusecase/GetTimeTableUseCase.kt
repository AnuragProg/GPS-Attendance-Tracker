package com.example.classattendanceapp.domain.usecases.timetableusecase


import com.example.classattendanceapp.data.models.TimeTable
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import com.example.classattendanceapp.presenter.utils.Days
import kotlinx.coroutines.flow.*


class GetTimeTableUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke() : Flow<Map<String, List<TimeTable>>>{
        return classAttendanceRepository.getTimeTable().map{
            val resultant = mutableMapOf<String, List<TimeTable>>()
            for(day in Days.values()){
                resultant[day.day] = it.filter{
                    it.dayOfTheWeek == day.value
                }
            }
            resultant
        }
    }
}