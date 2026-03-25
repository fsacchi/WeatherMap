package com.fsacchi.domain.usecase

import com.fsacchi.domain.repository.WeatherRepository
import com.fsacchi.domain.model.WeatherData
import com.fsacchi.domain.util.Result

class GetWeatherByLocationUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<WeatherData> {
        return repository.getWeatherByLocation(lat, lon)
    }
}
