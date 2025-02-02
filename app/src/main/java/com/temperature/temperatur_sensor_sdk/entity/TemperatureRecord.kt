package com.temperature.temperatur_sensor_sdk.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "temperature_records")
data class TemperatureRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "pm1_0")
    val pm10: Double,
    @ColumnInfo(name = "pm2_5")
    val pm25: Double,
    @ColumnInfo(name = "pm10_0")
    val pm100: Double,
    @ColumnInfo(name = "temperature")
    val temperature: Double,
    @ColumnInfo(name = "humidity")
    val humidity: Double,
    @ColumnInfo(name = "status")
    val status: String,
    val createTime: LocalDateTime
) {
    constructor(
        pm10: Double,
        pm25: Double,
        pm100: Double,
        temperature: Double,
        humidity: Double,
        status: String,
        createTime: LocalDateTime
    ) :
    this(0, pm10, pm25, pm100, temperature, humidity, status, createTime)
}
