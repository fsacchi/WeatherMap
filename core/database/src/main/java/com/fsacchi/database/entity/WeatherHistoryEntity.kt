package com.fsacchi.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_history")
data class WeatherHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val country: String,
    val temperature: Double,
    val iconCode: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long
)
