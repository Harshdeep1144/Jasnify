package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationBottomSheet(
    heading: String,
    subHeading: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonText: String? = null,
    isDestructive: Boolean = true
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                    .background(ContentTertiary, shape = RoundedCornerShape(100))
            )
        },
        shape = RoundedCornerShape(CornerExtraLarge, CornerExtraLarge),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = heading,
                style = JasnifyTheme.typography.displayLarge,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = subHeading,
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentSecondary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // Primary confirmation button
            CustomTextButton(
                onClick = onConfirm,
                text = confirmButtonText ?: "Remove",
                shapeStyle = ButtonShapeStyle.Square,
                containerColor = if (isDestructive) MaterialTheme.colorScheme.error else SurfaceBrandPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            // Transparent plain text cancel button
            CustomTextButton(
                onClick = onDismiss,
                text = "Cancel",
                shapeStyle = ButtonShapeStyle.Square,
                type = ButtonType.Tertiary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light Mode Preview", showBackground = true)
@Composable
fun CustomDeleteSheetPreview() {
    val sheetState = rememberModalBottomSheetState()
    JasnifyTheme {
        ConfirmationBottomSheet(
            heading = "Remove item?",
            subHeading = "The item will be removed from the Catering Menu.",
            onDismiss = {},
            onConfirm = {}
        )
    }
}