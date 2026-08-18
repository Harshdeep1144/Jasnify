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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.inputfield.AiChatInput
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.viewmodels.*
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.domain.model.*
import com.harshdeep.jasnify.presentation.components.carousels.VendorCarousel
import com.harshdeep.jasnify.presentation.components.carousels.VenueCarousel
import java.text.SimpleDateFormat
import java.util.*

// Data class for Chat Messages
data class AiMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val venueIds: List<String> = emptyList(),
    val vendorIds: List<String> = emptyList()
)

@Composable
fun AiChatScreen(
    modifier: Modifier = Modifier,
    initialContext: String? = null,
    viewModel: GenerativeViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    cateringViewModel: CateringViewModel = hiltViewModel(),
    venueViewModel: VenueViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    checklistViewModel: ChecklistViewModel = hiltViewModel(),
    guestViewModel: GuestViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onVenueClick: (Venue) -> Unit = {},
    onVendorClick: (Vendor) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    var isVoiceMode by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()

    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val expenses by budgetViewModel.expenses.collectAsStateWithLifecycle()
    val cateringItems by cateringViewModel.cateringItems.collectAsStateWithLifecycle()
    val savedVenues by venueViewModel.savedVenues.collectAsStateWithLifecycle()
    val savedVendors by vendorViewModel.savedVendors.collectAsStateWithLifecycle()
    val checklists by checklistViewModel.checklists.collectAsStateWithLifecycle()
    val guests by guestViewModel.guests.collectAsStateWithLifecycle()

    val allVenues by venueViewModel.allVenues.collectAsStateWithLifecycle()
    val allVendors by vendorViewModel.allVendors.collectAsStateWithLifecycle()

    LaunchedEffect(activeEvent, expenses, cateringItems, savedVenues, savedVendors, checklists, guests) {
        val event = activeEvent ?: return@LaunchedEffect
        
        val contextBuilder = StringBuilder()
        contextBuilder.append("User Name: ${event.ownerName}\n")
        contextBuilder.append("Event: ${event.name} (Type ID: ${event.typeId})\n")
        contextBuilder.append("Date: ${event.date?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) } ?: "TBD"}\n")
        contextBuilder.append("Total Budget: ₹${event.budget ?: 0}\n")
        
        contextBuilder.append("\nExpenses:\n")
        expenses.forEach { contextBuilder.append("- ${it.title}: ₹${it.amount} (${it.category})\n") }
        
        contextBuilder.append("\nCatering Menu:\n")
        cateringItems.forEach { contextBuilder.append("- ${it.name} (${it.cuisine}, ${it.type})\n") }
        
        contextBuilder.append("\nSaved Venues:\n")
        savedVenues.forEach { contextBuilder.append("- ${it.venueName}\n") }
        
        contextBuilder.append("\nSaved Vendors:\n")
        savedVendors.forEach { contextBuilder.append("- ${it.vendorName} (${it.category})\n") }
        
        contextBuilder.append("\nChecklists:\n")
        checklists.forEach { contextBuilder.append("- ${it.title} (${if (it.items.all { item -> item.checked }) "Completed" else "Pending"})\n") }
        
        contextBuilder.append("\nGuests:\n")
        val pendingGuests = guests.filter { !it.invited }
        contextBuilder.append("- Total: ${guests.size}, Pending to invite: ${pendingGuests.size}\n")
        
        if (initialContext != null) {
            contextBuilder.append("\nAdditional Info: $initialContext")
        }

        viewModel.setGlobalContext(contextBuilder.toString())
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        // Chat Message List (Edge-to-Edge scrolling behind gradients)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 68.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 84.dp
            )
        ) {
            items(messages) { message ->
                if (message.isUser) {
                    UserMessageBubble(message = message)
                } else {
                    AiMessageContent(
                        message = message,
                        allVenues = allVenues,
                        allVendors = allVendors,
                        onVenueClick = onVenueClick,
                        onVendorClick = onVendorClick
                    )
                }
            }

            if (isGenerating) {
                item {
                    GeneratingIndicator()
                }
            }
        }

        // Floating Top Bar with Top Gradient
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(brush = TopGradientBrush)
                .statusBarsPadding()
                .zIndex(10f)
        ) {
            CustomTopBar(
                title = "",
                onBackClick = onBackClick,
                onMenuClick = onMoreClick,
                backIcon = TopIcon.Predefined.DOWN,
                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                buttonStyle = ButtonBackground.TRANSLUCENT,
                translucentAlpha = 0.6f
            )
        }

        // Floating AI Input Area with Bottom Gradient
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(brush = BottomGradientBrush)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .zIndex(10f)
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
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                onStopClick = { },
                onVoiceClick = { isVoiceMode = true },
                onToggleMicMute = { isMicMuted = !isMicMuted },
                onCancelVoice = { isVoiceMode = false }
            )
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
                .background(SurfaceBrandSecondary)
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
                color = ContentPrimary,
                style = JasnifyTheme.typography.bodyXLarge
            )
        }
    }
}

/**
 * Structured Markdown-Style AI Response with Action Feedback Buttons and Rich Cards
 */
@Composable
fun AiMessageContent(
    message: AiMessage,
    allVenues: List<Venue> = emptyList(),
    allVendors: List<Vendor> = emptyList(),
    onVenueClick: (Venue) -> Unit = {},
    onVendorClick: (Vendor) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (message.text.isNotBlank()) {
            FormattedAiText(text = message.text)
        }

        if (message.venueIds.isNotEmpty()) {
            val matchedVenues = allVenues.filter { message.venueIds.contains(it.id) }
            if (matchedVenues.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                VenueCarousel(
                    title = "Suggested Venues",
                    venues = matchedVenues,
                    onVenueClick = onVenueClick,
                    onFavoriteToggle = {},
                    onSeeAllClick = {},
                    onOfferClick = {}
                )
            }
        }

        if (message.vendorIds.isNotEmpty()) {
            val matchedVendors = allVendors.filter { message.vendorIds.contains(it.id) }
            if (matchedVendors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                VendorCarousel(
                    title = "Suggested Vendors",
                    vendors = matchedVendors,
                    onVendorClick = onVendorClick,
                    onFavoriteToggle = {},
                    onSeeAllClick = {},
                    onOfferClick = {}
                )
            }
        }

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
                    tint = ContentSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = { /* Handle negative feedback */ },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_thumbs_down),
                    contentDescription = "Unhelpful",
                    tint = ContentSecondary,
                    modifier = Modifier.size(28.dp)
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
        style = JasnifyTheme.typography.bodyXLarge,
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