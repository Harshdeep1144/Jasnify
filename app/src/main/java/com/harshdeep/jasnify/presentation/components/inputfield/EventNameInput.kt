package com.harshdeep.jasnify.presentation.components.inputfield

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceInvPrimary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

// Dedicated data class representing the state of an item inside [EventNameInput]
data class EventNameInputItem(
    val id: String,
    val name: String,
    val isEditing: Boolean = false,
    val isExisting: Boolean = false
)

private enum class EventNameState {
    EDITING,
    DISPLAY,
    EMPTY
}

@Composable
fun EventNameInput(
    item: EventNameInputItem,
    onUpdate: (EventNameInputItem) -> Unit,
    onDelete: (EventNameInputItem) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceSecondary,
    hasBorder: Boolean = true
) {
    var tempItemState by remember { mutableStateOf(item) }

    LaunchedEffect(item) {
        tempItemState = item
    }

    val currentState = when {
        tempItemState.isEditing -> EventNameState.EDITING
        tempItemState.name.isEmpty() -> EventNameState.EMPTY
        else -> EventNameState.DISPLAY
    }

    AnimatedContent(
        targetState = currentState,
        transitionSpec = {
            val enterTransition = fadeIn(
                animationSpec = tween(durationMillis = 220, delayMillis = 80)
            ) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 300)
            )

            val exitTransition = fadeOut(
                animationSpec = tween(durationMillis = 150)
            ) + shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 300)
            )

            enterTransition.togetherWith(exitTransition)
                .using(
                    SizeTransform(clip = true, sizeAnimationSpec = { _, _ ->
                        tween(durationMillis = 300)
                    })
                )
        },
        label = "EventNameInputTransition"
    ) { state ->
        when (state) {
            EventNameState.EDITING -> {
                EditableEventNameCard(
                    item = tempItemState,
                    backgroundColor = backgroundColor,
                    hasBorder = hasBorder,
                    onValueChange = { newItem -> tempItemState = newItem },
                    onDone = {
                        onUpdate(tempItemState.copy(isEditing = false, isExisting = true))
                    },
                    onCancel = {
                        if (item.name.isEmpty()) {
                            onDelete(item)
                        } else {
                            onUpdate(item.copy(isEditing = false))
                        }
                    },
                    modifier = modifier
                )
            }
            EventNameState.EMPTY -> {
                EmptyDisplayEventName(
                    backgroundColor = backgroundColor,
                    hasBorder = hasBorder,
                    onEdit = {
                        onUpdate(item.copy(isEditing = true))
                    },
                    modifier = modifier
                )
            }
            EventNameState.DISPLAY -> {
                DisplayEventName(
                    item = tempItemState,
                    backgroundColor = backgroundColor,
                    hasBorder = hasBorder,
                    onEdit = {
                        onUpdate(item.copy(isEditing = true))
                    },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun EmptyDisplayEventName(
    backgroundColor: Color,
    hasBorder: Boolean,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (hasBorder) {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
        )
    } else {
        Modifier
    }

    Surface(
        color = backgroundColor,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onEdit,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .then(borderModifier),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enter primary event name",
                style = JasnifyTheme.typography.labelXLarge.copy(
                    color = ContentSecondary.copy(alpha = 0.7f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EditableEventNameCard(
    item: EventNameInputItem,
    backgroundColor: Color,
    hasBorder: Boolean,
    onValueChange: (EventNameInputItem) -> Unit,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (hasBorder) {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
        )
    } else {
        Modifier
    }

    Surface(
        color = backgroundColor,
        modifier = modifier.fillMaxWidth().then(borderModifier),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InputTextField(
                    value = item.name,
                    placeholder = "Enter primary event name",
                    textStyle = JasnifyTheme.typography.labelXLarge.copy(ContentPrimary),
                    onValueChange = { newName -> onValueChange(item.copy(name = newName)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider(Modifier.height(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            Row(
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = 74.dp)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfacePrimary),
                    shape = SquircleShape(100, CornerSmoothingDefault),
                    contentPadding = PaddingValues(16.dp, 12.dp),
                ) {
                    Text(
                        text = "Cancel", style = JasnifyTheme.typography.labelLarge.copy(ContentPrimary)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onDone,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceInvPrimary),
                    shape = SquircleShape(100, CornerSmoothingDefault),
                    contentPadding = PaddingValues(16.dp, 12.dp),
                    enabled = item.name.isNotBlank()
                ) {
                    Text("Done", style = JasnifyTheme.typography.labelLarge.copy(ContentInvPrimary))
                }
            }
        }
    }
}

@Composable
private fun DisplayEventName(
    item: EventNameInputItem,
    backgroundColor: Color,
    hasBorder: Boolean,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (hasBorder) {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
        )
    } else {
        Modifier
    }

    Surface(
        color = backgroundColor,
        modifier = modifier.fillMaxWidth()
            .then(borderModifier),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Primary Event Name :",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.name.ifEmpty { "Event Name" },
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
            }

            CustomIconButton(
                onClick = onEdit,
                icon = painterResource(id = R.drawable.ic_edit),
                containerColor = SurfacePrimary,
                contentColor = ContentPrimary,
                size = ButtonSize.Small
            )
        }
    }
}

@Composable
private fun InputTextField(
    value: String,
    placeholder: String,
    textStyle: TextStyle,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(lineHeight = 1.2.em),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle.copy(color = ContentSecondary.copy(alpha = 0.7f))
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "EventNameInput States Preview")
@Composable
fun EventNameInputPreview() {
    MaterialTheme {
        var stateEmpty by remember {
            mutableStateOf(EventNameInputItem(id = "p_empty", name = "", isExisting = false, isEditing = false))
        }
        var stateEditing by remember {
            mutableStateOf(EventNameInputItem(id = "p_editing", name = "Taylor & Travis's Wedding", isExisting = false, isEditing = true))
        }
        var stateDisplay by remember {
            mutableStateOf(EventNameInputItem(id = "p_display", name = "Taylor & Travis's Wedding", isExisting = true, isEditing = false))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            EventNameInput(
                item = stateEmpty,
                onUpdate = { stateEmpty = it },
                onDelete = { }
            )

            EventNameInput(
                item = stateEditing,
                onUpdate = { stateEditing = it },
                onDelete = { }
            )

            EventNameInput(
                item = stateDisplay,
                onUpdate = { stateDisplay = it },
                onDelete = { }
            )
        }
    }
}