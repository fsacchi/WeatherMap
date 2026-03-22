package com.fsacchi.weathermap.presentation.map

import com.fsacchi.weathermap.domain.model.GeocodingResult
import com.fsacchi.weathermap.domain.model.WeatherData

data class MapUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLat: Double? = null,
    val currentLon: Double? = null,
    val pinWeatherData: WeatherData? = null,
    val searchResults: List<GeocodingResult> = emptyList(),
    val searchQuery: String = "",
    val isSearching: Boolean = false
)
