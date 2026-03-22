package com.fsacchi.weathermap.presentation.history

import com.fsacchi.weathermap.domain.model.WeatherHistory

data class HistoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<WeatherHistory> = emptyList()
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty()
}
