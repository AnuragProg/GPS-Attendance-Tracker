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
            BottomSheetContent(subject)
        },
        content = {
                  subjectScreen()
        },
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
    )
}

@Composable
fun BottomSheetContent(
    subject: ModifiedSubjects?
){
    if(subject==null) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ){}
        return
    }

    Column(
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
            shape = RoundedCornerShape(10.dp)
        )
        BottomSheetInformativeContent(
            subject = subject
        )
    }
}

@Composable
fun BottomSheetInformativeContent(
    subject: ModifiedSubjects
) {
    Card(){
        Column() {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Present Stats" ,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.marked),
                            contentDescription = null
                        )
                    }
                    Text("Manual -> ${subject.daysPresent}")
                    Text("Logs -> ${subject.daysPresentOfLogs}")
                    Text("Total -> ${subject.daysPresent + subject.daysPresentOfLogs}")
                }


                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Absent Stats",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.exclamation_mark),
                            contentDescription = null,
                        )
                    }
                    Text("Manual -> ${subject.daysAbsent}")
                    Text("Logs -> ${subject.daysAbsentOfLogs}")
                    Text("Total -> ${subject.daysAbsent + subject.daysAbsentOfLogs}")
                }

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row() {
                    Text(
                        text="Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text("Total Presents -> ${subject.daysPresent + subject.daysPresentOfLogs}")
                Text("Total Absents -> ${subject.daysAbsent + subject.daysAbsentOfLogs}")
                Text("Total Days -> ${subject.totalDays}")
            }
        }
    }
}

@Composable
fun BottomSheetLocationCard(
    subject: ModifiedSubjects,
    modifier : Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium
) {
    val context = LocalContext.current
    Card(
        modifier = modifier,
        shape = shape
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = {
                              subject.let{
                                  if(it.latitude==null||it.longitude==null||it.range==null){
                                      Toast.makeText(context, "Missing location fields!", Toast.LENGTH_SHORT).show()
                                  }else{
                                      val mapsUri = Uri.parse("geo:${it.latitude},${it.longitude}")
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
                Text("Latitude -> ${subject.latitude ?: "Unknown"}")
                Text("Longitude -> ${subject.longitude ?: "Unknown"}")
                Text("Range ( in meters ) -> ${subject.range ?: "Unknown"}")
            }
        }
    }
}