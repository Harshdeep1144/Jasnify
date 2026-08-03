package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

data class TabItem<T>(
    val label: String,
    val value: T,
    val badgeCount: Int? = null
)

@Composable
fun <T> BottomTab(
    items: List<TabItem<T>>,
    selectedValue: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = ContentBrandDark
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Top shadow casting upwards, using graphicsLayer to avoid adding layout height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .align(Alignment.TopCenter)
                .graphicsLayer { translationY = -12.dp.toPx() }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.05f)
                        )
                    )
                )
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BackgroundPrimary,
            shadowElevation = 8.dp,
            tonalElevation = 2.dp
        ) {
            Column {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    val totalWidth = maxWidth
                    val tabCount = items.size

                    if (tabCount > 0) {
                        val tabWidth = totalWidth / tabCount
                        val selectedIndex =
                            items.indexOfFirst { it.value == selectedValue }
                                .coerceAtLeast(0)

                        val animatedIndex by animateFloatAsState(
                            targetValue = selectedIndex.toFloat(),
                            animationSpec = spring(
                                dampingRatio = 0.82f,
                                stiffness = 380f
                            ),
                            label = "IndicatorSlidingAnimation"
                        )

                        Box(
                            modifier = Modifier
                                .width(tabWidth)
                                .height(4.dp)
                                .offset(x = tabWidth * animatedIndex)
                                .padding(horizontal = 4.dp)
                                .background(
                                    color = activeColor,
                                    shape = RoundedCornerShape(
                                        bottomStart = 8.dp,
                                        bottomEnd = 8.dp
                                    )
                                )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        items.forEach { item ->
                            val isSelected = item.value == selectedValue

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        },
                                        indication = null
                                    ) {
                                        onItemSelected(item.value)
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    BottomNavItemContent(
                                        item = item,
                                        isSelected = isSelected,
                                        activeColor = activeColor
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun <T> BottomNavItemContent(
    item: TabItem<T>,
    isSelected: Boolean,
    activeColor: Color
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else ContentTertiary,
        label = "ContentColorAnimation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (item.badgeCount != null) {
            Surface(
                color = contentColor,
                shape = SquircleShape(100, 0.1f),
                modifier = Modifier
                    .width(32.dp)
                    .height(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = item.badgeCount.toString(),
                        color = ContentInvPrimary,
                        style = JasnifyTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = item.label,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            style = JasnifyTheme.typography.displaySmall
        )
    }
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
fun PreviewGenericBottomTab() {
    var selectedValue by remember { mutableStateOf(1) }

    val items = listOf(
        TabItem("Home", 1, badgeCount = 24),
        TabItem("Profile", 2, badgeCount = 5),
    )
    BottomTab(
        items = items,
        selectedValue = selectedValue,
        onItemSelected = { newValue ->
            selectedValue = newValue
        }
    )
}