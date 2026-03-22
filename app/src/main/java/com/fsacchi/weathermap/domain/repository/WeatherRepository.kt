package com.fsacchi.weathermap.domain.repository

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.model.ForecastItem
import com.fsacchi.weathermap.domain.model.GeocodingResult
import com.fsacchi.weathermap.domain.model.WeatherData

interface WeatherRepository {
    suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherData>
    suspend fun getWeatherByCity(cityName: String): Result<WeatherData>
    suspend fun getForecast(lat: Double, lon: Double): Result<List<ForecastItem>>
    suspend fun searchCity(query: String): Result<List<GeocodingResult>>
}
