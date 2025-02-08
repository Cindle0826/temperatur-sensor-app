package com.temperature.temperatur_sensor_sdk.screen

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.temperature.temperatur_sensor_sdk.component.ScanningProgress
import com.temperature.temperatur_sensor_sdk.component.bluetooth.BluetoothHeader
import com.temperature.temperatur_sensor_sdk.component.bluetooth.BluetoothState
import com.temperature.temperatur_sensor_sdk.component.bluetooth.BluetoothViewModelSingleton
import com.temperature.temperatur_sensor_sdk.component.bluetooth.DeviceList
import com.temperature.temperatur_sensor_sdk.util.BluetoothUtil.checkBluetoothPermissions

@Composable
fun BluetoothScreen() {

    val context = LocalContext.current
    val viewModel = remember(context) {
        BluetoothViewModelSingleton.getInstance(context.applicationContext as Application)
    }

    val state by viewModel.state.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }

    // 記錄權限請求的結果
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 在這裡處理權限請求的結果
        val allPermissionsGranted = permissions.all { it.value }
        if (allPermissionsGranted) {
            viewModel.updateBluetoothState(BluetoothState.Ready)
        } else {
            viewModel.updateBluetoothState(BluetoothState.Disabled)
        }
    }

    // 在畫面載入時檢查權限
    LaunchedEffect(Unit) {
        if (!checkBluetoothPermissions(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        } else {
            viewModel.updateBluetoothState(BluetoothState.Ready)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BluetoothHeader(
            state = state,
            onScanClick = {
                if (checkBluetoothPermissions(context)) {
                    viewModel.startScan()
                } else {
                    requestBluetoothPermissions(context)
                    showPermissionDialog = true
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DeviceList(
            devices = devices,
            onDeviceClick = { device -> viewModel.connectDevice(device) }
        )
    }

    if (isScanning) {
        ScanningProgress("正在掃描裝置... 請稍後")
    }
}

private fun requestBluetoothPermissions(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            100
        )
    }
}


