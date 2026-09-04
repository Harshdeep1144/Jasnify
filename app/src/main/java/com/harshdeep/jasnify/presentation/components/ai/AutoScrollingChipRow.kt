package com.harshdeep.jasnify.presentation.components.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

fun getContextualChips(initialContext: String?): Pair<List<String>, List<String>> {
    val ctx = initialContext?.lowercase() ?: ""
    return when {
        ctx.contains("budget") || ctx.contains("expense") || ctx.contains("cost") || ctx.contains("spend") -> {
            Pair(
                listOf("Spend log", "Catering cost", "Cost savings tips", "Daily spend", "Category breakdown"),
                listOf("Unplanned costs", "Budget remaining", "Payment history", "Compare spends", "Fund summary")
            )
        }
        ctx.contains("catering") || ctx.contains("menu") || ctx.contains("food") || ctx.contains("dish") -> {
            Pair(
                listOf("Food menu cost", "Per plate cost", "Vegetarian dishes", "Drink options", "Catering budget"),
                listOf("Custom menu ideas", "Dietary preferences", "Desserts list", "Catering vendors", "Meal breakdown")
            )
        }
        ctx.contains("venue") || ctx.contains("hall") || ctx.contains("location") -> {
            Pair(
                listOf("Venue booking cost", "Hall capacity", "Outdoor vs Indoor", "Parking availability", "Venue amenities"),
                listOf("Location comparison", "Venue policies", "Decor guidelines", "Booking status", "Space layout")
            )
        }
        ctx.contains("vendor") || ctx.contains("photographer") || ctx.contains("decorator") || ctx.contains("dj") -> {
            Pair(
                listOf("Photographer costs", "Decorator pricing", "Makeup artist", "DJ & Music", "Vendor payments"),
                listOf("Vendor contact list", "Booking status", "Contract terms", "Reviews & Ratings", "Vendor estimates")
            )
        }
        ctx.contains("checklist") || ctx.contains("task") || ctx.contains("todo") -> {
            Pair(
                listOf("Pending tasks", "Urgent checklist", "Today's tasks", "Task progress", "Completed items"),
                listOf("Timeline breakdown", "Assigned tasks", "Weekly goals", "Vendor tasks", "Payment checklist")
            )
        }
        ctx.contains("guest") || ctx.contains("rsvp") || ctx.contains("invite") -> {
            Pair(
                listOf("Guest RSVP status", "Total headcount", "Invited guests", "Confirmed guests", "Dietary requirements"),
                listOf("Seating arrangement", "Family vs Friends", "Outstation guests", "Accommodation list", "Invitation status")
            )
        }
        ctx.contains("card") || ctx.contains("invitation") -> {
            Pair(
                listOf("Invitation design", "Digital card text", "RSVP link text", "Event schedule", "Card distribution"),
                listOf("Card themes", "Guest invitation status", "Save the date text", "Welcome message", "Design ideas")
            )
        }
        else -> {
            Pair(
                listOf("Spend log", "Catering cost", "Cost savings tips", "Daily spend", "Vendor budget"),
                listOf("Category breakdown", "Analyse my spends", "Compare with budget", "Guest RSVPs", "Venue booking")
            )
        }
    }
}

@Composable
fun AutoScrollingChipRow(
    chips: List<String>,
    onChipClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialScrollOffset: Int = 0,
    reverseDirection: Boolean = false
) {
    val startIndex = if (reverseDirection) (chips.size * 50) else initialScrollOffset
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)

    LaunchedEffect(chips, listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            while (true) {
                if (listState.isScrollInProgress) break
                try {
                    val scrollDelta = if (reverseDirection) -1.2f else 1.2f
                    listState.scrollBy(scrollDelta)
                } catch (_: Exception) {
                    break
                }
                delay(16.milliseconds)
                if (!reverseDirection && !listState.canScrollForward) {
                    listState.scrollToItem(0)
                } else if (reverseDirection && !listState.canScrollBackward) {
                    listState.scrollToItem(chips.size * 50)
                }
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
        userScrollEnabled = true
    ) {
        items(chips.size * 100) { index ->
            val chipText = chips[index % chips.size]
            Surface(
                onClick = { onChipClick(chipText) },
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.9f)),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = chipText,
                    style = JasnifyTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF5D3898),
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
                )
            }
        }
    }
}
