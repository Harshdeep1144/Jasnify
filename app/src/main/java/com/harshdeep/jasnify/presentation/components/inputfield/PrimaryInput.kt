package com.harshdeep.jasnify.presentation.components.inputfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun PrimaryInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    textStyle: TextStyle = JasnifyTheme.typography.labelXLarge.copy(color = ContentPrimary),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: Painter? = null,
    trailingIconEnabled: Boolean = false, // Added disable/enable styling parameter
    shape: Shape = SquircleShape(CornerLarge, CornerSmoothingDefault), // Takes direct SquircleShape or other Shapes, defaulting to SquircleShape
    readOnly: Boolean = false // Expose readOnly configuration parameter
) {
    val isPassword = keyboardType == KeyboardType.Password

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val showTrailingIcon = value.isNotEmpty()

    val currentVisualTransformation = when {
        isPassword && !isPasswordVisible -> PasswordVisualTransformation()
        else -> visualTransformation
    }

    val defaultLeadingIcon: ImageVector? = when (keyboardType) {
        KeyboardType.Password -> if (value.isEmpty()) {
            Icons.Outlined.LockOpen
        } else {
            Icons.Outlined.Lock
        }
        KeyboardType.Email -> Icons.Rounded.MailOutline
        // No leading icon for other types (like Text, Number, Phone, etc.)
        else -> null
    }

    // Determine trailing icon color based on the enabled state flag
    val trailingIconTint = if (trailingIconEnabled) ContentPrimary else ContentSecondary

    // Border highlights: Consistent 1.dp border width for focused/unfocused states
    val showActiveBorder = isFocused && !readOnly
    val borderThickness = 1.dp
    val borderColor = if (showActiveBorder) {
        ContentPrimary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
    }

    BasicTextField(
        value = value,
        onValueChange = { if (!readOnly) onValueChange(it) },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        readOnly = readOnly, // Apply readOnly state to disable caret and block physical typing
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = textStyle,
        cursorBrush = SolidColor(ContentPrimary),
        visualTransformation = currentVisualTransformation,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(SurfaceSecondary, shape)
                    .border(borderThickness, borderColor, shape)
                    .defaultMinSize(minHeight = 56.dp)
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon Layer
                if (defaultLeadingIcon != null) {
                    Icon(
                        imageVector = defaultLeadingIcon,
                        contentDescription = null,
                        tint = ContentSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }

                // Input Content Container
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = ContentSecondary,
                            style = textStyle
                        )
                    }
                    innerTextField()
                }

                // Trailing Actions Layer
                if (trailingIcon != null) {
                    IconButton(onClick = { if (!readOnly) onValueChange("") }) {
                        Icon(
                            painter = trailingIcon,
                            contentDescription = "Custom Icon",
                            tint = trailingIconTint
                        )
                    }
                } else if (showTrailingIcon) {
                    if (isPassword) {
                        val image = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                        val description = if (isPasswordVisible) "Hide password" else "Show password"

                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible }
                        ) {
                            Icon(
                                imageVector = image,
                                contentDescription = description,
                                tint = trailingIconTint
                            )
                        }
                    } else {
                        IconButton(onClick = { if (!readOnly) onValueChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear input",
                                tint = trailingIconTint
                            )
                        }
                    }
                }
            }
        }
    )
}


// ----------------------------------------------- Preview -------------------------------------------------


@Preview(showBackground = true)
@Composable
fun PrimaryInputPreview() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("mysecretpass") }
    var text by remember { mutableStateOf("Some text") }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrimaryInput(
            value = email,
            onValueChange = { email = it },
            placeholder = "Enter email address",
            keyboardType = KeyboardType.Email
        )

        PrimaryInput(
            value = password,
            onValueChange = { password = it },
            placeholder = "Enter password",
            keyboardType = KeyboardType.Password
        )

        PrimaryInput(
            value = email,
            onValueChange = { email = it },
            placeholder = "Enter email address",
            keyboardType = KeyboardType.Password,
            shape = SquircleShape(CornerExtraSmall,CornerLarge,CornerLarge,CornerLarge,CornerSmoothingDefault)
        )

        PrimaryInput(
            value = text,
            onValueChange = { text = it },
            placeholder = "Enter regular text",
            keyboardType = KeyboardType.Text // KeyboardType.Text has a null defaultLeadingIcon
        )
    }
}