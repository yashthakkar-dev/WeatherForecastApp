package com.example.weatherforecastapp.model

data class Temp(
    var day: Double,
    val eve: Double,
    var max: Double,
    var min: Double,
    val morn: Double,
    val night: Double
)