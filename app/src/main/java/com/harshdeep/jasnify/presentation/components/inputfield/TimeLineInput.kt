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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSheet // Correct Import
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val DisplayDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

// --- Main Component ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeLineInput(
    item: SubEventItem,
    onUpdate: (SubEventItem) -> Unit,
    onDelete: (SubEventItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local state for the item being edited.
    var tempItemState by remember { mutableStateOf(item) }

    // State for managing the date picker.
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


    // The core component that switches between the two views with animation
    AnimatedContent(
        targetState = tempItemState.isEditing,
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
                    // Animates the container size change (height difference between cards)
                    SizeTransform(clip = false, sizeAnimationSpec = { _, _ ->
                        tween(durationMillis = 300)
                    })
                )
        },
        label = "TimeLineInputTransition"
    ) { isEditing ->
        if (isEditing) {
            EditableTimeLineCard(
                item = tempItemState,
                isExisting = item.isExisting,
                onValueChange = { newItem -> tempItemState = newItem },
                onDone = {
                    // When done, mark it as existing and exit editing mode
                    onUpdate(tempItemState.copy(isEditing = false, isExisting = true))
                },
                onCancel = {
                    if(item.date == "" && item.name == ""){
                        onDelete(item)
                    }else{
                        onUpdate(item.copy(isEditing = false))
                    }
                },
                onDelete = { onDelete(item) },
                onShowDatePicker = { isDatePickerVisible = true },
                modifier = modifier
            )
        } else {
            DisplayTimeLine(
                item = tempItemState,
                onEdit = {
                    // Enter editing mode
                    onUpdate(item.copy(isEditing = true))
                },
                modifier = modifier
            )
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

// ---- Editable Card ----
@Composable
private fun EditableTimeLineCard(
    item: SubEventItem,
    isExisting: Boolean,
    onValueChange: (SubEventItem) -> Unit,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onShowDatePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceSecondary,
        modifier = modifier.fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
            ),
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

                // LEFT SIDE: DELETE BUTTON
                if (isExisting) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(42.dp)
                            .background(SurfacePrimary, shape = SquircleShape(CornerLarge, CornerSmoothingDefault))
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Item",
                            tint = Color.Red.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(42.dp))
                }

                // RIGHT SIDE: CANCEL and DONE
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
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
            ),
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),

        colors = CardDefaults.cardColors(containerColor = SurfaceSecondary),
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

            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.Center
            ){
                // NOTE: painterResource will use the mock R.drawable.ic_edit value
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                        .clickable(onClick = onEdit)
                )
            }

        }
    }
}

// ---  Preview (Updated to use SubEventItem and handle deletion) ---
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TimeLineInputPreview() {

    // Mock Material Theme for the preview
    MaterialTheme() {
        // Sample data for the preview
        val items = remember {
            mutableStateListOf(
                // Existing item - saved, not editing
                SubEventItem(id = "1", date = "15 Oct 2025", name = "Project Review Meeting", isExisting = true),
                // Existing item - editing, so delete button will show
                SubEventItem(id = "2", date = "20 Nov 2025", name = "Final Deadline Prep", isExisting = true, isEditing = true),
                // New item that needs to be configured
                SubEventItem(id = "3", date = "", name = "", isExisting = false, isEditing = true)
            )
        }

        // Auto-generate unique IDs for new items
        val nextNewId = remember { mutableStateOf(0) }

        // --- NEW LOGIC: Check if any item is currently unsaved and being edited ---
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
                    // --- UPDATED: Disable if an unsaved item is currently being edited ---
                    enabled = !hasUnsavedEditingItem
                ) {
                    Text("Add New Timeline Item", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(16.dp))
            }

            items(items.size) { index ->
                TimeLineInput(
                    item = items[index],
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
