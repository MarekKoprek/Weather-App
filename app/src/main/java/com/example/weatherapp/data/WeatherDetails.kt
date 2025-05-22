package com.example.weatherapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val coord: Coordinates,
    val weather: List<WeatherCondition>,
    val base: String,
    val main: MainData,
    val visibility: Int,
    val wind: WindData,
    val rain: Rain? = null,
    val clouds: CloudsData,
    val dt: Long,
    val sys: SysData,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)

@JsonClass(generateAdapter = true)
data class MainData(
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @Json(name = "sea_level") val seaLevel: Int?,
    @Json(name = "grnd_level") val grndLevel: Int?
)

@JsonClass(generateAdapter = true)
data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class WindData(
    val speed: Double,
    val deg: Int,
    val gust: Double?
)

@JsonClass(generateAdapter = true)
data class CloudsData(
    val all: Int
)

@JsonClass(generateAdapter = true)
data class SysData(
    val type: Int?,
    val id: Int?,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

@JsonClass(generateAdapter = true)
data class Coordinates(
    val lon: Double,
    val lat: Double
)

@JsonClass(generateAdapter = true)
data class Rain(
    @Json(name = "1h") val lastHour: Double? = null
)
