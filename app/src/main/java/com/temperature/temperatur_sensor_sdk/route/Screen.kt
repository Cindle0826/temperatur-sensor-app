package com.temperature.temperatur_sensor_sdk.route

import androidx.compose.runtime.Composable
import com.temperature.temperatur_sensor_sdk.screen.BluetoothScreen
import com.temperature.temperatur_sensor_sdk.screen.HomeScreen
import com.temperature.temperatur_sensor_sdk.screen.LineTempAndHumHistoryChartScreen
import com.temperature.temperatur_sensor_sdk.screen.LinePmHistoryChartScreen

sealed class Screen {
    abstract val route: String
    abstract val content: @Composable () -> Unit

    data object Home : Screen() {
        override val route = "Home"
        override val content: @Composable () -> Unit = { HomeScreen() }
    }

    data object LinePmHistory : Screen() {
        override val route = "Pm History"
        override val content: @Composable () -> Unit = { LinePmHistoryChartScreen() }
    }

    data object LineTempAndHumHistory : Screen() {
        override val route = "Temp and Hum History"
        override val content: @Composable () -> Unit = { LineTempAndHumHistoryChartScreen() }
    }

    data object Bluetooth : Screen() {
        override val route = "Bluetooth"
        override val content: @Composable () -> Unit = { BluetoothScreen() }
    }

    companion object {
        val screens by lazy {
            listOf(Home, LinePmHistory, LineTempAndHumHistory, Bluetooth)
        }
    }
}