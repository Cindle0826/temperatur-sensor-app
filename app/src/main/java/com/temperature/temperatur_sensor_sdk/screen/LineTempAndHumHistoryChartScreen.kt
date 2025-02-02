package com.temperature.temperatur_sensor_sdk.screen

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.temperature.temperatur_sensor_sdk.TemperatureDatabase
import com.temperature.temperatur_sensor_sdk.component.date.TimeAndCountSelector
import com.temperature.temperatur_sensor_sdk.convert.DateTimeConverter
import com.temperature.temperatur_sensor_sdk.entity.TemperatureRecord
import java.time.LocalDateTime

@Composable
fun LineTempAndHumHistoryChartScreen() {

    val dao = TemperatureDatabase.dao

    val dateTimeConverter = DateTimeConverter()

    var startDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var endDateTime by remember { mutableStateOf(LocalDateTime.now().plusDays(10)) }
    var selectedCount by remember { mutableStateOf("10") }

    val records by dao.getLatestPMRecords(
        limit = selectedCount.toIntOrNull() ?: 10,
        startDateTime = dateTimeConverter.fromLocalDateTime(startDateTime) ?: "",
        endDateTime = dateTimeConverter.fromLocalDateTime(endDateTime) ?: "",
    ).collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "溫度和濕度歷史紀錄",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                factory = { context ->
                    LineChart(context).apply {
                        description.isEnabled = false
                        setTouchEnabled(true)
                        isDragEnabled = true
                        setScaleEnabled(true)
                        setPinchZoom(true)

                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true)
                            granularity = 1f
                        }

                        axisLeft.apply {
                            setDrawGridLines(true)
                            axisMinimum = 0f
                        }
                        axisRight.isEnabled = false
                    }
                },
                update = { chart ->
                    chart.data = createTempHumLineData(records)
                    chart.invalidate()
                }
            )

            TimeAndCountSelector(
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                selectedCount = selectedCount,
                onCountSelected = { selectedCount = it },
                onStartDateTimeChanged = { startDateTime = it },
                onEndDateTimeChanged = { endDateTime = it },
                onQueryClicked = null
            )
        }
    }
}

private fun createTempHumLineData(records: List<TemperatureRecord>): LineData {
    // 溫度數據
    val temperatureEntries = records.mapIndexed { index, record ->
        Entry(index.toFloat(), record.temperature.toFloat())
    }

    // 濕度數據
    val humidityEntries = records.mapIndexed { index, record ->
        Entry(index.toFloat(), record.humidity.toFloat())
    }

    // 使用更溫暖的色調
    val warmOrange = Color.parseColor("#FF9F1C")    // 溫暖的橘色
    val coolPurple = Color.parseColor("#7209B7")    // 清爽的紫色

    val temperatureDataSet = LineDataSet(temperatureEntries, "Temperature (°C)").apply {
        setDrawFilled(true)
        fillAlpha = 30
        fillColor = warmOrange
        color = warmOrange
        setCircleColor(warmOrange)
        lineWidth = 2f
        circleRadius = 4f
        setDrawCircleHole(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        valueTextSize = 10f
    }

    val humidityDataSet = LineDataSet(humidityEntries, "Humidity (%)").apply {
        setDrawFilled(true)
        fillAlpha = 30
        fillColor = coolPurple
        color = coolPurple
        setCircleColor(coolPurple)
        lineWidth = 2f
        circleRadius = 4f
        setDrawCircleHole(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        valueTextSize = 10f
    }

    return LineData(temperatureDataSet, humidityDataSet)
}
