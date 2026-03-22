package com.fsacchi.weathermap.data.remote.interceptor

import com.fsacchi.weathermap.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

fun createLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }.apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
    }
}
