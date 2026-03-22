package com.fsacchi.weathermap.util

import com.fsacchi.weathermap.domain.model.ForecastItem
import com.fsacchi.weathermap.domain.model.WeatherData

object TestInstrumentedFixtures {

    fun defaultWeather() = WeatherData(
        cityName = "São Paulo",
        country = "BR",
        temperature = 25.0,
        feelsLike = 24.0,
        tempMin = 20.0,
        tempMax = 30.0,
        humidity = 70,
        pressure = 1013,
        windSpeed = 5.0,
        sunrise = 1_000_000L,
        sunset = 2_000_000L,
        description = "clear sky",
        iconCode = "01d",
        lat = -23.5,
        lon = -46.6
    )

    fun defaultForecast() = listOf(
        ForecastItem(1_000_000L, 22.0, "sunny", "01d", 0.0),
        ForecastItem(2_000_000L, 20.0, "cloudy", "04d", 0.3),
        ForecastItem(3_000_000L, 18.0, "rain", "10d", 0.8)
    )
}