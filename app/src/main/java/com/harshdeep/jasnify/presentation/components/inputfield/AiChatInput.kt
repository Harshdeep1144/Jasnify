package com.harshdeep.jasnify.presentation.components.inputfield

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.animations.CyclingText
import com.harshdeep.jasnify.presentation.components.animations.VoiceOrb
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.*

val DefaultDynamicPlaceholders = listOf(
    "fixed and variable expenses",
    "vendors and caterers",
    "venue availability",
    "guest invitations & RSVPs",
    "checklist progress"
)

@Composable
fun AiChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderPrefix: String = "Search for ",
    dynamicPlaceholders: List<String> = DefaultDynamicPlaceholders,
    isGenerating: Boolean = false,
    isVoiceMode: Boolean = false,
    isMicMuted: Boolean = false,
    isAiSpeaking: Boolean = false,
    onStopClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    onToggleMicMute: () -> Unit = {},
    onCancelVoice: () -> Unit = {}
) {
    var hasSettled by remember(isVoiceMode) { mutableStateOf(!isVoiceMode) }
    LaunchedEffect(isVoiceMode) {
        if (isVoiceMode) {
            hasSettled = false
            kotlinx.coroutines.delay(250)
            hasSettled = true
        }
    }

    val animatedOrbSize by animateDpAsState(
        targetValue = when {
            !hasSettled -> 240.dp
            isAiSpeaking -> 210.dp
            else -> 170.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "OrbScaleTransition"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (isVoiceMode) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                VoiceOrb(
                    isMuted = isMicMuted,
                    isAiSpeaking = isAiSpeaking,
                    pointCount = if (isAiSpeaking || !hasSettled) 1300 else 1000,
                    userActiveColor = Color(0xFF9F4D1B),
                    aiActiveColor = Color(0xFF006363),
                    modifier = Modifier.size(animatedOrbSize)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomIconButton(
                        onClick = onToggleMicMute,
                        icon = if (isMicMuted) painterResource(R.drawable.ic_microphone_off) else painterResource(R.drawable.ic_microphone_on),
                        contentColor = if (isMicMuted) MaterialTheme.colorScheme.error else ContentBrandDark,
                        containerColor = if (isMicMuted) MaterialTheme.colorScheme.errorContainer else SurfaceBrandSecondary
                    )

                    CustomIconButton(
                        onClick = onCancelVoice,
                        icon = rememberVectorPainter(Icons.Rounded.Close),
                        type = ButtonType.Secondary
                    )
                }
            }
        } else {
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
                Spacer(Modifier.width(12.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_ai),
                    contentDescription = "AI Sparkle",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        CyclingText(
                            fixedPrefix = placeholderPrefix,
                            dynamicPhrases = dynamicPlaceholders,
                            cycleIntervalMs = 3000L,
                            style = JasnifyTheme.typography.labelXLarge.copy(lineHeight = 24.sp),
                            color = ContentSecondary
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