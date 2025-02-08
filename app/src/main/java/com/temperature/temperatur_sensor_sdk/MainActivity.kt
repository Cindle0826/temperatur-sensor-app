package com.temperature.temperatur_sensor_sdk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.temperature.temperatur_sensor_sdk.screen.MainScreen
import com.temperature.temperatur_sensor_sdk.ui.theme.TemperatursensorsdkTheme
import com.temperature.temperatur_sensor_sdk.util.BluetoothUtil

class MainActivity : ComponentActivity() {
    private lateinit var bluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>

    companion object {
        private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        bluetoothPermissionLauncher = BluetoothUtil.setupBluetoothPermissionLauncher(this) { granted ->
            if (granted) {
                Log.i("MainActivity", "Bluetooth permission granted")
            }
        }
        permissionLauncher = bluetoothPermissionLauncher

        setContent {
            TemperatursensorsdkTheme {
                MainScreen()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TemperatursensorsdkTheme {
        MainScreen()
    }
}
