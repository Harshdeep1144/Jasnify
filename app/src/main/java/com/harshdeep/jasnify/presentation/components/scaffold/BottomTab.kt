package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlin.math.roundToInt
import sv.lib.squircleshape.SquircleShape

enum class BottomTabStyle {
    STANDARD,
    FLOATING
}

data class TabItem<T>(
    val label: String,
    val value: T,
    val badgeCount: Int? = null,
    val icon: Painter? = null
)

private data class TabBounds(
    val left: Dp = 0.dp,
    val width: Dp = 165.dp
)

@Composable
fun <T> BottomTab(
    items: List<TabItem<T>>,
    selectedValue: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    style: BottomTabStyle = BottomTabStyle.STANDARD,
    activeColor: Color = ContentBrandDark,
    activeBg: Color = SurfaceBrandSecondary
) {
    when (style) {
        BottomTabStyle.STANDARD -> StandardBottomTab(
            items = items,
            selectedValue = selectedValue,
            onItemSelected = onItemSelected,
            modifier = modifier,
            activeColor = activeColor
        )

        BottomTabStyle.FLOATING -> FloatingBottomTab(
            items = items,
            selectedValue = selectedValue,
            onItemSelected = onItemSelected,
            modifier = modifier,
            activeColor = activeColor,
            activeBg = activeBg
        )
    }
}

@Composable
private fun <T> StandardBottomTab(
    items: List<TabItem<T>>,
    selectedValue: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = ContentBrandDark,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
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
                        val selectedIndex = items
                            .indexOfFirst { it.value == selectedValue }
                            .coerceAtLeast(0)

                        val animatedIndex by animateFloatAsState(
                            targetValue = selectedIndex.toFloat(),
                            animationSpec = spring(
                                dampingRatio = 0.82f,
                                stiffness = 380f
                            ),
                            label = "StandardIndicatorAnimation"
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
                                        interactionSource = remember { MutableInteractionSource() },
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
private fun <T> FloatingBottomTab(
    items: List<TabItem<T>>,
    selectedValue: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = ContentBrandDark,
    activeBg: Color = SurfaceBrandSecondary
) {
    val density = LocalDensity.current
    var tabBoundsMap by remember { mutableStateOf(mapOf<Int, TabBounds>()) }

    val selectedIndex = remember(items, selectedValue) {
        items.indexOfFirst { it.value == selectedValue }.coerceAtLeast(0)
    }

    // Current target bounds for the indicator
    val targetBounds = tabBoundsMap[selectedIndex] ?: TabBounds()

    // Smooth spring animation for offset and width
    val animatedLeft by animateDpAsState(
        targetValue = targetBounds.left,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "FloatingIndicatorLeft"
    )

    val animatedWidth by animateDpAsState(
        targetValue = targetBounds.width,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "FloatingIndicatorWidth"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(brush = BottomGradientBrush)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.pill360Shadow(
                ambientColor = Color.Black.copy(alpha = 0.10f),
                ambientBlur = 12.dp,
                ambientSpread = 2.dp,
                spotColor = Color.Black.copy(alpha = 0.15f),
                spotBlur = 18.dp,
                spotOffsetY = 4.dp
            ),
            color = SurfacePrimary,
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .wrapContentSize()
            ) {
                // Sliding Pill Background
                if (tabBoundsMap.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .offset(x = animatedLeft)
                            .width(animatedWidth)
                            .height(56.dp)
                            .background(
                                color = activeBg,
                                shape = CircleShape
                            )
                    )
                }

                // Row of Tabs
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = item.value == selectedValue

                        Box(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 165.dp)
                                .height(56.dp)
                                .onGloballyPositioned { coordinates ->
                                    with(density) {
                                        val left = coordinates.positionInParent().x.toDp()
                                        val width = coordinates.size.width.toDp()

                                        // Update bounds only when measurements actually change
                                        val current = tabBoundsMap[index]
                                        if (current == null || current.left != left || current.width != width) {
                                            tabBoundsMap = tabBoundsMap + (index to TabBounds(left, width))
                                        }
                                    }
                                }
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onItemSelected(item.value)
                                }
                                .padding(horizontal = 20.dp),
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
        if (item.icon != null) {
            Icon(
                painter = item.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

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
            fontWeight = FontWeight.Medium,
            style = JasnifyTheme.typography.displaySmall
        )
    }
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
fun PreviewStandardBottomTab() {
    var selectedValue by remember { mutableIntStateOf(1) }

    val items = listOf(
        TabItem("Home", 1, badgeCount = 24),
        TabItem("Profile", 2, badgeCount = 5),
    )
    BottomTab(
        items = items,
        selectedValue = selectedValue,
        onItemSelected = { newValue -> selectedValue = newValue },
        style = BottomTabStyle.STANDARD
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFloatingBottomTab() {
    var selectedValue by remember { mutableIntStateOf(1) }

    val items = listOf(
        TabItem("Explore Content", 1),
        TabItem("Saved Items & Collections", 2),
    )
    BottomTab(
        items = items,
        selectedValue = selectedValue,
        onItemSelected = { newValue -> selectedValue = newValue },
        style = BottomTabStyle.FLOATING
    )
}