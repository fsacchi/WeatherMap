package com.fsacchi.domain.usecase

import com.fsacchi.domain.repository.WeatherRepository
import com.fsacchi.domain.model.WeatherData
import com.fsacchi.domain.util.Result

class GetWeatherByCityUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(cityName: String): Result<WeatherData> {
        return repository.getWeatherByCity(cityName)
    }
}
