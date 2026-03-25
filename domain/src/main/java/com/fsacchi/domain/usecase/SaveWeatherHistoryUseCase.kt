package com.fsacchi.domain.usecase

import com.fsacchi.domain.repository.HistoryRepository
import com.fsacchi.domain.model.WeatherData
import com.fsacchi.domain.util.Result

class SaveWeatherHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(weather: WeatherData): Result<Unit> {
        return repository.saveWeather(weather)
    }
}
