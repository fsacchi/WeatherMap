package com.fsacchi.data.di

import com.fsacchi.data.repositoy.HistoryRepositoryImpl
import com.fsacchi.data.repositoy.WeatherRepositoryImpl
import com.fsacchi.domain.repository.HistoryRepository
import com.fsacchi.domain.repository.WeatherRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
}
