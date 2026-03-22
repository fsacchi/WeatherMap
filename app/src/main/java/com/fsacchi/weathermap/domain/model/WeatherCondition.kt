package com.fsacchi.weathermap.domain.model

enum class WeatherCondition {
    CLEAR_SKY,
    FEW_CLOUDS,
    SCATTERED_CLOUDS,
    BROKEN_CLOUDS,
    SHOWER_RAIN,
    RAIN,
    THUNDERSTORM,
    SNOW,
    MIST,
    UNKNOWN;

    companion object {
        fun fromIconCode(iconCode: String): WeatherCondition = when {
            iconCode.startsWith("01") -> CLEAR_SKY
            iconCode.startsWith("02") -> FEW_CLOUDS
            iconCode.startsWith("03") -> SCATTERED_CLOUDS
            iconCode.startsWith("04") -> BROKEN_CLOUDS
            iconCode.startsWith("09") -> SHOWER_RAIN
            iconCode.startsWith("10") -> RAIN
            iconCode.startsWith("11") -> THUNDERSTORM
            iconCode.startsWith("13") -> SNOW
            iconCode.startsWith("50") -> MIST
            else -> UNKNOWN
        }
    }
}
