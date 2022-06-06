package com.example.classattendanceapp.domain.usecases.subjectsusecase

import android.util.Log
import com.example.classattendanceapp.data.models.Subject
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.*

class GetSubjectsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(): Flow<List<Subject>>{
        return classAttendanceRepository.getAllSubjects()
    }
}