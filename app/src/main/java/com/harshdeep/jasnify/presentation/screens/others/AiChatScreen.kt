package com.harshdeep.jasnify.presentation.screens.others

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.inputfield.AiChatInput
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*


// Data class for Chat Messages
data class AiMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun AiChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    var isVoiceMode by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }

    // Sample data matching the provided screenshot
    val sampleAiResponse = """
        **Expense Overview :**
        Your event has utilized 47% (₹46,50,900) of its total budget, leaving a healthy safety buffer of 53% (₹53,49,100) in remaining funds.

        🚨 **Critical Vulnerability:**
        • The Risk: Your "Unplanned Costs" of ₹24,650 are dangerously low. Premium Indian events historically average 7% -10% in hidden overages.
        • The Action: Immediately allocate ₹3.5L from your remaining funds into a protected emergency reserve.

        🔮 **30-Day Predictive Forecast :**
        • Current Path: You are over-spending on Vendors by 11% vs. market trends. If this pattern continues, you risk a ₹2.1L budget overrun.
        • Optimized Path: Keep upcoming categories strictly indexed to market averages to finish with a ₹12L cash surplus.
    """.trimIndent()

    val messages = remember {
        mutableStateListOf(
            AiMessage(
                id = "1",
                text = sampleAiResponse,
                isUser = false
            )
        )
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "",
                onBackClick = onBackClick,
                onMenuClick = onMoreClick,
                backIcon = TopIcon.Predefined.DOWN,
                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                buttonStyle = ButtonBackground.OPAQUE
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                AiChatInput(
                    value = inputText,
                    onValueChange = { inputText = it },
                    isVoiceMode = isVoiceMode,
                    isMicMuted = isMicMuted,
                    isGenerating = isGenerating,
                    placeholder = "Ask more about expenses",
                    onSendClick = {
                        if (inputText.isNotBlank()) {
                            messages.add(
                                AiMessage(
                                    id = System.currentTimeMillis().toString(),
                                    text = inputText,
                                    isUser = true
                                )
                            )
                            inputText = ""
                            // Simulate generating
                            isGenerating = true
                        }
                    },
                    onStopClick = { isGenerating = false },
                    onVoiceClick = { isVoiceMode = true },
                    onToggleMicMute = { isMicMuted = !isMicMuted },
                    onCancelVoice = { isVoiceMode = false }
                )
            }
        },
        containerColor = BackgroundPrimary,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            items(messages) { message ->
                if (message.isUser) {
                    UserMessageBubble(message = message)
                } else {
                    AiMessageContent(message = message)
                }
            }

            if (isGenerating) {
                item {
                    GeneratingIndicator()
                }
            }
        }
    }
}

@Composable
fun GeneratingIndicator() {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "generating")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(ContentPrimary.copy(alpha = alpha))
            )
        }
    }
}

/**
 * User Prompt Bubble with Sparkle prefix
 */
@Composable
fun UserMessageBubble(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFEBECEF))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 300.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_ai),
                contentDescription = null,
                tint = Color.Transparent,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = message.text,
                color = Color(0xFF1E2022),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Structured Markdown-Style AI Response with Action Feedback Buttons
 */
@Composable
fun AiMessageContent(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        FormattedAiText(text = message.text)

        Spacer(modifier = Modifier.height(16.dp))

        // Feedback Buttons (Thumbs Up / Thumbs Down)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Handle positive feedback */ },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_thumbs_up),
                    contentDescription = "Helpful",
                    tint = Color(0xFF8E9094),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { /* Handle negative feedback */ },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_thumbs_down),
                    contentDescription = "Unhelpful",
                    tint = Color(0xFF8E9094),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Parses bold markdown annotations `**bold**` and formats bullets/headers cleanly
 */
@Composable
fun FormattedAiText(
    text: String,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(text) {
        buildAnnotatedString {
            val parts = text.split("**")
            var isBold = false
            for (part in parts) {
                if (isBold) {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2022),
                            fontSize = 15.5.sp
                        )
                    ) {
                        append(part)
                    }
                } else {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF2C2E30),
                            fontSize = 14.5.sp
                        )
                    ) {
                        append(part)
                    }
                }
                isBold = !isBold
            }
        }
    }

    Text(
        text = annotatedString,
        lineHeight = 22.sp,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    JasnifyTheme {
        AiChatScreen()
    }
}