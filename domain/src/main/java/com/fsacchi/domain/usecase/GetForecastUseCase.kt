package com.fsacchi.domain.usecase

import com.fsacchi.domain.repository.WeatherRepository
import com.fsacchi.domain.model.ForecastItem
import com.fsacchi.domain.util.Result

class GetForecastUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<List<ForecastItem>> {
        return repository.getForecast(lat, lon)
    }
}
