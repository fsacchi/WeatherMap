package com.fsacchi.domain.model

data class ForecastItem(
    val timestamp: Long,
    val temperature: Double,
    val description: String,
    val iconCode: String,
    val precipitationProbability: Double = 0.0
)
