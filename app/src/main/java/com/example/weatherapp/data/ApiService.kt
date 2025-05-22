package com.example.weatherapp.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    suspend fun getWeatherDetails(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String
    ): Response<WeatherForecast>
}