package com.temperature.temperatur_sensor_sdk.screen

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.temperature.temperatur_sensor_sdk.TemperatureDatabase
import com.temperature.temperatur_sensor_sdk.component.bluetooth.BluetoothState
import com.temperature.temperatur_sensor_sdk.component.bluetooth.BluetoothViewModelSingleton
import kotlinx.coroutines.delay

@Composable
fun HomeScreen() {

    val context = LocalContext.current
    val viewModel = remember(context) {
        BluetoothViewModelSingleton.getInstance(context.applicationContext as Application)
    }

    val dao = TemperatureDatabase.dao
    val latestRecord by dao.getLatestRecord().collectAsState(initial = null)
    val bluetoothState by viewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 標題
            Text(
                text = "空氣品質監測系統",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 數值顯示區域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // PM1.0 卡片
                DataCard(
                    title = "PM 1.0",
                    value = latestRecord?.pm10?.toString() ?: "-- ",
                    unit = "微克/立方米",
                    cardColor = CardColor.Purple
                )

                // PM2.5 卡片
                DataCard(
                    title = "PM2.5",
                    value = latestRecord?.pm25?.toString() ?: "-- ",
                    unit = "微克/立方米",
                    cardColor = CardColor.Blue
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // PM10 卡片
                DataCard(
                    title = "PM10",
                    value = latestRecord?.pm100?.toString() ?: "-- ",
                    unit = "微克/立方米",
                    cardColor = CardColor.Green
                )

                // 溫度卡片
                DataCard(
                    title = "溫度",
                    value = latestRecord?.temperature?.toString() ?: "-- ",
                    unit = "攝氏度",
                    cardColor = CardColor.Orange
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 濕度卡片
                DataCard(
                    title = "濕度",
                    value = latestRecord?.humidity?.toString() ?: "-- ",
                    unit = "%",
                    cardColor = CardColor.Red
                )

                // 地址卡片
                DataCard(
                    title = "狀態",
                    value = latestRecord?.status ?: "-- ",
                    unit = "",
                    cardColor = CardColor.Gray
                )
            }
        }
        // 藍牙狀態 - 放置在左下角
        Text(
            text = "藍牙狀態：${
                when (bluetoothState) {
                    BluetoothState.Connected -> "已連接"
                    BluetoothState.Disabled -> "未啟用"
                    BluetoothState.Ready -> "就緒"
                    BluetoothState.Scanning -> "掃描中"
                    else -> "未啟用"
                }
            }",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 16.dp)
        )
    }
}

enum class CardColor(val background: Color, val content: Color) {
    Purple(
        background = Color(0xFFEDE7F6),
        content = Color(0xFF673AB7)
    ),
    Blue(
        background = Color(0xFFE3F2FD),
        content = Color(0xFF2196F3)
    ),
    Green(
        background = Color(0xFFE8F5E9),
        content = Color(0xFF4CAF50)
    ),
    Orange(
        background = Color(0xFFFFF3E0),
        content = Color(0xFFFF9800)
    ),
    Red(
        background = Color(0xFFFFEBEE),
        content = Color(0xFFF44336)
    ),
    Gray(
        background = Color(0xFFF5F5F5),
        content = Color(0xFF9E9E9E)
    )
}

@Composable
private fun DataCard(
    title: String,
    value: String,
    unit: String,
    cardColor: CardColor,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor.background
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                color = cardColor.content
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                    color = cardColor.content
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                    color = cardColor.content
                )
            }
        }
    }
}
