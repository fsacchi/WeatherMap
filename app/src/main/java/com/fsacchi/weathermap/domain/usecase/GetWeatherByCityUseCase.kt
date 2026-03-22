package com.fsacchi.weathermap.domain.usecase

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.model.WeatherData
import com.fsacchi.weathermap.domain.repository.WeatherRepository

class GetWeatherByCityUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(cityName: String): Result<WeatherData> {
        return repository.getWeatherByCity(cityName)
    }
}
