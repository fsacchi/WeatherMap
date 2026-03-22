package com.fsacchi.weathermap.di

import com.fsacchi.weathermap.domain.usecase.GetForecastUseCase
import com.fsacchi.weathermap.domain.usecase.GetWeatherByCityUseCase
import com.fsacchi.weathermap.domain.usecase.GetWeatherByLocationUseCase
import com.fsacchi.weathermap.domain.usecase.GetWeatherHistoryUseCase
import com.fsacchi.weathermap.domain.usecase.SaveWeatherHistoryUseCase
import com.fsacchi.weathermap.domain.usecase.SearchCityUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetWeatherByLocationUseCase(get()) }
    factory { GetWeatherByCityUseCase(get()) }
    factory { GetForecastUseCase(get()) }
    factory { SearchCityUseCase(get()) }
    factory { SaveWeatherHistoryUseCase(get()) }
    factory { GetWeatherHistoryUseCase(get()) }
}
