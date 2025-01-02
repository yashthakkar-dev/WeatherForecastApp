package com.example.weatherforecastapp.repository

import android.util.Log
import com.example.weatherforecastapp.data.DataOrException
import com.example.weatherforecastapp.model.Weather
import com.example.weatherforecastapp.network.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val api: WeatherApi) {

    suspend fun getWeather(
        cityQuery: String,
        unit: String
    ): DataOrException<Weather, Boolean, Exception> {
        val response = try {
            api.getWeather(query = cityQuery, units = unit)
        } catch (e: Exception) {
            return DataOrException(e = e)
        }

        Log.d("yash", "getWeather: $response")
        return DataOrException(data = response)
    }

    suspend fun getWeatherByLocation(
        latitude: Double,
        longitude: Double
    ): DataOrException<Weather, Boolean, Exception> {
        return try {
            val response = withContext(Dispatchers.IO) {
                api.getWeatherByLocation(lat = latitude, lon = longitude).execute()
            }
            if (response.isSuccessful && response.body() != null) {
                Log.d("yash", "getWeather: ${response.body()}")
                DataOrException(data = response.body())
            } else {
                DataOrException(e = Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            DataOrException(e = e)
        }
    }

}