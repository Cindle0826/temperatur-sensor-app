package com.temperature.temperatur_sensor_sdk.screen

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
fun LinePmHistoryChartScreen() {

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
            // 標題
            Text(
                text = "PM 值歷史紀錄",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 折線圖
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                factory = { context ->
                    LineChart(context).apply {
                        // 基本設定
                        description.isEnabled = false
                        setTouchEnabled(true)
                        isDragEnabled = true
                        setScaleEnabled(true)
                        setPinchZoom(true)

                        // X軸設定
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true)
                            granularity = 1f
                        }

                        // Y軸設定
                        axisLeft.apply {
                            setDrawGridLines(true)
                            axisMinimum = 0f
                        }
                        axisRight.isEnabled = false
                    }
                },
                update = { chart ->
                    chart.data = createLineData(records)
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

private fun createLineData(records: List<TemperatureRecord>): LineData {
    // PM1.0 數據
    val pm1Entries = records.mapIndexed { index, record ->
        Entry(index.toFloat(), record.pm10.toFloat())
    }

    // PM2.5 數據
    val pm25Entries = records.mapIndexed { index, record ->
        Entry(index.toFloat(), record.pm25.toFloat())
    }

    // PM10 數據
    val pm10Entries = records.mapIndexed { index, record ->
        Entry(index.toFloat(), record.pm100.toFloat())
    }

    // 建立數據集
    val modernBlue = Color.parseColor("#4361EE")    // 靛藍色
    val modernGreen = Color.parseColor("#2EC4B6")   // 青綠色
    val modernRed = Color.parseColor("#FF6B6B")     // 珊瑚紅

    // 在 createLineData() 函數中更新顏色設定
    val pm1DataSet = LineDataSet(pm1Entries, "PM1.0").apply {
        setDrawFilled(true)
        fillAlpha = 30
        fillColor = modernBlue
        color = modernBlue
        setCircleColor(modernBlue)
        lineWidth = 2f
        circleRadius = 4f
        setDrawCircleHole(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER  // 添加曲線平滑效果
        valueTextSize = 10f
        enableDashedLine(10f, 5f, 0f)  // 虛線效果
    }

    val pm25DataSet = LineDataSet(pm25Entries, "PM2.5").apply {
        setDrawFilled(true)
        fillAlpha = 30
        fillColor = modernGreen
        color = modernGreen
        setCircleColor(modernGreen)
        lineWidth = 2f
        circleRadius = 4f
        setDrawCircleHole(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        valueTextSize = 10f
        enableDashedLine(10f, 5f, 0f)
    }

    val pm10DataSet = LineDataSet(pm10Entries, "PM10").apply {
        setDrawFilled(true)
        fillAlpha = 30
        fillColor = modernRed
        color = modernRed
        setCircleColor(modernRed)
        lineWidth = 2f
        circleRadius = 4f
        setDrawCircleHole(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        valueTextSize = 10f
        enableDashedLine(10f, 5f, 0f)
    }

    return LineData(pm1DataSet, pm25DataSet, pm10DataSet)
}