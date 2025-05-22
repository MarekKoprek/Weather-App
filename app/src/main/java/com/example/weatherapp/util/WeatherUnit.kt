package com.example.weatherapp.util

enum class WeatherUnit(val apiValue: String, val displayName: String) {
    METRIC("metric", "Metryczne (°C, m/s)"),
    IMPERIAL("imperial", "Imperialne (°F, mph)"),
    STANDARD("standard", "Standardowe (K, m/s)");

    companion object {
        fun fromApiValue(value: String): WeatherUnit? {
            return entries.find { it.apiValue == value }
        }
    }
}