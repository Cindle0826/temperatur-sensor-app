package com.temperature.temperatur_sensor_sdk.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temperature.temperatur_sensor_sdk.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BluetoothState {
    data object Disabled : BluetoothState()
    data object Searching : BluetoothState()
    data object Connected : BluetoothState()
    data object Ready : BluetoothState()
}

data class BluetoothDevice(
    val id: String,
    val name: String,
    val address: String,
    val isConnected: Boolean = false
)

class BluetoothViewModel : ViewModel() {
    private val _state = MutableStateFlow<BluetoothState>(BluetoothState.Disabled)
    val state: StateFlow<BluetoothState> = _state.asStateFlow()

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices.asStateFlow()

    fun toggleBluetooth() {
        when (_state.value) {
            is BluetoothState.Disabled -> startScanning()
            is BluetoothState.Ready -> _state.value = BluetoothState.Disabled
            else -> {} // 其他狀態不處理
        }
    }

    private fun startScanning() {
        viewModelScope.launch {
            _state.value = BluetoothState.Ready
            _state.value = BluetoothState.Searching
            // 模擬搜尋設備
            delay(3000)
            _state.value = BluetoothState.Ready
        }
    }

    fun connectDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            _state.value = BluetoothState.Connected
            // 更新設備連接狀態
            _devices.value = _devices.value.map {
                if (it.id == device.id) it.copy(isConnected = true) else it
            }
        }
    }
}

@Composable
fun BluetoothStatusIcon(
    state: BluetoothState,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val iconRes = when (state) {
        is BluetoothState.Disabled -> R.drawable.ic_bluetooth_disabled
        is BluetoothState.Searching -> R.drawable.ic_bluetooth_searching
        is BluetoothState.Connected -> R.drawable.ic_bluetooth_connected
        is BluetoothState.Ready -> R.drawable.ic_bluetooth
    }

    val rotation by animateFloatAsState(
        targetValue = if (state is BluetoothState.Searching) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Bluetooth Status",
            modifier = Modifier
                .size(24.dp)
                .rotate(if (state is BluetoothState.Searching) rotation else 0f),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun BluetoothDeviceList(
    devices: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    LazyColumn {
        items(devices) { device ->
            BluetoothDeviceItem(
                device = device,
                onClick = { onDeviceClick(device) }
            )
        }
    }
}

@Composable
fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (device.isConnected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bluetooth_connected),
                    contentDescription = "Connected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}




