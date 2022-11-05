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
        }
    }
}