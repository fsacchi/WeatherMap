package com.fsacchi.network.api

import com.fsacchi.network.dto.ForecastResponseDto
import com.fsacchi.network.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): WeatherResponseDto

    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): WeatherResponseDto

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br",
        @Query("cnt") count: Int = 16
    ): ForecastResponseDto
}
