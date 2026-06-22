package com.harshdeep.jasnify.presentation.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.others.CustomCheckbox
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.SearchBarType
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilterBottomSheet(
    title: String,
    sheetState: SheetState,
    options: List<String>,
    initialSelectedOptions: Set<String>,
    showSearchBar: Boolean,
    onDismiss: () -> Unit,
    onApply: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    isMultiSelect: Boolean = true // Added parameter to toggle between single and multi selection mode
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfacePrimary,
        scrimColor = Color.Black.copy(alpha = 0.8f),
        dragHandle = null,
        shape = SquircleShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        FilterBottomSheetContent(
            title = title,
            options = options,
            initialSelectedOptions = initialSelectedOptions,
            showSearchBar = showSearchBar,
            isMultiSelect = isMultiSelect,
            onDismiss = onDismiss,
            onApply = onApply,
            modifier = modifier
        )
    }
}

@Composable
fun FilterBottomSheetContent(
    title: String,
    options: List<String>,
    initialSelectedOptions: Set<String>,
    showSearchBar: Boolean,
    isMultiSelect: Boolean,
    onDismiss: () -> Unit,
    onApply: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    var tempSelectedOptions by remember(initialSelectedOptions) { mutableStateOf(initialSelectedOptions) }

    // Clear action is only available in multi-select mode and when at least one option is chosen
    val showClearButton = isMultiSelect && tempSelectedOptions.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Title
            Text(
                text = title,
                style = JasnifyTheme.typography.displayMedium,
                color = ContentPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            // Optional Search Bar Integration
            if (showSearchBar) {
                CustomSearchBar(
                    value = searchText,
                    placeholder = "Search",
                    onValueChange = { searchText = it },
                    type = SearchBarType.DEFAULT,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Scrollable List of Filterable Options
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp, max = 450.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Offset to ensure scrolling clear of pinned action buttons
            ) {
                val filteredOptions = if (showSearchBar && searchText.isNotBlank()) {
                    options.filter { it.contains(searchText, ignoreCase = true) }
                } else {
                    options
                }

                items(filteredOptions) { option ->
                    val isSelected = tempSelectedOptions.contains(option)

                    CustomCheckbox(
                        text = option,
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            tempSelectedOptions = if (isMultiSelect) {
                                // Multi-select behavior: add or remove item from the set
                                if (checked) {
                                    tempSelectedOptions + option
                                } else {
                                    tempSelectedOptions - option
                                }
                            } else {
                                // Single-select behavior: replace the entire set or clear it
                                if (checked) {
                                    setOf(option)
                                } else {
                                    emptySet()
                                }
                            }
                        },
                        isMultiSelect = isMultiSelect,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )
                }
            }
        }

        // Pinned Footer containing Cancel and Apply actions
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(SurfacePrimary),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.background(SurfacePrimary)
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Action Button (Clear or Cancel)
                    CustomTextButton(
                        onClick = {
                            if (showClearButton) {
                                tempSelectedOptions = emptySet()
                            } else {
                                onDismiss()
                            }
                        },
                        text = if (showClearButton) "Clear" else "Cancel",
                        modifier = Modifier.weight(1f),
                        type = ButtonType.Tertiary,
                        shapeStyle = ButtonShapeStyle.Square
                    )

                    // Right Primary Action Button (Apply)
                    CustomTextButton(
                        onClick = { onApply(tempSelectedOptions) },
                        text = "Apply",
                        modifier = Modifier.weight(1.2f),
                        type = ButtonType.Primary,
                        shapeStyle = ButtonShapeStyle.Square
                    )
                }
            }
        }
    }
}

// ---------------- Previews ----------------

@Preview(showBackground = true, name = "With Search Bar (Multi-Select Cuisine)")
@Composable
fun FilterBottomSheetWithSearchPreview() {
    JasnifyTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFF1E1E1E))
                .padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF7F8F7),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                FilterBottomSheetContent(
                    title = "Select Cuisine",
                    options = listOf(
                        "Indian",
                        "Japanese",
                        "Mexican",
                        "Italian",
                        "Chinese",
                        "French",
                        "Thai",
                        "Korean"
                    ),
                    initialSelectedOptions = setOf("Indian", "Mexican"),
                    showSearchBar = true,
                    isMultiSelect = true,
                    onDismiss = {},
                    onApply = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Without Search Bar (Single-Select Type)")
@Composable
fun FilterBottomSheetWithoutSearchPreview() {
    JasnifyTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFF1E1E1E))
                .padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF7F8F7),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                FilterBottomSheetContent(
                    title = "Select Type",
                    options = listOf(
                        "Starters",
                        "Beverages",
                        "Main Course",
                        "Desserts"
                    ),
                    initialSelectedOptions = setOf("Main Course"),
                    showSearchBar = false,
                    isMultiSelect = false, // Configured for single select
                    onDismiss = {},
                    onApply = {}
                )
            }
        }
    }
}