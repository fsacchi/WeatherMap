package com.fsacchi.map.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.fsacchi.domain.model.ForecastItem
import com.fsacchi.map.R
import com.fsacchi.ui.compose.LightBlue
import com.fsacchi.ui.compose.spacing
import com.fsacchi.ui.extension.toLocalDate
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PrecipitationCard(forecastItems: List<ForecastItem>) {
    val context = LocalContext.current

    val dailyPrecipitation = remember(forecastItems) {
        val today = LocalDate.now(ZoneId.systemDefault())
        forecastItems
            .groupBy { item -> item.timestamp.toLocalDate() }
            .entries
            .sortedBy { it.key }
            .take(3)
            .map { (date, items) ->
                val label = if (date == today) context.getString(R.string.today)
                else date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
                    .replaceFirstChar { it.uppercase() }
                val maxPop = items.maxOf { it.precipitationProbability }
                label to maxPop
            }
    }

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
                text = stringResource(R.string.precipitation_card_title),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.large)
            )
            dailyPrecipitation.forEach { (label, probability) ->
                PrecipitationRow(label = label, probability = probability)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }
        }
    }
}

@Composable
private fun PrecipitationRow(label: String, probability: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(44.dp)
        )
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            tint = LightBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = probability.toFloat().coerceIn(0f, 1f))
                    .height(6.dp)
                    .background(LightBlue, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
        Text(
            text = stringResource(R.string.precipitation_chance, (probability * 100).toInt()),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(36.dp)
        )
    }
}
