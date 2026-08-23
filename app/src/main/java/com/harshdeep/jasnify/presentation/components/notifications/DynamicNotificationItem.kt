package com.harshdeep.jasnify.presentation.components.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.notifications.model.NotificationConfig

@Composable
fun DynamicNotificationItem(
    config: NotificationConfig,
    onClick: () -> Unit
) {
    val bgColor = config.backgroundColor?.let { parseColor(it) } ?: MaterialTheme.colorScheme.surfaceVariant
    val textColor = config.textColor?.let { parseColor(it) } ?: MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        when (config.uiType) {
            "promo" -> PromoStyle(config, textColor)
            "alert" -> AlertStyle(config, textColor)
            else -> StandardStyle(config, textColor)
        }
    }
}

@Composable
private fun StandardStyle(config: NotificationConfig, textColor: Color) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = config.title, style = MaterialTheme.typography.titleMedium, color = textColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = config.body, style = MaterialTheme.typography.bodyMedium, color = textColor)
    }
}

@Composable
private fun PromoStyle(config: NotificationConfig, textColor: Color) {
    Column {
        config.imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = config.title, style = MaterialTheme.typography.headlineSmall, color = textColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = config.body, style = MaterialTheme.typography.bodyLarge, color = textColor)
            config.buttonText?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* Action taken by parent clickable */ }) {
                    Text(text = it)
                }
            }
        }
    }
}

@Composable
private fun AlertStyle(config: NotificationConfig, textColor: Color) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // You could add an icon here based on config.metadata
        Column(modifier = Modifier.weight(1f)) {
            Text(text = config.title, style = MaterialTheme.typography.titleLarge, color = textColor, fontWeight = FontWeight.ExtraBold)
            Text(text = config.body, style = MaterialTheme.typography.bodyMedium, color = textColor)
        }
    }
}

private fun parseColor(colorString: String): Color? {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        null
    }
}
