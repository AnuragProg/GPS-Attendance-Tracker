package com.gps.classattendanceapp.domain.usecases.timetableusecase


import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.data.models.TimeTable
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import com.gps.classattendanceapp.presenter.utils.Days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetTimeTableUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke() : Flow<Resource<Map<String, List<TimeTable>>>>{
        return classAttendanceRepository.getTimeTable().map{
            val resultant = mutableMapOf<String, List<TimeTable>>()
            for(day in Days.values()){
                resultant[day.day] = it.filter{
                    it.dayOfTheWeek == day.value
                }
            }
            Resource.Success(resultant)
        }
    }
}