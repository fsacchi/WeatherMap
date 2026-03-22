package com.fsacchi.weathermap.domain.usecase

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.model.WeatherData
import com.fsacchi.weathermap.domain.repository.HistoryRepository

class SaveWeatherHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(weather: WeatherData): Result<Unit> {
        return repository.saveWeather(weather)
    }
}
