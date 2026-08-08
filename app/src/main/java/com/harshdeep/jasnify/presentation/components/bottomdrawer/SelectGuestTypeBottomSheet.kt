package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
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
    var tempSelectedTypes by remember { mutableStateOf(initialSelectedTypes.toSet()) }

    CustomBottomSheet(
        heading = "Select Guest Type",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(guestTypes, key = { it.id }) { type ->
                    GuestTypeCard(
                        label = type.name,
                        guestCount = type.guestCount,
                        showChecker = true,
                        isSelected = tempSelectedTypes.contains(type.name),
                        onToggle = { isChecked ->
                            tempSelectedTypes = if (isChecked) {
                                tempSelectedTypes + type.name
                            } else {
                                tempSelectedTypes - type.name
                            }
                        },
                        imageUrls = imageUrls.take(if (type.guestCount > 0) 3 else 0),
                        verticalPadding = 8.dp
                    )
                }
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextButton(
                    onClick = {
                        if (tempSelectedTypes.size > 1) {
                            tempSelectedTypes = emptySet()
                        } else {
                            onDismiss()
                        }
                    },
                    text = if (tempSelectedTypes.size > 1) "Clear" else "Cancel",
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
fun PreviewSelectGuestTypeBottomSheet() {
    JasnifyTheme {
        SelectGuestTypeBottomSheet(
            guestTypes = listOf(
                GuestType(name = "Family", guestCount = 14),
                GuestType(name = "Close Friend", guestCount = 3),
                GuestType(name = "Office", guestCount = 25),
                GuestType(name = "Hometown", guestCount = 2),
                GuestType(name = "Apartment", guestCount = 11),
                GuestType(name = "College", guestCount = 41),
            ),
            initialSelectedTypes = listOf("Family"),
            onDismiss = {},
            onApply = {}
        )
    }
}
