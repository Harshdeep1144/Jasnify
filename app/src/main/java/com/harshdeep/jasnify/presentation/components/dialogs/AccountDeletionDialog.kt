package com.harshdeep.jasnify.presentation.components.dialogs

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker

@Composable
fun AccountDeletionDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animateTrigger by remember { mutableStateOf(false) }
    var isConfirmed by remember { mutableStateOf(false) }

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
                enter = scaleIn(initialScale = 0.75f, animationSpec = tween(150)) + fadeIn(tween(150)),
                exit = scaleOut(targetScale = 0.75f, animationSpec = tween(150)) + fadeOut(tween(150))
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(SquircleShape(CornerExtraLarge, CornerSmoothingDefault))
                        .background(SurfacePrimary)
                        .padding(16.dp)
                        .clickable(enabled = false) { },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_alert_triangle),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "What’s going to happen :",
                            style = JasnifyTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error
                        )

                        DeletionStepItem(
                            stepNumber = 1,
                            text = buildAnnotatedString {
                                append("After confirmation, your account will be ")
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append("locked for 30 days.")
                                }
                            }
                        )

                        DeletionStepItem(
                            stepNumber = 2,
                            text = buildAnnotatedString {
                                append("You can choose to ")
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append("cancel the deletion")
                                }
                                append(" process by logging in anytime within this 30-day period.")
                            }
                        )

                        DeletionStepItem(
                            stepNumber = 3,
                            text = buildAnnotatedString {
                                append("After this period, your account will be ")
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append("permanently deleted.")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { isConfirmed = !isConfirmed },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomChecker(
                            checked = isConfirmed,
                            onCheckedChange = {},
                            activeColor = ContentPrimary
                        )

                        Text(
                            text = "I understand that all my data will be deleted forever.",
                            style = JasnifyTheme.typography.headingSmall,
                            color = ContentPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextButton(
                        onClick = {
                            onConfirm()
                            onDismissRequest()
                        },
                        text = "Delete Account",
                        type = ButtonType.Primary,
                        enabled = isConfirmed,
                        containerColor = MaterialTheme.colorScheme.error,
                        disabledContainerColor = SurfaceInvSecondary,
                        contentColor = ContentInvPrimary,
                        disabledContentColor = ContentInvPrimary,
                        shapeStyle = ButtonShapeStyle.Square,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    CustomTextButton(
                        onClick = onDismissRequest,
                        text = "Cancel",
                        type = ButtonType.Tertiary,
                        shapeStyle = ButtonShapeStyle.Square,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DeletionStepItem(
    stepNumber: Int,
    text: androidx.compose.ui.text.AnnotatedString
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$stepNumber.",
            style = JasnifyTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = text,
            style = JasnifyTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountDeletionDialogPreview() {
    JasnifyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AccountDeletionDialog(
                onDismissRequest = {},
                onConfirm = {}
            )
        }
    }
}