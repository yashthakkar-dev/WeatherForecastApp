package com.example.weatherforecastapp.screens.main

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.DataOrException
import com.example.weatherforecastapp.model.Weather
import com.example.weatherforecastapp.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    var locationWeatherData =
        mutableStateOf<DataOrException<Weather, Boolean, Exception>>(DataOrException())
    private var latitude = 0.0
    private var longitude = 0.0

    var permissionState by mutableStateOf(PermissionState.WAITING)

    var isImperial by mutableStateOf(false)

    init {
        checkPermissionsAndFetchLocation()
    }

    fun checkPermissionsAndFetchLocation() {
        if (!isLocationEnabled()) {
            Toast.makeText(
                context,
                "Please enable location services",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            permissionState = PermissionState.WAITING
            Dexter.withContext(context)
                .withPermissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report != null && report.areAllPermissionsGranted()) {
                            permissionState = PermissionState.GRANTED
                            requestLocationData()
                        } else if (report?.isAnyPermissionPermanentlyDenied == true) {
                            permissionState = PermissionState.DENIED
                        } else {
                            permissionState = PermissionState.DENIED
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).onSameThread().check()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                fetchLocationWeather()
            }
        }
    }

    private fun fetchLocationWeather() {
        viewModelScope.launch {
            locationWeatherData.value = getWeatherByLocation()
        }
    }

    private fun convertValues(weather: Weather?): DataOrException<Weather, Boolean, Exception> {
        if (weather == null || weather.list.isEmpty()) return DataOrException(e = Exception("Empty list"))

        // Pre-compute the conversion factors
        val speedConversionFactor = if (isImperial) 2.23694 else 3.6
        val temperatureConversionFactor =
            if (isImperial) 9.0 / 5 else 1.0  // Ensure the result is Double
        val temperatureOffset = if (isImperial) 32.0 else 0.0  // Use Double for offset

        // Create a new list with modified temperatures and wind speeds
        val updatedList = weather.list.map { item ->
            item.copy(
                speed = round(item.speed * speedConversionFactor),
                feels_like = item.feels_like.copy(
                    day = convertTemperature(
                        item.feels_like.day,
                        temperatureConversionFactor,
                        temperatureOffset
                    ),
                ),
                temp = item.temp.copy(
                    min = convertTemperature(
                        item.temp.min,
                        temperatureConversionFactor,
                        temperatureOffset
                    ),
                    max = convertTemperature(
                        item.temp.max,
                        temperatureConversionFactor,
                        temperatureOffset
                    ),
                    day = convertTemperature(
                        item.temp.day,
                        temperatureConversionFactor,
                        temperatureOffset
                    )
                )
            )
        }

        // Return the updated Weather object
        return DataOrException(data = weather.copy(list = updatedList))
    }

    // Helper function to convert temperature based on the unit system (Imperial or Metric)
    private fun convertTemperature(
        temperatureInKelvin: Double,
        conversionFactor: Double,
        offset: Double
    ): Double {
        return (temperatureInKelvin - 273.15) * conversionFactor + offset
    }

    suspend fun getWeather(
        city: String,
        unit: String
    ): DataOrException<Weather, Boolean, Exception> {
        return repository.getWeather(cityQuery = city, unit = unit)
    }

    private suspend fun getWeatherByLocation(): DataOrException<Weather, Boolean, Exception> {
        val response = repository.getWeatherByLocation(latitude = latitude, longitude = longitude)
        return convertValues(response.data)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}

enum class PermissionState { WAITING, GRANTED, DENIED }