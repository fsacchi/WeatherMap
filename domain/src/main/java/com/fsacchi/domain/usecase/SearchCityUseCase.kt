package com.fsacchi.domain.usecase

import com.fsacchi.domain.repository.WeatherRepository
import com.fsacchi.domain.model.GeocodingResult
import com.fsacchi.domain.util.Result

class SearchCityUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(query: String): Result<List<GeocodingResult>> {
        return repository.searchCity(query)
    }
}
