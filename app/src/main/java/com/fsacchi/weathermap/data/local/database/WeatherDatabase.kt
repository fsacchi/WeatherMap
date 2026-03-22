package com.fsacchi.weathermap.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fsacchi.weathermap.data.local.dao.WeatherDao
import com.fsacchi.weathermap.data.local.entity.WeatherHistoryEntity

@Database(
    entities = [WeatherHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
