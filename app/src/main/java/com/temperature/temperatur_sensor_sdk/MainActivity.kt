package com.temperature.temperatur_sensor_sdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.temperature.temperatur_sensor_sdk.screen.MainScreen
import com.temperature.temperatur_sensor_sdk.ui.theme.TemperatursensorsdkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
