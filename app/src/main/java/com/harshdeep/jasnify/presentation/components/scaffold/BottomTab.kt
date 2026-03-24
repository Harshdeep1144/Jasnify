package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.selects.select
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
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
    ) {
        Column {
            // A single Row where each child is a weighted Column
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(12.dp, 0.dp, 12.dp, 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                items.forEach { item ->
                    val isSelected = item.value == selectedValue

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, // Set to null or LocalIndication.current for ripple
                                onClick = { onItemSelected(item.value) }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. The Top Indicator
                        val indicatorColor by animateColorAsState(
                            targetValue = if (isSelected) activeColor else Color.Transparent,
                            label = "IndicatorAnimation"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                                .height(4.dp)
                                .background(
                                    color = indicatorColor,
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                                )
                        )

                        // 2. The Navigation Item Content
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
            Spacer(modifier = Modifier.navigationBarsPadding())
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

    var selectedValue by remember { mutableIntStateOf(1) }

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