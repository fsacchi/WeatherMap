package com.fsacchi.weathermap.util

import com.fsacchi.weathermap.domain.model.ForecastItem
import com.fsacchi.weathermap.domain.model.GeocodingResult
import com.fsacchi.weathermap.domain.model.WeatherData
import com.fsacchi.weathermap.domain.model.WeatherHistory

object TestFixtures {

    fun weatherData(
        cityName: String = "São Paulo",
        country: String = "BR",
        temperature: Double = 25.0,
        feelsLike: Double = 24.0,
        tempMin: Double = 20.0,
        tempMax: Double = 30.0,
        humidity: Int = 70,
        pressure: Int = 1013,
        windSpeed: Double = 5.0,
        sunrise: Long = 1_000_000L,
        sunset: Long = 2_000_000L,
        description: String = "clear sky",
        iconCode: String = "01d",
        lat: Double = -23.5,
        lon: Double = -46.6
    ) = WeatherData(
        cityName = cityName,
        country = country,
        temperature = temperature,
        feelsLike = feelsLike,
        tempMin = tempMin,
        tempMax = tempMax,
        humidity = humidity,
        pressure = pressure,
        windSpeed = windSpeed,
        sunrise = sunrise,
        sunset = sunset,
        description = description,
        iconCode = iconCode,
        lat = lat,
        lon = lon
    )

    fun forecastItem(
        timestamp: Long = 1_000_000L,
        temperature: Double = 22.0,
        description: String = "sunny",
        iconCode: String = "01d",
        precipitationProbability: Double = 0.0
    ) = ForecastItem(
        timestamp = timestamp,
        temperature = temperature,
        description = description,
        iconCode = iconCode,
        precipitationProbability = precipitationProbability
    )

    fun forecastList(size: Int = 3): List<ForecastItem> =
        (1..size).map { i -> forecastItem(timestamp = 1_000_000L * i) }

    fun weatherHistory(
        id: Long = 1L,
        cityName: String = "São Paulo",
        country: String = "BR",
        temperature: Double = 25.0,
        iconCode: String = "01d",
        lat: Double = -23.5,
        lon: Double = -46.6,
        timestamp: Long = 1_700_000_000_000L
    ) = WeatherHistory(
        id = id,
        cityName = cityName,
        country = country,
        temperature = temperature,
        iconCode = iconCode,
        lat = lat,
        lon = lon,
        timestamp = timestamp
    )

    fun geocodingResult(
        name: String = "São Paulo",
        country: String = "BR",
        lat: Double = -23.5,
        lon: Double = -46.6
    ) = GeocodingResult(
        name = name,
        country = country,
        lat = lat,
        lon = lon
    )
}
