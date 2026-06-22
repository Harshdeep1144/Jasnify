package com.harshdeep.jasnify.presentation.components.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

enum class ChipShapeStyle {
    Square, Round
}

enum class Dietary {
    Veg, NonVeg
}

enum class ChipSize {
    Small, Large
}

data class ChipStyles(
    val containerColor: Color,
    val contentColor: Color,
    val shape: Shape,
    val border: BorderStroke?,
    val height: Dp,
    val iconSize: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val gap: Dp,
    val textStyle: TextStyle
)

@Composable
fun getChipStyles(
    isSelected: Boolean,
    shapeStyle: ChipShapeStyle,
    size: ChipSize,
    hasStroke: Boolean = false,
    foodType: Dietary? = null
): ChipStyles {
    // Shape
    val shape = when (shapeStyle) {
        ChipShapeStyle.Square -> SquircleShape(CornerLarge, CornerSmoothingDefault)
        ChipShapeStyle.Round -> CircleShape
    }

    // Colors (Container & Content)
    val (containerColor, contentColor) = if (foodType != null) {
        val container = when {
            !isSelected -> SurfaceSecondary
            foodType == Dietary.Veg -> Color(0x1A008E11)
            else -> Color(0x1A8E2300)
        }
        val content = when {
            !isSelected -> ContentSecondary
            foodType == Dietary.Veg -> Color(0xFF008E11)
            else -> Color(0xFF8E2300)
        }
        container to content
    } else {
        val container = when {
            isSelected && hasStroke -> SurfaceBrandSecondary
            isSelected -> SurfaceBrandPrimary
            else -> SurfaceSecondary
        }
        val content = when {
            isSelected && hasStroke -> ContentBrandDark
            isSelected -> ContentInvPrimary
            else -> ContentSecondary
        }
        container to content
    }

    // Border
    val border = when {
        foodType != null -> {
            if (isSelected) {
                BorderStroke(
                    width = 1.dp,
                    color = if (foodType == Dietary.Veg) Color(0xFF008E11) else Color(0xFF8E2300)
                )
            } else {
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
                )
            }
        }
        hasStroke -> {
            BorderStroke(
                width = 1.dp,
                color = if (isSelected) SurfaceBrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )
        }
        else -> null
    }

    // Sizing Configurations
    val height = if (size == ChipSize.Small) 40.dp else 56.dp
    val iconSize = if (size == ChipSize.Small) 20.dp else 24.dp
    val horizontalPadding = if (size == ChipSize.Small) 16.dp else 16.dp
    val verticalPadding = if (size == ChipSize.Small) 10.dp else 16.dp
    val gap = if (size == ChipSize.Small) 8.dp else 8.dp
    val textStyle = if (size == ChipSize.Small) JasnifyTheme.typography.labelLarge else JasnifyTheme.typography.labelXLarge

    return ChipStyles(
        containerColor = containerColor,
        contentColor = contentColor,
        shape = shape,
        border = border,
        height = height,
        iconSize = iconSize,
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
        gap = gap,
        textStyle = textStyle
    )
}

@Composable
fun FilterChip(
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    shapeStyle: ChipShapeStyle = ChipShapeStyle.Square,
    size: ChipSize = ChipSize.Small,
    hasStroke: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    hasDropdown: Boolean = false,
    onClick: () -> Unit = {},
    onTrailingIconClick: () -> Unit = {}
) {
    val styles = getChipStyles(
        isSelected = isSelected,
        shapeStyle = shapeStyle,
        size = size,
        hasStroke = hasStroke
    )

    Surface(
        onClick = onClick,
        modifier = modifier.height(styles.height),
        shape = styles.shape,
        color = styles.containerColor,
        contentColor = styles.contentColor,
        border = styles.border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = styles.horizontalPadding, vertical = styles.verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(styles.iconSize),
                    tint = styles.contentColor
                )
                Spacer(Modifier.width(styles.gap))
            }

            Text(
                text = label,
                style = styles.textStyle,
                color = styles.contentColor
            )

            if (hasDropdown && !isSelected) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(styles.iconSize),
                    tint = ContentPrimary
                )
            }

            if (trailingIcon != null) {
                Spacer(Modifier.width(styles.gap))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(styles.iconSize)
                        .clickable { onTrailingIconClick() },
                    tint = styles.contentColor
                )
            }
        }
    }
}

@Composable
fun FoodChip(
    foodType: Dietary,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    shapeStyle: ChipShapeStyle = ChipShapeStyle.Square,
    size: ChipSize = ChipSize.Small,
    onClick: () -> Unit = {}
) {
    val styles = getChipStyles(
        isSelected = isSelected,
        shapeStyle = shapeStyle,
        size = size,
        foodType = foodType
    )
    val label = if (foodType == Dietary.Veg) "Veg" else "Non-Veg"

    Surface(
        onClick = onClick,
        modifier = modifier.height(styles.height),
        shape = styles.shape,
        color = styles.containerColor,
        contentColor = styles.contentColor,
        border = styles.border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = styles.horizontalPadding, vertical = styles.verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            when (foodType) {
                Dietary.Veg -> Icon(
                    painter = painterResource(id = R.drawable.ic_veg),
                    contentDescription = "Veg",
                    modifier = Modifier.size(styles.iconSize),
                    tint = Color(0xFF008E11)
                )
                Dietary.NonVeg -> Icon(
                    painter = painterResource(id = R.drawable.ic_non_veg),
                    contentDescription = "Non-Veg",
                    modifier = Modifier.size(styles.iconSize),
                    tint = Color(0xFF8E2300)
                )
            }

            Spacer(Modifier.width(styles.gap))

            Text(
                text = label,
                style = styles.textStyle,
                color = styles.contentColor
            )
        }
    }
}

@Composable
fun CateringItemChip(
    label: String,
    foodType: Dietary,
    modifier: Modifier = Modifier,
    isMultiSelect: Boolean = true,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    val containerColor = Color.Transparent
    val contentColor = ContentPrimary
    val height = 40.dp
    val iconSize = 20.dp

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                when (foodType) {
                    Dietary.Veg -> Icon(
                        painter = painterResource(id = R.drawable.ic_veg),
                        contentDescription = "Veg",
                        modifier = Modifier.size(iconSize),
                        tint = Color(0xFF008216)
                    )
                    Dietary.NonVeg -> Icon(
                        painter = painterResource(id = R.drawable.ic_non_veg),
                        contentDescription = "Non-Veg",
                        modifier = Modifier.size(iconSize),
                        tint = Color(0xFF8B2000)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = label,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = contentColor
                )
            }

            if (isMultiSelect) {
                Spacer(modifier = Modifier.width(8.dp))
                CustomChecker(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = true
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "FilterChip & FoodChip Preview Layout")
@Composable
private fun ChipPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // --- Section 1: Basic Chips ---
        Column {
            ChipSectionHeader("Chip/Basic (Small & Large)")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Default (Small)", style = JasnifyTheme.typography.labelSmall, color = Color.Gray)
                    Text("Selected (Large)", style = JasnifyTheme.typography.labelSmall, color = Color.Gray)
                }

                ChipRow {
                    FilterChip(
                        label = "Label",
                        isSelected = false,
                        size = ChipSize.Small,
                        leadingIcon = Icons.Default.FilterList,
                        hasDropdown = true,
                        trailingIcon = Icons.Default.Close
                    )
                    FilterChip(
                        label = "Label",
                        isSelected = true,
                        size = ChipSize.Large,
                        leadingIcon = Icons.Default.FilterList,
                        trailingIcon = Icons.Default.Close
                    )
                }

                ChipRow {
                    FilterChip(
                        label = "Label",
                        isSelected = false,
                        shapeStyle = ChipShapeStyle.Round,
                        size = ChipSize.Small
                    )
                    FilterChip(
                        label = "Label",
                        isSelected = true,
                        shapeStyle = ChipShapeStyle.Round,
                        size = ChipSize.Large,
                        leadingIcon = Icons.Default.FilterList,
                        trailingIcon = Icons.Default.Close,
                        hasStroke = true
                    )
                }
            }
        }

        // --- Section 2: Veg, Non-Veg Chips (Small & Large Sizing) ---
        Column {
            ChipSectionHeader("Chip/Veg, Non-Veg (Small & Large Layouts)")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Small Size (Default)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Small (Default - 40.dp)", style = JasnifyTheme.typography.labelSmall, color = Color.Gray)
                    ChipRow {
                        FoodChip(
                            foodType = Dietary.Veg,
                            isSelected = false,
                            shapeStyle = ChipShapeStyle.Square,
                            size = ChipSize.Small
                        )
                        FoodChip(
                            foodType = Dietary.NonVeg,
                            isSelected = true,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small
                        )
                    }
                }

                // Large Size
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Large (48.dp)", style = JasnifyTheme.typography.labelSmall, color = Color.Gray)
                    ChipRow {
                        FoodChip(
                            foodType = Dietary.Veg,
                            isSelected = true,
                            shapeStyle = ChipShapeStyle.Square,
                            size = ChipSize.Large
                        )
                        FoodChip(
                            foodType = Dietary.NonVeg,
                            isSelected = false,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Large
                        )
                    }
                }
            }
        }

        // --- Section 3: Catering Item Chip ---
        Column {
            ChipSectionHeader("Catering Item Chip")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(24.dp),
            ) {
                CateringItemChip(
                    label = "Catering Item",
                    foodType = Dietary.Veg,
                    isMultiSelect = true,
                    checked = false,
                    onCheckedChange = {}
                )

                CateringItemChip(
                    label = "Catering Item",
                    foodType = Dietary.NonVeg,
                    isMultiSelect = true,
                    checked = true,
                    onCheckedChange = {}
                )

                CateringItemChip(
                    label = "Catering Item",
                    foodType = Dietary.Veg,
                    isMultiSelect = false
                )
            }
        }
    }
}

@Composable
private fun ChipSectionHeader(title: String) {
    Text(
        text = title,
        style = JasnifyTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        ),
        color = Color(0xFF6750A4),
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