package com.gps.classattendanceapp.domain.usecases.logsusecase

import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetPresentThroughLogsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke(subjectId: Int): Flow<Int>{
        return classAttendanceRepository.getPresentThroughLogs(subjectId)
    }
}