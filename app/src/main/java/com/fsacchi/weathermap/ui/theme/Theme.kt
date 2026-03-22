package com.fsacchi.weathermap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val WeatherDarkColorScheme = darkColorScheme(
    primary = SteelBlue,
    onPrimary = OnDarkPrimary,
    primaryContainer = NavyBlue,
    onPrimaryContainer = IceBlue,
    secondary = LightBlue,
    onSecondary = DeepNavy,
    secondaryContainer = PetrolBlue,
    onSecondaryContainer = OnDarkSecondary,
    tertiary = OnDarkTertiary,
    onTertiary = DeepNavy,
    background = DeepNavy,
    onBackground = OnDarkPrimary,
    surface = DarkSurface,
    onSurface = OnDarkPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = OnDarkSecondary,
    error = ErrorColor,
    onError = DeepNavy
)

@Composable
fun WeatherMapTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = WeatherDarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
