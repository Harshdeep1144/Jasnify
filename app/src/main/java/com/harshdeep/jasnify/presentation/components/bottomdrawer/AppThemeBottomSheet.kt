package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.OptionSelector
import com.harshdeep.jasnify.theme.*

enum class AppThemeOption(val label: String, val bodyText: String? = null) {
    SYSTEM_DEFAULT("System Default"),
    LIGHT_MODE("Light Mode"),
    DARK_MODE("Dark Mode", "Will be available soon")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppThemeBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    currentTheme: AppThemeOption,
    onThemeSelected: (AppThemeOption) -> Unit
) {
    CustomBottomSheet(
        heading = "App Theme",
        sheetHeight = 306.dp,
        sheetState = sheetState,
        onDismiss = onDismiss
    ) {
        AppThemeContent(
            currentTheme = currentTheme,
            onThemeSelected = onThemeSelected,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun AppThemeContent(
    currentTheme: AppThemeOption,
    onThemeSelected: (AppThemeOption) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(currentTheme) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ){
            AppThemeOption.entries.forEach { option ->
                OptionSelector(
                    label = option.label,
                    bodyText = option.bodyText,
                    isSelected = selectedTheme == option,
                    onClick = {
                        // In a real app, we might disable DARK_MODE if it's truly not available
                        // but for now, we just update the local state.
                        selectedTheme = option
                    }
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

        Column{
            CustomTextButton(
                onClick = {
                    onThemeSelected(selectedTheme)
                    onDismiss()
                },
                text = "Confirm",
                enabled = selectedTheme == AppThemeOption.LIGHT_MODE,
                size = ButtonSize.Medium,
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AppThemeBottomSheetPreview() {
    JasnifyTheme {
        Surface {
            AppThemeContent(
                currentTheme = AppThemeOption.LIGHT_MODE,
                onThemeSelected = {},
                onDismiss = {}
            )
        }
    }
}
