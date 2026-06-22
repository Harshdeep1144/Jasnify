package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDeleteSheet(
    heading: String,
    subHeading: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirmRemove: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonText: String? = null // 1. Added nullable parameter with a default null value
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
                    .background(ContentTertiary, shape = RoundedCornerShape(100))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
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

            // Primary red confirmation button
            CustomTextButton(
                onClick = onConfirmRemove,
                text = confirmButtonText ?: "Remove", // 2. Uses the Elvis operator to fall back to "Remove" if null
                shapeStyle = ButtonShapeStyle.Square,
                containerColor = MaterialTheme.colorScheme.error,
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
        CustomDeleteSheet(
            heading = "Remove item?",
            subHeading = "The item will be removed from the Catering Menu.",
            sheetState = sheetState,
            onDismiss = {},
            onConfirmRemove = {}
        )
    }
}