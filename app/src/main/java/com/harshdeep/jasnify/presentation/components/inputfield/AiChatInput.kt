package com.harshdeep.jasnify.presentation.components.inputfield

import android.annotation.SuppressLint
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.animations.CyclingText
import com.harshdeep.jasnify.presentation.components.animations.VoiceOrb
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.*

val DefaultDynamicPlaceholders = listOf(
    "fixed expenses",
    "vendors and caterers",
    "venue availability",
    "guest invitations",
    "checklist progress"
)

@SuppressLint("ConfigurationScreenWidthHeight")
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
    hasSpokenFirstMessage: Boolean = false,
    isMicMuted: Boolean = false,
    isAiSpeaking: Boolean = false,
    audioRms: Float = 0f,
    onStopClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    onToggleMicMute: () -> Unit = {},
    onCancelVoice: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Move down and shrink ONLY after meaningful speech is detected
    val dockProgress by animateFloatAsState(
        targetValue = if (hasSpokenFirstMessage) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "DockProgress"
    )

    val initialCenterOffsetY = -(screenHeight * 0.28f)
    val currentOrbOffsetY = lerp(initialCenterOffsetY, 0.dp, dockProgress)
    val currentOrbSize = lerp(260.dp, 160.dp, dockProgress)

    val controlsAlpha by animateFloatAsState(
        targetValue = if (isVoiceMode) 1f else 0f,
        animationSpec = tween(400),
        label = "ControlsAlpha"
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
                    audioRms = audioRms,
                    pointCount = 1800,
                    userActiveColor = Color(0xFF9F4D1B),
                    aiActiveColor = Color(0xFF006363),
                    modifier = Modifier
                        .offset(y = currentOrbOffsetY)
                        .size(currentOrbSize)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.graphicsLayer {
                        alpha = controlsAlpha
                    },
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomIconButton(
                        onClick = onToggleMicMute,
                        icon = if (isMicMuted) painterResource(R.drawable.ic_microphone_off) else painterResource(R.drawable.ic_microphone_on),
                        contentColor = Color.White,
                        containerColor = if (isMicMuted) Color(0xFFD32F2F) else Color(0xFF8B10F0)
                    )

                    CustomIconButton(
                        onClick = onCancelVoice,
                        icon = rememberVectorPainter(Icons.Rounded.Close),
                        contentColor = Color.White,
                        containerColor = Color(0xFF7000FF)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .shadow(12.dp, CircleShape, ambientColor = Color(0x30A033FF), spotColor = Color(0x407000FF))
                    .clip(CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .background(Color.White.copy(alpha = 0.88f))
                    .padding(start = 24.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        CyclingText(
                            fixedPrefix = placeholderPrefix,
                            dynamicPhrases = dynamicPlaceholders,
                            cycleIntervalMs = 3000L,
                            style = JasnifyTheme.typography.labelXLarge.copy(
                                lineHeight = 26.sp,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF8E75B2)
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = JasnifyTheme.typography.labelXLarge.copy(
                            color = Color(0xFF3C225C)
                        ),
                        cursorBrush = SolidColor(Color(0xFF8B10F0)),
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE512FF),
                                    Color(0xFF8A15E8),
                                    Color(0xFF4A159B)
                                )
                            )
                        )
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
                                    .background(Color.White)
                            )
                        }
                        value.isNotBlank() -> {
                            Icon(
                                imageVector = Icons.Rounded.ArrowUpward,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        else -> {
                            Icon(
                                painter = painterResource(R.drawable.ic_voice_input),
                                contentDescription = "Voice Input",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}