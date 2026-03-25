package com.fsacchi.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("name") val name: String = "",
    @SerialName("sys") val sys: SysDto = SysDto(),
    @SerialName("main") val main: MainDto = MainDto(),
    @SerialName("weather") val weather: List<WeatherDto> = emptyList(),
    @SerialName("wind") val wind: WindDto = WindDto(),
    @SerialName("coord") val coord: CoordDto = CoordDto()
)

@Serializable
data class SysDto(
    @SerialName("country") val country: String = "",
    @SerialName("sunrise") val sunrise: Long = 0,
    @SerialName("sunset") val sunset: Long = 0
)

@Serializable
data class MainDto(
    @SerialName("temp") val temp: Double = 0.0,
    @SerialName("feels_like") val feelsLike: Double = 0.0,
    @SerialName("temp_min") val tempMin: Double = 0.0,
    @SerialName("temp_max") val tempMax: Double = 0.0,
    @SerialName("humidity") val humidity: Int = 0,
    @SerialName("pressure") val pressure: Int = 0
)

@Serializable
data class WeatherDto(
    @SerialName("description") val description: String = "",
    @SerialName("icon") val icon: String = ""
)

@Serializable
data class WindDto(
    @SerialName("speed") val speed: Double = 0.0
)

@Serializable
data class CoordDto(
    @SerialName("lat") val lat: Double = 0.0,
    @SerialName("lon") val lon: Double = 0.0
)
