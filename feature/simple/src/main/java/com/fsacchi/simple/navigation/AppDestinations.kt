package com.fsacchi.simple.navigation

sealed class AppDestinations(val route: String) {
    object Home : AppDestinations("home")
}
