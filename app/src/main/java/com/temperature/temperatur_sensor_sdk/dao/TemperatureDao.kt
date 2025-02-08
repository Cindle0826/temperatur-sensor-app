package com.temperature.temperatur_sensor_sdk.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.temperature.temperatur_sensor_sdk.entity.TemperatureRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TemperatureDao {
    @Query("SELECT * FROM temperature_records ORDER BY id DESC LIMIT 1")
    fun getLatestRecord(): Flow<TemperatureRecord?>

    // 添加刷新方法
    @Query("SELECT * FROM temperature_records WHERE 1=1 LIMIT 1")
    suspend fun refreshLatestRecord(): TemperatureRecord?

    @Query("SELECT * FROM temperature_records WHERE createTime BETWEEN :startDateTime AND :endDateTime ORDER BY createTime DESC LIMIT :limit")
    fun getLatestPMRecords(startDateTime: String, endDateTime: String, limit: Int): Flow<List<TemperatureRecord>>

    @Query("SELECT * FROM temperature_records ORDER BY createTime DESC LIMIT :pageSize OFFSET :offset")
    fun getPagedRecords(pageSize: Int, offset: Int): Flow<List<TemperatureRecord>>

    @Insert
    suspend fun insertTemperatureRecord(temperatureRecord: TemperatureRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperatureRecords(records: List<TemperatureRecord>)

    @Query("""
       DELETE FROM temperature_records 
       WHERE id IN (
            SELECT id FROM temperature_records 
            WHERE createTime BETWEEN :startDateTime AND :endDateTime
            ORDER BY createTime ASC
            LIMIT :limit
        )
    """)
    suspend fun deleteTemperatureRecord(startDateTime: LocalDateTime, endDateTime: LocalDateTime, limit: Int): Int
}