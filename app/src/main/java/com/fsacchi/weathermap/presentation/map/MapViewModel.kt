package com.fsacchi.weathermap.presentation.map

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.model.GeocodingResult
import com.fsacchi.weathermap.domain.usecase.GetWeatherByLocationUseCase
import com.fsacchi.weathermap.domain.usecase.SaveWeatherHistoryUseCase
import com.fsacchi.weathermap.domain.usecase.SearchCityUseCase
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    application: Application,
    private val getWeatherByLocation: GetWeatherByLocationUseCase,
    private val searchCity: SearchCityUseCase,
    private val saveWeatherHistory: SaveWeatherHistoryUseCase
) : AndroidViewModel(application) {

    private companion object {
        const val LAT_DEFAULT_SAO_PAULO = -23.5
        const val LONG_DEFAULT_SAO_PAULO = -46.6
        const val MIN_SIZE_SEARCH = 3
    }

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private var searchJob: Job? = null

    fun onAction(action: MapAction) {
        when (action) {
            is MapAction.LoadWeatherByLocation -> loadWeather(action.lat, action.lon)
            is MapAction.SearchCity -> onSearchQuery(action.query)
            is MapAction.RecenterOnGps -> recenterOnGps()
            is MapAction.SelectSearchResult -> selectResult(action.result)
            is MapAction.TapOnMap -> loadWeather(action.lat, action.lon)
            is MapAction.DismissError -> _uiState.update { it.copy(error = null) }
            else -> {}
        }
    }

    private fun loadWeather(lat: Double, lon: Double, bySearchCity: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getWeatherByLocation(lat, lon)) {
                is Result.Success -> {
                    if (bySearchCity) {
                        saveWeatherHistory.invoke(result.data)
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pinWeatherData = result.data,
                            currentLat = lat,
                            currentLon = lon
                        )
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun onSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length < MIN_SIZE_SEARCH) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            _uiState.update { it.copy(isSearching = true) }
            when (val result = searchCity(query)) {
                is Result.Success -> _uiState.update {
                    it.copy(isSearching = false, searchResults = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isSearching = false, searchResults = emptyList())
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun selectResult(result: GeocodingResult) {
        _uiState.update {
            it.copy(
                searchResults = emptyList(),
                searchQuery = result.name
            )
        }
        loadWeather(result.lat, result.lon, bySearchCity = true)
    }

    @SuppressLint("MissingPermission")
    fun recenterOnGps() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                loadWeather(location.latitude, location.longitude)
            } else {
                loadWeather(LAT_DEFAULT_SAO_PAULO, LONG_DEFAULT_SAO_PAULO)
            }
        }
    }
}
