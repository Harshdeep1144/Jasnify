package com.harshdeep.jasnify.presentation.components.inputfield

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
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
import com.harshdeep.jasnify.data.models.SubEventItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceInvPrimary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val DisplayDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

private enum class TimelineState {
    EDITING,
    DISPLAY,
    EMPTY
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeLineInput(
    item: SubEventItem,
    onUpdate: (SubEventItem) -> Unit,
    onDelete: (SubEventItem) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceSecondary,
    hasBorder: Boolean = true
) {
    var tempItemState by remember { mutableStateOf(item) }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var isDatePickerVisible by remember { mutableStateOf(false) }

    // Update the temporary state whenever the hoisted item changes
    LaunchedEffect(item) {
        tempItemState = item
    }

    // Update tempItemState when selectedDate changes
    LaunchedEffect(selectedDate) {
        selectedDate?.let { date ->
            val formattedDate = date.format(DisplayDateFormatter)
            tempItemState = tempItemState.copy(date = formattedDate)
        }
    }

    // Determine the active state of the component
    val currentState = when {
        tempItemState.isEditing -> TimelineState.EDITING
        tempItemState.date.isEmpty() && tempItemState.name.isEmpty() -> TimelineState.EMPTY
        else -> TimelineState.DISPLAY
    }

    AnimatedContent(
        targetState = currentState,
        transitionSpec = {
            val enterTransition = fadeIn(
                animationSpec = tween(durationMillis = 300)
            ) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 300)
            )

            val exitTransition = fadeOut(
                animationSpec = tween(durationMillis = 300)
            ) + shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 300)
            )

            // Combine enter and exit with SizeTransform for card morphing
            enterTransition.togetherWith(exitTransition)
                .using(
                    // Animates the container size change (height difference between states)
                    SizeTransform(clip = false, sizeAnimationSpec = { _, _ ->
                        tween(durationMillis = 300)
                    })
                )
        },
        label = "TimeLineInputTransition"
    ) { state ->
        when (state) {
            TimelineState.EDITING -> {
                EditableTimeLineCard(
                    item = tempItemState,
                    isExisting = item.isExisting,
                    backgroundColor = backgroundColor,
                    hasBorder = hasBorder,
                    onValueChange = { newItem -> tempItemState = newItem },
                    onDone = {
                        // When done, mark it as existing and exit editing mode
                        onUpdate(tempItemState.copy(isEditing = false, isExisting = true))
                    },
                    onCancel = {
                        if (item.date.isEmpty() && item.name.isEmpty()) {
                            onDelete(item)
                        } else {
                            onUpdate(item.copy(isEditing = false))
                        }
                    },
                    onDelete = { onDelete(item) },
                    onShowDatePicker = { isDatePickerVisible = true },
                    modifier = modifier
                )
            }
            TimelineState.EMPTY -> {
                EmptyDisplayTimeLine(
                    backgroundColor = backgroundColor,
                    hasBorder = hasBorder,
                    onEdit = {
                        onUpdate(item.copy(isEditing = true))
                    },
                    modifier = modifier
                )
            }
            TimelineState.DISPLAY -> {
                DisplayTimeLine(
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

    // Show the date picker bottom sheet when needed
    if (isDatePickerVisible) {
        DatePickerSheet(
            onDismiss = { isDatePickerVisible = false },
            onDateSelected = { date ->
                selectedDate = date
                isDatePickerVisible = false
            }
        )
    }
}


//--------------------------------------- Helper Functions ----------------------------------------


// ---- Empty Display Card (Matching uploaded design mockup) ----
@Composable
private fun EmptyDisplayTimeLine(
    backgroundColor: Color,
    hasBorder: Boolean,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (hasBorder) {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // --- Date Placeholder ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select a date",
                    style = JasnifyTheme.typography.labelXLarge.copy(
                        color = ContentSecondary.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date Picker Placeholder",
                    tint = ContentSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.height(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )

            // --- Name Placeholder ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enter sub-event name of your choice",
                    style = JasnifyTheme.typography.labelXLarge.copy(
                        color = ContentSecondary.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


// ---- Editable Card ----
@Composable
private fun EditableTimeLineCard(
    item: SubEventItem,
    isExisting: Boolean,
    backgroundColor: Color,
    hasBorder: Boolean,
    onValueChange: (SubEventItem) -> Unit,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onShowDatePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (hasBorder) {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
        )
    } else {
        Modifier
    }

    Surface(
        color = backgroundColor,
        modifier = modifier.fillMaxWidth()
            .then(borderModifier),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // --- Date Input Field ---
            Row(
                modifier = Modifier.fillMaxWidth().
                heightIn(min = 56.dp)
                    .padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date.ifEmpty { "Select a date" },
                    style = JasnifyTheme.typography.labelXLarge.copy(
                        color = if (item.date.isEmpty()) ContentSecondary.copy(alpha = 0.7f) else ContentPrimary
                    ),
                    modifier = Modifier.weight(1f)
                        .clickable(
                            onClick = onShowDatePicker,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )

                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date Picker",
                    tint = ContentPrimary,
                    modifier = Modifier.size(20.dp)
                        .clickable(onClick = onShowDatePicker)
                )
            }

            HorizontalDivider(Modifier.height(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

            // --- Name Input Field ---
            Row(
                modifier = Modifier.fillMaxWidth().
                heightIn(min = 56.dp)
                    .padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InputTextField(
                    value = item.name,
                    placeholder = "Enter sub-event name of your choice",
                    textStyle = JasnifyTheme.typography.labelXLarge.copy(ContentPrimary),
                    onValueChange = { newName -> onValueChange(item.copy(name = newName)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider(Modifier.height(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

            // --- Action Buttons (Delete, Cancel, Done) ---
            Row(
                modifier = Modifier.fillMaxWidth().
                heightIn(min = 74.dp)
                    .padding(all = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (isExisting) {
                    CustomIconButton(
                        onClick = onDelete,
                        icon = painterResource(R.drawable.ic_delete),
                        modifier = Modifier.size(40.dp)
                            .border(
                                1.dp, MaterialTheme.colorScheme.error.copy(0.08f), SquircleShape(CornerMedium)
                            ),
                        shapeStyle = ButtonShapeStyle.Square,
                        contentColor = MaterialTheme.colorScheme.error,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    )
                } else {
                    Spacer(modifier = Modifier.width(40.dp))
                }

                Row(
                    horizontalArrangement = Arrangement.End
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
                        enabled = item.name.isNotBlank() && item.date.isNotBlank() // Ensure date is also selected
                    ) {
                        Text("Done", style = JasnifyTheme.typography.labelLarge.copy(ContentInvPrimary))
                    }
                }
            }
        }
    }
}

// ---- Custom BasicTextField wrapper ---
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

// --- Display Card  ---
@Composable
private fun DisplayTimeLine(
    item: SubEventItem,
    backgroundColor: Color,
    hasBorder: Boolean,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (hasBorder) {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
        )
    } else {
        Modifier
    }

    Card(
        modifier = modifier.fillMaxWidth()
            .then(borderModifier),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val dateText = item.date.ifEmpty { "Date not set" }

                Text(
                    text = dateText,
                    style = JasnifyTheme.typography.labelLarge.copy(ContentSecondary)
                )

                Text(
                    text = item.name.ifEmpty { "Sub-Event Name" },
                    style = JasnifyTheme.typography.labelXLarge.copy(ContentPrimary),
                    lineHeight = 1.2.em,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            CustomIconButton(
                onClick = onEdit,
                icon = painterResource(R.drawable.ic_edit),
                containerColor = backgroundColor,
                contentColor = ContentPrimary,
                size = ButtonSize.Small
            )
        }
    }
}

// ---  Preview ---


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TimeLineInputPreview() {
    MaterialTheme() {
        val items = remember {
            mutableStateListOf(
                SubEventItem(id = "1", date = "15 Oct 2025", name = "Project Review Meeting", isExisting = true),
                SubEventItem(id = "2", date = "20 Nov 2025", name = "Final Deadline Prep", isExisting = true, isEditing = true),
                SubEventItem(id = "3", date = "", name = "", isExisting = false, isEditing = false)
            )
        }

        // Auto-generate unique IDs for new items
        val nextNewId = remember { mutableStateOf(0) }

        // --- Check if any item is currently unsaved and being edited ---
        val hasUnsavedEditingItem by remember {
            derivedStateOf {
                // Check if any item is in editing mode AND is not yet marked as existing (i.e., not saved once)
                items.any { it.isEditing && !it.isExisting }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfacePrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // --- Button to Add New Item ---
                Button(
                    onClick = {
                        // Add a new item to the start of the list, marked for editing, but NOT existing
                        items.add(0, SubEventItem(
                            id = "new-${nextNewId.value++}",
                            isEditing = true,
                            isExisting = false
                        ))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    // --- Disable if an unsaved item is currently being edited ---
                    enabled = !hasUnsavedEditingItem
                ) {
                    Text("Add New Timeline Item", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(16.dp))
            }

            items(items.size) { index ->
                TimeLineInput(
                    item = items[index],
                    // Showcasing parameter overrides if needed (e.g., custom background and optional border)
                    backgroundColor = SurfaceSecondary,
                    hasBorder = true,
                    onUpdate = { updatedItem ->
                        // Only update if the ID matches to prevent concurrent modification issues
                        val foundIndex = items.indexOfFirst { it.id == updatedItem.id }
                        if (foundIndex != -1) {
                            items[foundIndex] = updatedItem
                        }
                    },
                    onDelete = { itemToDelete ->
                        // Remove the item from the list when deleted
                        items.remove(itemToDelete)
                    }
                )
            }
        }
    }
}