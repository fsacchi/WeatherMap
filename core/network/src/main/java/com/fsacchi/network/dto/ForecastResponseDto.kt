package com.fsacchi.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDto(
    @SerialName("list") val list: List<ForecastItemDto> = emptyList()
)

@Serializable
data class ForecastItemDto(
    @SerialName("dt") val dt: Long = 0,
    @SerialName("main") val main: MainDto = MainDto(),
    @SerialName("weather") val weather: List<WeatherDto> = emptyList(),
    @SerialName("pop") val pop: Double = 0.0
)
