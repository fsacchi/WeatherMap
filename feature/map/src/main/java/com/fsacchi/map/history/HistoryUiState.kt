package com.fsacchi.map.history

import com.fsacchi.domain.model.WeatherHistory

data class HistoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<WeatherHistory> = emptyList()
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty()
}
