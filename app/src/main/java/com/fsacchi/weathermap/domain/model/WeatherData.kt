package com.fsacchi.weathermap.domain.model

data class WeatherData(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val sunrise: Long,
    val sunset: Long,
    val description: String,
    val iconCode: String,
    val lat: Double,
    val lon: Double
)
