package com.fsacchi.domain.usecase

import com.fsacchi.domain.repository.HistoryRepository
import com.fsacchi.domain.model.WeatherHistory
import com.fsacchi.domain.util.Result

class GetWeatherHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(): Result<List<WeatherHistory>> {
        return repository.getHistory()
    }
}
