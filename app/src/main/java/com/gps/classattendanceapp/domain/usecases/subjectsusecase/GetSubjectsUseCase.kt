package com.gps.classattendanceapp.domain.usecases.subjectsusecase

import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.data.models.Subject
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSubjectsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(): Flow<Resource<List<Subject>>>{
        return classAttendanceRepository.getAllSubjects().map{
            Resource.Success(it)
        }
    }
}