package com.example.weatherapp.util

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.weatherapp.UiState
import com.example.weatherapp.WeatherViewModel
import com.example.weatherapp.data.ListItem
import com.example.weatherapp.data.WeatherForecast
import com.example.weatherapp.data.WeatherResponse
import java.text.SimpleDateFormat
import kotlin.text.format
import java.util.Locale.getDefault
import java.util.Date
import kotlin.math.roundToInt
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun MainScreenView(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current

    val weatherUiState by weatherViewModel.weatherData.collectAsState()
    val forecastUiState by weatherViewModel.weatherForecast.collectAsState()
    val isNetworkAvailable by weatherViewModel.isNetworkAvailable.collectAsState()
    val favouriteCities by weatherViewModel.favouriteCities.collectAsState()
    val newFavouriteCity by weatherViewModel.newFavouriteCity.collectAsState()
    val currentUnits by weatherViewModel.currentUnits.collectAsState()
    val currentInterval by weatherViewModel.refreshIntervalSeconds.collectAsState()

    var startPage: Int
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            when (windowSizeClass.heightSizeClass) {
                WindowHeightSizeClass.Medium -> startPage = 6
                WindowHeightSizeClass.Expanded -> startPage = 6
                else -> startPage = 1
            }
        }
        else -> {
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Medium -> startPage = 6
                WindowWidthSizeClass.Expanded -> startPage = 6
                else -> startPage = 1
            }
        }
    }

    var topText by remember { mutableStateOf("Podstawowe") }
    var currentPage by remember { mutableIntStateOf(startPage) }

    val updateTextBasedOnButton: (Int) -> Unit = { buttonNumber ->
        when (buttonNumber) {
            1 -> { topText = "Podstawowe" }
            2 -> { topText = "Dodatkowe" }
            3 -> { topText = "Prognoza pogody" }
            4 -> { topText = "Ulubione" }
            5 -> { topText = "Ustawienia" }
            6 -> { topText = "Pogoda" }
            else -> topText
        }
        currentPage = buttonNumber
    }

    val refreshButton: () -> Unit = {
        weatherViewModel.fetchWeatherForCity()
        weatherViewModel.fetchWeatherForecastForCity()
    }

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            when(windowSizeClass.heightSizeClass) {
                WindowHeightSizeClass.Compact -> {
                    LandscapeLayout(
                        modifier = modifier,
                        currentText = topText,
                        onButtonClick = updateTextBasedOnButton,
                        weatherUiState = weatherUiState,
                        isNetworkAvailable = isNetworkAvailable,
                        weatherViewModel = weatherViewModel,
                        currentPage = currentPage,
                        forecastUiState = forecastUiState,
                        favouriteCities = favouriteCities,
                        newFavouriteCity = newFavouriteCity,
                        currentUnits = currentUnits,
                        currentInterval = currentInterval,
                        onRefreshClick = refreshButton
                    )
                }
                else -> {
                    LandscapeLayoutTablet(
                        modifier = modifier,
                        currentText = topText,
                        onButtonClick = updateTextBasedOnButton,
                        weatherUiState = weatherUiState,
                        isNetworkAvailable = isNetworkAvailable,
                        weatherViewModel = weatherViewModel,
                        currentPage = currentPage,
                        forecastUiState = forecastUiState,
                        favouriteCities = favouriteCities,
                        newFavouriteCity = newFavouriteCity,
                        currentUnits = currentUnits,
                        currentInterval = currentInterval,
                        onRefreshClick = refreshButton
                    )
                }
            }
        }
        else -> {
            when(windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    PortraitLayout(
                        modifier = modifier,
                        currentText = topText,
                        onButtonClick = updateTextBasedOnButton,
                        weatherUiState = weatherUiState,
                        isNetworkAvailable = isNetworkAvailable,
                        weatherViewModel = weatherViewModel,
                        currentPage = currentPage,
                        forecastUiState = forecastUiState,
                        favouriteCities = favouriteCities,
                        newFavouriteCity = newFavouriteCity,
                        currentUnits = currentUnits,
                        currentInterval = currentInterval,
                        onRefreshClick = refreshButton
                    )
                }
                else -> {
                    PortraitLayoutTablet(
                        modifier = modifier,
                        currentText = topText,
                        onButtonClick = updateTextBasedOnButton,
                        weatherUiState = weatherUiState,
                        isNetworkAvailable = isNetworkAvailable,
                        weatherViewModel = weatherViewModel,
                        currentPage = currentPage,
                        forecastUiState = forecastUiState,
                        favouriteCities = favouriteCities,
                        newFavouriteCity = newFavouriteCity,
                        currentUnits = currentUnits,
                        currentInterval = currentInterval,
                        onRefreshClick = refreshButton
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortraitLayout(
    modifier: Modifier = Modifier,
    currentText: String,
    onButtonClick: (Int) -> Unit,
    weatherUiState: UiState<WeatherResponse>,
    isNetworkAvailable: Boolean,
    weatherViewModel: WeatherViewModel?,
    currentPage: Int,
    forecastUiState: UiState<WeatherForecast>,
    favouriteCities: List<String>?,
    newFavouriteCity: String,
    currentUnits: String,
    currentInterval: String,
    onRefreshClick: () -> Unit
) {
    val units: List<String>
    when (WeatherUnit.fromApiValue(currentUnits)) {
        WeatherUnit.METRIC -> units = listOf("°C", "km/h", "km", "mm")
        WeatherUnit.IMPERIAL -> units = listOf("°F", "mph", "m", "\"")
        WeatherUnit.STANDARD -> units = listOf("K", "m/s", "km", "mm")
        null -> units = emptyList()
    }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentText,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (currentPage == 4){
                FavoriteCitiesScreen(weatherViewModel = weatherViewModel, favouriteCities = favouriteCities, newFavouriteCity = newFavouriteCity)
            }
            else if(currentPage == 5){
                SettingsScreen(weatherViewModel, currentUnits, currentInterval, onRefreshClick)
            }
            else if (!isNetworkAvailable) {
                if(currentPage == 1 || currentPage == 2){
                    val weather = weatherViewModel?.loadWeatherData()
                    if (weather != null) {
                        if (currentPage == 1) {
                            WeatherDetailsView(weather, units, modifier)
                        } else {
                            WeatherMoreDetailsView(weather, units, modifier)
                        }
                        Row(
                            modifier = modifier.align(Alignment.BottomCenter)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "",
                                tint = Color.Yellow
                            )
                            Text("Przestażałe dane")
                        }
                    } else {
                        Text(text = "Brak danych", color = Color.Red)
                    }
                }
                else{
                    val forecast = weatherViewModel?.loadWeatherForecast()
                    if(forecast != null){
                        WeatherForecastView(forecast, units)
                        Row(
                            modifier = modifier.align(Alignment.BottomCenter)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "",
                                tint = Color.Yellow
                            )
                            Text("Przestażałe dane")
                        }
                    }
                    else {
                        Text(text = "Brak danych", color = Color.Red)
                    }
                }
            } else {
                if(currentPage == 1 || currentPage == 2){
                    when (weatherUiState) {
                        is UiState.Loading -> CircularProgressIndicator()
                        is UiState.Success -> {
                            val weather = weatherUiState.data
                            if (currentPage == 1) {
                                WeatherDetailsView(weather, units, modifier)
                            } else {
                                WeatherMoreDetailsView(weather, units, modifier)
                            }
                            weatherViewModel?.saveWeatherData(weather)
                        }

                        is UiState.Error -> {
                            val weather = weatherViewModel?.loadWeatherData()
                            if (weather != null) {
                                if (currentPage == 1) {
                                    WeatherDetailsView(weather, units, modifier)
                                } else {
                                    WeatherMoreDetailsView(weather, units, modifier)
                                }
                                Row(
                                    modifier = modifier.align(Alignment.BottomCenter)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = "",
                                        tint = Color.Yellow
                                    )
                                    Text("Przestażałe dane")
                                }
                            } else {
                                Text(text = "Brak danych", color = Color.Red)
                            }
                        }
                    }
                }
                else{
                    when(forecastUiState) {
                        is UiState.Loading -> CircularProgressIndicator()
                        is UiState.Success -> {
                            val forecast = forecastUiState.data
                            WeatherForecastView(forecast, units)
                            weatherViewModel?.saveWeatherForecast(forecast)
                        }
                        is UiState.Error -> {
                            val forecast = weatherViewModel?.loadWeatherForecast()
                            if (forecast != null) {
                                WeatherForecastView(forecast, units)
                                Row(
                                    modifier = modifier.align(Alignment.BottomCenter)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = "",
                                        tint = Color.Yellow
                                    )
                                    Text("Przestażałe dane")
                                }
                            } else {
                                Text(text = "Brak danych", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onButtonClick(1) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.WbSunny, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(2) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Grain, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(3) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.CalendarToday, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(4) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Star, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(5) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeLayout(
    modifier: Modifier = Modifier,
    currentText: String,
    onButtonClick: (Int) -> Unit,
    weatherUiState: UiState<WeatherResponse>,
    isNetworkAvailable: Boolean,
    weatherViewModel: WeatherViewModel?,
    currentPage: Int,
    forecastUiState: UiState<WeatherForecast>,
    favouriteCities: List<String>?,
    newFavouriteCity: String,
    currentUnits: String,
    currentInterval: String,
    onRefreshClick: () -> Unit
) {
    val units: List<String>
    when (WeatherUnit.fromApiValue(currentUnits)) {
        WeatherUnit.METRIC -> units = listOf("°C", "km/h", "km", "mm")
        WeatherUnit.IMPERIAL -> units = listOf("°F", "mph", "m", "\"")
        WeatherUnit.STANDARD -> units = listOf("K", "m/s", "km", "mm")
        null -> units = emptyList()
    }
    Row (
        modifier = modifier.fillMaxSize()
    ){
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onButtonClick(1) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.WbSunny, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(2) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Grain, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(3) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.CalendarToday, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(4) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Star, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(5) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        Column (
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (currentPage == 4){
                    FavoriteCitiesScreen(weatherViewModel = weatherViewModel, favouriteCities = favouriteCities, newFavouriteCity = newFavouriteCity)
                }
                else if(currentPage == 5){
                    SettingsScreen(weatherViewModel, currentUnits, currentInterval, onRefreshClick)
                }
                else if (!isNetworkAvailable) {
                    if(currentPage == 1 || currentPage == 2){
                        val weather = weatherViewModel?.loadWeatherData()
                        if (weather != null) {
                            if (currentPage == 1) {
                                WeatherDetailsViewLandscape(weather, units)
                            } else {
                                WeatherMoreDetailsViewLandscape(weather, units, Modifier.fillMaxHeight())
                            }
                            Row(
                                modifier = modifier.align(Alignment.BottomCenter)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "",
                                    tint = Color.Yellow
                                )
                                Text("Przestażałe dane")
                            }
                        } else {
                            Text(text = "Brak danych", color = Color.Red)
                        }
                    }
                    else{
                        val forecast = weatherViewModel?.loadWeatherForecast()
                        if(forecast != null){
                            WeatherForecastView(forecast, units)
                            Row(
                                modifier = modifier.align(Alignment.BottomCenter)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "",
                                    tint = Color.Yellow
                                )
                                Text("Przestażałe dane")
                            }
                        }
                        else {
                            Text(text = "Brak danych", color = Color.Red)
                        }
                    }
                } else {
                    if(currentPage == 1 || currentPage == 2){
                        when (weatherUiState) {
                            is UiState.Loading -> CircularProgressIndicator()
                            is UiState.Success -> {
                                val weather = weatherUiState.data
                                if (currentPage == 1) {
                                    WeatherDetailsViewLandscape(weather, units)
                                } else {
                                    WeatherMoreDetailsViewLandscape(weather, units, Modifier.fillMaxHeight())
                                }
                                weatherViewModel?.saveWeatherData(weather)
                            }

                            is UiState.Error -> {
                                val weather = weatherViewModel?.loadWeatherData()
                                if (weather != null) {
                                    if (currentPage == 1) {
                                        WeatherDetailsViewLandscape(weather, units)
                                    } else {
                                        WeatherMoreDetailsViewLandscape(weather, units, Modifier.fillMaxHeight())
                                    }
                                    Row(
                                        modifier = modifier.align(Alignment.BottomCenter)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Warning,
                                            contentDescription = "",
                                            tint = Color.Yellow
                                        )
                                        Text("Przestażałe dane")
                                    }
                                } else {
                                    Text(text = "Brak danych", color = Color.Red)
                                }
                            }
                        }
                    }
                    else{
                        when(forecastUiState) {
                            is UiState.Loading -> CircularProgressIndicator()
                            is UiState.Success -> {
                                val forecast = forecastUiState.data
                                WeatherForecastView(forecast, units)
                                weatherViewModel?.saveWeatherForecast(forecast)
                            }
                            is UiState.Error -> {
                                val forecast = weatherViewModel?.loadWeatherForecast()
                                if (forecast != null) {
                                    WeatherForecastView(forecast, units)
                                    Row(
                                        modifier = modifier.align(Alignment.BottomCenter)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Warning,
                                            contentDescription = "",
                                            tint = Color.Yellow
                                        )
                                        Text("Przestażałe dane")
                                    }
                                } else {
                                    Text(text = "Brak danych", color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortraitLayoutTablet(
    modifier: Modifier = Modifier,
    currentText: String,
    onButtonClick: (Int) -> Unit,
    weatherUiState: UiState<WeatherResponse>,
    isNetworkAvailable: Boolean,
    weatherViewModel: WeatherViewModel?,
    currentPage: Int,
    forecastUiState: UiState<WeatherForecast>,
    favouriteCities: List<String>?,
    newFavouriteCity: String,
    currentUnits: String,
    currentInterval: String,
    onRefreshClick: () -> Unit
) {
    val units: List<String>
    when (WeatherUnit.fromApiValue(currentUnits)) {
        WeatherUnit.METRIC -> units = listOf("°C", "km/h", "km", "mm")
        WeatherUnit.IMPERIAL -> units = listOf("°F", "mph", "m", "\"")
        WeatherUnit.STANDARD -> units = listOf("K", "m/s", "km", "mm")
        null -> units = emptyList()
    }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentText,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (currentPage == 4){
                FavoriteCitiesScreen(weatherViewModel = weatherViewModel, favouriteCities = favouriteCities, newFavouriteCity = newFavouriteCity)
            }
            else if(currentPage == 5){
                SettingsScreen(weatherViewModel, currentUnits, currentInterval, onRefreshClick)
            }
            else if (!isNetworkAvailable) {
                val weather = weatherViewModel?.loadWeatherData()
                val forecast = weatherViewModel?.loadWeatherForecast()
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.4f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (weather != null) {
                            WeatherDetailsView(weather, units, Modifier.weight(0.5f))
                            WeatherMoreDetailsView(weather, units, Modifier.weight(0.5f))
                        }
                        else {
                            Text(text = "Brak danych", color = Color.Red)
                        }
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.6f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (forecast != null) {
                            WeatherForecastView(forecast, units)
                        } else {
                            Text(text = "Brak danych", color = Color.Red)
                        }
                    }
                }
                Row(
                    modifier = modifier.align(Alignment.BottomCenter)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "",
                        tint = Color.Yellow
                    )
                    Text("Przestażałe dane")
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.4f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (weatherUiState) {
                            is UiState.Loading -> CircularProgressIndicator()
                            is UiState.Success -> {
                                val weather = weatherUiState.data
                                WeatherDetailsView(weather, units, Modifier.weight(0.5f))
                                WeatherMoreDetailsView(weather, units, Modifier.weight(0.5f))
                                weatherViewModel?.saveWeatherData(weather)
                            }
                            is UiState.Error -> {
                                val weather = weatherViewModel?.loadWeatherData()
                                if (weather != null) {
                                    WeatherDetailsView(weather, units, Modifier.weight(0.5f))
                                    WeatherMoreDetailsView(weather, units, Modifier.weight(0.5f))
                                }
                                else {
                                    Text(text = "Brak danych", color = Color.Red)
                                }
                            }
                        }
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.6f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when(forecastUiState) {
                            is UiState.Loading -> CircularProgressIndicator()
                            is UiState.Success -> {
                                val forecast = forecastUiState.data
                                WeatherForecastView(forecast, units)
                                weatherViewModel?.saveWeatherForecast(forecast)
                            }
                            is UiState.Error -> {
                                val forecast = weatherViewModel?.loadWeatherForecast()
                                if (forecast != null) {
                                    WeatherForecastView(forecast, units)
                                } else {
                                    Text(text = "Brak danych", color = Color.Red)
                                }
                            }
                        }
                    }
                }
                if (weatherUiState is UiState.Error || forecastUiState is UiState.Error) {
                    Row(
                        modifier = modifier.align(Alignment.BottomCenter)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "",
                            tint = Color.Yellow
                        )
                        Text("Przestażałe dane")
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onButtonClick(6) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.WbSunny, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(4) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Star, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(5) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeLayoutTablet(
    modifier: Modifier = Modifier,
    currentText: String,
    onButtonClick: (Int) -> Unit,
    weatherUiState: UiState<WeatherResponse>,
    isNetworkAvailable: Boolean,
    weatherViewModel: WeatherViewModel?,
    currentPage: Int,
    forecastUiState: UiState<WeatherForecast>,
    favouriteCities: List<String>?,
    newFavouriteCity: String,
    currentUnits: String,
    currentInterval: String,
    onRefreshClick: () -> Unit
) {
    val units: List<String>
    when (WeatherUnit.fromApiValue(currentUnits)) {
        WeatherUnit.METRIC -> units = listOf("°C", "km/h", "km", "mm")
        WeatherUnit.IMPERIAL -> units = listOf("°F", "mph", "m", "\"")
        WeatherUnit.STANDARD -> units = listOf("K", "m/s", "km", "mm")
        null -> units = emptyList()
    }
    Row (
        modifier = modifier.fillMaxSize()
    ){
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onButtonClick(6) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.WbSunny, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(4) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Star, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
            Button(
                onClick = { onButtonClick(5) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "", tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        Column (
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (currentPage == 4){
                    FavoriteCitiesScreen(weatherViewModel = weatherViewModel, favouriteCities = favouriteCities, newFavouriteCity = newFavouriteCity)
                }
                else if(currentPage == 5){
                    SettingsScreen(weatherViewModel, currentUnits, currentInterval, onRefreshClick)
                }
                else if (!isNetworkAvailable) {
                    val weather = weatherViewModel?.loadWeatherData()
                    val forecast = weatherViewModel?.loadWeatherForecast()
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column (
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.3f),
                            verticalArrangement = Arrangement.Top
                        ) {
                            if (weather != null) {
                                WeatherDetailsView(weather, units)
                                WeatherMoreDetailsView(weather, units)
                            }
                            else {
                                Text(text = "Brak danych", color = Color.Red)
                            }
                        }
                        Column (
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.7f),
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            if (forecast != null) {
                                WeatherForecastView(forecast, units)
                            } else {
                                Text(text = "Brak danych", color = Color.Red)
                            }
                        }
                    }
                    Row(
                        modifier = modifier.align(Alignment.BottomCenter)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "",
                            tint = Color.Yellow
                        )
                        Text("Przestażałe dane")
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.3f),
                            verticalArrangement = Arrangement.Top
                        ) {
                            when (weatherUiState) {
                                is UiState.Loading -> CircularProgressIndicator()
                                is UiState.Success -> {
                                    val weather = weatherUiState.data
                                    WeatherDetailsView(weather, units)
                                    WeatherMoreDetailsView(weather, units)
                                    weatherViewModel?.saveWeatherData(weather)
                                }
                                is UiState.Error -> {
                                    val weather = weatherViewModel?.loadWeatherData()
                                    if (weather != null) {
                                        WeatherDetailsView(weather, units)
                                        WeatherMoreDetailsView(weather, units)
                                    }
                                    else {
                                        Text(text = "Brak danych", color = Color.Red)
                                    }
                                }
                            }
                        }
                        Column (
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.7f),
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            when(forecastUiState) {
                                is UiState.Loading -> CircularProgressIndicator()
                                is UiState.Success -> {
                                    val forecast = forecastUiState.data
                                    WeatherForecastView(forecast, units)
                                    weatherViewModel?.saveWeatherForecast(forecast)
                                }
                                is UiState.Error -> {
                                    val forecast = weatherViewModel?.loadWeatherForecast()
                                    if (forecast != null) {
                                        WeatherForecastView(forecast, units)
                                    } else {
                                        Text(text = "Brak danych", color = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                    if (weatherUiState is UiState.Error || forecastUiState is UiState.Error) {
                        Row(
                            modifier = modifier.align(Alignment.BottomCenter)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "",
                                tint = Color.Yellow
                            )
                            Text("Przestażałe dane")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailsView(
    weatherResponse: WeatherResponse,
    currentUnits: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "")
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${weatherResponse.name}, ${weatherResponse.sys.country}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            weatherResponse.weather.firstOrNull()?.let { condition ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://openweathermap.org/img/wn/${condition.icon}@2x.png")
                            .crossfade(true)
                            .build(),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    val text = condition.description.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                        getDefault()) else it.toString() }
                    if(text.length > 20){
                        val split = text.split(" ")
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${split[0]} ${split[1]}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = split[2],
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    else {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "${String.format(getDefault(), "%.1f", weatherResponse.main.temp)}${currentUnits[0]}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Odczuwalna: ${String.format(getDefault(), "%.1f", weatherResponse.main.feelsLike)}${currentUnits[0]}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                WeatherBasicDetails(weatherResponse, modifier)
            }
        }
    }
}

@Composable
fun WeatherDetailsViewLandscape(
    weatherResponse: WeatherResponse,
    currentUnits: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "")
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${weatherResponse.name}, ${weatherResponse.sys.country}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                weatherResponse.weather.firstOrNull()?.let { condition ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://openweathermap.org/img/wn/${condition.icon}@2x.png")
                                .crossfade(true)
                                .build(),
                            contentDescription = "",
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = condition.description.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    getDefault()
                                ) else it.toString()
                            },
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "${String.format(getDefault(), "%.1f", weatherResponse.main.temp)}${currentUnits[0]}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Odczuwalna: ${
                        String.format(
                            getDefault(),
                            "%.1f",
                            weatherResponse.main.feelsLike
                        )
                    }${currentUnits[0]}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Column(Modifier
                .fillMaxHeight()
                .weight(0.3f),
                verticalArrangement = Arrangement.SpaceAround) {
                WeatherBasicDetails(weatherResponse, modifier)
            }
        }
    }
}

@Composable
fun WeatherMoreDetailsView(
    weatherResponse: WeatherResponse,
    currentUnits: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                DetailItem(icon = Icons.Filled.Thermostat, label = "Min/Max", value = "${String.format(
                    getDefault(), "%.1f", weatherResponse.main.tempMin)}° / ${String.format(
                    getDefault(), "%.1f", weatherResponse.main.tempMax)}°")
                DetailItem(icon = Icons.Filled.WaterDrop, label = "Wilgotność", value = "${weatherResponse.main.humidity}%")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                DetailItem(icon = Icons.Filled.Compress, label = "Ciśnienie", value = "${weatherResponse.main.pressure} hPa")
                DetailItem(icon = Icons.Filled.Air, label = "Wiatr", value = "${weatherResponse.wind.speed} ${currentUnits[1]}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                DetailItem(icon = Icons.Filled.Visibility, label = "Widoczność", value = "${weatherResponse.visibility / 1000} ${currentUnits[2]}")
                weatherResponse.rain?.lastHour?.let {
                    DetailItem(icon = Icons.Filled.Grain, label = "Opady (1h)", value = "$it ${currentUnits[3]}")
                } ?: DetailItem(icon = Icons.Filled.Grain, label = "Opady (1h)", value = "Brak")
            }
        }
    }
}

@Composable
fun WeatherMoreDetailsViewLandscape(
    weatherResponse: WeatherResponse,
    currentUnits: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                DetailItem(icon = Icons.Filled.Thermostat, label = "Min/Max", value = "${String.format(
                    getDefault(), "%.1f", weatherResponse.main.tempMin)}° / ${String.format(
                    getDefault(), "%.1f", weatherResponse.main.tempMax)}°")
                DetailItem(icon = Icons.Filled.WaterDrop, label = "Wilgotność", value = "${weatherResponse.main.humidity}%")
                DetailItem(icon = Icons.Filled.Compress, label = "Ciśnienie", value = "${weatherResponse.main.pressure} hPa")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                DetailItem(icon = Icons.Filled.Air, label = "Wiatr", value = "${weatherResponse.wind.speed} ${currentUnits[1]}")
                DetailItem(icon = Icons.Filled.Visibility, label = "Widoczność", value = "${weatherResponse.visibility / 1000} ${currentUnits[2]}")
                weatherResponse.rain?.lastHour?.let {
                    DetailItem(icon = Icons.Filled.Grain, label = "Opady (1h)", value = "$it ${currentUnits[3]}")
                } ?: DetailItem(icon = Icons.Filled.Grain, label = "Opady (1h)", value = "Brak")
            }
        }
    }
}

@Composable
fun WeatherBasicDetails(weatherResponse: WeatherResponse, modifier: Modifier = Modifier) {
    DetailItem(icon = Icons.Filled.Thermostat, label = "Min/Max", value = "${String.format(
        getDefault(), "%.1f", weatherResponse.main.tempMin)}° / ${String.format(
        getDefault(), "%.1f", weatherResponse.main.tempMax)}°")
    DetailItem(icon = Icons.Filled.Compress, label = "Ciśnienie", value = "${weatherResponse.main.pressure} hPa")
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

fun getNoonForecasts(weatherForecast: WeatherForecast): List<ListItem> {
    return weatherForecast.list.filter { item ->
        val dateSplit = item.dtTxt.split(" ")
        if (dateSplit.size > 1) {
            val hour = dateSplit[1].substring(0, 2)
            hour == "12"
        } else {
            false
        }
    }
}

@Composable
fun WeatherForecastView(
    weatherForecast: WeatherForecast,
    currentUnits: List<String>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(PaddingValues(top = 4.dp, bottom = 4.dp))
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        getNoonForecasts(weatherForecast).forEach {
            ForecastItemRow(it, currentUnits)
        }
    }
}

fun formatTimestampToDayMonth(timestampSeconds: Int): String {
    return try {
        val sdf = SimpleDateFormat("dd.MM", getDefault())
        val date = Date(timestampSeconds * 1000L)
        sdf.format(date)
    } catch (e: Exception) {
        "N/A"
    }
}

@Composable
fun ForecastItemRow(
    listItem: ListItem,
    currentUnits: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = formatTimestampToDayMonth(listItem.dt),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${listItem.main.tempMin.roundToInt()}° / ${listItem.main.tempMax.roundToInt()}${currentUnits[0]}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${listItem.main.pressure} hPa",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Ciśnienie",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            listItem.weather.firstOrNull()?.let { weatherItem ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://openweathermap.org/img/wn/${weatherItem.icon}@2x.png")
                        .crossfade(true)
                        .build(),
                    contentDescription = weatherItem.description,
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCitiesScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel?,
    favouriteCities: List<String>?,
    newFavouriteCity: String
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AddCityBottomCard(
                newCityName = newFavouriteCity,
                onCityNameChange = { weatherViewModel?.setNewFavouriteCity(it) },
                onAddClick = { weatherViewModel?.addFavouriteCity(newFavouriteCity) }
            )
        }
    ) { paddingValues ->
        if (favouriteCities == null || favouriteCities.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Brak ulubionych miast.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(items = favouriteCities) { city ->
                    FavoriteCityItem(
                        city = city,
                        onRemoveClick = { weatherViewModel?.removeFavouriteCity(city) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        onCityClick = { clickedCityName -> weatherViewModel?.setCurrentCity(clickedCityName) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteCityItem(
    city: String,
    onRemoveClick: () -> Unit,
    onCityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCityClick(city) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = city,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Usuń ${city}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCityBottomCard(
    newCityName: String,
    onCityNameChange: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCityName,
                onValueChange = onCityNameChange,
                label = { Text("Nazwa miasta") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onAddClick, enabled = newCityName.isNotBlank()) {
                Icon(Icons.Filled.Add, contentDescription = "Dodaj miasto")
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Dodaj")
            }
        }
    }
}

@Composable
fun SettingsScreen(
    weatherViewModel: WeatherViewModel?,
    currentSelectedUnit: String,
    currentInterval: String,
    onRefreshClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Wybierz typ jednostek",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        WeatherUnit.entries.forEach { unitKey ->
            UnitOptionItem(
                displayName = unitKey.displayName,
                isSelected = currentSelectedUnit == unitKey.apiValue,
                onOptionSelected = {
                    weatherViewModel?.setCurrentUnits(unitKey.apiValue)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Interwał odświeżania danych (sekundy)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = currentInterval,
            onValueChange = { newValue ->
                if (newValue.length <= 3 && newValue.all { it.isDigit() }) {
                    weatherViewModel?.setRefreshIntervalSeconds(newValue)
                } else if (newValue.isEmpty()) {
                    weatherViewModel?.setRefreshIntervalSeconds("")
                }
            },
            label = { Text("Interwał") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Pozostaw puste lub 0, aby wyłączyć automatyczne odświeżanie.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onRefreshClick) {
                Text("Odśwież dane")
            }
        }
    }
}

@Composable
fun UnitOptionItem(
    displayName: String,
    isSelected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOptionSelected)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Wybrano $displayName",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}