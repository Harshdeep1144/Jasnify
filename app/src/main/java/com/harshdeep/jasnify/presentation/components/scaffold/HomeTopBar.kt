package com.harshdeep.jasnify.presentation.components.scaffold

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    title: String,
    dateString: String,
    onMenuClick: () -> Unit = {},
    alpha: Float = 1f
) {
    val containerColor = BackgroundPrimary.copy(alpha = alpha)
    val contentColor =  ContentPrimary
    val buttonBackground = if (alpha > 0.5f) ButtonBackground.OPAQUE else ButtonBackground.TRANSLUCENT

    // Calculate the status subtitle based on the event date relative to now
    val subtitle = remember(dateString) {
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val eventDate = LocalDate.parse(dateString, formatter).atStartOfDay()

            val now = LocalDateTime.now()
            val today = LocalDate.now()
            val eventDay = eventDate.toLocalDate()
            val duration = Duration.between(now, eventDate)

            when {
                // Logic for events in the future
                !duration.isNegative -> {
                    when {
                        duration.toHours() < 24 -> "${duration.toHours()} hrs remaining"
                        duration.toDays() < 30 -> "${duration.toDays()} days remaining"
                        else -> "${duration.toDays() / 7} weeks remaining"
                    }
                }
                // Logic for events in the past or currently happening
                else -> {
                    when (eventDay) {
                        today -> "Happening"
                        today.minusDays(1) -> "Happened yesterday"
                        else -> {
                            val daysAgo = ChronoUnit.DAYS.between(eventDay, today)
                            "Happened $daysAgo days ago"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = JasnifyTheme.typography.displayMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = contentColor
                )

                Text(
                    text = subtitle,
                    style = JasnifyTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = contentColor,
                )
            }
        }

        TopBarIconButton(
            icon = TopIcon.Predefined.MENU_MODERN,
            onClick = onMenuClick,
            backgroundStyle = buttonBackground,
            iconSize = 24.dp,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeTopBarPreview() {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    JasnifyTheme {
        Column(
            modifier = Modifier.background(BackgroundPrimary).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Example: 2026-11-21 format
            HomeTopBar(title = "Launch", dateString = "2026-11-21")
            HomeTopBar(title = "Vacation", dateString = today.plusDays(1).format(formatter))
            HomeTopBar(title = "Today's Event", dateString = today.format(formatter))
            HomeTopBar(title = "Yesterday's News", dateString = today.minusDays(1).format(formatter))
            HomeTopBar(title = "Old Project", dateString = today.minusDays(10).format(formatter))
        }
    }
}