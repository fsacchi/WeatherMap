package com.fsacchi.data.repositoy

import com.fsacchi.data.util.safeRun
import com.fsacchi.domain.model.ForecastItem
import com.fsacchi.domain.model.GeocodingResult
import com.fsacchi.domain.model.WeatherData
import com.fsacchi.domain.repository.WeatherRepository
import com.fsacchi.domain.util.Result
import com.fsacchi.network.api.GeocodingApiService
import com.fsacchi.network.api.WeatherApiService
import com.fsacchi.network.dto.WeatherResponseDto

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApiService,
    private val geocodingApi: GeocodingApiService
) : WeatherRepository {

    override suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherData> {
        return safeRun { weatherApi.getWeatherByLocation(lat, lon).toDomain() }
    }

    override suspend fun getWeatherByCity(cityName: String): Result<WeatherData> {
        return safeRun { weatherApi.getWeatherByCity(cityName).toDomain() }
    }

    override suspend fun getForecast(lat: Double, lon: Double): Result<List<ForecastItem>> {
        return safeRun {
            weatherApi.getForecast(lat, lon).list.map { item ->
                ForecastItem(
                    timestamp = item.dt,
                    temperature = item.main.temp,
                    description = item.weather.firstOrNull()?.description ?: "",
                    iconCode = item.weather.firstOrNull()?.icon ?: "",
                    precipitationProbability = item.pop
                )
            }
        }
    }

    override suspend fun searchCity(query: String): Result<List<GeocodingResult>> {
        return safeRun {
            geocodingApi.searchCity(query).map { dto ->
                GeocodingResult(
                    name = dto.name,
                    country = dto.country,
                    lat = dto.lat,
                    lon = dto.lon
                )
            }
        }
    }

    private fun WeatherResponseDto.toDomain() = WeatherData(
        cityName = name,
        country = sys.country,
        temperature = main.temp,
        feelsLike = main.feelsLike,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        description = weather.firstOrNull()?.description ?: "",
        iconCode = weather.firstOrNull()?.icon ?: "",
        lat = coord.lat,
        lon = coord.lon
    )
}