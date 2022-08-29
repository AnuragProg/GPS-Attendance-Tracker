package com.gps.classattendanceapp.domain.usecases.logsusecase

import com.gps.classattendanceapp.data.models.toModifiedLogs
import com.gps.classattendanceapp.domain.models.ModifiedLogs
import com.gps.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetAllLogsUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {

    operator fun invoke() : Flow<List<ModifiedLogs>>{
        return classAttendanceRepository.getAllLogs().map{ logsList ->
            logsList.map{ log ->
                log.toModifiedLogs()
            }
        }
    }
}