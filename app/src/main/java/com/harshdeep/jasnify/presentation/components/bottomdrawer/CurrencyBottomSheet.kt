package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyBottomSheet(
    initialSelection: SelectableItem,
    onItemSelected: (SelectableItem) -> Unit,
    onDismiss: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    SelectableListBottomSheet(
        heading = "Select currency",
        items = getCurrencyCodes(),
        initialSelectedItem = initialSelection,
        onItemSelected = onItemSelected,
        onDismiss = onDismiss,
        onProgress = onProgress,
        selectButtonText = "Select",
        sheetHeight = 512.dp
    )
}

@Composable
fun CurrencySheetContent(
    initialSelection: SelectableItem,
    onItemSelected: (SelectableItem) -> Unit
) {
    SelectableListContent(
        items = getCurrencyCodes(),
        initialSelectedItem = initialSelection,
        onItemSelected = onItemSelected,
        selectButtonText = "Select"
    )
}
