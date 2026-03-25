package com.fsacchi.map.map

import com.fsacchi.domain.model.GeocodingResult
import com.fsacchi.domain.model.WeatherData

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
