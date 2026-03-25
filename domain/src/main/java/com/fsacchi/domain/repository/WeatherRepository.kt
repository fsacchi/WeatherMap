package com.fsacchi.domain.repository

import com.fsacchi.domain.model.ForecastItem
import com.fsacchi.domain.model.GeocodingResult
import com.fsacchi.domain.model.WeatherData
import com.fsacchi.domain.util.Result

interface WeatherRepository {
    suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherData>
    suspend fun getWeatherByCity(cityName: String): Result<WeatherData>
    suspend fun getForecast(lat: Double, lon: Double): Result<List<ForecastItem>>
    suspend fun searchCity(query: String): Result<List<GeocodingResult>>
}
