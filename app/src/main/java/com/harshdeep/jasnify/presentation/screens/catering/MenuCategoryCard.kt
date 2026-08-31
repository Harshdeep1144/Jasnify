package com.harshdeep.jasnify.presentation.screens.catering

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.chip.CateringItemChip
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun Modifier.detectCombinedClicks(
    onTap: () -> Unit,
    onLongPress: () -> Unit
): Modifier {
    val haptic = LocalHapticFeedback.current
    val currentOnTap by rememberUpdatedState(onTap)
    val currentOnLongPress by rememberUpdatedState(onLongPress)

    return this.pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(pass = PointerEventPass.Initial)
            val longPressTimeout = viewConfiguration.longPressTimeoutMillis

            try {
                withTimeout(longPressTimeout) {
                    val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (up != null) {
                        currentOnTap()
                    }
                }
            } catch (_: PointerEventTimeoutCancellationException) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                currentOnLongPress()
            }
        }
    }
}

private val CardTranslucentGradientBrush = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.25f)
    )
)

@Composable
fun MenuCategoryCard(
    categoryTitle: String,
    items: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit,
    onItemLongClick: (MenuItem) -> Unit = {},
    selectedItemIds: Set<String> = emptySet(),
    isSelectionMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val categoryStyle = remember(categoryTitle) { getCategoryStyle(categoryTitle) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = categoryStyle.containerBrush)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = categoryTitle.uppercase(),
                            style = JasnifyTheme.typography.displayLarge.copy(
                                fontFamily = FontFamily(Font(R.font.facadflux_bold)),
                                fontWeight = FontWeight.Bold,
                                color = categoryStyle.headerTextColor,
                                lineHeight = JasnifyTheme.typography.displayLarge.fontSize
                            )
                        )
                        Text(
                            text = "${items.size} ITEMS",
                            style = JasnifyTheme.typography.labelSmall.copy(
                                color = categoryStyle.subtitleTextColor,
                                letterSpacing = 2.sp
                            )
                        )
                    }

                    Image(
                        painter = painterResource(id = categoryStyle.illustrationRes),
                        contentDescription = categoryTitle,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardSquircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(brush = CardTranslucentGradientBrush, shape = CardSquircleShape)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            items.forEach { item ->
                                val isChecked = selectedItemIds.contains(item.id)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .detectCombinedClicks(
                                            onTap = {
                                                focusManager.clearFocus()
                                                onItemClick(item)
                                            },
                                            onLongPress = {
                                                focusManager.clearFocus()
                                                onItemLongClick(item)
                                            }
                                        )
                                ) {
                                    CateringItemChip(
                                        label = item.name,
                                        foodType = item.dietary,
                                        isMultiSelect = isSelectionMode,
                                        checked = isChecked,
                                        onCheckedChange = null,
                                        onClick = {},
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
