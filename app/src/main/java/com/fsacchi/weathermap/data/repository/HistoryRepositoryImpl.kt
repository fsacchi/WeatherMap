package com.fsacchi.weathermap.data.repository

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.core.util.safeRun
import com.fsacchi.weathermap.data.local.dao.WeatherDao
import com.fsacchi.weathermap.data.local.entity.WeatherHistoryEntity
import com.fsacchi.weathermap.domain.model.WeatherData
import com.fsacchi.weathermap.domain.model.WeatherHistory
import com.fsacchi.weathermap.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val dao: WeatherDao
) : HistoryRepository {

    override suspend fun saveWeather(weather: WeatherData): Result<Unit> {
        return safeRun {
            dao.insert(
                WeatherHistoryEntity(
                    cityName = weather.cityName,
                    country = weather.country,
                    temperature = weather.temperature,
                    iconCode = weather.iconCode,
                    lat = weather.lat,
                    lon = weather.lon,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun getHistory(): Result<List<WeatherHistory>> {
        return safeRun {

            dao.getAll().map { entity ->
                WeatherHistory(
                    id = entity.id,
                    cityName = entity.cityName,
                    country = entity.country,
                    temperature = entity.temperature,
                    iconCode = entity.iconCode,
                    lat = entity.lat,
                    lon = entity.lon,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun deleteById(id: Long): Result<Unit> {
        return safeRun { dao.deleteById(id) }
    }

    override suspend fun clearAll(): Result<Unit> {
        return safeRun { dao.deleteAll() }
    }
}
