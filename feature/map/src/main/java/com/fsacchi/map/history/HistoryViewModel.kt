package com.fsacchi.map.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsacchi.domain.repository.HistoryRepository
import com.fsacchi.domain.usecase.GetWeatherHistoryUseCase
import com.fsacchi.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getHistory: GetWeatherHistoryUseCase,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun onAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.LoadHistory -> loadHistory()
            is HistoryAction.DeleteItem -> deleteItem(action.id)
            is HistoryAction.DismissError -> _uiState.update { it.copy(error = null) }
            is HistoryAction.DeleteAll -> deleteAll()
            else -> {}
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getHistory()) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, items = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun deleteItem(id: Long) {
        viewModelScope.launch {
            when (val result = historyRepository.deleteById(id)) {
                is Result.Success -> loadHistory()
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch {
            when (val result = historyRepository.clearAll()) {
                is Result.Success -> loadHistory()
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is Result.Loading -> {}
            }
        }
    }
}
