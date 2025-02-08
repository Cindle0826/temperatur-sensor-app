package com.temperature.temperatur_sensor_sdk

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.temperature.temperatur_sensor_sdk.convert.DateTimeConverter
import com.temperature.temperatur_sensor_sdk.dao.TemperatureDao
import com.temperature.temperatur_sensor_sdk.entity.TemperatureRecord
import java.time.LocalDateTime

@Database(entities = [TemperatureRecord::class], version = 1)
@TypeConverters(value = [DateTimeConverter::class])
abstract class TemperatureDatabase : RoomDatabase() {
    companion object {
        private var db: TemperatureDatabase? = null
        private val LOCK = Any()

        lateinit var dao: TemperatureDao
            private set

        operator fun invoke(context: Context): TemperatureDatabase = db ?: synchronized(LOCK) {
            db ?: buildDatabase(context).also { instance ->
                db = instance
                dao = instance.temperatureDao()
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, TemperatureDatabase::class.java, "Temperature-database")
//                .addCallback(object : Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        super.onCreate(db)
//                        // 當數據庫創建時插入多筆假資料
//                        CoroutineScope(Dispatchers.IO).launch {
//                            val dao = invoke(context).temperatureDao()
//                            val fakeData = generateFakeData()
//                            dao.insertTemperatureRecords(fakeData)
//                        }
//                    }
//                })
                .build()

    }

    abstract fun temperatureDao(): TemperatureDao
}

// create fake data
@Suppress("unused")
private fun generateFakeData(): List<TemperatureRecord> {
    val fakeData = mutableListOf<TemperatureRecord>()
    for (i in 1..30) {
        fakeData.add(
            TemperatureRecord(
                pm10 = (5..50).random().toDouble(), // 隨機 PM1.0 數值
                pm25 = (10..100).random().toDouble(), // 隨機 PM2.5 數值
                pm100 = (20..200).random().toDouble(), // 隨機 PM10 數值
                temperature = (15..35).random().toDouble(), // 隨機溫度
                humidity = (30..80).random().toDouble(), // 隨機濕度
                status = if (i % 2 == 0) "Normal" else "Warning", // 偶數為 Normal，奇數為 Warning
                createTime = LocalDateTime.now().plusDays(i.toLong()) // 加去 i 天的時間
            )
        )
    }
    return fakeData
}

class TemperatureApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TemperatureDatabase(this)
    }
}
