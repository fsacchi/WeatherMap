package com.fsacchi.database.di

import androidx.room.Room
import com.fsacchi.database.database.WeatherDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            WeatherDatabase::class.java,
            "weather_database"
        ).build()
    }

    single {
        get<WeatherDatabase>().weatherDao()
    }
}
