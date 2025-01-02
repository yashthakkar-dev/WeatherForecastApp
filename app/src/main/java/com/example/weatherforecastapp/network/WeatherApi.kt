package com.example.weatherforecastapp.network

import com.example.weatherforecastapp.model.Weather
import com.example.weatherforecastapp.utils.Constants.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface WeatherApi {
    @GET(value = "data/2.5/forecast/daily")
    suspend fun getWeather(
        @Query("q") query: String,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = API_KEY // your api key
    ): Weather

    //    @GET("2.5/weather")
    @GET(value = "data/2.5/forecast/daily")
    fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
//    @Query("exclude") exclude: String = "minutely,hourly,alerts",
        @Query("cnt") cnt: Int = 7,
//    @Query("units") units: String = "metric",
        @Query("appid") appid: String = API_KEY,
    ): Call<Weather>
}