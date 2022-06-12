package com.example.classattendanceapp.domain.usecases.datastoreusecase

import androidx.datastore.preferences.core.Preferences
import com.example.classattendanceapp.domain.repository.ClassAttendanceRepository
import kotlinx.coroutines.flow.Flow


class GetCoordinateInDataStoreUseCase(
    private val classAttendanceRepository: ClassAttendanceRepository
) {
    suspend operator fun invoke(key: Preferences.Key<Double>): Flow<Double?> {
        return classAttendanceRepository.getCoordinateInDataStore(key)
    }
}