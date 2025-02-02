package com.temperature.temperatur_sensor_sdk.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.temperature.temperatur_sensor_sdk.component.BluetoothDeviceList
import com.temperature.temperatur_sensor_sdk.component.BluetoothState
import com.temperature.temperatur_sensor_sdk.component.BluetoothStatusIcon
import com.temperature.temperatur_sensor_sdk.component.BluetoothViewModel

@Composable
fun BluetoothScreen(
    viewModel: BluetoothViewModel = BluetoothViewModel()
) {
    val state by viewModel.state.collectAsState()
    val devices by viewModel.devices.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 頂部狀態列
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (state) {
                    is BluetoothState.Disabled -> "藍芽已關閉"
                    is BluetoothState.Searching -> "正在搜尋..."
                    is BluetoothState.Connected -> "已連接"
                    is BluetoothState.Ready -> "藍芽已開啟"
                },
                style = MaterialTheme.typography.titleLarge
            )

            BluetoothStatusIcon(
                state = state,
                onToggle = { viewModel.toggleBluetooth() }
            )
        }

        // 設備列表
        if (state !is BluetoothState.Disabled) {
            BluetoothDeviceList(
                devices = devices,
                onDeviceClick = { device -> viewModel.connectDevice(device) }
            )
        }
    }
}
