package com.example.classattendanceapp.domain.usecases.excelusecase

import android.content.Context
import android.net.Uri
import com.example.classattendanceapp.domain.models.ModifiedSubjects
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository

class WriteSubjectsStatsToExcelUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(context: Context, subjectsList: List<ModifiedSubjects>):Uri{
        return classAttendanceRepository.writeSubjectsStatsToExcel(context, subjectsList)
    }
}