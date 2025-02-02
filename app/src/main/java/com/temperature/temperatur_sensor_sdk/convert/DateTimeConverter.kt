package com.temperature.temperatur_sensor_sdk.convert

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeConverter {

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? {
        return dateString?.let {
            try {
                LocalDateTime.parse(it, FORMATTER)
            } catch (e: Exception) {
                null
            }
        }
    }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(FORMATTER)
    }
}