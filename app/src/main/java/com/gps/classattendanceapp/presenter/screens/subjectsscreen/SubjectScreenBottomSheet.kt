package com.gps.classattendanceapp.presenter.screens.subjectsscreen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.domain.models.ModifiedSubjects


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubjectScreenBottomSheet(
    sheetState: ModalBottomSheetState,
    subject: ModifiedSubjects? = null,
    subjectScreen: @Composable () -> Unit,
){

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            BottomSheetContent(
                subject=subject,
                headingSize=18.sp,
                contentSize=13.sp

            )
        },
        content = {
                  subjectScreen()
        },
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
    )
}

@Composable
fun BottomSheetContent(
    subject: ModifiedSubjects?,
    headingSize: TextUnit,
    contentSize: TextUnit
){
    if(subject==null) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ){}
        return
    }

    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier.padding(10.dp),
            text = subject.subjectName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        BottomSheetLocationCard(
            subject = subject,
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(10.dp),
            headingSize = headingSize,
            contentSize = contentSize
        )
        Spacer(modifier=Modifier.height(10.dp))
        BottomSheetInformativeContent(
            subject = subject,
            headingSize=headingSize,
            contentSize=contentSize
        )
    }
}

@Composable
fun BottomSheetInformativeContent(
    subject: ModifiedSubjects,
    headingSize: TextUnit,
    contentSize: TextUnit
) {

    Column{
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column{
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Present" ,
                        fontSize = headingSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.marked),
                        contentDescription = null
                    )
                }
                Text(
                    text="Manual -> ${subject.daysPresent}",
                    fontSize=contentSize
                )
                Text(
                    text="Logs -> ${subject.daysPresentOfLogs}",
                    fontSize=contentSize
                )
                Text(
                    text="Total -> ${subject.daysPresent + subject.daysPresentOfLogs}",
                    fontSize=contentSize
                )
            }


            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Absent",
                        fontSize = headingSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.exclamation_mark),
                        contentDescription = null,
                    )
                }
                Text(
                    text="Manual -> ${subject.daysAbsent}",
                    fontSize=contentSize
                )
                Text(
                    text="Logs -> ${subject.daysAbsentOfLogs}",
                    fontSize=contentSize
                )
                Text(
                    text="Total -> ${subject.daysAbsent + subject.daysAbsentOfLogs}",
                    fontSize=contentSize
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row{
                Text(
                    text="Summary",
                    fontSize = headingSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text="Total Presents -> ${subject.daysPresent + subject.daysPresentOfLogs}",
                fontSize=contentSize
            )
            Text(
                text="Total Absents -> ${subject.daysAbsent + subject.daysAbsentOfLogs}",
                fontSize=contentSize
            )
            Text(
                text="Total Days -> ${subject.totalDays}",
                fontSize=contentSize
            )
        }
    }

}

@Composable
fun BottomSheetLocationCard(
    subject: ModifiedSubjects,
    modifier : Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    headingSize: TextUnit,
    contentSize: TextUnit
) {
    val context = LocalContext.current


    Column(
        modifier=modifier
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Location",
                fontSize = headingSize,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick = {
                    subject.let{
                        if(it.latitude==null||it.longitude==null||it.range==null){
                            Toast.makeText(context, "Missing location fields!", Toast.LENGTH_SHORT).show()
                        }else{
                            val mapsUri = Uri.parse("geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}(${it.subjectName})")
                            val intent = Intent(Intent.ACTION_VIEW, mapsUri)
                                .apply{
                                    `package` = "com.google.android.apps.maps"
                                }
                            context.startActivity(intent)
                        }
                    }
                },
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Google Maps")
            }
        }
        Column(
        ) {
            Text(
                text = "Latitude -> ${subject.latitude ?: "Unknown"}",
                fontSize = contentSize
            )
            Text(
                text="Longitude -> ${subject.longitude ?: "Unknown"}",
                fontSize=contentSize
            )
            Text(
                text="Range -> ${subject.range?.let{"${String.format("%.2f",it)} meters"} ?: "Unknown"}",
                fontSize=contentSize
            )
        }
    }

}