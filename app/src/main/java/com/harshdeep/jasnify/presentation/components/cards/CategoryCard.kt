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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.Outfit
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

/**
 * A highly customizable Category Card component built to match the visual specification sheets.
 *
 * @param title The category name string (e.g. "Catering").
 * @param amount The formatted total cost or budget allocation value (e.g. "₹12,45,000").
 * @param modifier Custom modifier for external layouts or sizing adjustments.
 * @param icons List of category item icon painters to stack visually.
 * @param totalItemCount The actual number of assets/items. Displays a "+N" badge overlay if larger than visible slots (2).
 * @param onMenuClick Callback executed when the three-dot action button is tapped.
 * @param menuIconPainter Custom icon painter for the menu button. Defaults to standard vertical three dots.
 */
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
    // Custom Squircle layout container shape
    val cardShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = SurfacePrimary, shape = cardShape)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        // --- Left Information Column ---
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontFamily = Outfit,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                fontSize = 15.sp,
                fontFamily = Outfit,
                fontWeight = FontWeight.Normal,
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
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Custom small icon square tertiary default button
            CustomIconButton(
                onClick = onMenuClick,
                icon = menuIconPainter ?: rememberVectorPainter(Icons.Default.MoreVert),
                size = ButtonSize.Small,
                type = ButtonType.Tertiary,
                shapeStyle = ButtonShapeStyle.Square,
                containerColor = Color.Transparent, // Matching flat borderless design
                contentColor = ContentPrimary
            )
        }
    }
}

/**
 * Renders an overlapping horizontal circular icon bubble sequence with dynamic badge indicator.
 */
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
                    .background(color = Color(0xFFE2EDEB), shape = CircleShape) // Standardized soft mint background
                    .border(width = 1.dp, color = Color(0xFFCDDAD8), shape = CircleShape)
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = Color(0xFF2E4D48),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Display "+N" counter bubble overlay
        if (showBadge) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = Color(0xFFE2EDEB), shape = CircleShape)
                    .border(width = 1.dp, color = Color(0xFFCDDAD8), shape = CircleShape)
            ) {
                Text(
                    text = "+$badgeValue",
                    fontSize = 15.sp,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E4D48)
                )
            }
        }
    }
}

// --- Preview Layers matching Mockup Sheets ---

@Preview(showBackground = true, name = "Category Cards Stack Preview")
@Composable
private fun CategoryCardPreview() {
    Column(
        modifier = Modifier
            .background(color = Color(0xFFE6E6E6))
            .padding(16.dp)
            .width(360.dp)
    ) {
        Text(
            text = "❖ Category Card Preview",
            color = Color(0xFF5D3F75),
            fontSize = 14.sp,
            fontFamily = Outfit,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = Color(0xFF8B5CF6),
                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                )
                .background(
                    color = Color(0xFFEFEFEF),
                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                )
                .padding(16.dp)
        ) {
            // Case 1: Multiple Icons with Badge (+1)
            CategoryCard(
                title = "Catering",
                amount = "₹12,45,000",
                icons = listOf(
                    rememberVectorPainter(Icons.Outlined.Fastfood),
                    rememberVectorPainter(Icons.Outlined.Restaurant)
                ),
                totalItemCount = 3
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
}