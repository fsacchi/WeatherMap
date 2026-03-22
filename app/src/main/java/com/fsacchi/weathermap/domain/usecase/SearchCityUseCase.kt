package com.fsacchi.weathermap.domain.usecase

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.model.GeocodingResult
import com.fsacchi.weathermap.domain.repository.WeatherRepository

class SearchCityUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(query: String): Result<List<GeocodingResult>> {
        return repository.searchCity(query)
    }
}
