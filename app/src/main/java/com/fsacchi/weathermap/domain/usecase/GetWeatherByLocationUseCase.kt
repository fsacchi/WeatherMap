package com.fsacchi.weathermap.domain.usecase

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.model.WeatherData
import com.fsacchi.weathermap.domain.repository.WeatherRepository

class GetWeatherByLocationUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<WeatherData> {
        return repository.getWeatherByLocation(lat, lon)
    }
}
