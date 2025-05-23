package com.example.weatherapp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.ApiService
import com.example.weatherapp.data.RetrofitClient
import com.example.weatherapp.data.WeatherForecast
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.util.NetworkUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitClient.instance
    private val _weatherData = MutableStateFlow<UiState<WeatherResponse>>(UiState.Loading)
    val weatherData: StateFlow<UiState<WeatherResponse>> = _weatherData.asStateFlow()

    private val _weatherForecast = MutableStateFlow<UiState<WeatherForecast>>(UiState.Loading)
    val weatherForecast: StateFlow<UiState<WeatherForecast>> = _weatherForecast.asStateFlow()

    private val _favouriteCities = MutableStateFlow<List<String>?>(emptyList())
    val favouriteCities: StateFlow<List<String>?> = _favouriteCities.asStateFlow()

    private val _newFavouriteCity = MutableStateFlow<String>("")
    val newFavouriteCity: StateFlow<String> = _newFavouriteCity.asStateFlow()

    private val _currentCity = MutableStateFlow("Warszawa")
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val _currentUnits = MutableStateFlow("metric")
    val currentUnits: StateFlow<String> = _currentUnits.asStateFlow()

    private val _refreshIntervalSeconds = MutableStateFlow("0")
    val refreshIntervalSeconds: StateFlow<String> = _refreshIntervalSeconds.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        _currentCity.value = loadCurrentCity() ?: "Warszawa"
        _refreshIntervalSeconds.value = loadRefreshIntervalSeconds()
        fetchWeatherForCity()
        fetchWeatherForecastForCity()
        updateFavouriteCities()
        _favouriteCities.value = loadFavouriteCities()
        startAutoRefresh()
    }

    private fun checkNetworkConnection(){
        _isNetworkAvailable.value = NetworkUtils.isNetworkAvailable(getApplication())
    }

    fun setCurrentCity(newCity: String){
        _currentCity.value = newCity
        saveCurrentCity(newCity)
        fetchWeatherForCity()
        fetchWeatherForecastForCity()
    }

    fun setCurrentUnits(newUnits: String){
        _currentUnits.value = newUnits
    }

    fun saveWeatherData(weatherResponse: WeatherResponse){
        try{
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            preferences.edit {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val adapter = moshi.adapter(WeatherResponse::class.java)
                val json = adapter.toJson(weatherResponse)
                putString("last_weather", json)
            }
        } catch (e: Exception){
            Log.e("Weather", "Błąd przy zapisie pogody", e)
        }
    }

    fun loadWeatherData(): WeatherResponse? {
        try {
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            val json = preferences.getString("last_weather", null)
            if (json.isNullOrEmpty()) {
                return null
            }
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(WeatherResponse::class.java)
            return adapter.fromJson(json)
        } catch (e: Exception){
            Log.e("Weather", "Błąd przy odczycie pogody", e)
            return null
        }
    }

    fun fetchWeatherForCity(){
        checkNetworkConnection()
        _weatherData.value = UiState.Loading
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.WEATHER_API_KEY
                if (apiKey.isEmpty()) {
                    _weatherData.value = UiState.Error("Klucz API nie jest skonfigurowany.")
                    return@launch
                }

                if (_isNetworkAvailable.value) {
                    val response = apiService.getWeatherDetails(
                        city = _currentCity.value,
                        units = _currentUnits.value,
                        apiKey = apiKey,
                        lang = "pl"
                    )
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _weatherData.value = UiState.Success(responseBody)
                        } else {
                            _weatherData.value = UiState.Error("Otrzymano pomyślną odpowiedź, ale bez danych (puste ciało).")
                        }
                    }
                    else {
                        val errorBody = response.errorBody()?.string() ?: "Nieznany błąd serwera"
                        _weatherData.value = UiState.Error("Błąd: ${response.code()} - $errorBody")
                    }
                } else {
                    _weatherData.value = UiState.Error("Brak połączenia z internetem")
                }
            } catch (e: Exception) {
                _weatherData.value = UiState.Error("Wystąpił błąd: ${e.localizedMessage ?: "Nieznany błąd"}")
            }
        }
    }

    fun saveWeatherForecast(weatherForecast: WeatherForecast){
        try{
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            preferences.edit {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val adapter = moshi.adapter(WeatherForecast::class.java)
                val json = adapter.toJson(weatherForecast)
                putString("last_forecast", json)
            }
        } catch (e: Exception){
            Log.e("Forecast", "Błąd przy zapisie prognozy", e)
        }
    }

    fun loadWeatherForecast(): WeatherForecast? {
        try {
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            val json = preferences.getString("last_forecast", null)
            if (json.isNullOrEmpty()) {
                return null
            }
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(WeatherForecast::class.java)
            return adapter.fromJson(json)
        } catch (e: Exception){
            Log.e("Forecast", "Błąd przy odczycie prognozy", e)
            return null
        }
    }

    fun fetchWeatherForecastForCity(){
        checkNetworkConnection()
        _weatherForecast.value = UiState.Loading
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.WEATHER_API_KEY
                if (apiKey.isEmpty()) {
                    _weatherForecast.value = UiState.Error("Klucz API nie jest skonfigurowany.")
                    return@launch
                }

                if (_isNetworkAvailable.value) {
                    val response = apiService.getWeatherForecast(
                        city = _currentCity.value,
                        units = _currentUnits.value,
                        apiKey = apiKey,
                        lang = "pl"
                    )
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _weatherForecast.value = UiState.Success(responseBody)
                        } else {
                            _weatherForecast.value = UiState.Error("Otrzymano pomyślną odpowiedź, ale bez danych (puste ciało).")
                        }
                    }
                    else {
                        val errorBody = response.errorBody()?.string() ?: "Nieznany błąd serwera"
                        _weatherForecast.value = UiState.Error("Błąd: ${response.code()} - $errorBody")
                    }
                } else {
                    _weatherForecast.value = UiState.Error("Brak połączenia z internetem")
                }
            } catch (e: Exception) {
                _weatherForecast.value = UiState.Error("Wystąpił błąd: ${e.localizedMessage ?: "Nieznany błąd"}")
            }
        }
    }

    fun saveCurrentCity(currentCity: String){
        try{
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            preferences.edit {
                putString("last_city", currentCity)
            }
        } catch (e: Exception){
            Log.e("City", "Błąd przy zapisie miasta", e)
        }
    }

    fun loadCurrentCity(): String? {
        try {
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            val city = preferences.getString("last_city", null)
            if (city.isNullOrEmpty()) {
                return null
            }
            return city
        } catch (e: Exception){
            Log.e("City", "Błąd przy odczycie miasta", e)
            return null
        }
    }

    fun saveFavouriteCities() {
        try{
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
            val json = adapter.toJson(_favouriteCities.value)
            preferences.edit() {
                putString("favourites", json).apply()
            }
        } catch (e: Exception){
            Log.e("Favourite", "Błąd przy zapisie ulubionych miast", e)
        }
    }

    fun loadFavouriteCities(): List<String>? {
        try {
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            val cities = preferences.getString("favourites", null)
            if (cities.isNullOrEmpty()) {
                return null
            } else {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val adapter = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
                return adapter.fromJson(cities) ?: emptyList()
            }
        } catch (e: Exception){
            Log.e("Favourite", "Błąd przy odczycie ulubionych miast", e)
            return null
        }
    }

    fun updateFavouriteCities() {
        _favouriteCities.value = loadFavouriteCities()
    }

    fun addFavouriteCity(city: String) {
        if (city.isNotBlank()) {
            _favouriteCities.update { currentListNullable ->
                val currentList = currentListNullable ?: emptyList()
                if (!currentList.contains(city)) {
                    currentList + city
                } else {
                    currentList
                }
            }
        }
        saveFavouriteCities()
        _newFavouriteCity.value = ""
    }

    fun removeFavouriteCity(city: String) {
        _favouriteCities.update { currentListNullable ->
            currentListNullable?.let { currentList ->
                val newList = currentList - city
                if (newList.isEmpty() && currentListNullable.isNotEmpty()) {
                    null
                } else {
                    newList
                }
            }
        }
        saveFavouriteCities()
    }

    fun setNewFavouriteCity(city: String){
        _newFavouriteCity.value =  city
    }

    fun setRefreshIntervalSeconds(interval: String) {
        val numericInterval = interval.toIntOrNull()
        if (numericInterval != null && numericInterval > 0) {
            _refreshIntervalSeconds.value = interval
            saveRefreshIntervalSeconds(interval)
        } else if (interval.isEmpty()) {
            _refreshIntervalSeconds.value = ""
        }
    }

    fun saveRefreshIntervalSeconds(interval: String) {
        try{
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            preferences.edit {
                putString("interval", interval)
            }
        } catch (e: Exception){
            Log.e("Interval", "Błąd przy zapisie interwału", e)
        }
    }

    fun loadRefreshIntervalSeconds(): String {
        try {
            var context: Context = getApplication()
            var preferences = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE)
            val interval = preferences.getString("interval", null)
            if (interval.isNullOrEmpty()) {
                return "0"
            }
            return interval
        } catch (e: Exception){
            Log.e("Interval", "Błąd przy odczycie interwału", e)
            return "0"
        }
    }

    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()

        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                val intervalString = _refreshIntervalSeconds.value
                val intervalMs = intervalString.toLongOrNull()?.times(1000)
                if (intervalMs != null && intervalMs > 0){
                    delay(intervalMs)
                    if (isActive) {
                        checkNetworkConnection()
                        if (_isNetworkAvailable.value) {
                            fetchWeatherForCity()
                            fetchWeatherForecastForCity()
                        }
                    }
                }
                else {
                    delay(1000)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }
}