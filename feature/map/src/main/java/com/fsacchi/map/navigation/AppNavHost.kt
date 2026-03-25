package com.fsacchi.map.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fsacchi.map.detail.WeatherDetailScreen
import com.fsacchi.map.history.HistoryScreen
import com.fsacchi.map.map.MapScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.Map.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable(AppDestinations.Map.route) {
            MapScreen(
                viewModel = koinViewModel(),
                onNavigateToDetail = { lat, lon ->
                    navController.navigate(AppDestinations.WeatherDetail.createRoute(lat, lon))
                },
                onNavigateToHistory = {
                    navController.navigate(AppDestinations.History.route)
                }
            )
        }

        composable(
            route = AppDestinations.WeatherDetail.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            WeatherDetailScreen(
                lat = lat,
                lon = lon,
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.History.route) {
            HistoryScreen(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onSelectHistory = { lat, lon ->
                    navController.navigate(AppDestinations.WeatherDetail.createRoute(lat, lon)) {
                        popUpTo(AppDestinations.Map.route)
                    }
                }
            )
        }
    }
}
