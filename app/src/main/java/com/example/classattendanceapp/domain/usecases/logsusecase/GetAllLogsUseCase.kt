package com.example.classattendanceapp.domain.usecases.logsusecase

import com.example.classattendanceapp.data.models.toModifiedLogs
import com.example.classattendanceapp.domain.models.ModifiedLogs
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.*



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