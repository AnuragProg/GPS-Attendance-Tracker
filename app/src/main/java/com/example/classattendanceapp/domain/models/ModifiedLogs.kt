package com.example.classattendanceapp.domain.models



/*
 All fields are null by default
 Reason :- When User edits log, existing log participates in operation
           when user creates log, new log is generated with all fields preset
           to not initialize every value when we are taking information from the user
 */
data class ModifiedLogs(
    val _id: Int?=null,
    var subjectId: Int?=null,
    var subjectName: String?=null,
    var hour: Int?=null,
    var minute: Int?=null,
    var date: Int?=null,
    var day: String?=null,
    var month: String?=null,
    var monthNumber: Int?=null,
    var year: Int?=null,
    var wasPresent: Boolean=true,
    var latitude: Double?=null,
    var longitude: Double?=null,
    var distance: Double?=null
)