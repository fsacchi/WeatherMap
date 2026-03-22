package com.fsacchi.weathermap

import android.app.Application
import com.fsacchi.weathermap.BuildConfig
import timber.log.Timber
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import com.fsacchi.weathermap.di.networkModule
import com.fsacchi.weathermap.di.databaseModule
import com.fsacchi.weathermap.di.repositoryModule
import com.fsacchi.weathermap.di.useCaseModule
import com.fsacchi.weathermap.di.viewModelModule
import org.osmdroid.config.Configuration

class WeatherMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@WeatherMapApplication)
            modules(
                networkModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
