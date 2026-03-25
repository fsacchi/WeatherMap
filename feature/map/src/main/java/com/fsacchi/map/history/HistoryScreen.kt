package com.fsacchi.map.history

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fsacchi.domain.model.WeatherHistory
import com.fsacchi.map.R
import com.fsacchi.ui.compose.ErrorColor
import com.fsacchi.ui.compose.WeatherMapTheme
import com.fsacchi.ui.compose.spacing
import com.fsacchi.ui.extension.toHistoryDateTime
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit,
    onSelectHistory: (Double, Double) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HistoryContent(
        uiState = uiState,
        onAction = { action ->
            when (action) {
                is HistoryAction.NavigateBack -> onNavigateBack()
                is HistoryAction.SelectItem -> onSelectHistory(action.history.lat, action.history.lon)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    onAction: (HistoryAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_label)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(HistoryAction.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_content_desc))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.isEmpty -> {
                    EmptyHistoryState(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(R.string.clean_history),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ErrorColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.xxLarge)
                                .clickable{
                                    onAction(HistoryAction.DeleteAll)
                                },
                            textAlign = TextAlign.Center,
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = MaterialTheme.spacing.small)
                        ) {
                            items(
                                items = uiState.items,
                                key = { it.id }
                            ) { item ->
                                SwipeToDismissHistoryItem(
                                    item = item,
                                    onDelete = { onAction(HistoryAction.DeleteItem(item.id)) },
                                    onClick = { onAction(HistoryAction.SelectItem(item)) }
                                )
                            }
                        }
                    }

                }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(MaterialTheme.spacing.large),
                    action = {
                        TextButton(onClick = { onAction(HistoryAction.DismissError) }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                ) { Text(error) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissHistoryItem(
    item: WeatherHistory,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.large, vertical = MaterialTheme.spacing.extraSmall)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_content_desc),
                    tint = Color.White,
                    modifier = Modifier.padding(end = MaterialTheme.spacing.large)
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        HistoryItemCard(item = item, onClick = onClick)
    }
}

@Composable
private fun HistoryItemCard(item: WeatherHistory, onClick: () -> Unit) {
    val dateTime = remember(item.timestamp) {
        item.timestamp.toHistoryDateTime()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.large, vertical = MaterialTheme.spacing.extraSmall)
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(MaterialTheme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${item.iconCode}@2x.png",
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.cityName}, ${item.country}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${item.temperature.roundToInt()}°C",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyHistoryState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(MaterialTheme.spacing.xxxLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.WbCloudy,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        Text(
            text = stringResource(R.string.empty_history_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        Text(
            text = stringResource(R.string.empty_history_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    WeatherMapTheme {
        HistoryContent(
            uiState = HistoryUiState(
                items = listOf(
                    WeatherHistory(1, "São Paulo", "BR", 28.0, "01d", -23.5, -46.6, System.currentTimeMillis()),
                    WeatherHistory(2, "Rio de Janeiro", "BR", 32.0, "02d", -22.9, -43.1, System.currentTimeMillis() - 3600000),
                    WeatherHistory(3, "Curitiba", "BR", 18.0, "10d", -25.4, -49.2, System.currentTimeMillis() - 7200000)
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryEmptyPreview() {
    WeatherMapTheme {
        HistoryContent(
            uiState = HistoryUiState(items = emptyList()),
            onAction = {}
        )
    }
}
