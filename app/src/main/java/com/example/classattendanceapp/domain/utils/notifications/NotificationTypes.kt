package com.example.classattendanceapp.domain.utils.notifications

enum class NotificationTypes {
    SIMPLE_NOTIFICATION, // E.g. TCH201 MARK YOUR ATTENDANCE -> present absent
    ATTENDANCE_ALREADY_MARKED_ATTENDANCE_NOTIFICATION,  // E.g. MARKED ABSENT -> mark present OR MARKED PRESENT -> mark absent
}