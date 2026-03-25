package com.fsacchi.domain.di

import com.fsacchi.domain.usecase.GetForecastUseCase
import com.fsacchi.domain.usecase.GetWeatherByCityUseCase
import com.fsacchi.domain.usecase.GetWeatherByLocationUseCase
import com.fsacchi.domain.usecase.GetWeatherHistoryUseCase
import com.fsacchi.domain.usecase.SaveWeatherHistoryUseCase
import com.fsacchi.domain.usecase.SearchCityUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetWeatherByLocationUseCase(get()) }
    factory { GetWeatherByCityUseCase(get()) }
    factory { GetForecastUseCase(get()) }
    factory { SearchCityUseCase(get()) }
    factory { SaveWeatherHistoryUseCase(get()) }
    factory { GetWeatherHistoryUseCase(get()) }
}
