package com.fsacchi.weathermap.simple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fsacchi.simple.navigation.SimpleNavHost
import com.fsacchi.ui.compose.WeatherMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherMapTheme {
                SimpleNavHost()
            }
        }
    }
}
