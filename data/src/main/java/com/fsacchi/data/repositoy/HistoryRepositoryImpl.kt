package com.fsacchi.data.repositoy

import com.fsacchi.data.util.safeRun
import com.fsacchi.database.dao.WeatherDao
import com.fsacchi.database.entity.WeatherHistoryEntity
import com.fsacchi.domain.model.WeatherData
import com.fsacchi.domain.model.WeatherHistory
import com.fsacchi.domain.repository.HistoryRepository
import com.fsacchi.domain.util.Result

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