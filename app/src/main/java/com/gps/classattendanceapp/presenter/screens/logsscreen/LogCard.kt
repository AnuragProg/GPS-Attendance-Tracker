package com.gps.classattendanceapp.presenter.screens.logsscreen

import android.view.RoundedCorner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gps.classattendanceapp.R
import com.gps.classattendanceapp.domain.models.ModifiedLogs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogCard(
    log: ModifiedLogs,
    changeIsLogSelected: (Boolean)->Unit,
    onClick: ()->Unit
){
    var showAdditionalCardDetails by remember{
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    showAdditionalCardDetails = !showAdditionalCardDetails
                    onClick()
                },
                onLongClick = {
                    changeIsLogSelected(true)
                }
            )
            .padding(10.dp),
        backgroundColor = if(log.wasPresent)Color.Green else Color.Red,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            Row(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.subjectName!!,
                    overflow = if (!showAdditionalCardDetails) {
                        TextOverflow.Ellipsis
                    } else {
                        TextOverflow.Visible
                    },
                    maxLines = 1,
                    color = Color.White
                )
                Text(
                    text = log.month + " " + log.date.toString() + "," + log.year.toString(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = Color.White
                )

                Text(
                    text = when (log.wasPresent) {
                        true -> stringResource(R.string.present)
                        else -> stringResource(R.string.absent)
                    },
                    maxLines = 1,
                    color = Color.White
                )


            }
//            AnimatedVisibility(
//                visible = showAdditionalCardDetails
//            ) {

//                Box(
//                    modifier = Modifier.padding(20.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.Start
//                    ) {
//
//                        Text("Latitude :")
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Text("Longitude :")
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Text("Distance: ")
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Text("Date : ")
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Text("Time :")
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Text("Day :")
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Text("Status : ")
//
//                    }
//                    Column(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.End
//                    ) {
//
//                        Text(
//                            log.latitude?.let{
//                                String.format("%.5f",
//                                    log.latitude)
//                            }?: "Unknown"
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
//                        Text(
//                            log.longitude?.let{
//                                String.format("%.5f",
//                                    log.longitude)
//                            } ?: "Unknown"
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
//                        Text(
//                            log.distance?.let{
//                                String.format("%.5f",
//                                    log.distance)
//                            } ?: "Unknown"
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Text(
//                            text = "${log.month} ${log.date}, ${log.year}"
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
//                        Text(
//                            text = "${
//                                if (log.hour !!< 10) "0${log.hour}"
//                                else log.hour
//                            }:${
//                                if (log.minute!! < 10) "0${log.minute}"
//                                else log.minute
//                            }"
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
//                        Text(
//                            log.day!!
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Text(
//                            text = when (log.wasPresent) {
//                                true -> stringResource(R.string.present)
//                                else -> stringResource(R.string.absent)
//                            },
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
//                    }
//                }
//            }
        }
    }
}