package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomRadioButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.utils.toFlagEmoji
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import sv.lib.squircleshape.SquircleShape

// Data class to represent a selectable item (used for country code, currency, etc.)
data class SelectableItem(
    val code: String,
    val name: String,
    val iconData: String,
    val emoji: String = iconData.toFlagEmoji()
)

// Row for a single item (Country Code or Currency)
@Composable
fun SelectableItemRow(
    item: SelectableItem,
    isSelected: Boolean,
    onSelect: (SelectableItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
            .clickable { onSelect(item) }
            .background(if (isSelected) SurfaceBrandSecondary else BackgroundPrimary)
            .padding(0.dp, 0.dp, 12.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            CustomRadioButton(
                selected = isSelected,
                onClick = { onSelect(item) },
                modifier = Modifier.padding(0.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Display the Image/Icon using the resource ID
            Text(
                text = item.emoji,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.code,
                style = if (isSelected) JasnifyTheme.typography.labelXLarge else JasnifyTheme.typography.labelXLarge,
                color = if (isSelected) ContentBrandDark else ContentSecondary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }

        Text(
            text = item.name,
            style = if (isSelected) JasnifyTheme.typography.labelXLarge else JasnifyTheme.typography.labelXLarge,
            color = if (isSelected) ContentBrandDark else ContentSecondary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
// The main generic bottom sheet for selection with search functionality
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableListBottomSheet(
    heading: String,
    sheetHeight: Dp,
    items: List<SelectableItem>,
    initialSelectedItem: SelectableItem,
    onItemSelected: (SelectableItem) -> Unit,
    onDismiss: () -> Unit,
    selectButtonText: String,
    onProgress: ((Float) -> Unit)? = null
) {
    var selectedItem by remember {
        mutableStateOf(
            items.find { it.code == initialSelectedItem.code } ?: if (items.isNotEmpty()) items.first() else initialSelectedItem
        )
    }

    var searchText by remember { mutableStateOf("") }

    val filteredItems = remember(items, searchText) {
        if (searchText.isBlank()) {
            items
        } else {
            items.filter {
                it.code.contains(searchText, ignoreCase = true) ||
                        it.name.contains(searchText, ignoreCase = true)
            }
        }
    }

    CustomBottomSheet(
        heading = heading,
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = sheetHeight
    ) {
        // This outer column now takes up the remaining space inside the sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            CustomSearchBar(
                value = searchText,
                onValueChange = {searchText = it}
            )

            Spacer(Modifier.height(16.dp))
            // Selectable List - This uses weight(1f) to push the button down
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredItems, key = { it.code }) { item ->
                    SelectableItemRow(
                        item = item,
                        // Comparison logic remains the same
                        isSelected = item.code == selectedItem.code,
                        onSelect = { selectedItem = it }
                    )
                }
            }

            // Select Button
            CustomTextButton(
                onClick = {
                    onItemSelected(selectedItem)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp),
                text = selectButtonText,
                shapeStyle = ButtonShapeStyle.Square,
                size = ButtonSize.Medium,
                type = ButtonType.Primary
            )
        }
    }
}

// --------- Preview ------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableListBottomSheetPreview() {
    // Sample items
    val sampleItems = listOf(
        SelectableItem("+91", "India", "IN"),          // India
        SelectableItem("+1", "United States", "US"),    // United States
        SelectableItem("+44", "United Kingdom", "GB"),  // United Kingdom
        SelectableItem("+81", "Japan", "JP"),          // Japan
        SelectableItem("+94", "Sri Lanka", "LK"),       // Sri Lanka
    )

    var showSheet by remember { mutableStateOf(true) } // Always show in preview

    if (showSheet) {
        SelectableListBottomSheet(
            heading = "Select Country",
            items = sampleItems,
            initialSelectedItem = sampleItems.first(),
            onItemSelected = { /* Do nothing in preview */ },
            onDismiss = { showSheet = false },
            selectButtonText = "Select" ,
            sheetHeight = 512.dp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSelectableListBottomSheet() {
    SelectableListBottomSheetPreview()
}