package com.fsacchi.map.detail

import com.fsacchi.domain.model.ForecastItem
import com.fsacchi.domain.model.WeatherData

data class WeatherDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val weather: WeatherData? = null,
    val forecast: List<ForecastItem> = emptyList(),
    val shareTriggered: Boolean = false
)
