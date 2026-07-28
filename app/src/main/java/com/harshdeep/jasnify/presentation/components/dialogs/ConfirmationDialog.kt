package com.harshdeep.jasnify.presentation.components.dialogs

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    description: String,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    isDestructive: Boolean = false
) {
    var animateTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateTrigger = true
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismissRequest() },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = animateTrigger,
                enter = scaleIn(initialScale = 0.5f, animationSpec = tween(150)) + fadeIn(tween(150)),
                exit = scaleOut(targetScale = 0.5f, animationSpec = tween(150)) + fadeOut(tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(SquircleShape(CornerExtraLarge, CornerSmoothingDefault))
                        .background(SurfacePrimary)
                        .padding(16.dp)
                        .clickable(enabled = false) { }, // Prevent clicks from closing dialog when clicking inside card
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                        color = ContentPrimary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = description,
                        style = JasnifyTheme.typography.bodyLarge,
                        color = ContentSecondary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextButton(
                            onClick = {
                                onConfirm()
                                onDismissRequest()
                            },
                            text = confirmButtonText,
                            type = ButtonType.Primary,
                            containerColor = if (isDestructive) MaterialTheme.colorScheme.error else SurfaceBrandPrimary,
                            contentColor = ContentInvPrimary,
                            shapeStyle = ButtonShapeStyle.Square,
                            modifier = Modifier.weight(1f)
                        )

                        CustomTextButton(
                            onClick = onDismissRequest,
                            text = dismissButtonText,
                            shapeStyle = ButtonShapeStyle.Square,
                            type = ButtonType.Tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogoutConfirmationDialogPreview() {
    JasnifyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ConfirmationDialog(
                onDismissRequest = {},
                onConfirm = {},
                title = "Are you sure?",
                description = "You will be logged out from the app.",
                confirmButtonText = "Logout",
                dismissButtonText = "Cancel",
                isDestructive = true
            )
        }
    }
}