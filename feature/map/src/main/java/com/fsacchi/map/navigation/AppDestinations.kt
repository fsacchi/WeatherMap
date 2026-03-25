package com.fsacchi.map.navigation

sealed class AppDestinations(val route: String) {
    object Map : AppDestinations("map")
    object History : AppDestinations("history")
    object WeatherDetail : AppDestinations("weather_detail/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double) = "weather_detail/$lat/$lon"
    }
}
