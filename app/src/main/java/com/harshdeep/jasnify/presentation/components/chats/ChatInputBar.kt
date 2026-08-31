package com.harshdeep.jasnify.presentation.components.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.*

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    isEditing: Boolean = false,
    onCancelEdit: () -> Unit = {}
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }

    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, bottom = 12.dp, end = 12.dp)
    ) {
        Column {
            if (isEditing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, start = 12.dp, end = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = SurfaceBrandPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Editing message",
                            style = JasnifyTheme.typography.labelSmall,
                            color = SurfaceBrandPrimary
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = "Cancel",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onCancelEdit() },
                        tint = ContentSecondary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(SurfaceSecondary, RoundedCornerShape(28.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Text Input Field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = "Type a message...",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentSecondary
                        )
                    }
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            textFieldValue = newValue
                            if (newValue.text != value) {
                                onValueChange(newValue.text)
                            }
                        },
                        textStyle = JasnifyTheme.typography.labelLarge.copy(color = ContentPrimary),
                        cursorBrush = SolidColor(ContentPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                }

                // Send / Save Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (value.isNotBlank()) SurfacePrimary else SurfacePrimary.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .clickable(
                            enabled = value.isNotBlank(),
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onSendClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = if (isEditing) painterResource(R.drawable.ic_check) else painterResource(R.drawable.ic_send),
                        contentDescription = if (isEditing) "Save" else "Send",
                        tint = if (value.isNotBlank()) ContentPrimary else ContentSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
