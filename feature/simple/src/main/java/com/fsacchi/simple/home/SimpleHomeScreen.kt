package com.fsacchi.weathermap

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fsacchi.domain.model.WeatherData
import com.fsacchi.domain.util.Result
import com.fsacchi.domain.usecase.GetWeatherByLocationUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val LAT_DEFAULT_SAO_PAULO = -23.5505
private const val LONG_DEFAULT_SAO_PAULO = -46.6333

@Composable
fun SimpleHomeScreen(
    useCase: GetWeatherByLocationUseCase = koinInject()
) {
    var result by remember { mutableStateOf<Result<WeatherData>?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                scope.launch {
                    result = useCase(LAT_DEFAULT_SAO_PAULO, LONG_DEFAULT_SAO_PAULO)
                }
            }) {
                Text("Buscar clima de São Paulo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val r = result) {
                is Result.Success -> Text("Temperatura: ${r.data.temperature}°C")
                is Result.Error   -> Text("Erro: ${r.message}")
                null              -> Unit
                Result.Loading -> Unit
            }
        }
    }
}
