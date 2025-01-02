package com.example.weatherforecastapp.screens.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecastapp.data.DataOrException
import com.example.weatherforecastapp.model.Unit
import com.example.weatherforecastapp.model.Weather
import com.example.weatherforecastapp.model.WeatherItem
import com.example.weatherforecastapp.navigation.WeatherScreens
import com.example.weatherforecastapp.screens.settings.SettingsViewModel
import com.example.weatherforecastapp.utils.formatDate
import com.example.weatherforecastapp.utils.formatDecimals
import com.example.weatherforecastapp.widgets.HumidityWindPressureRow
import com.example.weatherforecastapp.widgets.SunsetSunRiseRow
import com.example.weatherforecastapp.widgets.WeatherAppBar
import com.example.weatherforecastapp.widgets.WeatherDetailRow
import com.example.weatherforecastapp.widgets.WeatherStateImage

@Composable
fun MainScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    city: String?
) {

    val context = LocalContext.current

    if (mainViewModel.showDialog) {
        AlertDialog(
            onDismissRequest = { mainViewModel.showDialog = false },
            title = { Text("Permissions Required") },
            text = {
                Text("It looks like you have turned off Location permissions. It can be enabled under Application Settings.")
            },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    mainViewModel.showDialog = false
                }) {
                    Text("Go To Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { mainViewModel.showDialog = false }) { Text("Cancel") }
            }
        )
    }

    val unitFromDb = settingsViewModel.unitList.collectAsState().value
    var unit by remember {
        mutableStateOf("imperial")
    }
    var isImperial by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(unitFromDb) {
        if (unitFromDb.isEmpty()) {
            settingsViewModel.insertUnit(Unit(unit = "metric"))
        } else {
            unit = unitFromDb[0].unit.split(" ")[0].lowercase()
            isImperial = unit == "imperial"
        }
    }

    if (unitFromDb.isNotEmpty()) {
        val weatherData = produceState(
            initialValue = DataOrException(loading = true)
        ) {
            value = if (mainViewModel.isLocationPermissionGranted) {
                val result = mainViewModel.getWeatherByLocation()
                mainViewModel.convertValues(isImperial, result.data)
            } else
                mainViewModel.getWeather(city ?: "Montreal", unit = unit)
        }.value

        Log.d(
            "yash",
            "isLocationPermissionGranted - ${mainViewModel.isLocationPermissionGranted} weatherData: ${weatherData.data}"
        )

        if (weatherData.loading == true) {
            CircularProgressIndicator()
        } else if (weatherData.data != null) {
            MainScaffold(weatherData.data!!, navController, isImperial = isImperial)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScaffold(weather: Weather, navController: NavController, isImperial: Boolean) {

    Scaffold(
        topBar = {
            WeatherAppBar(
                title = weather.city.name + ", ${weather.city.country}",
                navController = navController,
                onAddActionClicked = {
                    navController.navigate(WeatherScreens.SearchScreen.name)
                },
                elevation = 5.dp
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            MainContent(data = weather, isImperial = isImperial)
        }
    }
}

@Composable
fun MainContent(
    data: Weather, isImperial: Boolean
) {

    val weatherItem = data.list[0]
    val imageUrl = "https://openweathermap.org/img/wn/${weatherItem.weather[0].icon}.png"

    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatDate(weatherItem.dt),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(6.dp)
        )

        Surface(
            modifier = Modifier
                .padding(4.dp)
                .size(200.dp),
            shape = CircleShape,
            color = Color(0xFFFFC400)
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherStateImage(imageUrl = imageUrl)
                Text(
                    text = formatDecimals(weatherItem.temp.day) + "º",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = weatherItem.weather[0].main,
                    fontStyle = FontStyle.Italic
                )
            }
        }
        HumidityWindPressureRow(weather = data.list[0], isImperial = isImperial)
        HorizontalDivider()
        SunsetSunRiseRow(weather = data.list[0])

        Text(
            "This Week",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            color = Color(0xFFEEF1EF),
            shape = RoundedCornerShape(size = 14.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(2.dp),
                contentPadding = PaddingValues(1.dp)
            ) {
                items(items = data.list) { item: WeatherItem ->
                    WeatherDetailRow(weather = item)
                }
            }
        }
    }
}
