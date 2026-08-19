package com.harshdeep.jasnify.presentation.screens.others

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.inputfield.AiChatInput
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.viewmodels.*
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.domain.model.*
import com.harshdeep.jasnify.presentation.components.cards.*
import com.harshdeep.jasnify.presentation.components.carousels.VendorCarousel
import com.harshdeep.jasnify.presentation.components.carousels.VenueCarousel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

import com.harshdeep.jasnify.presentation.screens.venues.VenueDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.checklist.ChecklistDetailScreen
import com.harshdeep.jasnify.presentation.components.bottomdrawer.GuestDetailsBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddExpenseBottomSheet

// Data class for Chat Messages
data class AiMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val venueIds: List<String> = emptyList(),
    val vendorIds: List<String> = emptyList(),
    val guestIds: List<String> = emptyList(),
    val expenseIds: List<String> = emptyList(),
    val checklistIds: List<String> = emptyList(),
    val showBudgetSummary: Boolean = false
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
    onVenueClick: (Venue) -> Unit = {}, // Still here for other purposes maybe
    onVendorClick: (Vendor) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    var isVoiceMode by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()

    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val expenses by budgetViewModel.expenses.collectAsStateWithLifecycle()
    val budgetSettings by budgetViewModel.budgetSettings.collectAsStateWithLifecycle()
    val cateringItems by cateringViewModel.cateringItems.collectAsStateWithLifecycle()
    val savedVenues by venueViewModel.savedVenues.collectAsStateWithLifecycle()
    val savedVendors by vendorViewModel.savedVendors.collectAsStateWithLifecycle()
    val checklists by checklistViewModel.checklists.collectAsStateWithLifecycle()
    val guests by guestViewModel.guests.collectAsStateWithLifecycle()

    val allVenues by venueViewModel.allVenues.collectAsStateWithLifecycle()
    val allVendors by vendorViewModel.allVendors.collectAsStateWithLifecycle()

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUserUid = remember(auth.currentUser) { auth.currentUser?.uid.orEmpty() }
    val isOwner = activeEvent?.ownerId == currentUserUid
    val isViewer = !isOwner && activeEvent != null

    // Overlay States for direct composable usage
    var selectedVenueDetail by remember { mutableStateOf<Venue?>(null) }
    var selectedVendorDetail by remember { mutableStateOf<Vendor?>(null) }
    var selectedChecklistDetail by remember { mutableStateOf<Checklist?>(null) }
    var selectedGuestDetail by remember { mutableStateOf<Guest?>(null) }
    var selectedExpenseDetail by remember { mutableStateOf<ExpenseEntity?>(null) }

    LaunchedEffect(activeEvent) {
        val eventId = activeEvent?.id ?: return@LaunchedEffect
        viewModel.setEventId(eventId)
        budgetViewModel.setEventId(eventId)
        cateringViewModel.setEventId(eventId)
        venueViewModel.setEventId(eventId)
        vendorViewModel.setEventId(eventId)
        checklistViewModel.setEventId(eventId)
        guestViewModel.setEventId(eventId)
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    BackHandler(enabled = selectedVenueDetail != null || selectedVendorDetail != null || selectedChecklistDetail != null || selectedGuestDetail != null || selectedExpenseDetail != null) {
        selectedVenueDetail = null
        selectedVendorDetail = null
        selectedChecklistDetail = null
        selectedGuestDetail = null
        selectedExpenseDetail = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        if (messages.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai),
                    contentDescription = null,
                    tint = ContentSecondary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "How can I help you today?",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentSecondary
                )
            }
        } else {
            // Chat Message List (Edge-to-Edge scrolling behind gradients)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                            guests = guests,
                            expenses = expenses,
                            checklists = checklists,
                            budgetSettings = budgetSettings,
                            onVenueClick = { selectedVenueDetail = it },
                            onVendorClick = { selectedVendorDetail = it },
                            onGuestClick = { selectedGuestDetail = it },
                            onExpenseClick = { selectedExpenseDetail = it },
                            onChecklistClick = { selectedChecklistDetail = it }
                        )
                    }
                }

                if (isGenerating) {
                    item {
                        GeneratingIndicator()
                    }
                }
            }
        }

        // Floating Top Bar with Top Gradient
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(brush = TopGradientBrushLightTheme)
                .statusBarsPadding()
                .zIndex(10f)
        ) {
            CustomTopBar(
                title = "",
                onBackClick = onBackClick,
                onMenuClick = onMoreClick,
                backIcon = TopIcon.Predefined.DOWN,
                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                buttonStyle = ButtonBackground.OPAQUE
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

        // Direct Composable Overlays
        AnimatedVisibility(
            visible = selectedVenueDetail != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.zIndex(20f)
        ) {
            selectedVenueDetail?.let { venue ->
                VenueDetailScreen(
                    venueDetail = venue,
                    onBackClick = { selectedVenueDetail = null },
                    onChatClick = { /* Already in chat */ },
                    venueViewModel = venueViewModel
                )
            }
        }

        AnimatedVisibility(
            visible = selectedVendorDetail != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.zIndex(20f)
        ) {
            selectedVendorDetail?.let { vendor ->
                VendorDetailScreen(
                    vendorDetail = vendor,
                    onBackClick = { selectedVendorDetail = null },
                    onChatClick = { /* Already in chat */ },
                    vendorViewModel = vendorViewModel
                )
            }
        }

        AnimatedVisibility(
            visible = selectedChecklistDetail != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.zIndex(20f)
        ) {
            selectedChecklistDetail?.let { checklist ->
                ChecklistDetailScreen(
                    checklist = checklist,
                    onBackClick = { updated ->
                        if (updated != null) {
                            checklistViewModel.saveChecklist(updated)
                        }
                        selectedChecklistDetail = null
                    },
                    onDelete = { id ->
                        checklistViewModel.deleteChecklist(id)
                        selectedChecklistDetail = null
                    }
                )
            }
        }
    }

    // Bottom Sheets (Non-full screen overlays)
    if (selectedGuestDetail != null) {
        GuestDetailsBottomSheet(
            guest = selectedGuestDetail!!,
            onDismiss = { selectedGuestDetail = null },
            isViewer = false, // Assuming active role here or fetch from state
            onEditClick = { /* Handle if needed */ },
            onInviteClick = { /* Handle if needed */ },
            onDeleteClick = { /* Handle if needed */ }
        )
    }

    if (selectedExpenseDetail != null) {
        val expense = selectedExpenseDetail!!
        AddExpenseBottomSheet(
            onDismiss = { selectedExpenseDetail = null },
            onSave = { amount, receiver, category, emoji, phone, notes ->
                budgetViewModel.updateExpense(
                    expense.id, receiver, category, amount.toDouble(), emoji, "User", phone, notes
                )
                selectedExpenseDetail = null
            },
            categories = listOf("Venue", "Catering", "Vendors", "Staff & Crew", "Gifts"),
            onAddCategory = { /* Optional: handle adding category if needed */ },
            initialAmount = expense.amount.toString(),
            initialReceiver = expense.title,
            initialCategory = expense.category,
            initialEmoji = expense.emoji,
            initialPhoneNumber = expense.phoneNumber ?: "",
            initialNote = expense.note ?: ""
        )
    }
}

@Composable
fun GeneratingIndicator() {
    Row(
        modifier = Modifier.padding(vertical = 12.dp),
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
                    .size(4.dp)
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
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
    guests: List<Guest> = emptyList(),
    expenses: List<ExpenseEntity> = emptyList(),
    checklists: List<Checklist> = emptyList(),
    budgetSettings: BudgetEntity? = null,
    onVenueClick: (Venue) -> Unit = {},
    onVendorClick: (Vendor) -> Unit = {},
    onGuestClick: (Guest) -> Unit = {},
    onExpenseClick: (ExpenseEntity) -> Unit = {},
    onChecklistClick: (Checklist) -> Unit = {},
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
                    onOfferClick = {},
                    cardSize = CompactCardSize.MEDIUM
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
                    onOfferClick = {},
                    cardSize = CompactCardSize.MEDIUM
                )
            }
        }

        if (message.guestIds.isNotEmpty()) {
            val matchedGuests = guests.filter { message.guestIds.contains(it.id) }
            matchedGuests.forEach { guest ->
                Spacer(modifier = Modifier.height(12.dp))
                GuestCard(
                    name = guest.name,
                    label = guest.type,
                    isInvited = guest.invited,
                    onInviteClick = {},
                    onCardClick = { onGuestClick(guest) }
                )
            }
        }

        if (message.expenseIds.isNotEmpty()) {
            val matchedExpenses = expenses.filter { message.expenseIds.contains(it.id) }
            matchedExpenses.forEach { expense ->
                Spacer(modifier = Modifier.height(12.dp))
                ExpenseCard(
                    title = expense.title,
                    category = expense.category,
                    amount = "₹${expense.amount}",
                    emoji = expense.emoji,
                    lastUpdatedBy = expense.lastUpdatedBy,
                    lastUpdatedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.lastUpdatedDate)),
                    onDeleteClick = {},
                    onModifyClick = { onExpenseClick(expense) }
                )
            }
        }

        if (message.checklistIds.isNotEmpty()) {
            val matchedChecklists = checklists.filter { message.checklistIds.contains(it.id) }
            matchedChecklists.forEach { checklist ->
                Spacer(modifier = Modifier.height(12.dp))
                ChecklistCard(
                    checklist = checklist,
                    onClick = { onChecklistClick(checklist) }
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
 * Parses bold mark down annotations `**bold**` and formats bullets/headers cleanly
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