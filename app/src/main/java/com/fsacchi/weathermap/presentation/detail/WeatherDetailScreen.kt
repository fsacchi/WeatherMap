package com.fsacchi.weathermap.presentation.detail

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fsacchi.weathermap.R
import com.fsacchi.weathermap.core.extensions.shareScreenshot
import com.fsacchi.weathermap.domain.model.ForecastItem
import com.fsacchi.weathermap.domain.model.WeatherCondition
import com.fsacchi.weathermap.domain.model.WeatherData
import com.fsacchi.weathermap.ui.theme.ClearSkyColor
import com.fsacchi.weathermap.ui.theme.CloudyColor
import com.fsacchi.weathermap.ui.theme.DeepNavy
import com.fsacchi.weathermap.ui.theme.RainyColor
import com.fsacchi.weathermap.ui.theme.SnowyColor
import com.fsacchi.weathermap.ui.theme.StormyColor
import com.fsacchi.weathermap.ui.theme.WeatherMapTheme
import com.fsacchi.weathermap.ui.theme.spacing
import com.fsacchi.weathermap.core.extensions.toTimeHHmm
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun WeatherDetailScreen(
    lat: Double,
    lon: Double,
    viewModel: WeatherDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lat, lon) {
        viewModel.onAction(WeatherDetailAction.LoadWeather(lat, lon))
    }

    WeatherDetailContent(
        uiState = uiState,
        onAction = { action ->
            when (action) {
                is WeatherDetailAction.NavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailContent(
    uiState: WeatherDetailUiState,
    onAction: (WeatherDetailAction) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.shareTriggered) {
        if (!uiState.shareTriggered || uiState.weather == null) return@LaunchedEffect
        onAction(WeatherDetailAction.ResetShare)

        coroutineScope.launch {
            try {
                val shareText = context.getString(
                    R.string.share_weather_text,
                    uiState.weather.cityName,
                    uiState.weather.temperature.roundToInt(),
                    uiState.weather.description
                )
                val chooserTitle = context.getString(R.string.share_chooser_title)
                (context as Activity).shareScreenshot(view.width, view.height, shareText, chooserTitle)
            } catch (_: Exception) {
                onAction(WeatherDetailAction.ShowError(context.getString(R.string.error_share_screen)))
            }
        }
    }

    val gradientColors = uiState.weather?.let { getGradientColors(it) }
        ?: listOf(DeepNavy, Color(0xFF1B2A4A))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.small,
                        vertical = MaterialTheme.spacing.extraSmall
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onAction(WeatherDetailAction.NavigateBack) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_content_desc),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onAction(WeatherDetailAction.Share) }) {
                    Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share_content_desc), tint = Color.White)
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            uiState.weather?.let { weather ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                }
                Text(
                    text = "${weather.cityName}, ${weather.country}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xxLarge)
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.xxLarge),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${weather.iconCode}@2x.png",
                        contentDescription = weather.description,
                        modifier = Modifier.size(80.dp)
                    )
                    Column {
                        Text(
                            text = "${weather.temperature.roundToInt()}°C",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = weather.description.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.xxLarge),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.temp_min, weather.tempMin.roundToInt()),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.temp_max, weather.tempMax.roundToInt()),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.feels_like, weather.feelsLike.roundToInt()),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxLarge))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.large),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(MaterialTheme.spacing.xLarge)
                ) {
                    Column(modifier = Modifier.padding(MaterialTheme.spacing.xLarge)) {
                        Text(
                            text = stringResource(R.string.details_card_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = MaterialTheme.spacing.large)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherDetailItem(
                                icon = Icons.Default.WaterDrop,
                                label = stringResource(R.string.humidity_label),
                                value = "${weather.humidity}%"
                            )
                            WeatherDetailItem(
                                icon = Icons.Default.Air,
                                label = stringResource(R.string.wind_label),
                                value = "${weather.windSpeed} m/s"
                            )
                            WeatherDetailItem(
                                icon = Icons.Default.Compress,
                                label = stringResource(R.string.pressure_label),
                                value = "${weather.pressure} hPa"
                            )
                        }

                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SunTimeItem(label = stringResource(R.string.sunrise_label), timestamp = weather.sunrise)
                            SunTimeItem(label = stringResource(R.string.sunset_label), timestamp = weather.sunset)
                        }
                    }
                }

                if (uiState.forecast.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxLarge))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.large),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(MaterialTheme.spacing.xLarge)
                    ) {
                        Column(modifier = Modifier.padding(MaterialTheme.spacing.xLarge)) {
                            Text(
                                text = stringResource(R.string.forecast_card_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = MaterialTheme.spacing.large)
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                                items(uiState.forecast) { item ->
                                    ForecastItemCard(item = item)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxLarge))
                    PrecipitationCard(forecastItems = uiState.forecast)
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxLarge))
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(MaterialTheme.spacing.large),
                    action = {
                        TextButton(onClick = { onAction(WeatherDetailAction.DismissError) }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                ) { Text(error) }
            }

        }
        Image(
            painter = painterResource(id = R.drawable.open_weather),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = stringResource(R.string.by_open_weather),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = MaterialTheme.spacing.xxLarge)
                .width(150.dp)
        )
    }
}

@Composable
private fun WeatherDetailItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(text = label, color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SunTimeItem(label: String, timestamp: Long) {
    val time = remember(timestamp) {
        timestamp.toTimeHHmm()
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
        Text(text = time, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}



private fun getGradientColors(weather: WeatherData): List<Color> {
    return when (WeatherCondition.fromIconCode(weather.iconCode)) {
        WeatherCondition.CLEAR_SKY -> listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
        WeatherCondition.FEW_CLOUDS,
        WeatherCondition.SCATTERED_CLOUDS -> listOf(CloudyColor, Color(0xFF37474F))
        WeatherCondition.BROKEN_CLOUDS -> listOf(Color(0xFF455A64), Color(0xFF263238))
        WeatherCondition.SHOWER_RAIN,
        WeatherCondition.RAIN -> listOf(RainyColor, Color(0xFF01579B))
        WeatherCondition.THUNDERSTORM -> listOf(StormyColor, Color(0xFF212121))
        WeatherCondition.SNOW -> listOf(SnowyColor, Color(0xFF546E7A))
        WeatherCondition.MIST,
        WeatherCondition.UNKNOWN -> listOf(ClearSkyColor, DeepNavy)
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherDetailPreview() {
    WeatherMapTheme {
        WeatherDetailContent(
            uiState = WeatherDetailUiState(
                weather = WeatherData(
                    cityName = "São Paulo",
                    country = "BR",
                    temperature = 28.0,
                    feelsLike = 30.0,
                    tempMin = 22.0,
                    tempMax = 32.0,
                    humidity = 65,
                    pressure = 1013,
                    windSpeed = 3.5,
                    sunrise = 1710835200L,
                    sunset = 1710878400L,
                    description = "céu limpo",
                    iconCode = "0ed",
                    lat = -23.5,
                    lon = -46.6
                ),
                forecast = listOf(
                    ForecastItem(1710835200L, 25.0, "nublado", "04d", precipitationProbability = 0.3),
                    ForecastItem(1710846000L, 23.0, "chuva leve", "10d", precipitationProbability = 0.75),
                    ForecastItem(1710856800L, 20.0, "chuva", "09d", precipitationProbability = 0.9)
                )
            ),
            onAction = {}
        )
    }
}

