package com.example.weatherapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherForecast(
    val city: City,
    val cod: Int,
    val message: Double,
    val cnt: Int,
    val list: List<ListItem>
)

@JsonClass(generateAdapter = true)
data class City(
    val id: Int,
    val name: String,
    val coord: Coordinates,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

@JsonClass(generateAdapter = true)
data class ListItem(
    val dt: Int,
    val main: Main,
    val weather: List<WeatherItem>,
    val clouds: Clouds,
    val wind: Wind,
    val pop: Double,
    val rain: RainData? = null,
    val visibility: Int? = null,
    @Json(name = "dt_txt") val dtTxt: String
)

@JsonClass(generateAdapter = true)
data class Main(
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    val pressure: Int,
    @Json(name = "sea_level") val seaLevel: Int,
    @Json(name = "grnd_level") val grndLevel: Int,
    val humidity: Int,
    @Json(name = "temp_kf") val tempKf: Double
)

@JsonClass(generateAdapter = true)
data class WeatherItem(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class RainData(
    @Json(name = "3h") val threeHour: Double?
)

@JsonClass(generateAdapter = true)
data class Clouds(
    val all: Int
)

@JsonClass(generateAdapter = true)
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)
