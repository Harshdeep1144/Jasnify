package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.Outfit

@Composable
fun AuthButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: Painter,
    iconSize: Dp = 24.dp,
    enabled: Boolean = true,
    shapeStyle: ButtonShapeStyle = ButtonShapeStyle.Square,
    badgeText: String? = null,
    borderColor: Color = ContentPrimary
) {
    val size = ButtonSize.Medium
    val type = ButtonType.Tertiary
    val (colors, height, shape) = getButtonStyles(size, type, shapeStyle)
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    // --- ANIMATION STATES ---

    // State to trigger the animations once compiled & launched
    var animTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animTriggered = true
    }

    // Badge Slide Up Animation (Starts completely behind the button at 26.dp and slides up to 0.dp)
    val badgeOffsetY by animateDpAsState(
        targetValue = if (animTriggered) 0.dp else 26.dp,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "BadgeSlideAnimation"
    )

    // Badge Fade In Animation
    val badgeAlpha by animateFloatAsState(
        targetValue = if (animTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "BadgeFadeAnimation"
    )

    val buttonBorder = if (enabled) {
        BorderStroke(1.dp, borderColor)
    } else {
        BorderStroke(1.dp, ContentSecondary)
    }


    if (badgeText != null) {
        Box(modifier = modifier.fillMaxWidth()) {
            // Sliding & Fading Header Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp) // Covers visible space + underlap zone
                    .offset(y = badgeOffsetY) // Slides up from behind the button
                    .alpha(badgeAlpha)        // Fades in simultaneously
                    .background(
                        color = Color(0xFF008E11).copy(0.8f),
                        shape = shape
                    )
                    .padding(top = 4.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = badgeText,
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentInvPrimary
                )
            }

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp) // Offset down to keep the header space free
                    .height(height),
                enabled = enabled,
                shape = shape,
                colors = colors,
                contentPadding = contentPadding,
                border = buttonBorder
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = if (enabled) Color.Unspecified else ContentSecondary
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = text,
                        fontSize = 18.sp,
                        fontFamily = Outfit,
                        color = if (enabled) ContentPrimary else ContentSecondary
                    )
                }
            }
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(height),
            enabled = enabled,
            shape = shape,
            colors = colors,
            contentPadding = contentPadding,
            border = buttonBorder
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = if (enabled) Color.Unspecified else ContentSecondary
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontFamily = Outfit,
                    color = if (enabled) ContentPrimary else ContentSecondary
                )
            }
        }
    }
}


// --------------------------------------- Preview  ----------------------------------------------

@Preview(showBackground = true)
@Composable
fun AuthButtonPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Google sign-in with a custom red/green border color
        AuthButton(
            onClick = { },
            text = "Continue with Google",
            icon = painterResource(id = R.drawable.ic_google),
            badgeText = "Fastest & Most Used",
            borderColor = Color(0xFF008E11).copy(0.8f),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Standard Email sign-in option (uses default ContentPrimary border)
        AuthButton(
            onClick = { },
            text = "Sign in with Email",
            icon = rememberVectorPainter(Icons.Outlined.MailOutline)
        )
    }
}