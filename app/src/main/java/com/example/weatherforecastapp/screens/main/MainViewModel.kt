package com.example.weatherforecastapp.screens.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
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
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    var showDialog by mutableStateOf(false)

    var isLocationPermissionGranted = false

    private var latitude = 0.0
    private var longitude = 0.0

    init {
        if (!isLocationEnabled()) {
            Toast.makeText(
                context,
                "Please enable location services",
                Toast.LENGTH_SHORT
            ).show()
//            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            Dexter.withContext(context)
                .withPermissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report != null && report.areAllPermissionsGranted()) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                requestLocationData()
                            }
                        } else if (report?.isAnyPermissionPermanentlyDenied == true) {
                            Toast.makeText(
                                context,
                                "All permissions are required for the app to work",
                                Toast.LENGTH_SHORT
                            ).show()
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
                isLocationPermissionGranted = true
            }
        }
    }

    fun convertValues(
        isImperial: Boolean,
        weather: Weather?
    ): DataOrException<Weather, Boolean, Exception> {
        if (weather == null || weather.list.isEmpty()) return DataOrException(e = Exception("Empty list"))

        // Pre-compute the conversion factors
        val speedConversionFactor = if (isImperial) 2.23694 else 3.6
        val temperatureConversionFactor = if (isImperial) 9.0 / 5 else 1.0  // Ensure the result is Double
        val temperatureOffset = if (isImperial) 32.0 else 0.0  // Use Double for offset

        // Create a new list with modified temperatures and wind speeds
        val updatedList = weather.list.map { item ->
            item.copy(
                speed = round(item.speed * speedConversionFactor),
                temp = item.temp.copy(
                    min = convertTemperature(item.temp.min, temperatureConversionFactor, temperatureOffset),
                    max = convertTemperature(item.temp.max, temperatureConversionFactor, temperatureOffset),
                    day = convertTemperature(item.temp.day, temperatureConversionFactor, temperatureOffset)
                )
            )
        }

        // Return the updated Weather object
        return DataOrException(data = weather.copy(list = updatedList))
    }

    // Helper function to convert temperature based on the unit system (Imperial or Metric)
    private fun convertTemperature(temperatureInKelvin: Double, conversionFactor: Double, offset: Double): Double {
        return (temperatureInKelvin - 273.15) * conversionFactor + offset
    }

    suspend fun getWeather(
        city: String,
        unit: String
    ): DataOrException<Weather, Boolean, Exception> {
        return repository.getWeather(cityQuery = city, unit = unit)
    }

    suspend fun getWeatherByLocation(): DataOrException<Weather, Boolean, Exception> {
        return repository.getWeatherByLocation(latitude = latitude, longitude = longitude)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}