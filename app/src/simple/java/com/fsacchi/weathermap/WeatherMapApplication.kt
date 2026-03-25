package com.fsacchi.weathermap

import android.app.Application
import com.fsacchi.data.di.repositoryModule
import com.fsacchi.database.di.databaseModule
import com.fsacchi.domain.di.useCaseModule
import com.fsacchi.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class WeatherMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@WeatherMapApplication)
            modules(networkModule, databaseModule, repositoryModule, useCaseModule)
        }
    }
}
