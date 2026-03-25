package com.fsacchi.map.di

import com.fsacchi.map.detail.WeatherDetailViewModel
import com.fsacchi.map.history.HistoryViewModel
import com.fsacchi.map.map.MapViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mapModule = module {
    viewModel { MapViewModel(get(), get(), get(), get()) }
    viewModel { WeatherDetailViewModel(get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
}
