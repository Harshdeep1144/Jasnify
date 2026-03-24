package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmall
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

val DefaultToastBackground = Color(0xFF555555).copy(alpha = 0.9f)
val SuccessToastBackground = Color(0xFF26843D).copy(alpha = 0.9f)
val ErrorToastBackground = Color(0xFFA32626).copy(alpha = 0.9f)

enum class ToastType {
    DEFAULT,
    SUCCESS,
    ERROR
}

data class ToastStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val icon: ImageVector? = null,
    val buttonColor: Color
)

@Composable
fun getToastStyle(type: ToastType, customIcon: ImageVector? = null): ToastStyle {
    val baseStyle = when (type) {
        ToastType.DEFAULT -> ToastStyle(
            backgroundColor = DefaultToastBackground,
            contentColor = ContentInvPrimary,
            icon = null,
            buttonColor = ContentInvPrimary.copy(alpha = 0.16f)
        )
        ToastType.SUCCESS -> ToastStyle(
            backgroundColor = SuccessToastBackground,
            contentColor = ContentInvPrimary,
            icon = Icons.Default.CheckCircle,
            buttonColor = ContentInvPrimary.copy(alpha = 0.16f)
        )
        ToastType.ERROR -> ToastStyle(
            backgroundColor = ErrorToastBackground,
            contentColor = ContentInvPrimary,
            icon = Icons.Default.Warning,
            buttonColor = ContentInvPrimary.copy(alpha = 0.16f)
        )
    }

    return baseStyle.copy(icon = customIcon ?: baseStyle.icon)
}

@Composable
fun CustomToast(
    message: String,
    type: ToastType,
    leadingIcon: ImageVector? = null,
    buttonText: String? = "Button",
    onButtonClick: (() -> Unit)? = null
) {
    val style = getToastStyle(type, leadingIcon)

    val showButton = buttonText != null && onButtonClick != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = style.backgroundColor,
                shape = SquircleShape(16.dp, CornerSmoothingDefault)
            )
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        style.icon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = type.name,
                tint = style.contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
        }

        Text(
            text = message,
            color = style.contentColor,
            modifier = Modifier.weight(1f),
            style = JasnifyTheme.typography.labelXLarge
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (showButton) {
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = style.buttonColor,
                    contentColor = style.contentColor
                ),
                shape = SquircleShape(12.dp, CornerSmoothingDefault),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.dp),
                modifier = Modifier.height(40.dp).align(alignment = Alignment.CenterVertically)
            ) {
                Text(text = buttonText, style = JasnifyTheme.typography.labelLarge, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ToastComponentPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Toast Examples", color = Color.White)

        CustomToast(
            message = "Toast Message",
            type = ToastType.DEFAULT,
            buttonText = "Button",
            onButtonClick = {}
        )

        CustomToast(
            message = "Success Toast (Icon, No button)",
            type = ToastType.SUCCESS,
            buttonText = null,
            onButtonClick = null
        )

        CustomToast(
            message = "Error Toast with Custom Icon",
            type = ToastType.ERROR,
            leadingIcon = Icons.Default.CheckCircle,
        )

        CustomToast(
            message = "Toast (No Icon, No Button)",
            type = ToastType.DEFAULT,
            leadingIcon = null,
            buttonText = null,
            onButtonClick = null
        )
    }
}