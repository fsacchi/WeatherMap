package com.fsacchi.network.di

import com.fsacchi.network.BuildConfig
import com.fsacchi.network.api.GeocodingApiService
import com.fsacchi.network.api.WeatherApiService
import com.fsacchi.network.interceptor.ApiKeyInterceptor
import com.fsacchi.network.interceptor.createLoggingInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        val json: Json = get()
        Retrofit.Builder()
            .baseUrl(BuildConfig.URL_WEATHER)
            .client(get())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single<WeatherApiService> {
        get<Retrofit>().create(WeatherApiService::class.java)
    }

    single<GeocodingApiService> {
        get<Retrofit>().create(GeocodingApiService::class.java)
    }
}
