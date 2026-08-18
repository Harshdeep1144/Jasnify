package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

private val SelectableRowShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

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
            .clip(SelectableRowShape)
            .clickable { onSelect(item) }
            .background(if (isSelected) SurfaceBrandSecondary else BackgroundPrimary)
            .padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clip(SelectableRowShape)
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            CustomRadioButton(
                selected = isSelected,
                onClick = { onSelect(item) },
                modifier = Modifier.padding(0.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.emoji,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.code,
                style = JasnifyTheme.typography.labelXLarge,
                color = if (isSelected) ContentBrandDark else ContentSecondary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }

        Text(
            text = item.name,
            style = JasnifyTheme.typography.labelXLarge,
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
    var selectedItem by remember(items, initialSelectedItem) {
        mutableStateOf(
            items.find { it.code == initialSelectedItem.code } ?: if (items.isNotEmpty()) items.first() else initialSelectedItem
        )
    }

    var searchText by remember { mutableStateOf("") }

    val filteredItems = remember(items, searchText) {
        val query = searchText.trim()
        if (query.isEmpty()) {
            items
        } else {
            items.filter {
                it.code.contains(query, ignoreCase = true) ||
                        it.name.contains(query, ignoreCase = true)
            }
        }
    }

    CustomBottomSheet(
        heading = heading,
        onDismiss = onDismiss,
        onProgress = onProgress,
        showDragHandle = true,
        sheetGesturesEnabled = false,
        sheetHeight = sheetHeight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            CustomSearchBar(
                value = searchText,
                onValueChange = { searchText = it }
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = filteredItems,
                    key = { it.code },
                    contentType = { "selectable_item" }
                ) { item ->
                    SelectableItemRow(
                        item = item,
                        isSelected = item.code == selectedItem.code,
                        onSelect = { selectedItem = it }
                    )
                }
            }

            CustomTextButton(
                onClick = {
                    onItemSelected(selectedItem)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
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
    val sampleItems = listOf(
        SelectableItem("+91", "India", "IN"),
        SelectableItem("+1", "United States", "US"),
        SelectableItem("+44", "United Kingdom", "GB"),
        SelectableItem("+81", "Japan", "JP"),
        SelectableItem("+94", "Sri Lanka", "LK"),
    )

    var showSheet by remember { mutableStateOf(true) }

    if (showSheet) {
        SelectableListBottomSheet(
            heading = "Select Country",
            items = sampleItems,
            initialSelectedItem = sampleItems.first(),
            onItemSelected = { },
            onDismiss = { showSheet = false },
            selectButtonText = "Select",
            sheetHeight = 512.dp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSelectableListBottomSheet() {
    SelectableListBottomSheetPreview()
}