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
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.EmojiPeople
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.JasnifyTypography
import com.harshdeep.jasnify.theme.Outfit
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
fun CategoryCard(
    title: String,
    amount: String,
    modifier: Modifier = Modifier,
    icons: List<Painter> = emptyList(),
    totalItemCount: Int = icons.size,
    onMenuClick: () -> Unit = {},
    menuIconPainter: Painter? = null
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
            // Overlapping icon stack component
            if (totalItemCount > 0) {
                IconStack(
                    icons = icons,
                    totalCount = totalItemCount,
                )
            }
            Spacer(Modifier.width(12.dp))

            // Custom small icon square tertiary default button
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


@Composable
private fun IconStack(
    icons: List<Painter>,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val maxVisibleIcons = 2
    val displayedIcons = icons.take(maxVisibleIcons)
    val showBadge = totalCount > maxVisibleIcons
    val badgeValue = totalCount - maxVisibleIcons

    // Negative horizontal layout spacing creates standard UI overlap sequence (right elements layered on top)
    Row(
        horizontalArrangement = Arrangement.spacedBy((-14).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        displayedIcons.forEach { painter ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = SurfaceBrandSecondary, shape = CircleShape)
                    .border(width = 0.5.dp, color = ContentBrand, shape = CircleShape)
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
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

// --- Preview Layers matching Mockup Sheets ---

@Preview(showBackground = true, name = "Category Cards Stack Preview")
@Composable
private fun CategoryCardPreview() {
    Column {
        // Case 1: Multiple Icons with Badge (+1)
        CategoryCard(
            title = "Catering",
            amount = "₹12,45,000",
            icons = listOf(
                rememberVectorPainter(Icons.Outlined.Fastfood),
                rememberVectorPainter(Icons.Outlined.Restaurant),
                rememberVectorPainter(Icons.Outlined.Restaurant),
                rememberVectorPainter(Icons.Outlined.Restaurant),
                rememberVectorPainter(Icons.Outlined.Restaurant)
            ),
            totalItemCount = 5
        )

        // Case 2: Multiple Icons with larger Badge (+3)
        CategoryCard(
            title = "Equipment Rentals",
            amount = "₹4,79,990",
            icons = listOf(
                rememberVectorPainter(Icons.Outlined.Videocam),
                rememberVectorPainter(Icons.Outlined.Mic)
            ),
            totalItemCount = 5
        )

        // Case 3: Exactly Two Icons (No badge)
        CategoryCard(
            title = "Transportation",
            amount = "₹2,52,600",
            icons = listOf(
                rememberVectorPainter(Icons.Outlined.DirectionsCar),
                rememberVectorPainter(Icons.Outlined.LocalActivity)
            ),
            totalItemCount = 2
        )

        // Case 4: Exactly Two Icons (No badge - Staff / Crew alternative)
        CategoryCard(
            title = "Staff & Crew",
            amount = "₹38,000",
            icons = listOf(
                rememberVectorPainter(Icons.Outlined.Person),
                rememberVectorPainter(Icons.Outlined.EmojiPeople)
            ),
            totalItemCount = 2
        )

        // Case 5: Single Icon
        CategoryCard(
            title = "Unplanned Costs",
            amount = "₹24,650",
            icons = listOf(rememberVectorPainter(Icons.Outlined.CreditCard)),
            totalItemCount = 1
        )

        // Case 6: No Icons
        CategoryCard(
            title = "Gifts",
            amount = "₹0",
            icons = emptyList(),
            totalItemCount = 0
        )
    }
}