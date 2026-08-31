package com.harshdeep.jasnify.presentation.components.chats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
fun DeleteMessageConfirmationDialog(
    messageCount: Int,
    canDeleteForEveryone: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteForMe: () -> Unit,
    onDeleteForEveryone: () -> Unit,
    modifier: Modifier = Modifier
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
                enter = scaleIn(initialScale = 0.85f, animationSpec = tween(150)) + fadeIn(tween(150)),
                exit = scaleOut(targetScale = 0.85f, animationSpec = tween(150)) + fadeOut(tween(150))
            ) {
                DeleteMessageDialogContent(
                    messageCount = messageCount,
                    canDeleteForEveryone = canDeleteForEveryone,
                    onDismissRequest = onDismissRequest,
                    onDeleteForMe = onDeleteForMe,
                    onDeleteForEveryone = onDeleteForEveryone,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun DeleteMessageDialogContent(
    messageCount: Int,
    canDeleteForEveryone: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteForMe: () -> Unit,
    onDeleteForEveryone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSingle = messageCount <= 1
    val title = if (isSingle) "Delete message?" else "Delete $messageCount messages?"
    val description = if (isSingle) {
        "Choose how you want to delete this message."
    } else {
        "Choose how you want to delete these messages."
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(SquircleShape(CornerExtraLarge, CornerSmoothingDefault))
            .background(SurfacePrimary)
            .padding(16.dp)
            .clickable(enabled = false) { },
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
            color = ContentPrimary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = JasnifyTheme.typography.bodyLarge,
            color = ContentSecondary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            if (canDeleteForEveryone) {
                TextButton(
                    onClick = {
                        onDeleteForEveryone()
                        onDismissRequest()
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Delete for everyone",
                        style = JasnifyTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
            }

            TextButton(
                onClick = {
                    onDeleteForMe()
                    onDismissRequest()
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Delete for me",
                    style = JasnifyTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            TextButton(
                onClick = onDismissRequest,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Cancel",
                    style = JasnifyTheme.typography.bodyLarge,
                    color = ContentPrimary
                )
            }
        }
    }
}



@Preview(name = "Delete Single Message - Everyone", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun DeleteSingleMessagePreview() {
    JasnifyTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            DeleteMessageDialogContent(
                messageCount = 1,
                canDeleteForEveryone = true,
                onDismissRequest = {},
                onDeleteForMe = {},
                onDeleteForEveryone = {}
            )
        }
    }
}

@Preview(name = "Delete Multiple Messages - For Me Only", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun DeleteMultipleMessagesPreview() {
    JasnifyTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            DeleteMessageDialogContent(
                messageCount = 4,
                canDeleteForEveryone = false,
                onDismissRequest = {},
                onDeleteForMe = {},
                onDeleteForEveryone = {}
            )
        }
    }
}