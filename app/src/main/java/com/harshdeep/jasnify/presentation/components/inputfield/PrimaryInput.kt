package com.harshdeep.jasnify.presentation.components.inputfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

enum class CornerType {
    DEFAULT,
    MESSAGE
}

@Composable
fun PrimaryInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(color = ContentPrimary),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: Painter? = null,
    trailingIconEnabled: Boolean = false, // Added disable/enable styling parameter
    cornerType: CornerType = CornerType.DEFAULT
) {
    val isPassword = keyboardType == KeyboardType.Password

    var isPasswordVisible by remember { mutableStateOf(false) }
    val showTrailingIcon = value.isNotEmpty()

    val currentVisualTransformation = when {
        isPassword && !isPasswordVisible -> PasswordVisualTransformation()
        else -> visualTransformation
    }

    val shape = when (cornerType) {
        CornerType.DEFAULT -> SquircleShape(CornerLarge, CornerSmoothingDefault)
        CornerType.MESSAGE -> SquircleShape(
            CornerExtraSmall,
            CornerLarge,
            CornerLarge,
            CornerLarge,
            CornerSmoothingDefault
        )
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

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = ContentSecondary
            )
        },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = textStyle,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
            focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            errorBorderColor = Color.Red,
            focusedContainerColor = SurfaceSecondary,
            unfocusedContainerColor = SurfaceSecondary
        ),
        trailingIcon = {
            if (trailingIcon != null) {
                // Show the custom trailing icon immediately and always
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        painter = trailingIcon,
                        contentDescription = "Custom Icon",
                        tint = trailingIconTint
                    )
                }
            } else if (showTrailingIcon) {
                if (isPassword) {
                    val image =
                        if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
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
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear input",
                            tint = trailingIconTint
                        )
                    }
                }
            }
        },
        leadingIcon = if (defaultLeadingIcon != null) {
            {
                Icon(
                    imageVector = defaultLeadingIcon,
                    contentDescription = null,
                    tint = ContentSecondary
                )
            }
        } else {
            null // Explicitly set to null to remove the space
        },
        visualTransformation = currentVisualTransformation
    )
}


@Preview(showBackground = true)
@Composable
fun PrimaryInputPreview() {
    var email by remember { mutableStateOf("") }

    PrimaryInput(
        value = email,
        onValueChange = { email = it },
        placeholder = "Enter email address",
        keyboardType = KeyboardType.Email
    )
}

@Preview(showBackground = true)
@Composable
fun PrimaryInputPasswordPreview() {
    var password by remember { mutableStateOf("mysecretpass") }

    PrimaryInput(
        value = password,
        onValueChange = { password = it },
        placeholder = "Enter password",
        keyboardType = KeyboardType.Password
    )
}

@Preview(showBackground = true)
@Composable
fun PrimaryInputCustomPreview() {
    var email by remember { mutableStateOf("") }

    PrimaryInput(
        value = email,
        onValueChange = { email = it },
        placeholder = "Enter email address",
        keyboardType = KeyboardType.Password,
        cornerType = CornerType.MESSAGE
    )
}

@Preview(showBackground = true)
@Composable
fun PrimaryInputTextNoIconPreview() {
    var text by remember { mutableStateOf("Some text") }

    PrimaryInput(
        value = text,
        onValueChange = { text = it },
        placeholder = "Enter regular text",
        keyboardType = KeyboardType.Text // KeyboardType.Text has a null defaultLeadingIcon
    )
}