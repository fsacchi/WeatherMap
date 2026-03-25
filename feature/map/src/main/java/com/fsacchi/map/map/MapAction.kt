package com.fsacchi.map.map

import com.fsacchi.domain.model.GeocodingResult

sealed class MapAction {
    data class LoadWeatherByLocation(val lat: Double, val lon: Double) : MapAction()
    data class SearchCity(val query: String) : MapAction()
    object RecenterOnGps : MapAction()
    data class SelectSearchResult(val result: GeocodingResult) : MapAction()
    object NavigateToHistory : MapAction()
    data class TapOnMap(val lat: Double, val lon: Double) : MapAction()
    object NavigateToDetail : MapAction()
    object DismissError : MapAction()
}
