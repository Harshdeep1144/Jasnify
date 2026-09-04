package com.harshdeep.jasnify.presentation.screens.chats

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.model.AiMessage
import com.harshdeep.jasnify.domain.model.ChatSession
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.ai.AiMessageContent
import com.harshdeep.jasnify.presentation.components.ai.AutoScrollingChipRow
import com.harshdeep.jasnify.presentation.components.ai.ChatSidebarDrawer
import com.harshdeep.jasnify.presentation.components.ai.UserMessageBubble
import com.harshdeep.jasnify.presentation.components.ai.getContextualChips
import com.harshdeep.jasnify.presentation.components.bottomdrawer.budget.AddExpenseBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.guests.GuestDetailsBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.inputfield.AiChatInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.screens.budget.BudgetScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.checklist.ChecklistDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.guests.GuestsTab
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorsTab
import com.harshdeep.jasnify.presentation.screens.venues.VenueDetailScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.CateringViewModel
import com.harshdeep.jasnify.presentation.viewmodels.ChecklistViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.GenerativeViewModel
import com.harshdeep.jasnify.presentation.viewmodels.GuestViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AiChatScreen(
    modifier: Modifier = Modifier,
    eventId: String? = null,
    initialContext: String? = null,
    shouldStartNewSession: Boolean = false,
    viewModel: GenerativeViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    cateringViewModel: CateringViewModel = hiltViewModel(),
    venueViewModel: VenueViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    checklistViewModel: ChecklistViewModel = hiltViewModel(),
    guestViewModel: GuestViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    mainNavController: NavHostController? = null,
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    var inputText by remember { mutableStateOf("") }
    var isVoiceMode by remember { mutableStateOf(false) }
    var voiceModeStartTime by remember { mutableLongStateOf(0L) }
    var hasSpokenFirstMessage by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isAiSpeaking by remember { mutableStateOf(false) }
    var isTtsReady by remember { mutableStateOf(false) }
    var isDrawerOpen by remember { mutableStateOf(false) }

    var audioRms by remember { mutableFloatStateOf(0f) }
    var hasQueuedFinalForId by remember { mutableStateOf<String?>(null) }

    val firebaseUser = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser }
    val userFirstName = remember(firebaseUser) {
        firebaseUser?.displayName?.trim()?.split("\\s+".toRegex())?.firstOrNull() ?: "there"
    }

    var activeSpeakingMessageId by remember { mutableStateOf<String?>(null) }
    var wasInterrupted by remember { mutableStateOf(false) }
    var streamedDisplayMessage by remember { mutableStateOf<AiMessage?>(null) }
    var fullyCompletedMessageIds by remember { mutableStateOf(setOf<String>()) }
    var lastQueuedIndex by remember { mutableIntStateOf(0) }
    var currentProcessingId by remember { mutableStateOf<String?>(null) }

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val chatSessions by viewModel.chatSessions.collectAsStateWithLifecycle()
    val currentChatId by viewModel.currentChatId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (shouldStartNewSession) {
            viewModel.createNewChatSession()
        }
    }

    var currentSubScreen by remember { mutableStateOf("chat") }
    var selectedVendorCategory by remember { mutableStateOf<VendorCategoryItem?>(null) }
    var chatMerchantId by remember { mutableStateOf<String?>(null) }
    var chatItemId by remember { mutableStateOf<String?>(null) }
    var chatItemType by remember { mutableStateOf("Venue") }

    var toastData by remember { mutableStateOf<ToastData?>(null) }
    var recentlyDeletedSession by remember { mutableStateOf<ChatSession?>(null) }

    LaunchedEffect(toastData?.message) {
        if (toastData?.message != null) {
            delay(3000.milliseconds)
            toastData = null
            recentlyDeletedSession = null
        }
    }

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
                if (isVoiceMode && !isMicMuted && !isAiSpeaking) {
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

    fun interruptAndSaveSpokenPortion() {
        audioRms = 0f
        if (isAiSpeaking || activeSpeakingMessageId != null) {
            wasInterrupted = true
            ttsEngine?.stop()
            isAiSpeaking = false

            val currentStreamed = streamedDisplayMessage
            if (currentStreamed != null && currentStreamed.text.isNotBlank()) {
                viewModel.updateMessageText(currentStreamed.id, currentStreamed.text.trim())
            }

            activeSpeakingMessageId = null
            hasQueuedFinalForId = null
            lastQueuedIndex = 0
        }
    }

    fun enableVoiceMode() {
        voiceModeStartTime = System.currentTimeMillis()
        hasSpokenFirstMessage = false
        isVoiceMode = true
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
                            audioRms = 0f
                        }
                    }
                    override fun onDone(utteranceId: String?) {
                        mainHandler.post {
                            if (utteranceId?.endsWith("_final") == true) {
                                isAiSpeaking = false
                                val baseId = utteranceId.removeSuffix("_final")
                                if (!wasInterrupted) {
                                    fullyCompletedMessageIds = fullyCompletedMessageIds + baseId
                                }
                                activeSpeakingMessageId = null
                                hasQueuedFinalForId = null
                                audioRms = 0f
                                startListeningSafe()
                            }
                        }
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        mainHandler.post {
                            isAiSpeaking = false
                            activeSpeakingMessageId = null
                            hasQueuedFinalForId = null
                            audioRms = 0f
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
            override fun onRmsChanged(rmsdB: Float) {
                if (isVoiceMode && !isMicMuted && !isAiSpeaking) {
                    val normalized = ((rmsdB + 2.0f) / 12.0f).coerceIn(0f, 1f)
                    audioRms = normalized
                }
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                audioRms = 0f
            }

            override fun onError(error: Int) {
                audioRms = 0f
                mainHandler.postDelayed({
                    if (isVoiceMode && !isMicMuted && !isAiSpeaking) {
                        startListeningSafe()
                    }
                }, 300)
            }

            override fun onResults(results: Bundle?) {
                audioRms = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0].trim()
                    if (spokenText.length >= 2) {
                        hasSpokenFirstMessage = true
                        mainHandler.post {
                            interruptAndSaveSpokenPortion()
                            viewModel.sendMessage(spokenText)
                        }
                    }
                }
                mainHandler.postDelayed({
                    if (isVoiceMode && !isMicMuted && !isAiSpeaking) {
                        startListeningSafe()
                    }
                }, 200)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val partialText = matches[0].trim()
                    val words = partialText.split("\\s+".toRegex()).filter { it.isNotBlank() }
                    if (words.isNotEmpty() && (words.size >= 2 || partialText.length >= 4)) {
                        hasSpokenFirstMessage = true
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
            tts.stop()
            tts.shutdown()
            sr.destroy()
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableVoiceMode()
        }
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

    val latestMessage = messages.lastOrNull()
    LaunchedEffect(latestMessage?.text, latestMessage?.id, isVoiceMode, isTtsReady, isGenerating) {
        if (
            isVoiceMode &&
            isTtsReady &&
            latestMessage != null &&
            !latestMessage.isUser &&
            latestMessage.timestamp >= voiceModeStartTime
        ) {
            if (currentProcessingId != latestMessage.id) {
                currentProcessingId = latestMessage.id
                activeSpeakingMessageId = latestMessage.id
                hasQueuedFinalForId = null
                lastQueuedIndex = 0
                wasInterrupted = false
                isAiSpeaking = true
            }

            streamedDisplayMessage = latestMessage

            val currentFullText = latestMessage.text
            val sentenceDelimiters = charArrayOf('.', '!', '?', '\n', ':', ';')

            if (currentFullText.length > lastQueuedIndex) {
                val unreadPortion = currentFullText.substring(lastQueuedIndex)
                var lastDelimIndex = -1

                for (i in unreadPortion.indices) {
                    if (sentenceDelimiters.contains(unreadPortion[i])) {
                        lastDelimIndex = i
                    }
                }

                if (lastDelimIndex != -1) {
                    val completeSentenceChunk = unreadPortion.substring(0, lastDelimIndex + 1).trim()
                    if (completeSentenceChunk.isNotBlank()) {
                        val utteranceId = "${latestMessage.id}_chunk_${lastQueuedIndex}"
                        val params = Bundle().apply {
                            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                        }
                        ttsEngine?.speak(
                            completeSentenceChunk.replace("**", ""),
                            TextToSpeech.QUEUE_ADD,
                            params,
                            utteranceId
                        )
                    }
                    lastQueuedIndex += (lastDelimIndex + 1)
                }
            }

            if (!isGenerating && hasQueuedFinalForId != latestMessage.id) {
                hasQueuedFinalForId = latestMessage.id
                val utteranceId = "${latestMessage.id}_final"
                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                }

                if (currentFullText.length > lastQueuedIndex) {
                    val remainingChunk = currentFullText.substring(lastQueuedIndex).trim()
                    lastQueuedIndex = currentFullText.length
                    if (remainingChunk.isNotBlank()) {
                        ttsEngine?.speak(
                            remainingChunk.replace("**", ""),
                            TextToSpeech.QUEUE_ADD,
                            params,
                            utteranceId
                        )
                    } else {
                        ttsEngine?.playSilentUtterance(50, TextToSpeech.QUEUE_ADD, utteranceId)
                    }
                } else {
                    ttsEngine?.playSilentUtterance(50, TextToSpeech.QUEUE_ADD, utteranceId)
                }
            }
        }
    }

    LaunchedEffect(messages, isVoiceMode) {
        if (!isVoiceMode) {
            val allAiIds = messages.filter { !it.isUser }.map { it.id }.toSet()
            fullyCompletedMessageIds = fullyCompletedMessageIds + allAiIds
        }
    }

    val displayedMessages = remember(messages, isVoiceMode, voiceModeStartTime) {
        if (isVoiceMode) {
            messages.filter { it.timestamp >= voiceModeStartTime }
        } else {
            messages
        }
    }

    val isDockedAtBottom = displayedMessages.isNotEmpty() || isVoiceMode
    val dockProgress by animateFloatAsState(
        targetValue = if (isDockedAtBottom) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dockProgress"
    )

    val AiBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF3E8FA),
            Color(0xFFF8F0FC),
            Color(0xFFFAF4FE)
        )
    )

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

    val latestMessageText = displayedMessages.lastOrNull()?.text.orEmpty()
    val displayedCount = displayedMessages.size

    LaunchedEffect(displayedCount, latestMessageText.length, isGenerating) {
        if (displayedCount > 0) {
            val targetIndex = (displayedCount - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
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

    BackHandler(
        enabled = currentSubScreen != "chat" ||
                isVoiceMode ||
                isDrawerOpen ||
                selectedVenueDetail != null ||
                selectedVendorDetail != null ||
                selectedChecklistDetail != null ||
                selectedGuestDetail != null ||
                selectedExpenseDetail != null
    ) {
        when {
            currentSubScreen != "chat" -> {
                currentSubScreen = "chat"
                selectedVendorCategory = null
            }
            isVoiceMode -> {
                isVoiceMode = false
                hasSpokenFirstMessage = false
                interruptAndSaveSpokenPortion()
            }
            isDrawerOpen -> {
                isDrawerOpen = false
            }
            else -> {
                selectedVenueDetail = null
                selectedVendorDetail = null
                selectedChecklistDetail = null
                selectedGuestDetail = null
                selectedExpenseDetail = null
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        AnimatedContent(
            targetState = currentSubScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(300))
                    .togetherWith(fadeOut(animationSpec = tween(300)))
            },
            label = "ai_sub_screen_transition",
            modifier = Modifier.fillMaxSize()
        ) { screen ->
            when (screen) {
                "chat" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AiBackgroundGradient)
                    ) {
                        if (dockProgress > 0f) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp)
                                    .graphicsLayer { alpha = dockProgress },
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(
                                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 68.dp,
                                    bottom = if (isVoiceMode) 270.dp else 120.dp
                                )
                            ) {
                                items(
                                    displayedMessages,
                                    key = { it.id.ifEmpty { UUID.randomUUID().toString() } }) { message ->
                                    if (message.isUser) {
                                        UserMessageBubble(message = message)
                                    } else {
                                        val allowAttachments =
                                            !isVoiceMode || fullyCompletedMessageIds.contains(message.id)
                                        AiMessageContent(
                                            message = message,
                                            isStreaming = isGenerating && message.id == displayedMessages.lastOrNull()?.id,
                                            allVenues = if (allowAttachments) allVenues else emptyList(),
                                            allVendors = if (allowAttachments) allVendors else emptyList(),
                                            guests = if (allowAttachments) guests else emptyList(),
                                            expenses = if (allowAttachments) expenses else emptyList(),
                                            checklists = if (allowAttachments) checklists else emptyList(),
                                            budgetSettings = if (allowAttachments) budgetSettings else null,
                                            onFeedbackClick = { feedbackType ->
                                                viewModel.toggleMessageFeedback(
                                                    message.id,
                                                    feedbackType
                                                )
                                            },
                                            onVenueClick = { selectedVenueDetail = it },
                                            onVendorClick = { selectedVendorDetail = it },
                                            onGuestClick = { selectedGuestDetail = it },
                                            onExpenseClick = { selectedExpenseDetail = it },
                                            onChecklistClick = { selectedChecklistDetail = it },
                                            onSeeAllVenues = { currentSubScreen = "venues" },
                                            onSeeAllVendors = { currentSubScreen = "vendors" },
                                            onSeeAllGuests = { currentSubScreen = "guests" },
                                            onSeeAllBudget = { currentSubScreen = "budget" }
                                        )
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

                        if (dockProgress < 1f) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                                    .offset(y = ((-40).dp * (1f - dockProgress)))
                                    .graphicsLayer { alpha = (1f - dockProgress) },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ai),
                                    contentDescription = "AI Sparkles",
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .size(68.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Hi $userFirstName,",
                                    style = JasnifyTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 32.sp
                                    ),
                                    color = Color(0xFF3C225C),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "What can I do for you?",
                                    style = JasnifyTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 26.sp
                                    ),
                                    color = Color(0xFF3C225C),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(28.dp))

                                if (dockProgress < 0.1f) {
                                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        AiChatInput(
                                            value = inputText,
                                            onValueChange = { inputText = it },
                                            isVoiceMode = isVoiceMode,
                                            hasSpokenFirstMessage = hasSpokenFirstMessage,
                                            isMicMuted = isMicMuted,
                                            isAiSpeaking = isAiSpeaking,
                                            isGenerating = isGenerating,
                                            audioRms = audioRms,
                                            placeholderPrefix = "Ask more about ",
                                            dynamicPlaceholders = listOf(
                                                "expenses",
                                                "caterers",
                                                "venues",
                                                "guest RSVPs",
                                                "checklists"
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
                                                    enableVoiceMode()
                                                } else {
                                                    audioPermissionLauncher.launch(
                                                        Manifest.permission.RECORD_AUDIO
                                                    )
                                                }
                                            },
                                            onToggleMicMute = {
                                                isMicMuted = !isMicMuted
                                            },
                                            onCancelVoice = {
                                                isVoiceMode = false
                                                hasSpokenFirstMessage = false
                                                interruptAndSaveSpokenPortion()
                                            }
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(68.dp))
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                val (contextualRow1, contextualRow2) = remember(initialContext) { getContextualChips(initialContext) }

                                AutoScrollingChipRow(
                                    chips = contextualRow1,
                                    onChipClick = { chipText ->
                                        viewModel.sendMessage(chipText)
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                AutoScrollingChipRow(
                                    chips = contextualRow2,
                                    onChipClick = { chipText ->
                                        viewModel.sendMessage(chipText)
                                    },
                                    reverseDirection = true
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .zIndex(10f)
                        ) {
                            CustomTopBar(
                                title = "",
                                onBackClick = onBackClick,
                                onMenuClick = { isDrawerOpen = true },
                                backIcon = TopIcon.Predefined.DOWN,
                                borderColor = MaterialTheme.colorScheme.outline.copy(0.16f),
                                menuIcon = TopIcon.Predefined.MENU_MODERN,
                                buttonStyle = ButtonBackground.TRANSLUCENT
                            )
                        }

                        AnimatedVisibility(
                            visible = showScrollToBottomButton && !isVoiceMode && dockProgress > 0.5f,
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

                        if (dockProgress > 0f) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .imePadding()
                                    .navigationBarsPadding()
                                    .padding(horizontal = 12.dp, vertical = 12.dp)
                                    .zIndex(10f)
                                    .graphicsLayer {
                                        alpha = dockProgress
                                        translationY = (60.dp.value * density.density * (1f - dockProgress))
                                    }
                            ) {
                                AiChatInput(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    isVoiceMode = isVoiceMode,
                                    hasSpokenFirstMessage = hasSpokenFirstMessage,
                                    isMicMuted = isMicMuted,
                                    isAiSpeaking = isAiSpeaking,
                                    isGenerating = isGenerating,
                                    audioRms = audioRms,
                                    placeholderPrefix = "Ask more about ",
                                    dynamicPlaceholders = listOf(
                                        "expenses",
                                        "caterers",
                                        "venue costs",
                                        "guest RSVPs",
                                        "checklist items"
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
                                            enableVoiceMode()
                                        } else {
                                            audioPermissionLauncher.launch(
                                                Manifest.permission.RECORD_AUDIO
                                            )
                                        }
                                    },
                                    onToggleMicMute = {
                                        isMicMuted = !isMicMuted
                                    },
                                    onCancelVoice = {
                                        isVoiceMode = false
                                        hasSpokenFirstMessage = false
                                        interruptAndSaveSpokenPortion()
                                    }
                                )
                            }
                        }

                        if (isDrawerOpen) {
                            val drawerWidthDp = 320.dp
                            val drawerWidthPx = with(density) { drawerWidthDp.toPx() }
                            val animatedOffsetX = remember { Animatable(-drawerWidthPx) }

                            LaunchedEffect(Unit) {
                                animatedOffsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                            }

                            fun closeDrawerSmoothly() {
                                coroutineScope.launch {
                                    animatedOffsetX.animateTo(
                                        targetValue = -drawerWidthPx,
                                        animationSpec = tween(
                                            durationMillis = 260,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                    isDrawerOpen = false
                                }
                            }

                            val currentFraction =
                                (1f - (abs(animatedOffsetX.value) / drawerWidthPx)).coerceIn(0f, 1f)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f * currentFraction))
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        closeDrawerSmoothly()
                                    }
                                    .zIndex(25f)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .zIndex(30f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                val velocityTracker = remember { VelocityTracker() }

                                ChatSidebarDrawer(
                                    sessions = chatSessions,
                                    currentChatId = currentChatId,
                                    onSelectChat = { chatId ->
                                        viewModel.selectChatSession(chatId)
                                        closeDrawerSmoothly()
                                    },
                                    onNewChatClick = {
                                        viewModel.createNewChatSession("New Chat")
                                        closeDrawerSmoothly()
                                    },
                                    onDeleteChat = { session ->
                                        recentlyDeletedSession = session
                                        viewModel.deleteChatSession(session.id)
                                        toastData = ToastData("Chat deleted", ToastType.DEFAULT)
                                    },
                                    modifier = Modifier
                                        .offset {
                                            IntOffset(
                                                x = animatedOffsetX.value.roundToInt(),
                                                y = 0
                                            )
                                        }
                                        .pointerInput(Unit) {
                                            detectHorizontalDragGestures(
                                                onDragStart = { velocityTracker.resetTracking() },
                                                onDragEnd = {
                                                    val velocity =
                                                        velocityTracker.calculateVelocity().x
                                                    val shouldDismiss =
                                                        animatedOffsetX.value < -(drawerWidthPx * 0.35f) || velocity < -1000f

                                                    coroutineScope.launch {
                                                        if (shouldDismiss) {
                                                            animatedOffsetX.animateTo(
                                                                targetValue = -drawerWidthPx,
                                                                animationSpec = tween(
                                                                    durationMillis = 220,
                                                                    easing = FastOutSlowInEasing
                                                                )
                                                            )
                                                            isDrawerOpen = false
                                                        } else {
                                                            animatedOffsetX.animateTo(
                                                                targetValue = 0f,
                                                                animationSpec = spring(
                                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                                    stiffness = Spring.StiffnessMediumLow
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                onDragCancel = {
                                                    coroutineScope.launch {
                                                        animatedOffsetX.animateTo(0f)
                                                    }
                                                },
                                                onHorizontalDrag = { change, dragAmount ->
                                                    velocityTracker.addPosition(
                                                        change.uptimeMillis,
                                                        change.position
                                                    )
                                                    val newOffset =
                                                        (animatedOffsetX.value + dragAmount).coerceIn(
                                                            -drawerWidthPx,
                                                            0f
                                                        )
                                                    coroutineScope.launch {
                                                        animatedOffsetX.snapTo(newOffset)
                                                    }
                                                    change.consume()
                                                }
                                            )
                                        }
                                )
                            }
                        }
                    }
                }

                "budget" -> {
                    BudgetScreen(
                        onBackClick = { currentSubScreen = "chat" },
                        viewModel = budgetViewModel,
                        eventViewModel = eventViewModel,
                        roomViewModel = roomViewModel
                    )
                }

                "guests" -> {
                    GuestsTab(
                        onBackClick = { currentSubScreen = "chat" },
                        guestViewModel = guestViewModel,
                        eventViewModel = eventViewModel,
                        roomViewModel = roomViewModel
                    )
                }

                "venues" -> {
                    VenueScreen(
                        onBackClick = { currentSubScreen = "chat" },
                        onVenueClick = { selectedVenueDetail = it },
                        onChatClick = { venue ->
                            chatMerchantId = venue.merchantId.ifBlank { "unknown_merchant" }
                            chatItemId = venue.id.ifBlank { "unknown_venue" }
                            chatItemType = "Venue"
                            currentSubScreen = "chat_screen"
                        },
                        eventViewModel = eventViewModel,
                        venueViewModel = venueViewModel,
                        roomViewModel = roomViewModel
                    )
                }

                "vendors" -> {
                    VendorsTab(
                        mainNavController = mainNavController ?: rememberNavController(),
                        onBottomBarVisibilityChange = {},
                        onBackClick = {
                            currentSubScreen = "chat"
                            selectedVendorCategory = null
                        },
                        onChatClick = { vendor ->
                            chatMerchantId = vendor.merchantId.ifBlank { "unknown_merchant" }
                            chatItemId = vendor.id.ifBlank { "unknown_vendor" }
                            chatItemType = "Vendor"
                            currentSubScreen = "chat_screen"
                        },
                        initialCategory = selectedVendorCategory,
                        vendorViewModel = vendorViewModel,
                        eventViewModel = eventViewModel,
                        roomViewModel = roomViewModel
                    )
                }

                "chat_screen" -> {
                    ChatScreen(
                        merchantId = chatMerchantId,
                        itemId = chatItemId,
                        itemType = chatItemType,
                        onBackClick = { currentSubScreen = "chat" },
                        venueViewModel = venueViewModel,
                        vendorViewModel = vendorViewModel
                    )
                }
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
                    onAiSearchClick = { query ->
                        selectedVenueDetail = null
                        currentSubScreen = "chat"
                        viewModel.sendMessage(query)
                    },
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
                    onAiSearchClick = { query ->
                        selectedVendorDetail = null
                        currentSubScreen = "chat"
                        viewModel.sendMessage(query)
                    },
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

        AnimatedVisibility(
            visible = toastData?.message != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 100.dp)
                .padding(horizontal = 16.dp)
                .zIndex(40f)
        ) {
            toastData?.let { data ->
                CustomToast(
                    message = data.message ?: "",
                    type = data.type,
                    leadingIcon = painterResource(id = R.drawable.ic_delete),
                    buttonText = if (recentlyDeletedSession != null) "Undo" else null,
                    onButtonClick = {
                        recentlyDeletedSession?.let { session ->
                            viewModel.restoreChatSession(session)
                            recentlyDeletedSession = null
                            toastData = null
                        }
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



@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    JasnifyTheme {
        AiChatScreen()
    }
}