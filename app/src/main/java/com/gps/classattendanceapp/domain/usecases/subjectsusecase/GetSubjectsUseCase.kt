package com.gps.classattendanceapp.domain.usecases.subjectsusecase

import com.gps.classattendanceapp.components.Resource
import com.gps.classattendanceapp.data.models.toModifiedSubjects
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSubjectsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(): Flow<Resource<List<com.gps.classattendanceapp.domain.models.ModifiedSubjects>>>{
        return classAttendanceRepository.getAllSubjects().map{subjectsList->
            Resource.Success(
                data = subjectsList.map{ subject->
                    subject.toModifiedSubjects()
                }
            )
        }
    }
}