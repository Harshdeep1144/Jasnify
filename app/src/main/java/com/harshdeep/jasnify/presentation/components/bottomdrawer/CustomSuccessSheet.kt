package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSuccessBottomSheet(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "Close",
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfacePrimary,
        shape = SquircleShape(topStart = 28.dp, topEnd = 28.dp),
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
            )
        },
        modifier = modifier
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(396.dp)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_tick),
                        contentDescription = "Success checkmark",
                        modifier = Modifier.size(80.dp),
                        tint = ContentBrandDark
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = message, // Using the dynamic input here
                    style = JasnifyTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    color = ContentBrandDark
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CustomTextButton(
                    onClick = onDismiss,
                    text = buttonText, // Using the dynamic button text
                    type = ButtonType.Secondary,
                    shapeStyle = ButtonShapeStyle.Square,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}