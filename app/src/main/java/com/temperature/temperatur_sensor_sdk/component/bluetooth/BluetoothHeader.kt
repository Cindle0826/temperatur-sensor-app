package com.temperature.temperatur_sensor_sdk.component.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.temperature.temperatur_sensor_sdk.R

/**
 * 顯示藍牙狀態圖示、提供掃描裝置按鈕
 */
@Composable
fun BluetoothHeader(
    state: BluetoothState,
    onScanClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 狀態圖示
        Icon(
            painter = when (state) {
                is BluetoothState.Unsupported -> painterResource(R.drawable.ic_bluetooth_disabled)
                is BluetoothState.Ready, is BluetoothState.Disabled -> painterResource(R.drawable.ic_bluetooth)
                is BluetoothState.Scanning -> painterResource(R.drawable.ic_bluetooth_searching)
                is BluetoothState.Connected -> painterResource(R.drawable.ic_bluetooth_connected)
            },
            contentDescription = null,
            tint = when (state) {
                is BluetoothState.Disabled -> Color.Gray
                is BluetoothState.Connected -> Color.Green
                else -> MaterialTheme.colorScheme.primary
            }
        )

        // 掃描按鈕
        Button(
            onClick = onScanClick,
            enabled = state !is BluetoothState.Scanning && state !is BluetoothState.Disabled
        ) {
            Text(
                text = when (state) {
                    is BluetoothState.Scanning -> "掃描中..."
                    else -> "掃描裝置"
                }
            )
        }
    }
}
