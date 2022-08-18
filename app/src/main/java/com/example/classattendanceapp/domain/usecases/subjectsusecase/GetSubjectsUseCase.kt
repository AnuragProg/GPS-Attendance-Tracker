package com.example.classattendanceapp.domain.usecases.subjectsusecase

import com.example.classattendanceapp.data.models.toModifiedSubjects
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.*

class GetSubjectsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(): Flow<List<ModifiedSubjects>>{
        return classAttendanceRepository.getAllSubjects().map{subjectsList->
            subjectsList.map{ subject->
                subject.toModifiedSubjects()
            }
        }
    }
}