package com.temperature.temperatur_sensor_sdk.component.bluetooth

import android.bluetooth.BluetoothDevice

/**
 * 定義藍芽的狀態、與資料類別
 */
sealed class BluetoothState {
    data object Unsupported: BluetoothState()
    data object Ready: BluetoothState()
    data object Disabled : BluetoothState()
    data object Scanning : BluetoothState()
    data object Connected : BluetoothState()
}

data class BluetoothDeviceInfo(
    val device: BluetoothDevice,
    val address: String = getSafeAddress(device),
    val name: String = getSafeName(device),
    val isConnected: Boolean = false
) {
    companion object {
        private fun getSafeAddress(device: BluetoothDevice): String {
            return try {
                device.address
            } catch (e: SecurityException) {
                "未知地址"
            }
        }

        private fun getSafeName(device: BluetoothDevice): String {
            return try {
                device.name
            } catch (e: SecurityException) {
                "未知裝置"
            }
        }
    }
}

