package com.fsacchi.weathermap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fsacchi.weathermap.presentation.navigation.AppNavHost
import com.fsacchi.weathermap.ui.theme.WeatherMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherMapTheme {
                AppNavHost()
            }
        }
    }
}
