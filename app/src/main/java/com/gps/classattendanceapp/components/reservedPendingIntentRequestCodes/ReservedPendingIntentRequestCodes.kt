package com.gps.classattendanceapp.components.reservedPendingIntentRequestCodes

enum class ReservedPendingIntentRequestCodes (
    val requestCode: Int
){
    OPEN_MAIN_ACTIVITY(-1),
    MARK_PRESENT(-20),
    MARK_ABSENT(-30),
    INVERT_PREVIOUSLY_MARKED_ATTENDANCE(-40)
}