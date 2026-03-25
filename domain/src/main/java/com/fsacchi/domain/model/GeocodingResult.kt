package com.fsacchi.domain.model

data class GeocodingResult(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)
