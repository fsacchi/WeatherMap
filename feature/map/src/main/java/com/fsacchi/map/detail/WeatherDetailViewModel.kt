package com.fsacchi.map.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsacchi.domain.usecase.GetForecastUseCase
import com.fsacchi.domain.usecase.GetWeatherByLocationUseCase
import com.fsacchi.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherDetailViewModel(
    private val getWeatherByLocation: GetWeatherByLocationUseCase,
    private val getForecast: GetForecastUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherDetailUiState())
    val uiState: StateFlow<WeatherDetailUiState> = _uiState.asStateFlow()

    fun onAction(action: WeatherDetailAction) {
        when (action) {
            is WeatherDetailAction.LoadWeather -> loadWeather(action.lat, action.lon)
            is WeatherDetailAction.Share -> _uiState.update { it.copy(shareTriggered = true) }
            is WeatherDetailAction.ResetShare -> _uiState.update { it.copy(shareTriggered = false) }
            is WeatherDetailAction.DismissError -> _uiState.update { it.copy(error = null) }
            is WeatherDetailAction.ShowError -> showError(action.message)
            else -> {}
        }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    private fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val weatherResult = getWeatherByLocation(lat, lon)
            val forecastResult = getForecast(lat, lon)

            when (weatherResult) {
                is Result.Success -> {
                    val weather = weatherResult.data
                    _uiState.update { it.copy(weather = weather) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = weatherResult.message) }
                }
                is Result.Loading -> {}
            }

            when (forecastResult) {
                is Result.Success -> _uiState.update { it.copy(forecast = forecastResult.data) }
                else -> {}
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
