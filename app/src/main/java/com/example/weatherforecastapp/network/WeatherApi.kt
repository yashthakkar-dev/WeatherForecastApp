package com.example.weatherforecastapp.network

import com.example.weatherforecastapp.model.Weather
import com.example.weatherforecastapp.utils.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface WeatherApi {
    @GET(value = "data/2.5/forecast/daily")
    suspend fun getWeather(
        @Query("q") query : String,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = API_KEY // your api key
    ): Weather
}