package com.gps.classattendanceapp.presenter.screens

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable


@Composable
fun DeleteConfirmationDialog(
    onConfirm: ()->Unit,
    onReject: ()->Unit,

    // provide a method to change state to hide dialog
    hide: ()->Unit,
) {
    AlertDialog(
        onDismissRequest = {
            hide()
                           },
        confirmButton = {
            TextButton(onClick = {
                hide()
                onConfirm()
            }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                hide()
                onReject()
            }) {
                Text("Cancel")
            }
        },
        title = {Text("Delete Confirmation")},
        text = {Text("Do you want to delete this item?")}
    )
}