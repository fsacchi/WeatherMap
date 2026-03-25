package com.fsacchi.domain.model

data class WeatherHistory(
    val id: Long = 0,
    val cityName: String,
    val country: String,
    val temperature: Double,
    val iconCode: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long
)
