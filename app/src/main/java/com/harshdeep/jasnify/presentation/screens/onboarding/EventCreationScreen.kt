package com.harshdeep.jasnify.presentation.screens.onboarding

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.models.eventTypes
import com.harshdeep.jasnify.presentation.components.bottomdrawer.budget.CurrencyBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.DatePickerSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.event.EventTimeLineInfoSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.SelectableItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.getCurrencyCodes
import com.harshdeep.jasnify.presentation.components.buttons.*
import com.harshdeep.jasnify.presentation.components.chip.EventTypeChip
import com.harshdeep.jasnify.presentation.components.inputfield.BudgetInput
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.inputfield.TimeLineInput
import com.harshdeep.jasnify.presentation.components.others.*
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.EventCreationState
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.viewmodels.EventCreateUiState
import com.harshdeep.jasnify.presentation.viewmodels.SubEventItem
import kotlin.time.Duration.Companion.milliseconds

@RequiresApi(Build.VERSION_CODES.O)
private val DisplayDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

// Formatter for saving date to Firebase (String format)
@RequiresApi(Build.VERSION_CODES.O)
private val PersistenceDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // e.g., "2025-10-18"

enum class NavigationDirection {
    FORWARD,
    BACKWARD
}

enum class EventCreationStep(val title: String, val stepNumber: Int) {
    EVENT_TYPE("Event Type", 1),
    EVENT_NAME("Event Name", 2),
    EVENT_DAYS("Event Days", 3),
    EVENT_DATE("Event Date", 4),
    EVENT_TIMELINE("Event Timeline", 4),
    EVENT_BUDGET("Event Budget", 5);

    companion object {
        const val totalSteps = 5
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventCreation(
    navController: NavController,
    eventViewModel: EventViewModel = hiltViewModel(),
    authViewModel: com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel = hiltViewModel(),
    fromProfile: Boolean = false
) {
    SetStatusBarTheme(
        useDarkIcons = true,
        statusBarColor = Color.Transparent
    )

    var eventData by remember {
        mutableStateOf(
            EventCreateUiState(
                isMultiDay = true,
                subEvents = listOf(
                    SubEventItem(
                        id = UUID.randomUUID().toString(),
                        isEditing = false
                    )
                )
            )
        )
    }
    var currentStep by remember { mutableStateOf(EventCreationStep.EVENT_TYPE) }

    var navigationDirection by remember { mutableStateOf(NavigationDirection.FORWARD) }
    // Pair of (Step, Direction) as the targetState type
    val stepState = remember { mutableStateOf(currentStep to NavigationDirection.FORWARD) }

    // Update stepState whenever currentStep or navigationDirection changes
    LaunchedEffect(currentStep, navigationDirection) {
        stepState.value = currentStep to navigationDirection
    }

    // --- Toast State ---
    var toastData by remember { mutableStateOf(ToastData()) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(toastData.message) {
        if (toastData.message != null && toastData.type != ToastType.SUCCESS) {
            if (toastData.type == ToastType.ERROR) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (toastData.message?.contains("Please", ignoreCase = true) == true ||
                    toastData.message?.contains("enter", ignoreCase = true) == true ||
                    toastData.message?.contains("select", ignoreCase = true) == true
                ) {
                    delay(80.milliseconds)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            delay(3000L.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    // --- Bottom Sheet Visibility State ---
    var isTimelineInfoSheetVisible by remember { mutableStateOf(false) }
    var showTimelineDatePicker by remember { mutableStateOf(false) }
    var selectedTimelineItem by remember { mutableStateOf<SubEventItem?>(null) }
    var isSingleDayDatePickerVisible by remember { mutableStateOf(false) }
    var isCurrencySheetVisible by remember { mutableStateOf(false) }

    var sheetMotionProgress by remember { mutableFloatStateOf(0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            isTimelineInfoSheetVisible || showTimelineDatePicker || isSingleDayDatePickerVisible || isCurrencySheetVisible
        }
    }

    val targetScale = if (isAnyBottomSheetOpen) {
        0.92f + (0.08f * sheetMotionProgress)
    } else {
        1.0f
    }

    val backdropScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadius by animateDpAsState(
        targetValue = if (isAnyBottomSheetOpen) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    // --- Observe Event State for feedback ---
    val eventState by eventViewModel.eventState.collectAsState()

    // Handle navigation/toast based on successful event saving
    LaunchedEffect(eventState) {
        when (eventState) {
            is EventCreationState.Success -> {
                val successMessage = (eventState as EventCreationState.Success).message
                toastData = ToastData(successMessage, ToastType.SUCCESS)

                authViewModel.setCompletedOnboarding(true)
                delay(500L.milliseconds)

                navController.navigate(Screen.MainAppScreen.route) {
                    popUpTo(Screen.OnboardingGraph.route) {
                        inclusive = true
                    }
                }

                eventViewModel.resetEventState()
            }
            is EventCreationState.Error -> {
                val errorMessage = (eventState as EventCreationState.Error).message
                toastData = ToastData(errorMessage, ToastType.ERROR)
                eventViewModel.resetEventState()
            }
            is EventCreationState.Loading -> {
                // Optional: Show loading indicator if needed
            }
            EventCreationState.Idle -> {
                // Do nothing
            }
        }
    }

    // Determine if the timeline is fully valid and saved
    val isTimelineValid = remember(eventData.subEvents) {
        eventData.subEvents.all { !it.isEditing && it.name.isNotBlank() && it.dateString.isNotBlank() }
    }

    // Dynamic state control to enable or disable the main Action button
    val isContinueEnabled = remember(currentStep, eventState, isTimelineValid) {
        val isNotLoading = eventState !is EventCreationState.Loading
        val isStepValid = when (currentStep) {
            EventCreationStep.EVENT_TIMELINE -> isTimelineValid
            else -> true
        }
        isNotLoading && isStepValid
    }

    // --- Navigation Logic ---

    val onSkip: () -> Unit = {
        navigationDirection = NavigationDirection.FORWARD
        // Clear any existing toast message when skipping
        toastData = toastData.copy(message = null)

        when (currentStep) {
            EventCreationStep.EVENT_TIMELINE, EventCreationStep.EVENT_DATE -> {
                currentStep = EventCreationStep.EVENT_BUDGET
            }
            EventCreationStep.EVENT_BUDGET -> {
                val updatedData = eventData.copy(budget = "")
                eventData = updatedData
                eventViewModel.saveEventData(updatedData)
            }
            else -> {}
        }
    }

    val onNext: () -> Unit = {
        // Set direction to FORWARD
        navigationDirection = NavigationDirection.FORWARD
        when (currentStep) {
            EventCreationStep.EVENT_TYPE -> {
                if (eventData.selectedEventTypeId == null) {
                    toastData = ToastData("Please select an event type!", ToastType.ERROR)
                } else {
                    currentStep = EventCreationStep.EVENT_NAME
                }
            }
            EventCreationStep.EVENT_NAME -> {
                if (eventData.eventName.isBlank()) {
                    toastData = ToastData("Please enter the event name!", ToastType.ERROR)
                } else {
                    currentStep = EventCreationStep.EVENT_DAYS
                }
            }
            EventCreationStep.EVENT_DAYS -> {
                when (eventData.isMultiDay) {
                    true -> currentStep = EventCreationStep.EVENT_TIMELINE
                    false -> currentStep = EventCreationStep.EVENT_DATE
                    null -> toastData = ToastData("Please select days", ToastType.ERROR)
                }
            }
            EventCreationStep.EVENT_DATE -> {
                // CHECK the new singleDayDateString property
                if (eventData.singleDayDateString != null) {
                    currentStep = EventCreationStep.EVENT_BUDGET
                } else {
                    toastData = ToastData("Please select a date!", ToastType.ERROR)
                }
            }
            EventCreationStep.EVENT_TIMELINE -> {
                currentStep = EventCreationStep.EVENT_BUDGET
            }
            EventCreationStep.EVENT_BUDGET -> {
                if (eventData.budget.isBlank()) {
                    toastData = ToastData("Please enter your budget!", ToastType.ERROR)
                } else {
                    // CALL THE VIEWMODEL to save the data
                    eventViewModel.saveEventData(eventData)
                }
            }
        }
    }

    val onBack: () -> Unit = {
        // Set direction to BACKWARD
        navigationDirection = NavigationDirection.BACKWARD
        currentStep = when (currentStep) {
            EventCreationStep.EVENT_TYPE -> {
                navController.popBackStack() // Exit the flow
                EventCreationStep.EVENT_TYPE
            }
            EventCreationStep.EVENT_NAME -> EventCreationStep.EVENT_TYPE
            EventCreationStep.EVENT_DAYS -> EventCreationStep.EVENT_NAME
            EventCreationStep.EVENT_DATE, EventCreationStep.EVENT_TIMELINE -> EventCreationStep.EVENT_DAYS
            EventCreationStep.EVENT_BUDGET -> {
                if (eventData.isMultiDay == true) EventCreationStep.EVENT_TIMELINE else EventCreationStep.EVENT_DATE
            }
        }
    }

    // SYSTEM BACK BUTTON HANDLER
    BackHandler(enabled = currentStep != EventCreationStep.EVENT_TYPE) {
        onBack()
    }

    // Determine back button visibility and progress bar step
    val showBackButton = currentStep != EventCreationStep.EVENT_TYPE || fromProfile
    val currentProgressStep = when (currentStep) {
        EventCreationStep.EVENT_DATE, EventCreationStep.EVENT_TIMELINE -> 4
        else -> currentStep.stepNumber
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Background for scaling effect
    ) {
        Scaffold(
            topBar = {
                Surface(
                    color = BackgroundPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    CustomTopBar(
                        onBackClick = if (showBackButton) onBack else null,
                        title = currentStep.title,
                        isLargeTitle = true
                    )
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPrimary)
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(12.dp)
                ) {
                    val buttonText =
                        if (currentStep == EventCreationStep.EVENT_BUDGET) "Finish Event Creation" else "Continue"

                    CustomTextButton(
                        onClick = onNext,
                        text = buttonText,
                        size = ButtonSize.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        type = ButtonType.Primary,
                        shapeStyle = ButtonShapeStyle.Square,
                        enabled = isContinueEnabled,
                        isLoading = eventState is EventCreationState.Loading
                    )

                    if (currentStep == EventCreationStep.EVENT_TYPE) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Event Type can’t be changed later.",
                            textAlign = TextAlign.Center,
                            color = ContentSecondary,
                            style = JasnifyTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }
                }
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundPrimary)
                        .padding(paddingValues)
                        .padding(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StepperProgressBar(
                            currentStep = currentProgressStep,
                            totalSteps = EventCreationStep.totalSteps,
                        )

                        // --- Main Content Switcher ( for directional animation) ---
                        AnimatedContent(
                            targetState = stepState.value,
                            transitionSpec = {
                                val (_, direction) = targetState

                                val slideIn =
                                    slideInHorizontally(animationSpec = tween(300)) { fullWidth ->
                                        when (direction) {
                                            NavigationDirection.FORWARD -> fullWidth // Slide in from right
                                            NavigationDirection.BACKWARD -> -fullWidth // Slide in from left
                                        }
                                    }
                                val slideOut =
                                    slideOutHorizontally(animationSpec = tween(300)) { fullWidth ->
                                        when (direction) {
                                            NavigationDirection.FORWARD -> -fullWidth // Slide out to left
                                            NavigationDirection.BACKWARD -> fullWidth // Slide out to right
                                        }
                                    }

                                (slideIn + fadeIn(animationSpec = tween(300)))
                                    .togetherWith(slideOut + fadeOut(animationSpec = tween(300)))
                                    .using(SizeTransform(clip = false))
                            }, label = "Step Transition"
                        ) { (targetStep, _) ->
                            val updateEventData: (EventCreateUiState) -> Unit =
                                { updatedData -> eventData = updatedData }

                            when (targetStep) {
                                EventCreationStep.EVENT_TYPE -> EventTypeContent(
                                    eventData,
                                    updateEventData
                                )

                                EventCreationStep.EVENT_NAME -> EventNameContent(
                                    eventData,
                                    updateEventData
                                )

                                EventCreationStep.EVENT_DAYS -> EventDaysContent(
                                    eventData,
                                    updateEventData
                                )

                                EventCreationStep.EVENT_DATE -> EventSingleDayContent(
                                    eventData,
                                    updateEventData,
                                    onSkip,
                                    onShowDatePicker = { isSingleDayDatePickerVisible = true },
                                    isLoading = eventState is EventCreationState.Loading
                                )

                                EventCreationStep.EVENT_TIMELINE -> EventMultiDayContent(
                                    eventData,
                                    updateEventData,
                                    onSkip,
                                    onInfoClick = { isTimelineInfoSheetVisible = true },
                                    onShowDatePicker = { item ->
                                        selectedTimelineItem = item
                                        showTimelineDatePicker = true
                                    },
                                    isLoading = eventState is EventCreationState.Loading
                                )

                                EventCreationStep.EVENT_BUDGET -> EventBudgetContent(
                                    eventData,
                                    updateEventData,
                                    onSkip,
                                    onCurrencyClick = { isCurrencySheetVisible = true },
                                    isLoading = eventState is EventCreationState.Loading
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .graphicsLayer {
                    scaleX = backdropScale
                    scaleY = backdropScale
                }
                .clip(RoundedCornerShape(backdropCornerRadius))
        )

        // --- CustomToast Display  ---
        AnimatedVisibility(
            visible = toastData.message != null,
            enter = slideInVertically(initialOffsetY = { -it - 500 }),
            exit = slideOutVertically(targetOffsetY = { -it - 500 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .zIndex(99f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            CustomToast(
                message = toastData.message ?: "",
                type = toastData.type,
                buttonText = null,
                onButtonClick = null,
            )
        }

        // --- Event Timeline Info Bottom Sheet ---
        if (isTimelineInfoSheetVisible) {
            EventTimeLineInfoSheet(
                onDismiss = { isTimelineInfoSheetVisible = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        DatePickerSheet(
            isVisible = isSingleDayDatePickerVisible,
            onDismiss = { isSingleDayDatePickerVisible = false },
            onDateSelected = { date ->
                val dateString = date.format(PersistenceDateFormatter)
                val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                eventData = eventData.copy(
                    singleDayDateString = dateString,
                    singleDayDate = millis
                )
                isSingleDayDatePickerVisible = false
            },
            initialDate = eventData.singleDayDateString?.let {
                try { LocalDate.parse(it, PersistenceDateFormatter) } catch (e: Exception) { LocalDate.now() }
            } ?: LocalDate.now(),
            onProgress = { sheetMotionProgress = it }
        )

        DatePickerSheet(
            isVisible = showTimelineDatePicker,
            onDismiss = { showTimelineDatePicker = false },
            onDateSelected = { date ->
                selectedTimelineItem?.let { item ->
                    val formattedDate = date.format(DisplayDateFormatter)
                    val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    val newList = eventData.subEvents.map {
                        if (it.id == item.id) it.copy(date = timestamp, dateString = formattedDate) else it
                    }
                    eventData = eventData.copy(subEvents = newList)
                }
                showTimelineDatePicker = false
            },
            initialDate = selectedTimelineItem?.date?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            } ?: LocalDate.now(),
            onProgress = { sheetMotionProgress = it }
        )

        if (isCurrencySheetVisible) {
            val currentCurrencyCode = eventData.budget.takeWhile { !it.isDigit() && it != '.' }.ifEmpty { "INR" }
            val currentCurrency = remember(currentCurrencyCode) {
                getCurrencyCodes().find { it.code == currentCurrencyCode } ?: SelectableItem(currentCurrencyCode, "", "")
            }
            CurrencyBottomSheet(
                initialSelection = currentCurrency,
                onItemSelected = { selectedItem ->
                    val numericValue = eventData.budget.removePrefix(currentCurrencyCode)
                    eventData = eventData.copy(budget = selectedItem.code + numericValue)
                    isCurrencySheetVisible = false
                },
                onDismiss = { isCurrencySheetVisible = false },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}


// ---------------------   Step Content Components -------------------------------------

// ---------------------   Event Type Content -------------------------------------
@Composable
fun EventTypeContent(
    eventData: EventCreateUiState,
    updateEventData: (EventCreateUiState) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What type of event you’re planning?",
            style = JasnifyTheme.typography.displayLarge
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                eventTypes.forEach { event ->
                    EventTypeChip(
                        iconPainter = painterResource(id = event.iconResId),
                        label = event.label,
                        isSelected = eventData.selectedEventTypeId == event.id,
                        onClick = {
                            val newId = if (eventData.selectedEventTypeId == event.id) null else event.id
                            updateEventData(eventData.copy(selectedEventTypeId = newId))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ---------------------   Event Name Content -------------------------------------
@Composable
fun EventNameContent(
    eventData: EventCreateUiState,
    updateEventData: (EventCreateUiState) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What should we call this event?",
            style = JasnifyTheme.typography.displayLarge
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryInput(
                placeholder = "Enter event name of your choice",
                keyboardType = KeyboardType.Text,
                value = eventData.eventName,
                onValueChange = { updateEventData(eventData.copy(eventName = it)) },
                trailingIconEnabled = true,
                shape = SquircleShape(CornerExtraSmall,CornerLarge,CornerLarge,CornerLarge,CornerSmoothingDefault)
            )

            Text(
                text = "eg. Romeo & Juliet’s Wedding, Jack’s Birthday, etc",
                style = JasnifyTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Start),
                color = ContentSecondary
            )
        }
    }
}

// ---------------------   Event Days Content -------------------------------------
@Composable
fun EventDaysContent(
    eventData: EventCreateUiState,
    updateEventData: (EventCreateUiState) -> Unit
) {
    val options = listOf(
        Pair("Yes, Multiple Days", "The event is spread across multiple days."),
        Pair("No, Single Day", "The event is happening on one specific day."),
    )
    val selectedIndex = when(eventData.isMultiDay){
        true -> 0
        false -> 1
        else -> 0
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Is this event divided into multiple days?",
            style = JasnifyTheme.typography.displayLarge
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
        ) {
            options.forEachIndexed { index, option ->
                val isMultiDayOption = index == 0
                OptionSelector(
                    label = option.first,
                    bodyText = option.second,
                    isSelected = selectedIndex == index,
                    onClick = {
                        updateEventData(eventData.copy(isMultiDay = isMultiDayOption))
                    }
                )
            }
        }
    }
}

// ---------------------   Event Single Day Content -------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSingleDayContent(
    eventData: EventCreateUiState,
    updateEventData: (EventCreateUiState) -> Unit,
    onSkip: () -> Unit,
    onShowDatePicker: () -> Unit,
    isLoading: Boolean = false
) {
    val selectedDate = remember(eventData.singleDayDateString) {
        eventData.singleDayDateString?.let {
            try {
                LocalDate.parse(it, PersistenceDateFormatter)
            } catch (e: Exception) {
                null
            }
        }
    }

    val displayDateText = remember(selectedDate) {
        selectedDate?.format(DisplayDateFormatter) ?: "Select a date"
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "When is this happening?",
            style = JasnifyTheme.typography.displayLarge
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    shape = SquircleShape(
                        CornerExtraSmall,
                        CornerLarge,
                        CornerLarge,
                        CornerLarge,
                        CornerSmoothingDefault
                    )
                )
                .clip(
                    SquircleShape(
                        CornerExtraSmall,
                        CornerLarge,
                        CornerLarge,
                        CornerLarge,
                        CornerSmoothingDefault
                    )
                )
                .background(SurfaceSecondary)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayDateText,
                style = JasnifyTheme.typography.labelXLarge.copy(
                    color = if (selectedDate == null) ContentSecondary.copy(alpha = 0.7f) else ContentPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = onShowDatePicker,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )

            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = "Date Picker",
                tint = ContentSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onShowDatePicker)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Not yet decided?",
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentSecondary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Skip for later",
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary,
                modifier = Modifier.clickable(
                    enabled = !isLoading,
                    onClick = onSkip
                )
            )
        }
    }
}

// ---------------------   Event Multi Day Content -------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventMultiDayContent(
    eventData: EventCreateUiState,
    updateEventData: (EventCreateUiState) -> Unit,
    onSkip: () -> Unit,
    onInfoClick: () -> Unit,
    onShowDatePicker: (SubEventItem) -> Unit,
    isLoading: Boolean = false
) {
    val hasUnsavedEditingItem by remember(eventData.subEvents) {
        derivedStateOf {
            eventData.subEvents.any { it.isEditing }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add event timeline",
                style = JasnifyTheme.typography.displayLarge
            )
            CustomIconButton(
                onClick = onInfoClick,
                icon = painterResource(R.drawable.ic_info),
                containerColor = SurfacePrimary,
                contentColor = ContentPrimary,
                size = ButtonSize.Small
            )
        }


        Text(
            text = "It helps you organize your event schedule across multiple days.",
            style = JasnifyTheme.typography.bodyLarge,
            color = ContentSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(eventData.subEvents, key = { _, item -> item.id }) { index, item ->
                TimeLineInput(
                    item = item,
                    onUpdate = { updatedItem ->
                        val newList = eventData.subEvents.map {
                            if (it.id == updatedItem.id) updatedItem else it
                        }
                        updateEventData(eventData.copy(subEvents = newList))
                    },
                    onDelete = { itemToDelete ->
                        val newList = eventData.subEvents.filter { it.id != itemToDelete.id }
                        val finalNewList = if (newList.isEmpty()) {
                            listOf(SubEventItem(id = UUID.randomUUID().toString(), isEditing = true))
                        } else {
                            newList
                        }
                        updateEventData(eventData.copy(subEvents = finalNewList))
                    },
                    onShowDatePicker = onShowDatePicker
                )
            }

            item {
                val hasAnyExistingItem = eventData.subEvents.any { it.isExisting }

                if (!hasAnyExistingItem){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Not yet decided?",
                            style = JasnifyTheme.typography.labelXLarge,
                            color = ContentSecondary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Skip for later",
                            style = JasnifyTheme.typography.labelXLarge,
                            color = ContentPrimary,
                            modifier = Modifier.clickable(
                                enabled = !isLoading,
                                onClick = onSkip
                            )
                        )
                    }
                }
                else {
                    Spacer(Modifier.width(16.dp))
                    CustomTextButton(
                        text = "Add another day",
                        type = ButtonType.Secondary,
                        size = ButtonSize.Medium,
                        shapeStyle = ButtonShapeStyle.Square,
                        onClick = {
                            val newList = eventData.subEvents.toMutableList()
                            newList.add(0, SubEventItem(
                                id = UUID.randomUUID().toString(),
                                isEditing = true,
                                isExisting = false
                            ))
                            updateEventData(eventData.copy(subEvents = newList))
                        },
                        enabled = !hasUnsavedEditingItem,
                        leadingIcon = painterResource(R.drawable.ic_plus),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

// ---------------------   Event Budget Content -------------------------------------
@Composable
fun EventBudgetContent(
    eventData: EventCreateUiState,
    updateEventData: (EventCreateUiState) -> Unit,
    onSkip: () -> Unit,
    onCurrencyClick: () -> Unit,
    isLoading: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "What is the estimate budget?",
                style = JasnifyTheme.typography.displayLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "This figure will be used to manage your budget.",
                style = JasnifyTheme.typography.bodyLarge,
                color = ContentSecondary
            )
        }
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            BudgetInput(
                value = eventData.budget,
                onValueChange = { updateEventData(eventData.copy(budget = it)) },
                onCurrencyClick = onCurrencyClick
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Not yet decided?",
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentSecondary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Skip for later",
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentPrimary,
                    modifier = Modifier.clickable(
                        enabled = !isLoading,
                        onClick = onSkip
                    )
                )
            }
        }
    }
}

// ---------------------   Preview -------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EventCreationPreview() {
    JasnifyTheme {
        EventCreation(navController = rememberNavController())
    }
}