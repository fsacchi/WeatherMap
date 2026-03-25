package com.fsacchi.map.history

import com.fsacchi.domain.model.WeatherHistory

sealed class HistoryAction {
    object LoadHistory : HistoryAction()
    data class DeleteItem(val id: Long) : HistoryAction()
    data class SelectItem(val history: WeatherHistory) : HistoryAction()
    object NavigateBack : HistoryAction()
    object DismissError : HistoryAction()
    object DeleteAll : HistoryAction()

}
