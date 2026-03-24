package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

enum class ButtonBackground {
    TRANSPARENT,
    TRANSLUCENT,
    OPAQUE
}
sealed interface TopIcon {
    enum class Predefined : TopIcon {
        BACK,           // Chevron Left (<)
        BACK_2,         // Arrow Left (←)
        CLOSE,          // Cross (X)
        MENU_VERTICAL,  // Vertical Dots (⋮)
        MENU_HORIZONTAL // Horizontal Dots (...)
    }
    data class Custom(val imageVector: ImageVector) : TopIcon
}

@Composable
fun TopBarIconButton(
    icon: TopIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundStyle: ButtonBackground = ButtonBackground.TRANSPARENT,
    iconColor: Color = ContentPrimary,
    size: Dp = 40.dp,
    iconSize: Dp = 30.dp
) {
    val backgroundColor = when (backgroundStyle) {
        ButtonBackground.TRANSPARENT -> Color.Transparent
        ButtonBackground.TRANSLUCENT -> SurfaceSecondary.copy(alpha = 0.5f)
        ButtonBackground.OPAQUE -> SurfaceSecondary
    }

    val imageVector = when (icon) {
        TopIcon.Predefined.BACK -> Icons.AutoMirrored.Rounded.KeyboardArrowLeft
        TopIcon.Predefined.BACK_2 -> Icons.AutoMirrored.Rounded.ArrowBack
        TopIcon.Predefined.CLOSE -> Icons.Rounded.Close
        TopIcon.Predefined.MENU_VERTICAL -> Icons.Rounded.MoreVert
        TopIcon.Predefined.MENU_HORIZONTAL -> Icons.Rounded.MoreHoriz
        is TopIcon.Custom -> icon.imageVector
    }

    val tintColor = iconColor

    Box(
        modifier = modifier
            .size(size)
            .clip(shape = SquircleShape(100, 0f))
            .background(backgroundColor)
            .then(
                if (backgroundStyle == ButtonBackground.TRANSLUCENT) {
                    Modifier.border(
                        width = 0.5.dp,
                        color = SurfaceSecondary.copy(alpha = 0.25f),
                        shape = SquircleShape(100, CornerSmoothingDefault)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = tintColor,
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
        TopIcon.Predefined.CLOSE,
        TopIcon.Predefined.MENU_VERTICAL,
        TopIcon.Predefined.MENU_HORIZONTAL
    )

    // Using a column layout for the preview to mimic the screenshot
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        iconList.forEach { icon ->
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 1: TRANSPARENT (just icon)
                TopBarIconButton(
                    icon = icon,
                    onClick = { /* Handle click */ },
                    backgroundStyle = ButtonBackground.TRANSPARENT,
                    iconColor = Color.White
                )

                Spacer(Modifier.size(16.dp))

                // Column 2: TRANSLUCENT (semi-transparent background)
                TopBarIconButton(
                    icon = icon,
                    onClick = { /* Handle click */ },
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    iconColor = Color.White
                )

                Spacer(Modifier.size(16.dp))

                // Column 3: OPAQUE (solid background, black icon for contrast)
                TopBarIconButton(
                    icon = icon,
                    onClick = { /* Handle click */ },
                    backgroundStyle = ButtonBackground.OPAQUE,
                )
            }
        }
    }
}
