package com.fsacchi.domain.repository

import com.fsacchi.domain.model.WeatherData
import com.fsacchi.domain.model.WeatherHistory
import com.fsacchi.domain.util.Result

interface HistoryRepository {
    suspend fun saveWeather(weather: WeatherData): Result<Unit>
    suspend fun getHistory(): Result<List<WeatherHistory>>
    suspend fun deleteById(id: Long): Result<Unit>
    suspend fun clearAll(): Result<Unit>
}
