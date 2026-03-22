package com.fsacchi.weathermap.presentation.history

import com.fsacchi.weathermap.domain.model.WeatherHistory

sealed class HistoryAction {
    object LoadHistory : HistoryAction()
    data class DeleteItem(val id: Long) : HistoryAction()
    data class SelectItem(val history: WeatherHistory) : HistoryAction()
    object NavigateBack : HistoryAction()
    object DismissError : HistoryAction()
    object DeleteAll : HistoryAction()

}
