package com.temperature.temperatur_sensor_sdk.component.date

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DateTimePicker(
    label: String,
    currentDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    // 本地狀態：用於保存用戶選擇的日期和時間
    var selectedDate by remember { mutableStateOf(currentDateTime.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(currentDateTime.toLocalTime()) }

    // 格式化日期時間
    val formattedDateTime = remember(selectedDate, selectedTime) {
        LocalDateTime.of(selectedDate, selectedTime)
            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
    }

    Column {
        // 改成可點擊的輸入框
        OutlinedTextField(
            value = formattedDateTime,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dateDialogState.show() },
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Selected Date",
                    modifier = Modifier.clickable { dateDialogState.show() }
                )
            }
        )

        // 日期選擇器
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(
                    text = "APPLY",
                    textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
                ) {
                    onDateTimeSelected(LocalDateTime.of(selectedDate, selectedTime))
                }
                positiveButton(
                    text = "NEXT",
                    textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.secondary)
                ) {
                    timeDialogState.show() // 打開時間選擇器
                }
                negativeButton(
                    text = "CANCEL",
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                )
            }
        ) {
            datepicker(
                initialDate = currentDateTime.toLocalDate(),
                title = "Selected Date"
            ) { pickedDate -> selectedDate = pickedDate }
        }

        // 時間選擇器
        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(
                    text = "APPLY",
                    textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
                ) {
                    onDateTimeSelected(LocalDateTime.of(selectedDate, selectedTime))
                }
                negativeButton(
                    text = "CANCEL",
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                )
            }
        ) {
            timepicker(
                initialTime = currentDateTime.toLocalTime(),
                title = "Selected Time",
                colors = TimePickerDefaults.colors(
                    activeBackgroundColor = MaterialTheme.colorScheme.primary,
                    inactiveBackgroundColor = MaterialTheme.colorScheme.surface,
                    activeTextColor = MaterialTheme.colorScheme.onPrimary,
                    inactiveTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) { pickedTime -> selectedTime = pickedTime }
        }
    }
}