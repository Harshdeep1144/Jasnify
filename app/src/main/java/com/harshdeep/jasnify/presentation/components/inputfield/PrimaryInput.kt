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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
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
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    trailingIconEnabled: Boolean = true,
    shape: Shape = RoundedCornerShape(CornerLarge),
    readOnly: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE
) {
    val isPassword = keyboardType == KeyboardType.Password

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val showTrailingIcon = value.isNotEmpty() && isFocused

    val currentVisualTransformation = when {
        isPassword && !isPasswordVisible -> PasswordVisualTransformation()
        else -> visualTransformation
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
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = textStyle,
        cursorBrush = SolidColor(ContentPrimary),
        visualTransformation = currentVisualTransformation,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.background(SurfaceSecondary, shape)
                    .border(borderThickness, borderColor, shape)
                    .defaultMinSize(minHeight = 56.dp)
                    .padding(start = 16.dp, end = 8.dp, top = if (singleLine) 0.dp else 16.dp, bottom = if (singleLine) 0.dp else 16.dp),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
            ) {
                // Leading Icon Layer (Only renders if leadingIcon is provided)
                if (leadingIcon != null) {
                    Icon(
                        painter = leadingIcon,
                        contentDescription = null,
                        tint = ContentSecondary,
                        modifier = Modifier.padding(end = 12.dp).then(if (!singleLine) Modifier.padding(top = 4.dp) else Modifier)
                    )
                }

                // Input Content Container
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
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
                    IconButton(
                        onClick = { if (!readOnly) onValueChange("") },
                        modifier = if (!singleLine) Modifier.align(Alignment.Top) else Modifier
                    ) {
                        Icon(
                            painter = trailingIcon,
                            contentDescription = "Custom Icon",
                            tint = trailingIconTint
                        )
                    }
                } else if (showTrailingIcon) {
                    if (isPassword) {
                        val icon = if (isPasswordVisible) painterResource(R.drawable.ic_eye_open) else painterResource(R.drawable.ic_eye_closed)
                        val description = if (isPasswordVisible) "Hide password" else "Show password"

                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible }
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = description,
                                tint = trailingIconTint
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { if (!readOnly) onValueChange("") },
                            modifier = if (!singleLine) Modifier.align(Alignment.Top) else Modifier
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_circle_cross),
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(12.dp)
    ) {
        // Field with custom leading icon
        PrimaryInput(
            value = email,
            onValueChange = { email = it },
            leadingIcon = painterResource(R.drawable.ic_mail),
            placeholder = "Enter email address",
            keyboardType = KeyboardType.Email,
        )

        // Password field with custom leading icon
        PrimaryInput(
            value = password,
            onValueChange = { password = it },
            leadingIcon = painterResource(R.drawable.ic_key),
            placeholder = "Enter password",
            keyboardType = KeyboardType.Password,
        )

        // Custom shape, no leading icon (default null)
        PrimaryInput(
            value = email,
            onValueChange = { email = it },
            placeholder = "Enter email address",
            keyboardType = KeyboardType.Password,
            trailingIconEnabled = true,
            shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge, CornerLarge, CornerSmoothingDefault)
        )

        // Standard text field, no leading icon (default null)
        PrimaryInput(
            value = text,
            onValueChange = { text = it },
            placeholder = "Enter regular text",
            keyboardType = KeyboardType.Text,
            trailingIconEnabled = true
        )
    }
}