package com.gps.classattendanceapp.presenter.screens.logsscreen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gps.classattendanceapp.domain.models.ModifiedLogs


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogsScreenBottomSheet(
    sheetState: ModalBottomSheetState,
    log: ModifiedLogs? = null,
    logsScreen: @Composable () -> Unit,
) {

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            LogsScreenBottomSheetContent(
                log=log,
                headingSize = 18.sp,
                contentSize = 13.sp
            )
        },
        content = {
            logsScreen()
        },
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
    )
}

@Composable
fun LogsScreenBottomSheetContent(
    log: ModifiedLogs?,
    headingSize: TextUnit,
    contentSize: TextUnit
) {

    val context = LocalContext.current

    if(log==null) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ){}
        return
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            modifier = Modifier.padding(10.dp),
            text = log.subjectName?: "Unknown",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

//        BottomSheetLocationCard(
//            modifier = Modifier.padding(10.dp),
//            log = log,
//            headingSize = headingSize,
//            contentSize = contentSize
//        )
        if (log.latitude != null && log.longitude != null) {
            Button(
                onClick = {
                    val mapsUri =
                        Uri.parse("geo:${log.latitude},${log.longitude}?q=${log.latitude},${log.longitude}(${log.subjectName})")
                    val intent = Intent(Intent.ACTION_VIEW, mapsUri)
                        .apply {
                            `package` = "com.google.android.apps.maps"
                        }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color.White
                )
                Text("Google Maps")
            }
        }
        Spacer(modifier=Modifier.height(10.dp))
        BottomSheetInformativeContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            log = log,
            headingSize = headingSize,
            contentSize = contentSize
        )

    }

}

@Composable
fun BottomSheetInformativeContent(
    modifier : Modifier = Modifier,
    log: ModifiedLogs,
    headingSize: TextUnit,
    contentSize: TextUnit
) {
    Card(
        modifier = modifier,
        backgroundColor = if(log.wasPresent)Color.Green else Color.Red,
        shape = RoundedCornerShape(10.dp)
    ){
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column{

                Text(
                    text = "${
                        if (log.hour!! < 10) "0${log.hour}"
                        else log.hour
                    }:${
                        if (log.minute!! < 10) "0${log.minute}"
                        else log.minute
                    }",
                    color = Color.White,
                    fontSize = contentSize
                )
                Spacer(modifier=Modifier.height(5.dp))
                Text(
                    text=log.day!!,
                    color = Color.White,
                    fontSize=contentSize
                )
                Spacer(modifier=Modifier.height(5.dp))
                Text(
                    text = "${log.month} ${log.date}, ${log.year}",
                    color = Color.White,
                    fontSize=contentSize
                )

                Spacer(modifier=Modifier.height(5.dp))

            }

            Text(
                text = if(log.wasPresent) "Present" else "Absent",
                fontSize = headingSize,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}


@Composable
fun BottomSheetLocationCard(
    modifier : Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    log: ModifiedLogs,
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
                    log.let{
                        if(it.latitude==null||it.longitude==null){
                            Toast.makeText(context, "Missing location fields!", Toast.LENGTH_SHORT).show()
                        }else{
                            val mapsUri = Uri.parse("geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}(You were here!)")
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
        Column{
            Text(
                text="Latitude -> ${log.latitude ?: "Unknown"}",
                fontSize=contentSize
            )
            Text(
                text="Longitude -> ${log.longitude ?: "Unknown"}",
                fontSize=contentSize
            )
            Text(
                text="Distance from location -> ${log.distance?.let{"${String.format("%.2f",it)} meters"} ?: "Unknown"}",
                fontSize=contentSize
            )
        }
    }

}
