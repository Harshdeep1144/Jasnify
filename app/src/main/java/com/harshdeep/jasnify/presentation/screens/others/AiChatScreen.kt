package com.harshdeep.jasnify.presentation.screens.others

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.harshdeep.jasnify.presentation.screens.venues.VenueDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.checklist.ChecklistDetailScreen
import com.harshdeep.jasnify.presentation.components.bottomdrawer.GuestDetailsBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddExpenseBottomSheet

// Data class for Chat Messages
@IgnoreExtraProperties
data class AiMessage(
    var id: String = "",
    var text: String = "",
    @get:PropertyName("isUser")
    @set:PropertyName("isUser")
    var isUser: Boolean = false,
    var timestamp: Long = 0,
    var venueIds: List<String> = emptyList(),
    var vendorIds: List<String> = emptyList(),
    var guestIds: List<String> = emptyList(),
    var expenseIds: List<String> = emptyList(),
    var checklistIds: List<String> = emptyList(),
    @get:PropertyName("showBudgetSummary")
    @set:PropertyName("showBudgetSummary")
    var showBudgetSummary: Boolean = false
)

@Composable
fun AiChatScreen(
    modifier: Modifier = Modifier,
    eventId: String? = null,
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
    var isDrawerOpen by remember { mutableStateOf(false) }

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val chatSessions by viewModel.chatSessions.collectAsStateWithLifecycle()
    val currentChatId by viewModel.currentChatId.collectAsStateWithLifecycle()

    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val expenses by budgetViewModel.expenses.collectAsStateWithLifecycle()
    val budgetSettings by budgetViewModel.budgetSettings.collectAsStateWithLifecycle()
    val checklists by checklistViewModel.checklists.collectAsStateWithLifecycle()
    val guests by guestViewModel.guests.collectAsStateWithLifecycle()

    val allVenues by venueViewModel.allVenues.collectAsStateWithLifecycle()
    val allVendors by vendorViewModel.allVendors.collectAsStateWithLifecycle()

    // Overlay States
    var selectedVenueDetail by remember { mutableStateOf<Venue?>(null) }
    var selectedVendorDetail by remember { mutableStateOf<Vendor?>(null) }
    var selectedChecklistDetail by remember { mutableStateOf<Checklist?>(null) }
    var selectedGuestDetail by remember { mutableStateOf<Guest?>(null) }
    var selectedExpenseDetail by remember { mutableStateOf<ExpenseEntity?>(null) }

    val targetEventId = eventId ?: activeEvent?.id
    LaunchedEffect(targetEventId) {
        if (!targetEventId.isNullOrBlank()) {
            viewModel.setEventId(targetEventId)
            budgetViewModel.setEventId(targetEventId)
            cateringViewModel.setEventId(targetEventId)
            venueViewModel.setEventId(targetEventId)
            vendorViewModel.setEventId(targetEventId)
            checklistViewModel.setEventId(targetEventId)
            guestViewModel.setEventId(targetEventId)
        }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    BackHandler(enabled = isDrawerOpen || selectedVenueDetail != null || selectedVendorDetail != null || selectedChecklistDetail != null || selectedGuestDetail != null || selectedExpenseDetail != null) {
        if (isDrawerOpen) {
            isDrawerOpen = false
        } else {
            selectedVenueDetail = null
            selectedVendorDetail = null
            selectedChecklistDetail = null
            selectedGuestDetail = null
            selectedExpenseDetail = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        if (messages.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ask anything about your expenses, guest lists, vendors, or event planning.",
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
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
                items(messages, key = { it.id.ifEmpty { UUID.randomUUID().toString() } }) { message ->
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
                onMenuClick = { isDrawerOpen = true },
                backIcon = TopIcon.Predefined.DOWN,
                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                buttonStyle = ButtonBackground.OPAQUE
            )
        }

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
                placeholder = "Ask about expenses, vendors, guests...",
                onSendClick = {
                    if (inputText.isNotBlank()) {
                        val query = inputText
                        inputText = ""
                        viewModel.sendMessage(query)
                    }
                },
                onStopClick = { },
                onVoiceClick = { isVoiceMode = true },
                onToggleMicMute = { isMicMuted = !isMicMuted },
                onCancelVoice = { isVoiceMode = false }
            )
        }

        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { isDrawerOpen = false }
                    .zIndex(25f)
            )

            AnimatedVisibility(
                visible = isDrawerOpen,
                enter = slideInHorizontally(initialOffsetX = { -it }),
                exit = slideOutHorizontally(targetOffsetX = { -it }),
                modifier = Modifier.zIndex(30f)
            ) {
                ChatSidebarDrawer(
                    sessions = chatSessions,
                    currentChatId = currentChatId,
                    onSelectChat = { chatId ->
                        viewModel.selectChatSession(chatId)
                        isDrawerOpen = false
                    },
                    onNewChatClick = {
                        viewModel.createNewChatSession("New Chat")
                        isDrawerOpen = false
                    },
                    onDeleteChat = { chatId ->
                        viewModel.deleteChatSession(chatId)
                    }
                )
            }
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
                    onChatClick = { selectedVenueDetail = null },
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
                    onChatClick = { selectedVendorDetail = null },
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

    // Bottom Sheets
    if (selectedGuestDetail != null) {
        GuestDetailsBottomSheet(
            guest = selectedGuestDetail!!,
            onDismiss = { selectedGuestDetail = null },
            isViewer = false,
            onEditClick = { },
            onInviteClick = { },
            onDeleteClick = { }
        )
    }

    if (selectedExpenseDetail != null) {
        val expense = selectedExpenseDetail!!
        AddExpenseBottomSheet(
            onDismiss = { selectedExpenseDetail = null },
            onSave = { amount, receiver, category, emoji, phone, notes ->
                budgetViewModel.updateExpense(
                    expense.id,
                    receiver,
                    category,
                    amount.toString().toDoubleOrNull() ?: expense.amount,
                    emoji,
                    "User",
                    phone,
                    notes
                )
                selectedExpenseDetail = null
            },
            categories = listOf("Venue", "Catering", "Vendors", "Staff & Crew", "Gifts"),
            onAddCategory = { },
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
fun ChatSidebarDrawer(
    sessions: List<ChatSession>,
    currentChatId: String?,
    onSelectChat: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onDeleteChat: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredSessions = remember(sessions, searchQuery) {
        if (searchQuery.isBlank()) sessions
        else sessions.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    val groupedSessions = remember(filteredSessions) {
        val calendar = Calendar.getInstance()
        val nowMillis = System.currentTimeMillis()

        calendar.timeInMillis = nowMillis
        val todayYear = calendar.get(Calendar.YEAR)
        val todayDay = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayYear = calendar.get(Calendar.YEAR)
        val yesterdayDay = calendar.get(Calendar.DAY_OF_YEAR)

        val groups = LinkedHashMap<String, MutableList<ChatSession>>()

        filteredSessions.forEach { session ->
            val sessCal = Calendar.getInstance().apply { timeInMillis = session.timestamp }
            val sessYear = sessCal.get(Calendar.YEAR)
            val sessDay = sessCal.get(Calendar.DAY_OF_YEAR)

            val groupKey = when {
                sessYear == todayYear && sessDay == todayDay -> "TODAY"
                sessYear == yesterdayYear && sessDay == yesterdayDay -> "YESTERDAY"
                else -> SimpleDateFormat("d'TH' MMM, yyyy", Locale.US).format(Date(session.timestamp)).uppercase()
            }

            groups.getOrPut(groupKey) { mutableListOf() }.add(session)
        }
        groups
    }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(310.dp),
        color = Color(0xFFF3F3F3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Row: Jasnify Logo & Search Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jasnify",
                    style = TextStyle(
                        fontFamily = FontFamily.Cursive,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1F)
                    )
                )

                IconButton(
                    onClick = { isSearchActive = !isSearchActive },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E2E2))
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF1C1B1F)
                    )
                }
            }

            // Search Bar Input
            AnimatedVisibility(visible = isSearchActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Search chat history...", color = Color.Gray, fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // + New Chat Pill Button
            Button(
                onClick = onNewChatClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD2E5E4)
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = Color(0xFF13504E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New Chat",
                        color = Color(0xFF13504E),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chat Session History List grouped by Date
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedSessions.forEach { (dateHeader, sessionList) ->
                    item(key = dateHeader) {
                        Text(
                            text = dateHeader,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8E8E93),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(sessionList, key = { it.id }) { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (session.id == currentChatId) Color(0xFFE4ECEB) else Color.Transparent)
                                .clickable { onSelectChat(session.id) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = session.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF1C1B1F),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { onDeleteChat(session.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = Color(0xFF1C1B1F),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratingIndicator() {
    Row(
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceBrandSecondary)
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .widthIn(max = 280.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = message.text,
                color = ContentPrimary,
                style = JasnifyTheme.typography.bodyXLarge
            )
        }
    }
}

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

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_thumbs_up),
                    contentDescription = "Helpful",
                    tint = ContentSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_thumbs_down),
                    contentDescription = "Unhelpful",
                    tint = ContentSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

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