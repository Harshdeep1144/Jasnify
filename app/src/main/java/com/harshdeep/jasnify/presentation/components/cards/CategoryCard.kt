package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
fun CategoryCard(
    title: String,
    amount: String,
    modifier: Modifier = Modifier,
    emojis: List<String> = emptyList(),
    totalItemCount: Int = emojis.size,
    onMenuClick: () -> Unit = {},
    menuIconPainter: Painter? = null,
    showMenu: Boolean = true
) {
    val cardShape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = SurfacePrimary, shape = cardShape)
            .padding(16.dp, 16.dp, 8.dp, 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Left Information Column ---
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary
            )
        }

        // --- Right Content & Action Row ---
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Overlapping emoji stack component
            if (totalItemCount > 0) {
                EmojiStack(
                    emojis = emojis,
                    totalCount = totalItemCount,
                )
            }
            Spacer(Modifier.width(12.dp))

            if (showMenu) {
                CustomIconButton(
                    onClick = onMenuClick,
                    icon = menuIconPainter ?: rememberVectorPainter(Icons.Default.MoreVert),
                    size = ButtonSize.Small,
                    type = ButtonType.Tertiary,
                    shapeStyle = ButtonShapeStyle.Square,
                )
            }
        }
    }
}


@Composable
private fun EmojiStack(
    emojis: List<String>,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val maxVisibleIcons = 2
    val displayedEmojis = emojis.take(maxVisibleIcons)
    val showBadge = totalCount > maxVisibleIcons
    val badgeValue = totalCount - maxVisibleIcons

    // Negative horizontal layout spacing creates standard UI overlap sequence
    Row(
        horizontalArrangement = Arrangement.spacedBy((-14).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        displayedEmojis.forEach { emoji ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = SurfaceBrandSecondary, shape = CircleShape)
                    .border(width = 0.5.dp, color = ContentBrand, shape = CircleShape)
            ) {
                Text(
                    text = emoji,
                    fontSize = 20.sp
                )
            }
        }

        // Display "+N" counter bubble overlay
        if (showBadge) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = SurfaceBrandSecondary, shape = CircleShape)
                    .border(width = 0.5.dp, color = ContentBrand, shape = CircleShape)
            ) {
                Text(
                    text = "+$badgeValue",
                    style = JasnifyTheme.typography.displaySmall,
                    color = ContentBrandDark
                )
            }
        }
    }
}

// --- Preview  ---

@Preview(showBackground = true, name = "Category Cards Stack Preview")
@Composable
private fun CategoryCardPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Case 1: Multiple Emojis with Badge (+3)
        CategoryCard(
            title = "Catering",
            amount = "₹12,45,000",
            emojis = listOf("🍔", "🍕", "🍰", "🍩", "🍣"),
            totalItemCount = 5,
            showMenu = true
        )

        // Case 2: Multiple Emojis with larger Badge (+3)
        CategoryCard(
            title = "Equipment Rentals",
            amount = "₹4,79,990",
            emojis = listOf("📹", "🎙️"),
            totalItemCount = 5,
            showMenu = true
        )

        // Case 3: Exactly Two Emojis (No badge)
        CategoryCard(
            title = "Transportation",
            amount = "₹2,52,600",
            emojis = listOf("🚗", "🎫"),
            totalItemCount = 2,
            showMenu = true
        )

        // Case 4: Exactly Two Emojis (No badge - Staff / Crew alternative)
        CategoryCard(
            title = "Staff & Crew",
            amount = "₹38,000",
            emojis = listOf("🧑", "🙌"),
            totalItemCount = 2,
            showMenu = true
        )

        // Case 5: Single Emoji
        CategoryCard(
            title = "Unplanned Costs",
            amount = "₹24,650",
            emojis = listOf("💳"),
            totalItemCount = 1,
            showMenu = true
        )

        // Case 6: No Emojis
        CategoryCard(
            title = "Gifts",
            amount = "₹0",
            emojis = emptyList(),
            totalItemCount = 0,
            showMenu = true
        )
    }
}