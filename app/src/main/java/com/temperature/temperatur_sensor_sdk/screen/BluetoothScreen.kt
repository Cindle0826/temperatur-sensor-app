package com.temperature.temperatur_sensor_sdk.screen

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.temperature.temperatur_sensor_sdk.component.CommonDialog
import com.temperature.temperatur_sensor_sdk.component.bluetooth.BluetoothHeader
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


    // 在畫面載入時檢查權限
    LaunchedEffect(Unit) {
        if (!checkBluetoothPermissions(context)) {
            requestBluetoothPermissions(context)
            showPermissionDialog = true
        }
    }

    // 權限對話框
    if (showPermissionDialog) {
        CommonDialog(
            showDialog = true,
            onDismiss = { showPermissionDialog = false },
            title = "需要藍牙權限",
            content = "請允許藍牙權限以掃描和連接設備",
            confirmText = "設定",
            cancelText = "取消",
            onConfirm = {
                showPermissionDialog = false
                try {
                    // 如果用戶拒絕權限，引導他們到設置頁面
                    if (!checkBluetoothPermissions(context)) {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(this)
                        }
                    } else {
                        // 如果已經有權限，直接請求
                        requestBluetoothPermissions(context)
                    }
                } catch (e: Exception) {
                    Log.e("BluetoothScreen", "無法開啟設定頁面: ${e.message}")
                }
            }
        )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color.White,
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "正在掃描裝置...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "請稍候",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
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


