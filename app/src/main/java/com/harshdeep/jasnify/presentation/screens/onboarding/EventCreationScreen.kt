package com.harshdeep.jasnify.presentation.screens.onboarding

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.models.EventData
import com.harshdeep.jasnify.data.models.SubEventItem
import com.harshdeep.jasnify.data.models.eventTypes
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSheet
import com.harshdeep.jasnify.presentation.components.buttons.*
import com.harshdeep.jasnify.presentation.components.chip.EventTypeChip
import com.harshdeep.jasnify.presentation.components.inputfield.BudgetInput
import com.harshdeep.jasnify.presentation.components.inputfield.CornerType
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.inputfield.TimeLineInput
import com.harshdeep.jasnify.presentation.components.others.*
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.onboarding.authentication.ToastData
import com.harshdeep.jasnify.presentation.util.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.EventCreationState
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
private val DisplayDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

// Formatter for saving date to Firebase (String format)
@RequiresApi(Build.VERSION_CODES.O)
private val PersistenceDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // e.g., "2025-10-18"


enum class NavigationDirection {
    FORWARD,
    BACKWARD
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventCreation(
    navController: NavController,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    SetStatusBarTheme(
        useDarkIcons = true,
        statusBarColor = BackgroundPrimary
    )


    var eventData by remember {
        mutableStateOf(
            // Initialize with one editable sub-event for multi-day flow
            EventData(isMultiDay = true, subEvents = listOf(
                SubEventItem(
                    id = UUID.randomUUID().toString(), isEditing = true
                )
            ))
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
    LaunchedEffect(toastData.message) {
        if (toastData.message != null && toastData.type != ToastType.SUCCESS) {
            delay(3000L)
            toastData = toastData.copy(message = null)
        }
    }

    // --- Observe Event State for feedback ---
    val eventState by eventViewModel.eventState.collectAsState()

    // Handle navigation/toast based on successful event saving
    LaunchedEffect(eventState) {
        when (eventState) {
            is EventCreationState.Success -> {
                val successMessage = (eventState as EventCreationState.Success).message
                toastData = ToastData(successMessage, ToastType.SUCCESS)

                delay(500L)

                navController.navigate(Screen.MainAppGraph.route) {
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


    // --- Keyboard/IME State for bottom padding ---
    val imePadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val density = LocalDensity.current
    val isKeyboardOpen = WindowInsets.ime.getBottom(density) > 0
    val bottomPadding = if (isKeyboardOpen) imePadding + 12.dp else 32.dp

    // --- Navigation Logic ---

    val onSkip: () -> Unit = {
        navigationDirection = NavigationDirection.FORWARD
        // Force the step change without validation
        if(currentStep == EventCreationStep.EVENT_TIMELINE) {
            currentStep = EventCreationStep.EVENT_BUDGET
        }
        if(currentStep == EventCreationStep.EVENT_DATE) {
            currentStep = EventCreationStep.EVENT_BUDGET
        }
    }

    val onNext: () -> Unit = {
        // Set direction to FORWARD
        navigationDirection = NavigationDirection.FORWARD
        when (currentStep) {
            EventCreationStep.EVENT_TYPE -> {
                if (eventData.selectedEventTypeId == null) {
                    toastData = ToastData("Please select an event type", ToastType.ERROR)
                } else {
                    currentStep = EventCreationStep.EVENT_NAME
                }
            }
            EventCreationStep.EVENT_NAME -> {
                if (eventData.eventName.isBlank()) {
                    toastData = ToastData("Please enter an event name", ToastType.ERROR)
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
                    toastData = ToastData("Please select a date for the event", ToastType.ERROR)
                }
            }
            EventCreationStep.EVENT_TIMELINE -> {
                // Multi-day validation: All sub-events must be 'saved' (not in editing mode)
                val allSaved = eventData.subEvents.all { !it.isEditing }
                if (allSaved && eventData.subEvents.isNotEmpty()) {
                    currentStep = EventCreationStep.EVENT_BUDGET
                } else {
                    toastData = ToastData("Please save all event timeline details.", ToastType.ERROR)
                }
            }
            EventCreationStep.EVENT_BUDGET -> {
                // CALL THE VIEWMODEL to save the data
                eventViewModel.saveEventData(eventData)
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

    // Determine back button visibility and progress bar step
    val showBackButton = currentStep != EventCreationStep.EVENT_TYPE
    val currentProgressStep = when (currentStep) {
        EventCreationStep.EVENT_DATE, EventCreationStep.EVENT_TIMELINE -> 4
        else -> currentStep.stepNumber
    }


    Scaffold(
        topBar = {
            CustomTopBar(
                onBackClick = if (showBackButton) onBack else null,
                title = currentStep.title
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundPrimary)
                    .padding(12.dp, 8.dp, 12.dp, bottomPadding)
            ) {
                if (currentStep == EventCreationStep.EVENT_TYPE) {
                    Text(
                        text = "Event Type can’t be changed later.",
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        style = JasnifyTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, top = 0.dp)
                    )
                }

                val buttonText = if (currentStep == EventCreationStep.EVENT_BUDGET) "Finish Event Creation" else "Continue"

                CustomTextButton(
                    onClick = onNext,
                    text = buttonText,
                    size = ButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    type = ButtonType.Primary,
                    shapeStyle = ButtonShapeStyle.Square,
                    enabled = eventState !is EventCreationState.Loading
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundPrimary)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StepperProgressBar(
                        currentStep = currentProgressStep,
                        totalSteps = EventCreationStep.Companion.totalSteps,
                    )

                    // --- Main Content Switcher ( for directional animation) ---
                    AnimatedContent(
                        targetState = stepState.value,
                        transitionSpec = {
                            val (_, direction) = targetState

                            val slideIn = slideInHorizontally(animationSpec = tween(300)) { fullWidth ->
                                when (direction) {
                                    NavigationDirection.FORWARD -> fullWidth // Slide in from right
                                    NavigationDirection.BACKWARD -> -fullWidth // Slide in from left
                                }
                            }
                            val slideOut = slideOutHorizontally(animationSpec = tween(300)) { fullWidth ->
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
                        val updateEventData: (EventData) -> Unit = { updatedData -> eventData = updatedData }

                        when (targetStep) {
                            EventCreationStep.EVENT_TYPE -> EventTypeContent(eventData, updateEventData)
                            EventCreationStep.EVENT_NAME -> EventNameContent(eventData, updateEventData)
                            EventCreationStep.EVENT_DAYS -> EventDaysContent(eventData, updateEventData)
                            EventCreationStep.EVENT_DATE -> EventSingleDayContent(eventData, updateEventData, onSkip)
                            EventCreationStep.EVENT_TIMELINE -> EventMultiDayContent(eventData, updateEventData, onSkip)
                            EventCreationStep.EVENT_BUDGET -> EventBudgetContent(eventData, updateEventData)
                        }
                    }
                }

                // --- CustomToast Display  ---
                AnimatedVisibility(
                    visible = toastData.message != null,
                    // Slides down from the top edge
                    enter = slideInVertically(initialOffsetY = { -it }),
                    // Slides up and off the top edge
                    exit = slideOutVertically(targetOffsetY = { -it }),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                ) {
                    CustomToast(
                        message = toastData.message ?: "",
                        type = toastData.type,
                        buttonText = null,
                        onButtonClick = null,
                    )
                }
            }
        }
    )
}

// ---------------------   Step Content Components -------------------------------------


// ---------------------   Event Type Content -------------------------------------


@Composable
fun EventTypeContent(
    eventData: EventData,
    updateEventData: (EventData) -> Unit
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
    eventData: EventData,
    updateEventData: (EventData) -> Unit
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
                cornerType = CornerType.MESSAGE
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
    eventData: EventData,
    updateEventData: (EventData) -> Unit
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
    eventData: EventData,
    updateEventData: (EventData) -> Unit,
    onSkip: () -> Unit
) {
    var isDatePickerVisible by remember { mutableStateOf(false) }

    // Convert the persistent String back to LocalDate for UI use
    val selectedDate = remember(eventData.singleDayDateString) {
        eventData.singleDayDateString?.let {
            try {
                LocalDate.parse(it, PersistenceDateFormatter)
            } catch (e: Exception) {
                null // Handle case where saved string is invalid
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
            text = "When is this event?",
            style = JasnifyTheme.typography.displayLarge
        )
        Spacer(Modifier.height(16.dp))
        // --- Input Field UI ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    shape = SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge, CornerLarge, CornerSmoothingDefault)
                )
                .clip(SquircleShape(CornerExtraSmall, CornerLarge, CornerLarge, CornerLarge, CornerSmoothingDefault)                )
                .background(SurfaceSecondary)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(5.dp))
            Text(
                text = displayDateText,
                style = JasnifyTheme.typography.labelXLarge.copy(
                    color = if (selectedDate == null) ContentSecondary.copy(alpha = 0.7f) else ContentPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = { isDatePickerVisible = true },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )

            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Date Picker",
                tint = ContentPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = { isDatePickerVisible = true })
            )
            Spacer(Modifier.width(5.dp))
        }
        Column {
            OrDivider()
            CustomTextButton(
                text = "Not decided yet",
                type = ButtonType.Secondary,
                size = ButtonSize.Medium,
                shapeStyle = ButtonShapeStyle.Square,
                onClick = onSkip,
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- Date Picker Bottom Sheet ---
        if (isDatePickerVisible) {
            DatePickerSheet(
                onDismiss = { isDatePickerVisible = false },
                onDateSelected = { date ->
                    // Convert LocalDate to String before updating EventData
                    val dateString = date.format(PersistenceDateFormatter)
                    updateEventData(eventData.copy(singleDayDateString = dateString)) // <-- Update the String property
                    isDatePickerVisible = false
                },
                // Preselect the current date if available
                initialDate = selectedDate ?: LocalDate.now()
            )
        }
    }
}


// ---------------------   Event Multi Day Content -------------------------------------


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventMultiDayContent(
    eventData: EventData,
    updateEventData: (EventData) -> Unit,
    onSkip: () -> Unit
) {
    // We use a mutableStateList derived from the EventData list for local, in-place manipulation
    val subEventsList = remember {
        mutableStateListOf<SubEventItem>().apply { addAll(eventData.subEvents) }
    }

    // Update EventData when the local list changes
    DisposableEffect(subEventsList.toList()) {
        updateEventData(eventData.copy(subEvents = subEventsList.toList()))
        onDispose {}
    }

    // Check if any item is currently unsaved and being edited
    val hasUnsavedEditingItem by remember {
        derivedStateOf {
            subEventsList.any { it.isEditing }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add event timeline",
            style = JasnifyTheme.typography.displayLarge
        )

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

            itemsIndexed(subEventsList, key = { _, item -> item.id }) { index, item ->
                TimeLineInput(
                    item = item,
                    onUpdate = { updatedItem ->
                        // Find and update the item in the list
                        val foundIndex = subEventsList.indexOfFirst { it.id == updatedItem.id }
                        if (foundIndex != -1) {
                            subEventsList[foundIndex] = updatedItem
                        }
                    },
                    onDelete = { itemToDelete ->
                        subEventsList.remove(itemToDelete)
                        // If the list is empty, re-initialize with one empty item for the flow
                        if (subEventsList.isEmpty()) {
                            subEventsList.add(SubEventItem(id = UUID.randomUUID().toString(), isEditing = true))
                        }
                    }
                )
            }

            item {
                val hasAnyExistingItem = eventData.subEvents.any { it.isExisting }

                if (!hasAnyExistingItem){
                    OrDivider()
                    CustomTextButton(
                        text = "Not decided yet",
                        type = ButtonType.Secondary,
                        size = ButtonSize.Medium,
                        shapeStyle = ButtonShapeStyle.Square,
                        onClick = onSkip,
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else {
                    Spacer(Modifier.width(16.dp))
                    CustomTextButton(
                        text = "Add another day",
                        type = ButtonType.Secondary,
                        size = ButtonSize.Medium,
                        shapeStyle = ButtonShapeStyle.Square,
                        onClick = {
                            subEventsList.add(
                                0, SubEventItem(
                                    id = UUID.randomUUID().toString(),
                                    isEditing = true,
                                    isExisting = false
                                )
                            )
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
    eventData: EventData,
    updateEventData: (EventData) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What is the expected budget?",
            style = JasnifyTheme.typography.displayLarge
        )

        Spacer(Modifier.height(4.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BudgetInput(
                value = eventData.budget,
                onValueChange = { updateEventData(eventData.copy(budget = it)) }
            )

            OrDivider()

            CustomTextButton(
                onClick = { updateEventData(eventData.copy(budget = "0")) },
                type = ButtonType.Secondary,
                size = ButtonSize.Medium,
                shapeStyle = ButtonShapeStyle.Square,
                text = "Not Decided yet",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


// ---------------------   Preview -------------------------------------


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EventCreationPreview() {
    JasnifyTheme {
        // Note: Previewing requires mocking the ViewModel dependencies
        // This is a minimal mock for UI preview purposes only
        EventCreation(navController = rememberNavController())
    }
}
