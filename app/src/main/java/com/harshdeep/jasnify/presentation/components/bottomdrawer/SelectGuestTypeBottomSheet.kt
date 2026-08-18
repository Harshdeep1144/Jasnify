package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.GuestType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.GuestTypeCard
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun SelectGuestTypeBottomSheet(
    guestTypes: List<GuestType>,
    initialSelectedTypes: List<String>,
    onDismiss: () -> Unit,
    onApply: (List<String>) -> Unit,
    imageUrls: List<String> = emptyList(),
    onProgress: (Float) -> Unit = {}
) {
    var tempSelectedTypes by remember(initialSelectedTypes) {
        mutableStateOf(initialSelectedTypes.toSet())
    }

    val previewThumbnails = remember(imageUrls) {
        imageUrls.take(3)
    }

    val showClearButton = tempSelectedTypes.size > 1

    CustomBottomSheet(
        heading = "Select Guest Type",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = guestTypes,
                    key = { it.name },
                    contentType = { "guest_type_card" }
                ) { type ->
                    val isSelected = tempSelectedTypes.contains(type.name)

                    GuestTypeCard(
                        label = type.name,
                        guestCount = type.guestCount,
                        showChecker = true,
                        isSelected = isSelected,
                        onToggle = { isChecked ->
                            tempSelectedTypes = if (isChecked) {
                                tempSelectedTypes + type.name
                            } else {
                                tempSelectedTypes - type.name
                            }
                        },
                        imageUrls = if (type.guestCount > 0) previewThumbnails else emptyList(),
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextButton(
                    onClick = {
                        if (showClearButton) {
                            tempSelectedTypes = emptySet()
                        } else {
                            onDismiss()
                        }
                    },
                    text = if (showClearButton) "Clear" else "Cancel",
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Medium,
                    type = ButtonType.Tertiary,
                    shapeStyle = ButtonShapeStyle.Square
                )

                CustomTextButton(
                    onClick = { onApply(tempSelectedTypes.toList()) },
                    text = "Apply",
                    modifier = Modifier.weight(1f),
                    shapeStyle = ButtonShapeStyle.Square,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectGuestTypeBottomSheetPreview() {
    JasnifyTheme {
        SelectGuestTypeBottomSheet(
            guestTypes = listOf(
            ),
            initialSelectedTypes = listOf("Family", "Friends"),
            onDismiss = {},
            onApply = {}
        )
    }
}