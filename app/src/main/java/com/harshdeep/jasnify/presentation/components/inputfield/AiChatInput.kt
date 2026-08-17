package com.harshdeep.jasnify.presentation.components.inputfield

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary


@Composable
fun AiChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Ask more about expenses",
    isGenerating: Boolean = false,
    isVoiceMode: Boolean = false,
    isMicMuted: Boolean = false,
    onStopClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    onToggleMicMute: () -> Unit = {},
    onCancelVoice: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (isVoiceMode) {
            // Voice Recording Bar UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mic / Mute Toggle Button
                CustomIconButton(
                    onClick = onToggleMicMute,
                    icon = if (isMicMuted) painterResource(R.drawable.ic_microphone_on) else painterResource(R.drawable.ic_microphone_off),
                    contentColor = if (isMicMuted) ContentBrandDark else MaterialTheme.colorScheme.error,
                    containerColor = if (isMicMuted) SurfaceBrandSecondary else MaterialTheme.colorScheme.errorContainer
                )

                // Animated Sound wave Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clip(RoundedCornerShape(100))
                        .background(SurfaceBrandPrimary)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AudioWaveformVisualizer(isMuted = isMicMuted)
                }

                CustomIconButton(
                    onClick = onCancelVoice,
                    icon = rememberVectorPainter(Icons.Rounded.Close),
                    type = ButtonType.Secondary
                )
            }
        } else {
            // Standard AI Text Input Bar UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(100))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.16f), RoundedCornerShape(100))
                    .background(SurfaceSecondary)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sparkle Icon
                Spacer(Modifier.width(12.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_ai),
                    contentDescription = "AI Sparkle",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Text Input / Placeholder
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = ContentSecondary,
                            style = JasnifyTheme.typography.labelXLarge,
                            lineHeight = 24.sp
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = JasnifyTheme.typography.labelXLarge,
                        cursorBrush = SolidColor(ContentPrimary),
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Action Button (Send / Stop / Waveform Mic)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SurfaceBrandPrimary)
                        .noRippleClickable {
                            when {
                                isGenerating -> onStopClick()
                                value.isNotBlank() -> onSendClick()
                                else -> onVoiceClick()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isGenerating -> {
                            // Rounded Stop Square
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(ContentInvPrimary)
                            )
                        }
                        value.isNotBlank() -> {
                            Icon(
                                imageVector = Icons.Rounded.ArrowUpward,
                                contentDescription = "Send",
                                tint = ContentInvPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        else -> {
                            // Audio Waveform Equalizer Icon
                            Icon(
                                painter = painterResource(R.drawable.ic_voice_input),
                                contentDescription = "Voice Input",
                                tint = ContentInvPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * Animated Dotted Voice Visualizer for Active Speech
 */
@Composable
fun AudioWaveformVisualizer(
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotCount = 24
        for (i in 0 until dotCount) {
            val animProgress by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = (i * 35) % 600,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$i"
            )

            val height = if (isMuted) 3.dp else (3.dp + (animProgress * 12.dp))
            val alpha = if (isMuted) 0.4f else 0.85f

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(height)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(Color.White.copy(alpha = alpha))
            )
        }
    }
}




// ---------------- Previews for all states ----------------

@Preview(showBackground = true, name = "1. Empty / Idle State")
@Composable
fun AiChatInputIdlePreview() {
    AiChatInput(
        value = "",
        onValueChange = {},
        onSendClick = {}
    )
}

@Preview(showBackground = true, name = "2. Typed Text State")
@Composable
fun AiChatInputTypingPreview() {
    AiChatInput(
        value = "Compare my expenses with market trends & give me insights",
        onValueChange = {},
        onSendClick = {}
    )
}

@Preview(showBackground = true, name = "3. Generating / Stop State")
@Composable
fun AiChatInputGeneratingPreview() {
    AiChatInput(
        value = "",
        onValueChange = {},
        onSendClick = {},
        isGenerating = true
    )
}

@Preview(showBackground = true, name = "4. Active Voice Recording")
@Composable
fun AiChatInputVoiceActivePreview() {
    AiChatInput(
        value = "",
        onValueChange = {},
        onSendClick = {},
        isVoiceMode = true,
        isMicMuted = false
    )
}

@Preview(showBackground = true, name = "5. Muted Voice Recording")
@Composable
fun AiChatInputVoiceMutedPreview() {
    AiChatInput(
        value = "",
        onValueChange = {},
        onSendClick = {},
        isVoiceMode = true,
        isMicMuted = true
    )
}