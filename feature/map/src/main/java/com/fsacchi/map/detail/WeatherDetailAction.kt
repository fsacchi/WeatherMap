package com.fsacchi.map.detail

import android.os.Message

sealed class WeatherDetailAction {
    data class LoadWeather(val lat: Double, val lon: Double) : WeatherDetailAction()
    object NavigateBack : WeatherDetailAction()
    object Share : WeatherDetailAction()
    object ResetShare : WeatherDetailAction()
    object DismissError : WeatherDetailAction()
    data class ShowError(val message: String) : WeatherDetailAction()
}
