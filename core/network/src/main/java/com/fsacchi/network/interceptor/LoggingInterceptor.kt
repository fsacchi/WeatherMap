package com.fsacchi.network.interceptor

import com.fsacchi.network.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

fun createLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }.apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
    }
}
