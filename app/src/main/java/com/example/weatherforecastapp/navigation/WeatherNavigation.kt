package com.example.weatherforecastapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherforecastapp.screens.about.AboutScreen
import com.example.weatherforecastapp.screens.favorites.FavoriteViewModel
import com.example.weatherforecastapp.screens.favorites.FavoritesScreen
import com.example.weatherforecastapp.screens.main.MainScreen
import com.example.weatherforecastapp.screens.main.MainViewModel
import com.example.weatherforecastapp.screens.search.SearchScreen
import com.example.weatherforecastapp.screens.settings.SettingsScreen
import com.example.weatherforecastapp.screens.settings.SettingsViewModel
import com.example.weatherforecastapp.screens.splash.SplashScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WeatherNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = WeatherScreens.SplashScreen.name
    ) {
        composable(WeatherScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        val mainScreenRoute = WeatherScreens.MainScreen.name
        composable(
            "$mainScreenRoute/{city}",
            arguments = listOf(
                navArgument(name = "city") {
                    type = NavType.StringType
                })
        ) { navBack ->
            navBack.arguments?.getString("city").let { city ->

                val mainViewModel = hiltViewModel<MainViewModel>()
                MainScreen(
                    navController = navController, mainViewModel,
                    city = city
                )
            }
        }

        composable(WeatherScreens.SearchScreen.name) {
            SearchScreen(navController = navController)
        }

        composable(WeatherScreens.AboutScreen.name) {
            AboutScreen(navController = navController)
        }

        composable(WeatherScreens.SettingsScreen.name) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(navController = navController, settingsViewModel)
        }

        composable(WeatherScreens.FavoriteScreen.name) {
            val favoriteViewModel = hiltViewModel<FavoriteViewModel>()
            FavoritesScreen(navController = navController, favoriteViewModel)
        }

    }
}