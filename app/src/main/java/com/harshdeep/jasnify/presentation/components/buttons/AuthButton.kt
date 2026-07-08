package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.Outfit
import sv.lib.squircleshape.SquircleShape

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
    borderColor: Color = ContentPrimary // Added customizable border color parameter
) {
    val size = ButtonSize.Medium
    val type = ButtonType.Tertiary
    val (colors, height, shape) = getButtonStyles(size, type, shapeStyle)
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    // Dynamic border based on enabled state and input color
    val buttonBorder = if (enabled) BorderStroke(1.dp, borderColor) else BorderStroke(1.dp, ContentSecondary)

    if (badgeText != null) {
        Box(modifier = modifier.fillMaxWidth()) {
            // Green Header Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp) // Cover the visible header space + underlap zone
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
                    .padding(top = 26.dp) // Shifted down to leave the green badge visible
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

// ---------------------------- Preview  ----------------------------------------------

@Preview(showBackground = true)
@Composable
fun AuthButtonPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Google sign-in with a custom red border color
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