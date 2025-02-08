package com.temperature.temperatur_sensor_sdk.component.bluetooth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.temperature.temperatur_sensor_sdk.R

/**
 * 定義藍芽裝置的列表，與顯示單個藍芽裝置的卡片元件
 */
@Composable
fun DeviceList(
    devices: List<BluetoothDeviceInfo>,
    onDeviceClick: (BluetoothDeviceInfo) -> Unit
) {
    LazyColumn {
        items(
            items = devices,
            key = { it.address }  // 使用裝置地址作為唯一識別
        ) { device ->
            DeviceItem(
                device = device,
                onClick = { onDeviceClick(device) }
            )
        }
    }
}

@Composable
fun DeviceItem(
    device: BluetoothDeviceInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (device.isConnected) {
                Icon(
                    painter = painterResource(R.drawable.ic_bluetooth_connected),
                    contentDescription = null,
                    tint = Color.Green
                )
            }
        }
    }
}
