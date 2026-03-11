package com.harshdeep.jasnify.presentation.components.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

enum class ChipSize {
    Small, Medium
}

enum class ChipShapeStyle {
    Square, Round
}

@Composable
fun getChipStyles(
    isSelected: Boolean,
    size: ChipSize,
    shapeStyle: ChipShapeStyle,
    hasStroke: Boolean
): Triple<Color, Color, Shape> {
    val containerColor = when {
        isSelected && hasStroke -> SurfaceBrandSecondary
        isSelected -> SurfaceBrandPrimary
        else -> SurfaceSecondary
    }

    val contentColor = when {
        isSelected && hasStroke -> ContentBrandDark
        isSelected -> ContentInvPrimary
        else -> ContentPrimary
    }

    val shape = when (shapeStyle) {
        ChipShapeStyle.Square -> {
            val corner = if (size == ChipSize.Small) CornerMedium else CornerLarge
            SquircleShape(corner, CornerSmoothingDefault)
        }
        ChipShapeStyle.Round -> CircleShape
    }

    return Triple(containerColor, contentColor, shape)
}

/**
 * FilterChip component - A highly customizable chip for filtering and selection.
 * Renamed from BasicChip to reflect its primary use case in the UI.
 */
@Composable
fun FilterChip(
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    size: ChipSize = ChipSize.Medium,
    shapeStyle: ChipShapeStyle = ChipShapeStyle.Square,
    hasStroke: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    hasDropdown: Boolean = false,
    onClick: () -> Unit = {}
) {
    val (containerColor, contentColor, shape) = getChipStyles(isSelected, size, shapeStyle, hasStroke)

    // Border logic: Only apply border if hasStroke is true
    val border = if (hasStroke) {
        BorderStroke(1.dp, if (isSelected) SurfaceBrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
    } else null

    // Sizing and Padding Configuration
    val height = if (size == ChipSize.Small) 36.dp else 40.dp
    val iconSize = if (size == ChipSize.Small) 16.dp else 20.dp

    // Custom padding as requested
    val horizontalPadding = if (size == ChipSize.Small) 12.dp else 16.dp
    val verticalPadding = if (size == ChipSize.Small) 8.dp else 10.dp
    val gap = 12.dp

    val style = if (size == ChipSize.Small) JasnifyTheme.typography.labelMedium else JasnifyTheme.typography.labelLarge

    Surface(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 1. Leading Icon
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(Modifier.width(gap))
            }

            // 2. Label
            Text(
                text = label,
                style = style,
            )

            // 3. Dropdown Icon
            if (hasDropdown) {
                Spacer(Modifier.width(gap))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }

            // 4. Trailing Icon
            if (trailingIcon != null) {
                Spacer(Modifier.width(gap))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

// --------- Previews ----------

@Preview(showBackground = true, name = "All FilterChip Combinations")
@Composable
private fun ChipPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Section 1: Without Stroke (Solid) ---
        ChipSectionHeader("Without Stroke (Solid Variations)")
        ChipRow {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Default", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                FilterChip(label = "Filter Chip", hasStroke = false)
                FilterChip(
                    label = "Full House",
                    leadingIcon = Icons.Default.FilterList,
                    hasDropdown = true,
                    trailingIcon = Icons.Default.Close,
                    hasStroke = false
                )
                FilterChip(label = "Rounded", shapeStyle = ChipShapeStyle.Round, hasStroke = false)
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Selected (Solid)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                FilterChip(label = "Filter Chip", isSelected = true, hasStroke = false)
                FilterChip(
                    label = "Full House",
                    isSelected = true,
                    leadingIcon = Icons.Default.FilterList,
                    trailingIcon = Icons.Default.Close,
                    hasStroke = false
                )
                FilterChip(label = "Rounded", shapeStyle = ChipShapeStyle.Round, isSelected = true, hasStroke = false)
            }
        }

        Spacer(Modifier.height(32.dp))

        // --- Section 2: With Stroke (Outlined) ---
        ChipSectionHeader("With Stroke (Outlined Variations)")
        ChipRow {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Default", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                FilterChip(label = "Stroked", hasStroke = true)
                FilterChip(label = "Rounded", shapeStyle = ChipShapeStyle.Round, hasStroke = true)
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Selected (Stroked)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                FilterChip(label = "Stroked", isSelected = true, hasStroke = true)
                FilterChip(label = "Rounded", shapeStyle = ChipShapeStyle.Round, isSelected = true, hasStroke = true)
            }
        }

        Spacer(Modifier.height(32.dp))

        // --- Section 3: Small Sizes ---
        ChipSectionHeader("Small Sizing Examples")
        ChipRow {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Small Default", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                FilterChip(label = "Small Chip", size = ChipSize.Small)
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Small Selected", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                FilterChip(label = "Small Sel", size = ChipSize.Small, isSelected = true)
            }
        }
    }
}

@Composable
private fun ChipSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun ChipRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        content = content
    )
}