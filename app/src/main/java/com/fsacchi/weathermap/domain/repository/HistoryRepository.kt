package com.fsacchi.weathermap.domain.repository

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.model.WeatherData
import com.fsacchi.weathermap.domain.model.WeatherHistory

interface HistoryRepository {
    suspend fun saveWeather(weather: WeatherData): Result<Unit>
    suspend fun getHistory(): Result<List<WeatherHistory>>
    suspend fun deleteById(id: Long): Result<Unit>
    suspend fun clearAll(): Result<Unit>
}
