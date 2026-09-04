package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

enum class ToastType {
    DEFAULT,
    SUCCESS,
    ERROR
}

// --- Toast State Management ---
@Immutable
data class ToastData(
    val message: String? = null,
    val type: ToastType = ToastType.DEFAULT,
    val buttonText: String? = null,
    val onButtonClick: (() -> Unit)? = null
)

@Composable
fun CustomToast(
    message: String,
    type: ToastType,
    leadingIcon: Painter? = null,
    iconColor: Color = ContentInvPrimary,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    durationMillis: Int = 2000
) {
    val backgroundColor = when (type) {
        ToastType.DEFAULT -> Color(0xFF555555)
        ToastType.SUCCESS -> Color(0xFF26843D)
        ToastType.ERROR -> Color(0xFFA32626)
    }

    val contentColor = ContentInvPrimary
    val buttonColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
    val progressBarColor = ContentInvPrimary.copy(alpha = 0.7f)
    val showButton = buttonText != null && onButtonClick != null
    val toastShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

    val progress = remember { Animatable(1f) }

    LaunchedEffect(message) {
        progress.snapTo(1f)
        progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(toastShape)
            .background(
                color = backgroundColor,
                shape = toastShape
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconModifier = Modifier.size(24.dp)

            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = type.name,
                    modifier = iconModifier,
                    tint = iconColor
                )
            } else {
                val defaultIcon = when (type) {
                    ToastType.DEFAULT -> painterResource(R.drawable.ic_tick2)
                    ToastType.SUCCESS -> painterResource(R.drawable.ic_tick2)
                    ToastType.ERROR -> painterResource(R.drawable.ic_info)
                }
                Icon(
                    painter = defaultIcon,
                    contentDescription = type.name,
                    tint = iconColor,
                    modifier = iconModifier
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = message,
                color = contentColor,
                modifier = Modifier.weight(1f),
                style = JasnifyTheme.typography.labelXLarge
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (showButton) {
                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = contentColor
                    ),
                    shape = SquircleShape(CornerMedium, CornerSmoothingDefault),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .align(alignment = Alignment.CenterVertically)
                ) {
                    Text(
                        text = buttonText,
                        style = JasnifyTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Progress bar shrinking towards the left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(progress.value)
                .height(3.dp)
                .background(progressBarColor)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ToastComponentPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomToast(
            message = "Toast Message (Default Icon)",
            type = ToastType.DEFAULT,
            buttonText = "Button",
            onButtonClick = {}
        )

        CustomToast(
            message = "Success Toast (Default Icon, No button)",
            type = ToastType.SUCCESS
        )

        CustomToast(
            message = "Error Toast with a custom painter resource icon override",
            type = ToastType.ERROR,
            leadingIcon = painterResource(R.drawable.ic_info),
            buttonText = "Retry",
            onButtonClick = {}
        )

        CustomToast(
            message = "Toast (No Icon, No Button)",
            type = ToastType.DEFAULT,
            leadingIcon = painterResource(R.drawable.ic_tick2)
        )
    }
}