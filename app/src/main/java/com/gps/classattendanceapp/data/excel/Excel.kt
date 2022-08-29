package com.gps.classattendanceapp.data.excel

import android.content.Context
import android.net.Uri
import com.gps.classattendanceapp.domain.models.ModifiedLogs
import com.gps.classattendanceapp.domain.models.ModifiedSubjects
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class Excel {

    fun writeSubjectsStatsToExcel(context: Context, subjectsList: List<ModifiedSubjects>):Uri{
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Subjects Stats")

        // Set headers in excel file
        val headers = listOf(
            "Subject Name",
            "Latitude",
            "Longitude",
            "Range",
            "Total days present",
            "Total days absent",
            "Attendance"
        )


        var row = sheet.createRow(0)
        for((index, header) in headers.withIndex()){
            val cell = row.createCell(index)
            cell.setCellValue(header)
            sheet.setColumnWidth(index,20 * 256)
        }

        for((index,subject) in subjectsList.withIndex()){
            row = sheet.createRow(index+1)
            var cell = row.createCell(0)
            cell.setCellValue(subject.subjectName)
            cell = row.createCell(1)
            cell.setCellValue(
                if(subject.latitude==null) "Unknown"
                else subject.latitude.toString()
            )
            cell = row.createCell(2)
            cell.setCellValue(
                if(subject.longitude==null)"Unknown"
                else subject.longitude.toString()
            )
            cell = row.createCell(3)
            cell.setCellValue(
                if(subject.range ==null) "Unknown"
                else subject.range.toString()
            )
            cell = row.createCell(4)
            cell.setCellValue(subject.totalPresents.toString())
            cell = row.createCell(5)
            cell.setCellValue(subject.totalAbsents.toString())
            cell = row.createCell(6)
            cell.setCellValue(subject.attendancePercentage.toString() + "%")
        }

        val externalFileLocation = context.getExternalFilesDir(null)
        val excelFile = File(externalFileLocation , "Subject_Stats.xls")
        val outputStream = FileOutputStream(excelFile)
        workbook.write(outputStream)
        outputStream.close()
        return Uri.parse(excelFile.absolutePath)
    }

    fun writeLogsStatsToExcel(context: Context, logsList: List<ModifiedLogs>): Uri{
        val workbook = HSSFWorkbook()
        val headers = listOf(
            "Subject Name",
            "Time",
            "Date",
            "Day",
            "Latitude",
            "Longitude",
            "Distance",
            "Attendance"
        )

        val sheet = workbook.createSheet("Log Stats")
        var row = sheet.createRow(0)
        for((index, header) in headers.withIndex()){
            sheet.setColumnWidth(index, 20*256)
            val cell = row.createCell(index)
            cell.setCellValue(header)
        }

        for((index, log) in logsList.withIndex()){
            row = sheet.createRow(index+1)
            var cell = row.createCell(0)
            cell.setCellValue(log.subjectName)
            cell = row.createCell(1)
            cell.setCellValue("${log.hour}:${log.minute}")
            cell = row.createCell(2)
            cell.setCellValue("${log.month} ${log.date}, ${log.year}")
            cell = row.createCell(3)
            cell.setCellValue(log.day)
            cell = row.createCell(4)
            cell.setCellValue(
                if(log.latitude==null)"Unknown"
                else log.latitude.toString()
            )
            cell = row.createCell(5)
            cell.setCellValue(
                if(log.longitude==null)"Unknown"
                else log.longitude.toString()
            )
            cell = row.createCell(6)
            cell.setCellValue(
                if(log.distance==null)"Unknown"
                else log.distance.toString()
            )
            cell = row.createCell(7)
            cell.setCellValue(if(log.wasPresent)"Present" else "Absent")
        }

        val externalFileLocation = context.getExternalFilesDir(null)
        val excelFile = File(externalFileLocation, "Logs_Stats.xls")
        val outputFileStream = FileOutputStream(excelFile)
        workbook.write(outputFileStream)
        outputFileStream.close()
        return Uri.parse(excelFile.absolutePath)
    }

}