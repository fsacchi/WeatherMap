package com.fsacchi.network.api

import com.fsacchi.network.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {
    @GET("geo/1.0/direct")
    suspend fun searchCity(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): List<GeocodingResponseDto>
}
