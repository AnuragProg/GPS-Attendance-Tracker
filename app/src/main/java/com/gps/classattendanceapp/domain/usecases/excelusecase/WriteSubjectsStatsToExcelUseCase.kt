package com.gps.classattendanceapp.domain.usecases.excelusecase

import android.content.Context
import android.net.Uri
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository

class WriteSubjectsStatsToExcelUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(context: Context, subjectsList: List<ModifiedSubjects>):Uri{
        return classAttendanceRepository.writeSubjectsStatsToExcel(context, subjectsList)
    }
}