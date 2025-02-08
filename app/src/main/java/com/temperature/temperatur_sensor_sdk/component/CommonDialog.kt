package com.temperature.temperatur_sensor_sdk.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

data class DialogState(
    val isShow: Boolean = false,
    val title: String = "",
    val content: String = "",
    val confirmText: String = "apply",
    val cancelText: String = "cancel",
    val onConfirm: () -> Unit = {}
)

@Composable
fun CommonDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String,
    content: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            dismissButton = {
                TextButton(
                    onClick = onConfirm,
                    // 添加點擊範圍
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = confirmText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = cancelText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = true
            )
        )
    }
}
