package com.harshdeep.jasnify.presentation.screens.others

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.model.*
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddExpenseBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.GuestDetailsBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.*
import com.harshdeep.jasnify.presentation.components.carousels.VendorCarousel
import com.harshdeep.jasnify.presentation.components.carousels.VenueCarousel
import com.harshdeep.jasnify.presentation.components.inputfield.AiChatInput
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.screens.main.tabs.checklist.ChecklistDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorDetailScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueDetailScreen
import com.harshdeep.jasnify.presentation.viewmodels.*
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    var inputText by remember { mutableStateOf("") }
    var isVoiceMode by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isAiSpeaking by remember { mutableStateOf(false) }
    var isTtsReady by remember { mutableStateOf(false) }
    var isDrawerOpen by remember { mutableStateOf(false) }

    // Streaming buffer and interruption tracking
    var activeSpeakingMessageId by remember { mutableStateOf<String?>(null) }
    var wasInterrupted by remember { mutableStateOf(false) }
    var streamedDisplayMessage by remember { mutableStateOf<AiMessage?>(null) }
    var streamJob by remember { mutableStateOf<Job?>(null) }
    var processedMessageIds by remember { mutableStateOf(setOf<String>()) }
    var fullyCompletedMessageIds by remember { mutableStateOf(setOf<String>()) }

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

    var selectedVenueDetail by remember { mutableStateOf<Venue?>(null) }
    var selectedVendorDetail by remember { mutableStateOf<Vendor?>(null) }
    var selectedChecklistDetail by remember { mutableStateOf<Checklist?>(null) }
    var selectedGuestDetail by remember { mutableStateOf<Guest?>(null) }
    var selectedExpenseDetail by remember { mutableStateOf<ExpenseEntity?>(null) }

    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 800L)
        }
    }

    fun startListeningSafe() {
        mainHandler.post {
            try {
                if (isVoiceMode && !isMicMuted) {
                    speechRecognizer?.cancel()
                    speechRecognizer?.startListening(recognizerIntent)
                }
            } catch (e: Exception) {
                Log.e("AiChatScreen", "Error starting recognizer: ${e.message}")
            }
        }
    }

    fun stopListeningSafe() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
            } catch (e: Exception) {
                Log.e("AiChatScreen", "Error stopping recognizer: ${e.message}")
            }
        }
    }

    // Handles user speech barge-in and permanently marks the message as interrupted
    fun interruptAndSaveSpokenPortion() {
        if (isAiSpeaking || activeSpeakingMessageId != null) {
            wasInterrupted = true
            ttsEngine?.stop()
            isAiSpeaking = false
            streamJob?.cancel()

            val currentStreamed = streamedDisplayMessage
            if (currentStreamed != null && currentStreamed.text.isNotBlank()) {
                viewModel.updateMessageText(currentStreamed.id, currentStreamed.text.trim())
            }

            activeSpeakingMessageId = null
        }
    }

    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                }
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        mainHandler.post {
                            isAiSpeaking = true
                            wasInterrupted = false
                        }
                    }
                    override fun onDone(utteranceId: String?) {
                        mainHandler.post {
                            isAiSpeaking = false
                            if (!wasInterrupted && utteranceId != null) {
                                fullyCompletedMessageIds = fullyCompletedMessageIds + utteranceId
                            }
                            activeSpeakingMessageId = null
                            startListeningSafe()
                        }
                    }
                    override fun onError(utteranceId: String?) {
                        mainHandler.post {
                            isAiSpeaking = false
                            activeSpeakingMessageId = null
                            startListeningSafe()
                        }
                    }
                })
            }
        }
        ttsEngine = tts

        val sr = SpeechRecognizer.createSpeechRecognizer(context)
        sr.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                mainHandler.postDelayed({
                    if (isVoiceMode && !isMicMuted) {
                        startListeningSafe()
                    }
                }, 300)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0].trim()
                    if (spokenText.length >= 2) {
                        mainHandler.post {
                            interruptAndSaveSpokenPortion()
                            viewModel.sendMessage(spokenText)
                        }
                    }
                }
                mainHandler.postDelayed({
                    if (isVoiceMode && !isMicMuted) {
                        startListeningSafe()
                    }
                }, 200)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val partialText = matches[0].trim()
                    // Filter out transient background clicks/pops - require meaningful multi-character voice tokens
                    val words = partialText.split("\\s+".toRegex()).filter { it.isNotBlank() }
                    if (words.isNotEmpty() && (words.size >= 2 || partialText.length >= 4)) {
                        mainHandler.post {
                            interruptAndSaveSpokenPortion()
                        }
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        speechRecognizer = sr

        onDispose {
            tts?.stop()
            tts?.shutdown()
            sr.destroy()
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isVoiceMode = isGranted
    }

    LaunchedEffect(isVoiceMode, isMicMuted) {
        if (isVoiceMode && !isMicMuted) {
            startListeningSafe()
        } else {
            stopListeningSafe()
            if (!isVoiceMode) {
                interruptAndSaveSpokenPortion()
            }
        }
    }

    // Process new AI messages in Voice Mode: sequential audio play + progressive word stream
    val latestMessage = messages.lastOrNull()
    LaunchedEffect(latestMessage?.id, isVoiceMode, isTtsReady) {
        if (isVoiceMode && latestMessage != null && !latestMessage.isUser && latestMessage.text.isNotBlank()) {
            if (!processedMessageIds.contains(latestMessage.id) && activeSpeakingMessageId != latestMessage.id) {
                processedMessageIds = processedMessageIds + latestMessage.id
                activeSpeakingMessageId = latestMessage.id
                wasInterrupted = false

                isAiSpeaking = true
                streamedDisplayMessage = latestMessage.copy(
                    text = "",
                    venueIds = emptyList(),
                    vendorIds = emptyList(),
                    guestIds = emptyList(),
                    expenseIds = emptyList(),
                    checklistIds = emptyList(),
                    showBudgetSummary = false
                )

                startListeningSafe()

                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, latestMessage.id)
                }
                ttsEngine?.speak(latestMessage.text, TextToSpeech.QUEUE_FLUSH, params, latestMessage.id)

                streamJob?.cancel()
                streamJob = coroutineScope.launch {
                    val words = latestMessage.text.split(" ")
                    val stringBuilder = StringBuilder()
                    for (i in words.indices) {
                        if (!isAiSpeaking || wasInterrupted) break
                        if (i > 0) stringBuilder.append(" ")
                        stringBuilder.append(words[i])
                        streamedDisplayMessage = streamedDisplayMessage?.copy(text = stringBuilder.toString())
                        delay(280L)
                    }
                    if (isAiSpeaking && !wasInterrupted) {
                        streamedDisplayMessage = latestMessage.copy(
                            text = latestMessage.text,
                            venueIds = emptyList(),
                            vendorIds = emptyList(),
                            guestIds = emptyList(),
                            expenseIds = emptyList(),
                            checklistIds = emptyList(),
                            showBudgetSummary = false
                        )
                    }
                }
            }
        }
    }

    // Mark non-voice messages completed immediately; enforce completion check for voice mode
    LaunchedEffect(messages, isVoiceMode) {
        if (!isVoiceMode) {
            val allAiIds = messages.filter { !it.isUser }.map { it.id }.toSet()
            fullyCompletedMessageIds = fullyCompletedMessageIds + allAiIds
        }
    }

    val displayedMessages = remember(
        messages,
        isVoiceMode,
        streamedDisplayMessage,
        activeSpeakingMessageId,
        fullyCompletedMessageIds
    ) {
        if (isVoiceMode) {
            val nonUser = messages.filter { !it.isUser }.map { msg ->
                val isCompleted = fullyCompletedMessageIds.contains(msg.id)
                if (!isCompleted) {
                    msg.copy(
                        venueIds = emptyList(),
                        vendorIds = emptyList(),
                        guestIds = emptyList(),
                        expenseIds = emptyList(),
                        checklistIds = emptyList(),
                        showBudgetSummary = false
                    )
                } else {
                    msg
                }
            }

            if (activeSpeakingMessageId != null && streamedDisplayMessage != null) {
                val history = nonUser.filter { it.id != activeSpeakingMessageId }
                if (streamedDisplayMessage!!.text.isNotBlank()) {
                    history + streamedDisplayMessage!!
                } else {
                    history
                }
            } else {
                nonUser
            }
        } else {
            messages
        }
    }

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
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val rawKeyboardHeightDp = with(density) { imeBottom.toDp() }

    val animatedKeyboardSpacerDp by animateDpAsState(
        targetValue = rawKeyboardHeightDp,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "KeyboardSpacerAnimation"
    )

    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) true
            else {
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                val lastIndex = lastVisibleItem?.index ?: 0
                lastIndex >= totalItems - 2
            }
        }
    }

    val showScrollToBottomButton by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems <= 1) false
            else {
                val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleIndex < totalItems - 2
            }
        }
    }

    LaunchedEffect(displayedMessages.size, streamedDisplayMessage?.text?.length) {
        if (displayedMessages.isNotEmpty()) {
            listState.animateScrollToItem(displayedMessages.size - 1)
        }
    }

    var wasAtBottomBeforeIme by remember { mutableStateOf(true) }
    LaunchedEffect(imeBottom) {
        if (imeBottom > 0) {
            if (wasAtBottomBeforeIme && displayedMessages.isNotEmpty()) {
                listState.animateScrollToItem(displayedMessages.size)
            }
        } else {
            wasAtBottomBeforeIme = isAtBottom
        }
    }

    LaunchedEffect(isAtBottom) {
        if (imeBottom == 0) {
            wasAtBottomBeforeIme = isAtBottom
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
        if (displayedMessages.isEmpty() && !isVoiceMode) {
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
                    textAlign = TextAlign.Center
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
                    bottom = if (isVoiceMode) 270.dp else 120.dp
                )
            ) {
                items(displayedMessages, key = { it.id.ifEmpty { UUID.randomUUID().toString() } }) { message ->
                    if (message.isUser) {
                        UserMessageBubble(message = message)
                    } else {
                        if (message.text.isNotBlank()) {
                            val allowAttachments = !isVoiceMode || fullyCompletedMessageIds.contains(message.id)
                            AiMessageContent(
                                message = message,
                                allVenues = if (allowAttachments) allVenues else emptyList(),
                                allVendors = if (allowAttachments) allVendors else emptyList(),
                                guests = if (allowAttachments) guests else emptyList(),
                                expenses = if (allowAttachments) expenses else emptyList(),
                                checklists = if (allowAttachments) checklists else emptyList(),
                                budgetSettings = if (allowAttachments) budgetSettings else null,
                                onVenueClick = { selectedVenueDetail = it },
                                onVendorClick = { selectedVendorDetail = it },
                                onGuestClick = { selectedGuestDetail = it },
                                onExpenseClick = { selectedExpenseDetail = it },
                                onChecklistClick = { selectedChecklistDetail = it }
                            )
                        }
                    }
                }

                if (isGenerating) {
                    item(key = "generating_indicator") {
                        GeneratingIndicator()
                    }
                }

                item(key = "keyboard_bottom_spacer") {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(animatedKeyboardSpacerDp)
                    )
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

        AnimatedVisibility(
            visible = showScrollToBottomButton && !isVoiceMode,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .imePadding()
                .padding(end = 16.dp, bottom = 108.dp)
                .zIndex(15f)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        if (displayedMessages.isNotEmpty()) {
                            listState.animateScrollToItem(displayedMessages.size)
                        }
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .shadow(4.dp, CircleShape),
                shape = CircleShape,
                containerColor = SurfaceBrandPrimary,
                contentColor = ContentInvPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Scroll to bottom",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding()
                .background(brush = BottomGradientBrush)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .zIndex(10f)
        ) {
            AiChatInput(
                value = inputText,
                onValueChange = { inputText = it },
                isVoiceMode = isVoiceMode,
                isMicMuted = isMicMuted,
                isAiSpeaking = isAiSpeaking,
                isGenerating = isGenerating,
                placeholderPrefix = "Search for ",
                dynamicPlaceholders = listOf(
                    "fixed and variable expenses",
                    "photographers & vendors",
                    "venue availability",
                    "guest invitations & RSVPs",
                    "checklist progress"
                ),
                onSendClick = {
                    if (inputText.isNotBlank()) {
                        val query = inputText
                        inputText = ""
                        viewModel.sendMessage(query)
                    }
                },
                onStopClick = { },
                onVoiceClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        isVoiceMode = true
                    } else {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onToggleMicMute = {
                    isMicMuted = !isMicMuted
                },
                onCancelVoice = {
                    isVoiceMode = false
                    interruptAndSaveSpokenPortion()
                }
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