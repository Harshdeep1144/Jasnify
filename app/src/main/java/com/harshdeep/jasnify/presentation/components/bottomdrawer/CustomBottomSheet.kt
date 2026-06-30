package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.ContentTertiary
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    heading: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    sheetHeight: Dp = 400.dp,
    sheetGesturesEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfacePrimary,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(56.dp)
                    .height(4.dp)
                    .background(ContentTertiary, shape = SquircleShape(100))
            )
        },
        sheetGesturesEnabled = sheetGesturesEnabled,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfacePrimary)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(12.dp, 0.dp, 12.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = heading,
                    style = JasnifyTheme.typography.displayLarge,
                    color = ContentPrimary
                )

                TopBarIconButton(
                    backgroundStyle = ButtonBackground.OPAQUE,
                    icon = TopIcon.Predefined.CLOSE,
                    onClick = onDismiss
                )
            }

            // Sheet content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sheetHeight)
            ) {
                content()
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Custom Bottom Sheet Preview")
@Composable
fun CustomBottomSheetPreview() {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Surface {
        CustomBottomSheet(
            heading = "Sample Sheet",
            sheetState = sheetState,
            onDismiss = {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "This is some content inside the bottom sheet.",
                    style = MaterialTheme.typography.bodyLarge
                )

            }
        }
    }
}
