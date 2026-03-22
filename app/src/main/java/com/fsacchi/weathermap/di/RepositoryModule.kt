package com.fsacchi.weathermap.di

import com.fsacchi.weathermap.data.repository.HistoryRepositoryImpl
import com.fsacchi.weathermap.data.repository.WeatherRepositoryImpl
import com.fsacchi.weathermap.domain.repository.HistoryRepository
import com.fsacchi.weathermap.domain.repository.WeatherRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<WeatherRepository> {
        WeatherRepositoryImpl(get(), get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }
}
