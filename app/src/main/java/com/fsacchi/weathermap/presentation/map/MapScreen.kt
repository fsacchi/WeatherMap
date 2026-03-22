package com.fsacchi.weathermap.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.fsacchi.weathermap.R
import com.fsacchi.weathermap.ui.theme.spacing
import androidx.core.content.ContextCompat
import com.fsacchi.weathermap.domain.model.GeocodingResult
import com.fsacchi.weathermap.domain.model.WeatherData
import com.fsacchi.weathermap.ui.theme.WeatherMapTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.roundToInt
import androidx.core.graphics.createBitmap
import coil.compose.AsyncImage

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateToDetail: (Double, Double) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.recenterOnGps()
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.uiState.value.currentLat != null) return@LaunchedEffect
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            viewModel.recenterOnGps()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    MapScreenContent(
        uiState = uiState,
        onAction = { action ->
            when (action) {
                is MapAction.NavigateToHistory -> onNavigateToHistory()
                is MapAction.NavigateToDetail -> {
                    uiState.pinWeatherData?.let {
                        onNavigateToDetail(it.lat, it.lon)
                    }
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenContent(
    uiState: MapUiState,
    onAction: (MapAction) -> Unit
) {
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                Configuration.getInstance().userAgentValue = ctx.packageName
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(13.0)
                    mapViewRef.value = this
                    setOnLongClickListener { false }
                    overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                        override fun onSingleTapConfirmed(e: android.view.MotionEvent?, mapView: MapView?): Boolean {
                            val projection = mapView?.projection ?: return false
                            val geoPoint = projection.fromPixels(e?.x?.toInt() ?: 0, e?.y?.toInt() ?: 0)
                            onAction(MapAction.TapOnMap(geoPoint.latitude, geoPoint.longitude))
                            return true
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(uiState.currentLat, uiState.currentLon) {
            val lat = uiState.currentLat ?: return@LaunchedEffect
            val lon = uiState.currentLon ?: return@LaunchedEffect
            mapViewRef.value?.let { map ->
                map.controller.animateTo(GeoPoint(lat, lon))
                map.overlays.removeAll { it is Marker }

                val marker = Marker(map).apply {
                    position = GeoPoint(lat, lon)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    uiState.pinWeatherData?.let { weather ->
                        val temp = "${weather.temperature.roundToInt()}°"
                        icon = createWeatherMarkerBitmap(temp)
                    }
                    setOnMarkerClickListener { _, _ ->
                        onAction(MapAction.NavigateToDetail)
                        true
                    }
                }
                map.overlays.add(marker)
                map.invalidate()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = MaterialTheme.spacing.large)
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { onAction(MapAction.SearchCity(it)) },
                onSearch = { onAction(MapAction.SearchCity(it)) },
                active = false,
                onActiveChange = {},
                placeholder = { Text(stringResource(R.string.search_city_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { onAction(MapAction.NavigateToHistory) }) {
                        Icon(Icons.Default.History, contentDescription = stringResource(R.string.history_label))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp)
            ) {}

            AnimatedVisibility(visible = uiState.searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    shape = RoundedCornerShape(
                        bottomStart = MaterialTheme.spacing.large,
                        bottomEnd = MaterialTheme.spacing.large
                    )
                ) {
                    LazyColumn {
                        items(uiState.searchResults) { result ->
                            ListItem(
                                headlineContent = { Text(result.name) },
                                supportingContent = { Text(result.country) },
                                leadingContent = { Icon(Icons.Default.WbSunny, null) },
                                modifier = Modifier.clickable {
                                    keyboardController?.hide()
                                    onAction(MapAction.SelectSearchResult(result))
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        uiState.pinWeatherData?.let { weather ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = MaterialTheme.spacing.large)
                    .padding(bottom = MaterialTheme.spacing.large)
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .clickable { onAction(MapAction.NavigateToDetail) },
                shape = RoundedCornerShape(MaterialTheme.spacing.large)
            ) {
                Row(
                    modifier = Modifier.padding(MaterialTheme.spacing.large),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${weather.iconCode}@2x.png",
                        contentDescription = weather.description,
                        modifier = Modifier.size(40.dp)
                            .padding(end = MaterialTheme.spacing.small)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${weather.cityName}, ${weather.country}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weather.description.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${weather.temperature.roundToInt()}°C",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.tap_for_details),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { onAction(MapAction.RecenterOnGps) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = MaterialTheme.spacing.large)
                .padding(bottom = if (uiState.pinWeatherData != null) 120.dp else MaterialTheme.spacing.large)
                .navigationBarsPadding()
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = stringResource(R.string.recenter_content_desc))
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(MaterialTheme.spacing.large)
                    .navigationBarsPadding(),
                action = {
                    TextButton(onClick = { onAction(MapAction.DismissError) }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            ) {
                Text(error)
            }
        }
    }
}

private fun createWeatherMarkerBitmap(tempText: String): android.graphics.drawable.BitmapDrawable {
    val size = 240
    val center = size / 2f
    val radius = center - 4
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)

    Paint(Paint.ANTI_ALIAS_FLAG).also { paint ->
        paint.color = android.graphics.Color.argb(220, 13, 27, 42)
        canvas.drawCircle(center, center, radius, paint)

        paint.color = android.graphics.Color.argb(255, 46, 109, 164)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawCircle(center, center, radius, paint)

        paint.color = android.graphics.Color.WHITE
        paint.style = Paint.Style.FILL
        paint.textSize = 64f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(tempText, center, center - (paint.descent() + paint.ascent()) / 2, paint)
    }

    return android.graphics.drawable.BitmapDrawable(null, bitmap)
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    WeatherMapTheme {
        MapScreenContent(
            uiState = MapUiState(
                pinWeatherData = WeatherData(
                    cityName = "São Paulo",
                    country = "BR",
                    temperature = 28.0,
                    feelsLike = 30.0,
                    tempMin = 22.0,
                    tempMax = 32.0,
                    humidity = 65,
                    pressure = 1013,
                    windSpeed = 3.5,
                    sunrise = 0,
                    sunset = 0,
                    description = "céu limpo",
                    iconCode = "01d",
                    lat = -23.5,
                    lon = -46.6
                )
            ),
            onAction = {}
        )
    }
}
