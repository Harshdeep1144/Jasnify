package com.harshdeep.jasnify.presentation.components.inputfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
    cornerType: CornerType = CornerType.DEFAULT
) {
    val isPassword = keyboardType == KeyboardType.Password
    var isPasswordVisible by remember { mutableStateOf(false) }
    val showClearIcon = value.isNotEmpty()

    val currentVisualTransformation = when {
        isPassword && !isPasswordVisible -> PasswordVisualTransformation()
        else -> visualTransformation // Use the provided visualTransformation for other cases (or VisualTransformation.None)
    }

    val shape = when (cornerType) {
        CornerType.DEFAULT -> SquircleShape(CornerLarge, CornerSmoothingDefault)
        CornerType.MESSAGE -> SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge,CornerLarge, CornerSmoothingDefault)
    }

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
            if (showClearIcon) {
                if (isPassword) {
                    // Password visibility toggle icon
                    val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (isPasswordVisible) "Hide password" else "Show password"

                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = description,
                            tint = ContentSecondary
                        )
                    }
                } else {
                    // Clear text button for non-password fields
                    IconButton(onClick = { onValueChange("") }) {
                        if (trailingIcon != null) {
                            // Custom icon provided, show it but it will clear the text on click
                            Icon(
                                painter = trailingIcon,
                                contentDescription = "Custom Icon",
                                tint = ContentSecondary
                            )
                        } else {
                            // Default clear icon
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear input",
                                tint = ContentSecondary
                            )
                        }
                    }
                }
            }
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
        keyboardType = KeyboardType.Email,
        cornerType = CornerType.MESSAGE
    )
}