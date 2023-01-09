package com.gps.classattendanceapp.components.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gps.classattendanceapp.MainActivity
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.components.notifications.deletepreviouslymarkedattendance.DeletePreviouslyMarkedAttendanceBroadcastReceiver
import com.gps.classattendanceapp.components.notifications.invertpreviouslymarkedattendance.InvertPreviouslyMarkedAttendanceBroadcastReceiver
import com.gps.classattendanceapp.components.notifications.markpresentabsentthroughnotification.MarkPresentAbsentThroughNotificationBroadcastReceiver
import com.gps.classattendanceapp.components.reservedPendingIntentRequestCodes.ReservedPendingIntentRequestCodes
import kotlin.random.Random

object NotificationHandler {

    private const val CHANNELID = "CLASSATTENDANCECHANNEL"


    fun createNotificationChannel(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                CHANNELID,
                "Class Attendance Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply{
                description = "Creates Channel for Class Attendance Channel"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun createAndShowNotification(
        context: Context, // to create intent and pendingIntent
        logsId: Int?=null, // for inverting attendance if successfully marked attendance using gps
        markedPresentOrAbsent: Boolean?=null, // for inverting attendance if successfully marked attendance using gps
        timeTableId: Int, // for requestCode of pendingIntent to be unique
        subjectId: Int, // for extracting subject from db and manipulate its data on action button click
        subjectName: String, // for showing in notification (information purpose)
        hour: Int, // show time in notification
        minute: Int, // show time in notification
        message: String?, // present or absent
        reason: String = "", // in case of failure then reason of failure
    ){

        val intent = Intent(context, MainActivity::class.java).apply{
            putExtra(NotificationKeys.TIMETABLE_ID.key, timeTableId)
        }

        val pendingIntent = PendingIntent.getActivity(context, ReservedPendingIntentRequestCodes.OPEN_MAIN_ACTIVITY.requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val contentMessage = message?.let{
            "Marked $it"
        } ?: "$subjectName - $hour:$minute"

        val markPresentIntent = Intent(context, MarkPresentAbsentThroughNotificationBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.notifications.markpresentabsentthroughnotification.MarkPresentAbsentThroughNotificationBroadcastReceiver"
            putExtra(NotificationKeys.ATTENDANCE_STATUS.key, true)
            putExtra(NotificationKeys.NOTIFICATION_PUSH.key, timeTableId)
            putExtra(NotificationKeys.SUBJECT_ID.key, subjectId)
        }

        val markPresentPendingIntent = PendingIntent.getBroadcast(context, ReservedPendingIntentRequestCodes.MARK_PRESENT.requestCode, markPresentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val markAbsentIntent = Intent(context, MarkPresentAbsentThroughNotificationBroadcastReceiver::class.java).apply{
            action = "com.example.classattendanceapp.domain.utils.notifications.markpresentabsentthroughnotification.MarkPresentAbsentThroughNotificationBroadcastReceiver"
            putExtra(NotificationKeys.ATTENDANCE_STATUS.key, false)
            putExtra(NotificationKeys.NOTIFICATION_PUSH.key, timeTableId)
            putExtra(NotificationKeys.SUBJECT_ID.key, subjectId)
        }
        val markAbsentPendingIntent = PendingIntent.getBroadcast(context, ReservedPendingIntentRequestCodes.MARK_ABSENT.requestCode, markAbsentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val invertPreviouslyMarkedAttendanceIntent = Intent(
            context, InvertPreviouslyMarkedAttendanceBroadcastReceiver::class.java
        ).apply{
            action = "com.example.classattendanceapp.domain.utils.notifications.invertpreviouslymarkedattendance.InvertPreviouslyMarkedAttendanceBroadcastReceiver"
            putExtra(NotificationKeys.NOTIFICATION_ID.key, timeTableId)
            putExtra(NotificationKeys.LOGS_ID.key, logsId)
        }
        val invertPreviouslyMarkedAttendancePendingIntent = PendingIntent.getBroadcast(context, ReservedPendingIntentRequestCodes.INVERT_PREVIOUSLY_MARKED_ATTENDANCE.requestCode, invertPreviouslyMarkedAttendanceIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val deletePreviouslyMarkedAttendanceIntent = Intent(context, DeletePreviouslyMarkedAttendanceBroadcastReceiver::class.java)
            .apply{
                action = "com.gps.classattendanceapp.components.notifications.deletepreviouslymarkedattendance.DeletePreviouslyMarkedAttendanceBroadcastReceiver"
                putExtra(NotificationKeys.NOTIFICATION_ID.key, timeTableId)
                putExtra(NotificationKeys.LOGS_ID.key, logsId)
            }
        val deletePreviouslyMarkedAttendancePendingIntent = PendingIntent.getBroadcast(context, Random.nextInt(), deletePreviouslyMarkedAttendanceIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val invertedAttendance = when (markedPresentOrAbsent) {
            true -> "Absent"
            false -> "Present"
            else -> null
        }

       val title =  "$subjectName: ${if(hour<10) "0$hour" else hour}:${if(minute<10)"0$minute" else minute}"
       val boldTitle = SpannableString(title).apply{
           setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
       }
        val notification = if(message!=null){
            NotificationCompat.Builder(context, CHANNELID)
                .setSmallIcon(R.drawable.marked)
                .setContentTitle(boldTitle) // context.getString(R.string.marked_your_attendance)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                    contentMessage
                ))
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .addAction(R.drawable.marked,"No, I was $invertedAttendance",invertPreviouslyMarkedAttendancePendingIntent)
                .addAction(R.drawable.marked, "Delete", deletePreviouslyMarkedAttendancePendingIntent)
                .build()
        }else{
            NotificationCompat.Builder(context, CHANNELID)
                .setSmallIcon(R.drawable.exclamation_mark)
                .setContentTitle(boldTitle) // context.getString(R.string.marked_your_attendance)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .addAction(R.drawable.marked, "Present", markPresentPendingIntent)
                .addAction(R.drawable.marked, "Absent", markAbsentPendingIntent)
                .apply{
                    if(reason.isNotBlank())
                        setStyle(NotificationCompat.BigTextStyle().bigText("$contentMessage\nReason: $reason"))
                    else setContentText(contentMessage)
                }
                .build()
        }
        with(NotificationManagerCompat.from(context)){
            notify(timeTableId, notification)
        }
    }
}