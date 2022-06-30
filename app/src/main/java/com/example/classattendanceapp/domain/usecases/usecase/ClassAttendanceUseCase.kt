package com.example.classattendanceapp.domain.usecases.usecase

import com.example.classattendanceapp.domain.usecases.datastoreusecase.DeleteCoordinateInDataStoreUseCase
import com.example.classattendanceapp.domain.usecases.datastoreusecase.GetCoordinateInDataStoreUseCase
import com.example.classattendanceapp.domain.usecases.datastoreusecase.WriteOrUpdateCoordinateInDataStoreUseCase
import com.example.classattendanceapp.domain.usecases.logsusecase.*
import com.example.classattendanceapp.domain.usecases.subjectsusecase.*
import com.example.classattendanceapp.domain.usecases.timetableusecase.*

data class ClassAttendanceUseCase(
    val updateSubjectUseCase: UpdateSubjectUseCase,
    val deleteLogsUseCase: DeleteLogsUseCase,
    val deleteSubjectUseCase: DeleteSubjectUseCase,
    val deleteTimeTableUseCase: DeleteTimeTableUseCase,
    val deleteLogsWithSubjectUseCase: DeleteLogsWithSubjectUseCase,
    val deleteLogsWithSubjectIdUseCase: DeleteLogsWithSubjectIdUseCase,
    val getAllLogsUseCase: GetAllLogsUseCase,
    val getTimeTableUseCase: GetTimeTableUseCase,
    val getTimeTableWithIdUseCase: GetTimeTableWithIdUseCase,
    val getSubjectsUseCase: GetSubjectsUseCase,
    val getSubjectWithIdWithUseCase: GetSubjectWithIdWithUseCase,
    val getLogsWithIdUseCase: GetLogsWithIdUseCase,
    val insertLogsUseCase: InsertLogsUseCase,
    val insertSubjectUseCase: InsertSubjectUseCase,
    val insertTimeTableUseCase: InsertTimeTableUseCase,
    val getLogOfSubjectUseCase: GetLogOfSubjectUseCase,
    val getLogOfSubjectIdUseCase: GetLogOfSubjectIdUseCase,
    val getTimeTableOfDayUseCase: GetTimeTableOfDayUseCase,
    val getCoordinateInDataStoreUseCase: GetCoordinateInDataStoreUseCase,
    val writeOrUpdateCoordinateInDataStoreUseCase: WriteOrUpdateCoordinateInDataStoreUseCase,
    val deleteCoordinateInDataStoreUseCase: DeleteCoordinateInDataStoreUseCase
)
