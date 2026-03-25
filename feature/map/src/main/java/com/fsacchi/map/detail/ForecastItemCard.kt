package com.fsacchi.map.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import coil.compose.AsyncImage
import com.fsacchi.domain.model.ForecastItem
import com.fsacchi.ui.compose.spacing
import com.fsacchi.ui.extension.toForecastDateTime
import kotlin.math.roundToInt

@Composable
fun ForecastItemCard(item: ForecastItem) {
    val time = remember(item.timestamp) {
        item.timestamp.toForecastDateTime()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.small
            )
    ) {
        Text(text = time, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${item.iconCode}.png",
            contentDescription = item.description,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "${item.temperature.roundToInt()}°C",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}