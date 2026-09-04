package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.harshdeep.jasnify.presentation.components.others.ThreeDotsWaveLoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.Outfit
import com.harshdeep.jasnify.theme.SurfaceAccent
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfaceInvSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

enum class ButtonSize {
    Small, Medium, Large
}
enum class ButtonType {
    Primary, Secondary, Tertiary
}
enum class ButtonShapeStyle {
    Square, Round
}

@Composable
fun getButtonStyles(
    size: ButtonSize,
    type: ButtonType,
    shapeStyle: ButtonShapeStyle,
    customContainerColor: Color? = null,
    customContentColor: Color? = null,
    customDisabledContainerColor: Color? = null,
    customDisabledContentColor: Color? = null
): Triple<ButtonColors, Dp, Shape> {

    // Colors
    val colors = when (type) {
        ButtonType.Primary -> ButtonDefaults.buttonColors(
            containerColor = customContainerColor ?: SurfaceBrandPrimary,
            contentColor = customContentColor ?: ContentInvPrimary,
            disabledContainerColor = customDisabledContainerColor
                ?: customContainerColor?.copy(alpha = 0.38f)
                ?: SurfaceInvSecondary,
            disabledContentColor = customDisabledContentColor
                ?: customContentColor?.copy(alpha = 0.38f)
                ?: ContentInvPrimary
        )
        ButtonType.Secondary -> ButtonDefaults.buttonColors(
            containerColor = customContainerColor ?: SurfaceBrandSecondary,
            contentColor = customContentColor ?: ContentBrandDark,
            disabledContainerColor = customDisabledContainerColor
                ?: customContainerColor?.copy(alpha = 0.38f)
                ?: SurfaceInvSecondary,
            disabledContentColor = customDisabledContentColor
                ?: customContentColor?.copy(alpha = 0.38f)
                ?: ContentInvPrimary
        )
        ButtonType.Tertiary -> ButtonDefaults.buttonColors(
            containerColor = customContainerColor ?: SurfacePrimary,
            contentColor = customContentColor ?: ContentPrimary,
            disabledContainerColor = customDisabledContainerColor
                ?: customContainerColor?.copy(alpha = 0.38f)
                ?: SurfaceInvSecondary,
            disabledContentColor = customDisabledContentColor
                ?: customContentColor?.copy(alpha = 0.38f)
                ?: ContentInvPrimary
        )
    }

    // Size (Height and Icon Size)
    val height = when (size) {
        ButtonSize.Small -> 40.dp
        ButtonSize.Medium -> 56.dp
        ButtonSize.Large -> 84.dp
    }

    // Shape
    val shape = when (shapeStyle) {
        ButtonShapeStyle.Square -> when (size) {
            ButtonSize.Small -> SquircleShape(CornerMedium, CornerSmoothingDefault)
            ButtonSize.Medium -> SquircleShape(CornerLarge, CornerSmoothingDefault)
            ButtonSize.Large -> SquircleShape(CornerExtraLarge, CornerSmoothingDefault)
        }
        ButtonShapeStyle.Round -> CircleShape // Fully rounded pill shape
    }

    return Triple(colors, height, shape)
}

// --- Custom Text Button Composable ---

@Composable
fun CustomTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    type: ButtonType = ButtonType.Primary,
    shapeStyle: ButtonShapeStyle = ButtonShapeStyle.Round,
    enabled: Boolean = true,
    customBorder: BorderStroke? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    disabledContainerColor: Color? = null,
    disabledContentColor: Color? = null,
    isLoading: Boolean = false
) {
    val (colors, height, shape) = getButtonStyles(
        size = size,
        type = type,
        shapeStyle = shapeStyle,
        customContainerColor = containerColor,
        customContentColor = contentColor,
        customDisabledContainerColor = disabledContainerColor,
        customDisabledContentColor = disabledContentColor
    )

    // Calculate content padding and icon size based on the button height
    val contentPadding = when (size) {
        ButtonSize.Small -> PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        ButtonSize.Medium -> PaddingValues(horizontal = 24.dp, vertical = 18.dp)
        ButtonSize.Large -> PaddingValues(horizontal = 48.dp, vertical = 32.dp)
    }

    val iconSize = when (size) {
        ButtonSize.Small -> 18.dp
        ButtonSize.Medium -> 20.dp
        ButtonSize.Large -> 20.dp
    }

    val finalBorder = customBorder?: null

    Button(
        onClick = if (isLoading) ({}) else onClick,
        modifier = modifier.height(height),
        enabled = enabled,
        shape = shape,
        border = finalBorder,
        colors = if (isLoading) {
            ButtonDefaults.buttonColors(
                containerColor = colors.containerColor,
                contentColor = colors.contentColor,
                disabledContainerColor = colors.containerColor,
                disabledContentColor = colors.contentColor
            )
        } else colors,
        contentPadding = contentPadding
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                ThreeDotsWaveLoadingIndicator(
                    dotSize = when (size) {
                        ButtonSize.Small -> 4.dp
                        ButtonSize.Medium -> 6.dp
                        ButtonSize.Large -> 8.dp
                    },
                    dotColor = colors.contentColor,
                    travelDistance = 5.dp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight(unbounded = true) // Allows the row to be outside bounds
                    .graphicsLayer { alpha = if (isLoading) 0f else 1f }
            ) {

            // Leading Icon
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                )
                // Spacer only if there is text to separate icon from text
                if (text.isNotEmpty()) Spacer(Modifier.width(8.dp))
            }

            // Text
            Text(
                text = text,
                fontSize = when(size) {
                    ButtonSize.Small -> 16.sp
                    ButtonSize.Medium -> 18.sp
                    ButtonSize.Large -> 18.sp
                },
                fontFamily = Outfit
            )

            // Trailing Icon
            if (trailingIcon != null) {
                // Spacer only if there is text to separate icon from text
                if (text.isNotEmpty()) Spacer(Modifier.width(8.dp))
                Icon(
                    painter = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}
}

// --- Custom Icon Button Composable (Icon Only) ---

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    type: ButtonType = ButtonType.Primary,
    shapeStyle: ButtonShapeStyle = ButtonShapeStyle.Round,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color? = null,
    disabledContainerColor: Color? = null,
    disabledContentColor: Color? = null,
    isLoading: Boolean = false
) {
    val (colors, height, _) = getButtonStyles(
        size = size,
        type = type,
        shapeStyle = shapeStyle,
        customContainerColor = containerColor,
        customContentColor = contentColor,
        customDisabledContainerColor = disabledContainerColor,
        customDisabledContentColor = disabledContentColor
    )

    // For icon buttons, the container size is determined by 'height', and the padding is adjusted
    val iconButtonSize = height

    // Icon size is slightly smaller than the container size for padding
    val iconSize = when (size) {
        ButtonSize.Small -> 20.dp
        ButtonSize.Medium -> 28.dp
        ButtonSize.Large -> 36.dp
    }

    // Icon buttons often use a square or circle shape.
    val shape = when (shapeStyle) {
        ButtonShapeStyle.Square -> SquircleShape(CornerLarge, CornerSmoothingDefault)
        ButtonShapeStyle.Round -> CircleShape
    }

    Button(
        onClick = if (isLoading) ({}) else onClick,
        modifier = modifier.size(iconButtonSize),
        enabled = enabled,
        shape = shape,
        colors = if (isLoading) {
            ButtonDefaults.buttonColors(
                containerColor = colors.containerColor,
                contentColor = colors.contentColor,
                disabledContainerColor = colors.containerColor,
                disabledContentColor = colors.contentColor
            )
        } else colors,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                ThreeDotsWaveLoadingIndicator(
                    dotSize = 6.dp,
                    dotColor = colors.contentColor,
                    travelDistance = 4.dp,
                    dotSpacing = 4.dp
                )
            } else {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

// ---  Previews (Visualizing the Design System) ---

@Preview(showBackground = true, name = "All Text Button Variations")
@Composable
private fun TextButtonPreview() {
    val iconPainter = painterResource(R.drawable.ic_google)

    @Composable
    fun ButtonSection(size: ButtonSize) {
        Text(
            text = "${size.name} Buttons (${size.name.lowercase()})",
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        // Primary - Square (with leading icon)
        CustomTextButton(
            onClick = {},
            text = "Primary Square",
            size = size,        // ButtonSize.medium
            type = ButtonType.Primary,
            shapeStyle = ButtonShapeStyle.Square,
            leadingIcon = iconPainter,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Secondary - Round (with trailing icon)
        CustomTextButton(
            onClick = {},
            text = "Secondary Round",
            size = size,
            type = ButtonType.Secondary,
            shapeStyle = ButtonShapeStyle.Round,
            trailingIcon = iconPainter,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Tertiary - Disabled (Square, with both icons)
        CustomTextButton(
            onClick = {},
            text = "Tertiary Disabled",
            size = size,
            type = ButtonType.Tertiary,
            shapeStyle = ButtonShapeStyle.Square,
            enabled = false,
            leadingIcon = iconPainter,
            trailingIcon = iconPainter,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        ButtonSection(size = ButtonSize.Small)
        ButtonSection(size = ButtonSize.Medium)
        ButtonSection(size = ButtonSize.Large)
    }
}

@Preview(showBackground = true, name = "All Icon Button Variations")
@Composable
private fun IconButtonPreview() {
    // The icon is already defined as a Painter, which is now accepted by the composable
    val icon = painterResource(R.drawable.ic_google)

    Column(modifier = Modifier.padding(16.dp)) { // Wrap in Column with padding
        Text(
            text = "Icon Only Buttons",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Small Icons
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Small:", Modifier.width(60.dp))
            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Small, type = ButtonType.Primary, shapeStyle = ButtonShapeStyle.Round)
            Spacer(Modifier.width(8.dp))
            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Small, type = ButtonType.Secondary, shapeStyle = ButtonShapeStyle.Square)
            Spacer(Modifier.width(8.dp))
            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Small, type = ButtonType.Tertiary, shapeStyle = ButtonShapeStyle.Round, enabled = false)
        }

        // Medium Icons
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Medium:", Modifier.width(60.dp))
            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Medium, type = ButtonType.Primary, shapeStyle = ButtonShapeStyle.Square)
            Spacer(Modifier.width(8.dp))
            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Medium, type = ButtonType.Secondary, shapeStyle = ButtonShapeStyle.Round)
            Spacer(Modifier.width(8.dp))
            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Medium, type = ButtonType.Tertiary, shapeStyle = ButtonShapeStyle.Square, enabled = false)
        }

        // Large Icons
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Large:", Modifier.width(60.dp))
            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Large, type = ButtonType.Primary, shapeStyle = ButtonShapeStyle.Round)
            Spacer(Modifier.width(8.dp))
            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Large, type = ButtonType.Secondary, shapeStyle = ButtonShapeStyle.Square)
            Spacer(Modifier.width(8.dp))


            CustomIconButton(onClick = {}, icon = icon, size = ButtonSize.Large, type = ButtonType.Tertiary, shapeStyle = ButtonShapeStyle.Round, enabled = true, containerColor = Color.Red, contentColor = Color.Green, disabledContainerColor = Color.Yellow, disabledContentColor = Color.Blue)
        }
    }
}