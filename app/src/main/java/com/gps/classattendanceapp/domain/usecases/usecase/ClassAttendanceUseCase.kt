package com.gps.classattendanceapp.domain.usecases.usecase

import com.gps.classattendanceapp.domain.usecases.logsusecase.*
import com.gps.classattendanceapp.domain.usecases.subjectsusecase.*
import com.gps.classattendanceapp.domain.usecases.timetableusecase.*

data class ClassAttendanceUseCase(
    val updateSubjectUseCase: UpdateSubjectUseCase,
    val updateLogUseCase: UpdateLogUseCase,
    val deleteLogsUseCase: DeleteLogsUseCase,
    val deleteSubjectUseCase: DeleteSubjectUseCase,
    val deleteTimeTableUseCase: DeleteTimeTableUseCase,
    val deleteTimeTableWithSubjectIdUseCase: DeleteTimeTableWithSubjectIdUseCase,
    val deleteLogsWithSubjectUseCase: DeleteLogsWithSubjectUseCase,
    val deleteLogsWithSubjectIdUseCase: DeleteLogsWithSubjectIdUseCase,
    val getAllLogsUseCase: GetAllLogsUseCase,
    val getTimeTableUseCase: GetTimeTableUseCase,
    val getTimeTableWithIdUseCase: GetTimeTableWithIdUseCase,
    val getTimeTableWithSubjectIdUseCase: GetTimeTableWithSubjectIdUseCase,
    val getSubjectsUseCase: GetSubjectsUseCase,
    val getSubjectWithIdWithUseCase: GetSubjectWithIdWithUseCase,
    val getLogsWithIdUseCase: GetLogsWithIdUseCase,
    val insertLogsUseCase: InsertLogsUseCase,
    val insertSubjectUseCase: InsertSubjectUseCase,
    val insertTimeTableUseCase: InsertTimeTableUseCase,
    val getLogOfSubjectUseCase: GetLogOfSubjectUseCase,
    val getLogOfSubjectIdUseCase: GetLogOfSubjectIdUseCase,
    val getTimeTableOfDayUseCase: GetTimeTableOfDayUseCase,
    val getPresentThroughLogsUseCase: GetPresentThroughLogsUseCase,
    val getAbsentThroughLogsUseCase: GetAbsentThroughLogsUseCase,


)
