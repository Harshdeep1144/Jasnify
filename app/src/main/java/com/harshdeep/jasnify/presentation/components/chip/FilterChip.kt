package com.harshdeep.jasnify.presentation.components.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
        else -> ContentSecondary
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

@Composable
fun FilterChip(
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    size: ChipSize = ChipSize.Small,
    shapeStyle: ChipShapeStyle = ChipShapeStyle.Square,
    hasStroke: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    hasDropdown: Boolean = false,
    onClick: () -> Unit = {},
    onTrailingIconClick: () -> Unit = {}
) {
    val (containerColor, contentColor, shape) = getChipStyles(isSelected, size, shapeStyle, hasStroke)

    // Border logic: Apply primary brand color border for outlined-selected state
    val border = if (hasStroke) {
        BorderStroke(
            width = 1.dp,
            color = if (isSelected) SurfaceBrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
        )
    } else null

    // Sizing and Layout Configuration
    val height = if (size == ChipSize.Small) 36.dp else 40.dp
    val iconSize = if (size == ChipSize.Small) 16.dp else 20.dp
    val horizontalPadding = if (size == ChipSize.Small) 12.dp else 16.dp
    val gap = 8.dp

    val textStyle = if (size == ChipSize.Small)
        JasnifyTheme.typography.labelMedium
    else
        JasnifyTheme.typography.labelLarge

    Surface(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Leading Icon
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = contentColor
                )
                Spacer(Modifier.width(gap))
            }

            // Label
            Text(
                text = label,
                style = textStyle,
                color = contentColor
            )

            //  Dropdown Icon
            if (hasDropdown && !isSelected) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = contentColor
                )
            }

            if (trailingIcon != null) {
                Spacer(Modifier.width(gap))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { onTrailingIconClick() },
                    tint = contentColor
                )
            }
        }
    }
}

// --------- Previews ----------

@Preview(showBackground = true, name = "FilterChip Design System")
@Composable
private fun ChipPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE)) // Slightly darker background to see the chips
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // --- Section 1: Default & Solid Selected (Matches top of image) ---
        Column {
            ChipSectionHeader("Solid Style")
            ChipRow {
                FilterChip(
                    label = "Label",
                    isSelected = false,
                    leadingIcon = Icons.Default.FilterList,
                    hasDropdown = true,
                    trailingIcon = Icons.Default.Close
                )
                FilterChip(
                    label = "Label",
                    isSelected = true,
                    leadingIcon = Icons.Default.FilterList,
                    trailingIcon = Icons.Default.Close
                )
            }
        }

        // --- Section 2: Outlined Style (Matches bottom of image) ---
        Column {
            ChipSectionHeader("Outlined Style")
            ChipRow {
                FilterChip(
                    label = "Label",
                    isSelected = false,
                    hasStroke = true,
                    leadingIcon = Icons.Default.FilterList,
                    hasDropdown = true,
                    trailingIcon = Icons.Default.Close
                )
                FilterChip(
                    label = "Label",
                    isSelected = true,
                    hasStroke = true,
                    leadingIcon = Icons.Default.FilterList,
                    trailingIcon = Icons.Default.Close
                )
            }
        }

        // --- Section 3: Rounded vs Square ---
        Column {
            ChipSectionHeader("Shape Comparison")
            ChipRow {
                FilterChip(label = "Squircle", shapeStyle = ChipShapeStyle.Square, isSelected = true)
                FilterChip(label = "Circle", shapeStyle = ChipShapeStyle.Round, isSelected = true)
            }
        }
    }
}

@Composable
private fun ChipSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = JasnifyTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun ChipRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}