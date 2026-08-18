package com.harshdeep.jasnify.presentation.components.buttons

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

enum class ButtonBackground {
    TRANSPARENT,
    TRANSLUCENT,
    OPAQUE
}

sealed interface TopIcon {
    enum class Predefined : TopIcon {
        BACK,            // Chevron Left (<)
        BACK_2,          // Arrow Left (←)
        DOWN,
        CLOSE,           // Cross (X)
        MENU_VERTICAL,   // Vertical Dots (⋮)
        MENU_HORIZONTAL, // Horizontal Dots (...)
        MENU_MODERN,
        SEARCH,          // Magnifying Glass
        PLUS,            // Plus icon
        PIN,             // Pushpin/Thumbtack
        PIN_FILLED,
        CHECKLIST,       // Document with Checklist
        HEART,
        HEART_FILLED
    }
    data class CustomPainter(val painter: Painter, val isTinted: Boolean = true) : TopIcon
}

@Composable
fun TopBarIconButton(
    icon: TopIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundStyle: ButtonBackground = ButtonBackground.TRANSPARENT,
    buttonColor: Color = SurfaceSecondary,
    iconColor: Color = ContentPrimary,
    borderColor: Color? = null,
    borderGradientColors: List<Color>? = null,
    borderWidth: Dp = 1.dp,
    size: Dp = 40.dp,
    iconSize: Dp = 24.dp,
    translucentAlpha: Float = 0.2f,
    iconShadowColor: Color = Color.Transparent,
    iconShadowBlur: Dp = 4.dp,
    iconShadowOffsetY: Dp = 0.dp
) {
    val backgroundColor = when (backgroundStyle) {
        ButtonBackground.TRANSPARENT -> Color.Transparent
        ButtonBackground.TRANSLUCENT -> Color.White.copy(alpha = translucentAlpha)
        ButtonBackground.OPAQUE -> buttonColor
    }

    val painter: Painter = when (icon) {
        TopIcon.Predefined.BACK -> painterResource(R.drawable.ic_back)
        TopIcon.Predefined.BACK_2 -> painterResource(R.drawable.ic_back_02)
        TopIcon.Predefined.DOWN -> painterResource(R.drawable.ic_down)
        TopIcon.Predefined.CLOSE -> painterResource(R.drawable.ic_cross)
        TopIcon.Predefined.MENU_VERTICAL -> rememberVectorPainter(Icons.Rounded.MoreVert)
        TopIcon.Predefined.MENU_HORIZONTAL -> rememberVectorPainter(Icons.Rounded.MoreHoriz)
        TopIcon.Predefined.MENU_MODERN -> painterResource(R.drawable.ic_menu_modern)
        TopIcon.Predefined.SEARCH -> rememberVectorPainter(Icons.Rounded.Search)
        TopIcon.Predefined.PLUS -> painterResource(R.drawable.ic_plus)
        TopIcon.Predefined.PIN -> painterResource(R.drawable.ic_pin)
        TopIcon.Predefined.PIN_FILLED -> painterResource(R.drawable.ic_pin_filled)
        TopIcon.Predefined.CHECKLIST -> painterResource(R.drawable.ic_checklists)
        TopIcon.Predefined.HEART -> painterResource(R.drawable.ic_top_bar_heart)
        TopIcon.Predefined.HEART_FILLED -> painterResource(R.drawable.ic_heart_filled)
        is TopIcon.CustomPainter -> icon.painter
    }

    val finalIconTint = when (icon) {
        TopIcon.Predefined.HEART, TopIcon.Predefined.HEART_FILLED -> Color.Unspecified
        is TopIcon.CustomPainter -> if (icon.isTinted) iconColor else Color.Unspecified
        else -> iconColor
    }

    val defaultOutline = MaterialTheme.colorScheme.outline
    val borderBrush: Brush = remember(borderGradientColors, borderColor, defaultOutline) {
        when {
            borderGradientColors != null -> Brush.verticalGradient(borderGradientColors)
            borderColor != null -> SolidColor(borderColor)
            else -> SolidColor(defaultOutline.copy(alpha = 0.16f))
        }
    }

    // Border applies only for TRANSLUCENT style
    val borderModifier = if (backgroundStyle == ButtonBackground.TRANSLUCENT) {
        Modifier.border(
            width = borderWidth,
            brush = borderBrush,
            shape = SquircleShape(100, 0f)
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape = SquircleShape(100, 0f))
            .background(backgroundColor)
            .then(borderModifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Drop Shadow behind icon in TRANSPARENT mode
        if (backgroundStyle == ButtonBackground.TRANSPARENT && iconShadowColor != Color.Transparent) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = iconShadowColor,
                modifier = Modifier
                    .size(iconSize)
                    .offset(y = iconShadowOffsetY)
                    .then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && iconShadowBlur > 0.dp) {
                            Modifier.graphicsLayer {
                                renderEffect = android.graphics.RenderEffect
                                    .createBlurEffect(
                                        iconShadowBlur.toPx(),
                                        iconShadowBlur.toPx(),
                                        android.graphics.Shader.TileMode.DECAL
                                    )
                                    .asComposeRenderEffect()
                            }
                        } else {
                            Modifier
                        }
                    )
            )
        }

        // Foreground Icon
        Icon(
            painter = painter,
            contentDescription = null,
            tint = finalIconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TopBarIconButtonPreview() {
    val iconList = listOf(
        TopIcon.Predefined.BACK,
        TopIcon.Predefined.BACK_2,
        TopIcon.Predefined.DOWN,
        TopIcon.Predefined.CLOSE,
        TopIcon.Predefined.MENU_VERTICAL,
        TopIcon.Predefined.MENU_MODERN,
        TopIcon.Predefined.SEARCH,
        TopIcon.Predefined.PIN,
        TopIcon.Predefined.PIN_FILLED,
        TopIcon.Predefined.CHECKLIST
    )

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        iconList.forEach { icon ->
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    icon = icon,
                    onClick = {},
                    backgroundStyle = ButtonBackground.TRANSPARENT,
                    iconColor = Color.White,
                    iconShadowColor = Color.Black.copy(alpha = 0.5f)
                )

                Spacer(Modifier.size(16.dp))

                TopBarIconButton(
                    icon = icon,
                    onClick = {},
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    iconColor = Color.White,
                    borderColor = Color.White.copy(alpha = 0.5f)
                )

                Spacer(Modifier.size(16.dp))

                TopBarIconButton(
                    icon = icon,
                    onClick = {},
                    backgroundStyle = ButtonBackground.OPAQUE,
                    buttonColor = Color(0xFF1E1E1E),
                    iconColor = Color.White
                )
            }
        }
    }
}