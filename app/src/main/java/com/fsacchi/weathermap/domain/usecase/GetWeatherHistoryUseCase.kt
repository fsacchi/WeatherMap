package com.fsacchi.weathermap.domain.usecase

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.model.WeatherHistory
import com.fsacchi.weathermap.domain.repository.HistoryRepository

class GetWeatherHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(): Result<List<WeatherHistory>> {
        return repository.getHistory()
    }
}
