package com.example.classattendanceapp.domain.utils.notifications

enum class NotificationKeys(
    val key: String
) {
    TIMETABLE_ID("subject_id"),
    SUBJECT_ID("subjectId"),
    ATTENDANCE_STATUS("attendance"),
    NOTIFICATION_PUSH("notificationPush"),

    NOTIFICATION_ID("notification_id"),
    LOGS_ID("logs_id")
}