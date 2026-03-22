package com.fsacchi.weathermap.di

import com.fsacchi.weathermap.presentation.detail.WeatherDetailViewModel
import com.fsacchi.weathermap.presentation.history.HistoryViewModel
import com.fsacchi.weathermap.presentation.map.MapViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MapViewModel(get(), get(), get(), get()) }
    viewModel { WeatherDetailViewModel(get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
}
