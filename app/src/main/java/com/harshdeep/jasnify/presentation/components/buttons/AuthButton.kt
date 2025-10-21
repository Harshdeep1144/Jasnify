package com.harshdeep.jasnify.presentation.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.Outfit
import com.harshdeep.jasnify.theme.SurfaceInvPrimary // Assuming this is needed for the border color

val defaultBorder = BorderStroke(1.dp, SurfaceInvPrimary)

@Composable
fun AuthButton(
    onClick: () -> Unit,
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shapeStyle: ButtonShapeStyle = ButtonShapeStyle.Square
) {
    val size = ButtonSize.Medium
    val type = ButtonType.Tertiary
    val (colors, height, shape) = getButtonStyles(size, type, shapeStyle)
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        shape = shape,
        border = if (enabled) defaultBorder else BorderStroke(1.dp, ContentSecondary),
        colors = colors,
        contentPadding = contentPadding
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Leading Icon
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (enabled) Color.Unspecified else ContentSecondary
            )

            Spacer(Modifier.width(8.dp))

            // Text
            Text(
                text = text,
                fontSize = 18.sp,
                fontFamily = Outfit,
                color = if (enabled) ContentPrimary else ContentSecondary
            )
        }
    }
}


// --- Preview  ---

@Preview(showBackground = true)
@Composable
fun AuthButtonPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthButton(
            onClick = {  },
            text = "Sign in with Email",
            icon = rememberVectorPainter(Icons.Outlined.MailOutline)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            onClick = {  },
            text = "Sign in with Google",
            icon = painterResource(id = R.drawable.ic_google)
        )
    }
}

