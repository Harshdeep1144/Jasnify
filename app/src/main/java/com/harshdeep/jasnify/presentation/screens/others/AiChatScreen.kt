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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddExpenseBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.GuestDetailsBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.ChecklistCard
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.cards.GuestCard
import com.harshdeep.jasnify.presentation.components.carousels.VendorCarousel
import com.harshdeep.jasnify.presentation.components.carousels.VenueCarousel
import com.harshdeep.jasnify.presentation.components.inputfield.AiChatInput
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.screens.main.tabs.checklist.ChecklistDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorDetailScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueDetailScreen
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.CateringViewModel
import com.harshdeep.jasnify.presentation.viewmodels.ChatSession
import com.harshdeep.jasnify.presentation.viewmodels.ChecklistViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.GenerativeViewModel
import com.harshdeep.jasnify.presentation.viewmodels.GuestViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmall
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.TopGradientBrushLightTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

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
    var showBudgetSummary: Boolean = false,
    @get:PropertyName("feedback")
    @set:PropertyName("feedback")
    var feedback: Int = 0 // 0 = neutral, 1 = liked, -1 = disliked
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
            tts?.stop()
            tts?.shutdown()
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

    val displayedMessages = remember(
        messages,
        isVoiceMode,
        voiceModeStartTime,
        streamedDisplayMessage,
        activeSpeakingMessageId
    ) {
        if (isVoiceMode) {
            val voiceSessionMessages = messages.filter { it.timestamp >= voiceModeStartTime }
            if (activeSpeakingMessageId != null && streamedDisplayMessage != null) {
                voiceSessionMessages.map { msg ->
                    if (msg.id == activeSpeakingMessageId) streamedDisplayMessage!! else msg
                }
            } else {
                voiceSessionMessages
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
                                onFeedbackClick = { feedbackType ->
                                    viewModel.toggleMessageFeedback(message.id, feedbackType)
                                },
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
                hasSpokenFirstMessage = hasSpokenFirstMessage,
                isMicMuted = isMicMuted,
                isAiSpeaking = isAiSpeaking,
                isGenerating = isGenerating,
                audioRms = audioRms,
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
                        enableVoiceMode()
                    } else {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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

        if (isDrawerOpen) {
            val drawerWidthDp = 320.dp
            val drawerWidthPx = with(density) { drawerWidthDp.toPx() }
            val animatedOffsetX = remember { Animatable(-drawerWidthPx) }

            LaunchedEffect(Unit) {
                animatedOffsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }

            fun closeDrawerSmoothly() {
                coroutineScope.launch {
                    animatedOffsetX.animateTo(
                        targetValue = -drawerWidthPx,
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                    )
                    isDrawerOpen = false
                }
            }

            val currentFraction = (1f - (abs(animatedOffsetX.value) / drawerWidthPx)).coerceIn(0f, 1f)

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
                        .offset { IntOffset(x = animatedOffsetX.value.roundToInt(), y = 0) }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = { velocityTracker.resetTracking() },
                                onDragEnd = {
                                    val velocity = velocityTracker.calculateVelocity().x
                                    val shouldDismiss = animatedOffsetX.value < -(drawerWidthPx * 0.35f) || velocity < -1000f

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
                                    velocityTracker.addPosition(change.uptimeMillis, change.position)
                                    val newOffset = (animatedOffsetX.value + dragAmount).coerceIn(-drawerWidthPx, 0f)
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
                    iconColor = ContentInvPrimary,
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

@Composable
fun ChatSidebarDrawer(
    sessions: List<ChatSession>,
    currentChatId: String?,
    onSelectChat: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onDeleteChat: (ChatSession) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    var wasFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            wasFocused = false
            delay(100)
            searchFocusRequester.requestFocus()
        }
    }

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
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp),
        color = SurfacePrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_app),
                    contentDescription = "App Logo",
                    tint = ContentPrimary,
                    modifier = Modifier.height(28.dp)
                )
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = isSearchActive,
                transitionSpec = {
                    if (targetState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "NewChatSearchTransition",
                modifier = Modifier.padding(horizontal = 12.dp)
            ) { active ->
                if (active) {
                    CustomSearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search history...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(searchFocusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    wasFocused = true
                                } else if (wasFocused) {
                                    isSearchActive = false
                                    searchQuery = ""
                                    wasFocused = false
                                }
                            },
                        onActiveChange = {}
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextButton(
                            onClick = onNewChatClick,
                            text = "New Chat",
                            leadingIcon = painterResource(R.drawable.ic_plus),
                            type = ButtonType.Secondary,
                            modifier = Modifier.weight(1f)
                        )

                        TopBarIconButton(
                            onClick = { isSearchActive = true },
                            icon = TopIcon.Predefined.SEARCH,
                            size = 56.dp,
                            iconSize = 24.dp,
                            borderColor = ContentPrimary,
                            backgroundStyle = ButtonBackground.OPAQUE
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                groupedSessions.forEach { (dateHeader, sessionList) ->
                    item(key = dateHeader) {
                        Text(
                            text = dateHeader,
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentSecondary,
                            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                        )
                    }

                    items(sessionList, key = { it.id }) { session ->
                        SwipeToDismissChatSessionItem(
                            session = session,
                            isSelected = session.id == currentChatId,
                            onSelectChat = { onSelectChat(session.id) },
                            onDeleteChat = { onDeleteChat(session) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeToDismissChatSessionItem(
    session: ChatSession,
    isSelected: Boolean,
    onSelectChat: () -> Unit,
    onDeleteChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var itemWidthPx by remember { mutableFloatStateOf(1f) }
    val velocityTracker = remember { VelocityTracker() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(CornerMedium))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFE53935))
                .padding(start = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete Chat",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = offsetX.value.roundToInt(), y = 0) }
                .background(if (isSelected) SurfaceBrandSecondary else SurfacePrimary)
                .pointerInput(session.id) {
                    itemWidthPx = size.width.toFloat()
                    detectHorizontalDragGestures(
                        onDragStart = { velocityTracker.resetTracking() },
                        onDragEnd = {
                            val velocity = velocityTracker.calculateVelocity().x
                            val triggerDistance = itemWidthPx * 0.55f
                            val shouldDismiss = offsetX.value > triggerDistance || velocity > 1200f

                            coroutineScope.launch {
                                if (shouldDismiss) {
                                    offsetX.animateTo(
                                        targetValue = itemWidthPx,
                                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                                    )
                                    onDeleteChat()
                                } else {
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetX.animateTo(0f)
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                            val newOffset = (offsetX.value + dragAmount).coerceIn(0f, itemWidthPx)
                            coroutineScope.launch {
                                offsetX.snapTo(newOffset)
                            }
                            change.consume()
                        }
                    )
                }
                .clickable { onSelectChat() }
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = session.title,
                style = JasnifyTheme.typography.labelXLarge,
                color = if (isSelected) ContentBrandDark else ContentPrimary,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun GeneratingIndicator() {
    Row(
        modifier = Modifier.padding(vertical = 20.dp, horizontal = 24.dp),
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
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .clip(
                    shape = SquircleShape(
                        topStart = CornerExtraLarge,
                        topEnd = CornerExtraLarge,
                        bottomStart = CornerExtraLarge,
                        bottomEnd = CornerSmall,
                        cornerSmoothing = CornerSmoothingDefault
                    )
                )
                .background(SurfaceBrandSecondary)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .widthIn(max = 280.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = message.text,
                color = ContentPrimary,
                style = JasnifyTheme.typography.labelLarge
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
    onFeedbackClick: (Int) -> Unit = {},
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

        val isLiked = message.feedback == 1
        val isDisliked = message.feedback == -1

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onFeedbackClick(1) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = if (isLiked) painterResource(R.drawable.ic_thumbs_up_filled) else painterResource(R.drawable.ic_thumbs_up),
                    contentDescription = "Helpful",
                    tint = ContentSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { onFeedbackClick(-1) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = if (isDisliked) painterResource(R.drawable.ic_thumbs_down_filled) else painterResource(R.drawable.ic_thumbs_down),
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
    val baseStyle = JasnifyTheme.typography.labelLarge

    val annotatedString = remember(text, baseStyle) {
        buildAnnotatedString {
            val pattern = Regex("""\*\*(.*?)\*\*""")
            var lastIndex = 0

            pattern.findAll(text).forEach { matchResult ->
                val range = matchResult.range

                if (range.first > lastIndex) {
                    val normalText = text.substring(lastIndex, range.first)
                    withStyle(
                        style = SpanStyle(
                            fontWeight = baseStyle.fontWeight ?: FontWeight.Normal,
                            color = ContentPrimary
                        )
                    ) {
                        append(normalText)
                    }
                }

                val boldContent = matchResult.groupValues[1]
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E2022)
                    )
                ) {
                    append(boldContent)
                }

                lastIndex = range.last + 1
            }

            if (lastIndex < text.length) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = baseStyle.fontWeight ?: FontWeight.Normal,
                        color = ContentPrimary
                    )
                ) {
                    append(text.substring(lastIndex))
                }
            }
        }
    }

    Text(
        text = annotatedString,
        style = baseStyle,
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