package com.example.weatherforecastapp.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecastapp.model.Favorite
import com.example.weatherforecastapp.navigation.WeatherScreens
import com.example.weatherforecastapp.widgets.WeatherAppBar

@Composable
fun FavoritesScreen(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            WeatherAppBar(
                title = "Favorite Cities",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                false,
                elevation = 5.dp,
                navController = navController
            ) { navController.popBackStack() }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val list = favoriteViewModel.favList.collectAsState().value

                if (list.isEmpty()) {
                    // Show empty state
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            "No favorite cities found",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                } else {
                    LazyColumn {
                        items(items = list) {
                            CityRow(it, navController = navController, favoriteViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CityRow(
    favorite: Favorite,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel
) {

    val configuration = LocalContext.current.resources.configuration

    Surface(
        shadowElevation = 2.dp,
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                navController.navigate(WeatherScreens.MainScreen.name + "/${favorite.city}")
            },
        shape = CircleShape.copy(
            topEnd = CornerSize(6.dp),
            bottomStart = CornerSize(6.dp)
        ),
        color = Color(0xFFB2DFDB)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (configuration.screenWidthDp * 0.15).dp),
            verticalAlignment = Alignment.CenterVertically, // Ensures all items are aligned in the center vertically
            horizontalArrangement = Arrangement.SpaceBetween // Adjusts spacing between elements
        ) {
            // City Name
            Text(
                text = favorite.city,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f) // Allows text to take available space proportionally
            )

            // Country Name
            Surface(
                modifier = Modifier
                    .padding(end = (configuration.screenWidthDp * 0.25).dp),
                shape = CircleShape,
                color = Color(0xFFD1E3E1)
            ) {
                Text(
                    text = favorite.country,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Delete Icon
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "delete",
                modifier = Modifier.clickable {
                    favoriteViewModel.deleteFavorite(favorite)
                },
                tint = Color.Red.copy(alpha = 0.6f)
            )
        }
    }
}
