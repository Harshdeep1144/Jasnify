package com.harshdeep.jasnify.presentation.screens.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.models.SubEventItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSlider
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.EventTimeLineInfoSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.InfoCard
import com.harshdeep.jasnify.presentation.components.cards.InfoCardNature
import com.harshdeep.jasnify.presentation.components.inputfield.EventNameInput
import com.harshdeep.jasnify.presentation.components.inputfield.EventNameInputItem
import com.harshdeep.jasnify.presentation.components.inputfield.PrimaryInput
import com.harshdeep.jasnify.presentation.components.inputfield.TimeLineInput
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.OptionSelector
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfaceInvPrimary
import com.harshdeep.jasnify.theme.SurfaceInvSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds
import com.harshdeep.jasnify.presentation.components.others.ToastData
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.data.models.eventTypes
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailsScreen(
    onBackClick: () -> Unit,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activeEvent by eventViewModel.activeEvent.collectAsState()

    LaunchedEffect(Unit) {
        eventViewModel.fetchUserEvents()
    }

    var eventId by remember { mutableStateOf("...") }
    var eventType by remember { mutableStateOf("...") }

    // Core dynamic values driven by state
    var timelineType by remember { mutableStateOf("Multi-day") }
    var primaryEventName by remember { mutableStateOf("") }
    var singleDaySelectedDate by remember { mutableStateOf<String?>(null) }

    // Bottom Sheet Control States
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- Timeline Info Sheet State ---
    var isTimelineInfoSheetVisible by remember { mutableStateOf(false) }

    // Dedicated Custom Date Picker Sheet States (Triggers directly if no timelines available)
    var showDatePickerSheet by remember { mutableStateOf(false) }
    var datePickerInitialDate by remember { mutableStateOf(LocalDate.now()) }
    var onDateSelectedCallback by remember { mutableStateOf<((LocalDate) -> Unit)?>(null) }

    // Secondary navigation steps inside bottom sheet
    var bottomSheetStep by remember { mutableIntStateOf(0) }
    var tempTimelineType by remember { mutableStateOf(timelineType) }
    var pickDateSegmentSelected by remember { mutableStateOf(true) } // true: "Pick a date", false: "Not yet decided"
    var tempSelectedDateString by remember { mutableStateOf<String?>(null) }

    // Tab-isolated draft selections preserving saved versus custom picked dates
    var draftSavedDateString by remember { mutableStateOf<String?>(null) }
    var draftCustomDateString by remember { mutableStateOf<String?>(null) }

    // Tracks if calendar tab sheet was opened directly via the Event Date widget
    var isDirectDateEdit by remember { mutableStateOf(false) }

    var pickerActiveTab by remember { mutableIntStateOf(0) } // 0: Saved Timelines, 1: Custom Date

    // Primary Event Name State
    var isEditingEventName by remember { mutableStateOf(false) }
    var isExistingEventName by remember { mutableStateOf(true) }

    val timelineItems = remember {
        mutableStateListOf<SubEventItem>()
    }

    LaunchedEffect(activeEvent) {
        activeEvent?.let { event ->
            eventId = event.id.take(8).uppercase()
            primaryEventName = event.name
            timelineType = if (event.isMultiDay) "Multi-day" else "Single-day"
            eventType = eventTypes.find { it.id == event.typeId }?.label ?: "Others"

            val uiSubEvents = event.subEvents.map { subEvent ->
                val dateStr = subEvent.date?.let {
                    formatToOrdinalDate(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate())
                } ?: ""
                SubEventItem(
                    id = subEvent.id,
                    name = subEvent.name,
                    date = subEvent.date,
                    dateString = dateStr,
                    isExisting = true,
                    isEditing = false,
                    isCompleted = subEvent.isCompleted
                )
            }

            timelineItems.clear()
            timelineItems.addAll(uiSubEvents)
            val parsed = timelineItems.associate { it.id to parseFormattedDate(it.dateString) }
            timelineItems.sortBy { parsed[it.id] }

            if (!event.isMultiDay && event.date != null) {
                singleDaySelectedDate = formatToOrdinalDate(Instant.ofEpochMilli(event.date).atZone(ZoneId.systemDefault()).toLocalDate())
            }
        }
    }

    // --- Toast State ---
    var toastData by remember { mutableStateOf(ToastData()) }
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(2000L.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    // Check if any item is currently unsaved and being edited to avoid duplicate blank inserts
    val hasUnsavedEditingItem by remember {
        derivedStateOf {
            timelineItems.any { it.isEditing && !it.isExisting }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BackgroundSecondary,
            topBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .statusBarsPadding()
                ) {
                    CustomTopBar(
                        title = "Event Details",
                        onBackClick = onBackClick,
                        buttonStyle = ButtonBackground.TRANSLUCENT,
                        translucentAlpha = 0.5f,
                        isLargeTitle = true
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing to optimize list placement animations
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp, 12.dp, 12.dp, 0.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Event Type Card
                            EventInfoHalfCard(
                                title = "Event Type :",
                                value = eventType,
                                icon = painterResource(id = R.drawable.ic_info),
                                modifier = Modifier.weight(1f),
                                onClick = { }
                            )

                            // Event ID Card
                            EventInfoHalfCard(
                                title = "Event ID :",
                                value = eventId,
                                icon = painterResource(id = R.drawable.ic_copy),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Event ID", eventId)
                                    clipboard.setPrimaryClip(clip)

                                    toastData = ToastData("Copied to clipboard!", ToastType.SUCCESS)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Joined Card Group: Primary Event Name, Event Timeline Type, and Event Date
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val eventNameInputItem = remember(primaryEventName, isEditingEventName, isExistingEventName) {
                                EventNameInputItem(
                                    id = eventId,
                                    name = primaryEventName,
                                    isEditing = isEditingEventName,
                                    isExisting = isExistingEventName
                                )
                            }

                            // Primary Event Name Input Card
                            EventNameInput(
                                item = eventNameInputItem,
                                onUpdate = { updatedItem ->
                                    primaryEventName = updatedItem.name
                                    isEditingEventName = updatedItem.isEditing
                                    isExistingEventName = updatedItem.isExisting
                                },
                                onDelete = {
                                    primaryEventName = ""
                                    isEditingEventName = false
                                    isExistingEventName = false
                                },
                                backgroundColor = SurfacePrimary,
                                hasBorder = false,
                                modifier = Modifier
                                    .clip(SquircleShape(CornerLargeIncrease, CornerLargeIncrease,CornerExtraSmall,CornerExtraSmall))
                                    .background(SurfacePrimary)
                            )

                            // Event Timeline Type Card with dynamic bottom border treatments
                            val timelineTypeShape = if (timelineType == "Single-day") {
                                SquircleShape(CornerExtraSmall)
                            } else {
                                SquircleShape(CornerExtraSmall,CornerExtraSmall,CornerLargeIncrease, CornerLargeIncrease)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(timelineTypeShape)
                                    .background(SurfacePrimary)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Event Timeline Type :",
                                        style = JasnifyTheme.typography.labelLarge,
                                        color = ContentPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = timelineType,
                                        style = JasnifyTheme.typography.labelLarge,
                                        color = ContentSecondary
                                    )
                                }
                                CustomIconButton(
                                    onClick = {
                                        isDirectDateEdit = false
                                        bottomSheetStep = 0
                                        tempTimelineType = timelineType
                                        tempSelectedDateString = singleDaySelectedDate
                                        pickDateSegmentSelected = singleDaySelectedDate != "Not yet decided"
                                        showBottomSheet = true
                                    },
                                    icon = painterResource(id = R.drawable.ic_edit),
                                    containerColor = SurfacePrimary,
                                    contentColor = ContentPrimary,
                                    size = ButtonSize.Small
                                )
                            }

                            // Dynamically Render Event Date Card
                            if (timelineType == "Single-day") {
                                val displayDate = singleDaySelectedDate ?: "Not yet decided"
                                val isDateAdded = displayDate != "Not yet decided"

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(
                                            SquircleShape(CornerExtraSmall,CornerExtraSmall,CornerLargeIncrease,CornerLargeIncrease)
                                        )
                                        .background(SurfacePrimary)
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Event Date :",
                                            style = JasnifyTheme.typography.labelLarge,
                                            color = ContentPrimary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = displayDate,
                                            style = JasnifyTheme.typography.labelLarge,
                                            color = ContentSecondary
                                        )
                                    }

                                    if (isDateAdded) {
                                        CustomIconButton(
                                            onClick = {
                                                if (timelineItems.isEmpty()) {
                                                    datePickerInitialDate = parseFormattedDate(singleDaySelectedDate)
                                                    onDateSelectedCallback = { localDate ->
                                                        singleDaySelectedDate = formatToOrdinalDate(localDate)
                                                    }
                                                    showDatePickerSheet = true
                                                } else {
                                                    isDirectDateEdit = true
                                                    draftSavedDateString = singleDaySelectedDate
                                                    draftCustomDateString = singleDaySelectedDate
                                                    pickerActiveTab = 0
                                                    bottomSheetStep = 2
                                                    showBottomSheet = true
                                                }
                                            },
                                            icon = painterResource(id = R.drawable.ic_edit),
                                            containerColor = SurfacePrimary,
                                            contentColor = ContentPrimary,
                                            size = ButtonSize.Small
                                        )
                                    } else {
                                        CustomTextButton(
                                            onClick = {
                                                if (timelineItems.isEmpty()) {
                                                    datePickerInitialDate = LocalDate.now()
                                                    onDateSelectedCallback = { localDate ->
                                                        singleDaySelectedDate = formatToOrdinalDate(localDate)
                                                    }
                                                    showDatePickerSheet = true
                                                } else {
                                                    isDirectDateEdit = true
                                                    draftSavedDateString = null
                                                    draftCustomDateString = formatToOrdinalDate(LocalDate.now())
                                                    pickerActiveTab = 1
                                                    bottomSheetStep = 2
                                                    showBottomSheet = true
                                                }
                                            },
                                            text = "Add date",
                                            size = ButtonSize.Small,
                                            type = ButtonType.Primary,
                                            shapeStyle = ButtonShapeStyle.Round,
                                            containerColor = Color(0xFF517576),
                                            contentColor = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Render Event Timeline lists and setups ONLY if Multi-day is active
                    if (timelineType == "Multi-day") {
                        // Timeline Section Header
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Event Timeline",
                                        style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                                        color = ContentPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))

                                    CustomIconButton(
                                        onClick = { isTimelineInfoSheetVisible = true },
                                        icon = painterResource(R.drawable.ic_info),
                                        containerColor = BackgroundSecondary,
                                        contentColor = ContentPrimary,
                                        size = ButtonSize.Small
                                    )
                                }

                                // Dynamically active "Add" button linked to lists
                                Row(
                                    modifier = Modifier
                                        .clickable(enabled = !hasUnsavedEditingItem) {
                                            // Inserts a new timeline event at the top of the list instantly
                                            timelineItems.add(
                                                0,
                                                SubEventItem(
                                                    id = java.util.UUID.randomUUID().toString(),
                                                    dateString = "",
                                                    name = "",
                                                    isExisting = false,
                                                    isEditing = true
                                                )
                                            )
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_plus),
                                        contentDescription = "Add Timeline",
                                        tint = if (hasUnsavedEditingItem) ContentSecondary else ContentBrandDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Add",
                                        color = if (hasUnsavedEditingItem) ContentSecondary else ContentBrandDark,
                                        style = JasnifyTheme.typography.labelXLarge
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Event Timeline Stack rendering direct animated items in LazyColumn
                        itemsIndexed(
                            items = timelineItems,
                            key = { _, item -> item.id }
                        ) { index, item ->
                            val isEditing = item.isEditing

                            // Dynamic margins/paddings target values
                            val targetTopPadding = if (isEditing && index > 0 && !timelineItems[index - 1].isEditing) 12.dp else 0.dp
                            val targetBottomPadding = if (isEditing && index == 0) {
                                12.dp
                            } else if (isEditing && index < timelineItems.lastIndex && !timelineItems[index + 1].isEditing) {
                                12.dp
                            } else {
                                0.dp
                            }

                            // Animating paddings smoothly using animateDpAsState to avoid harsh vertical layout jumps
                            val animatedTopPadding by animateDpAsState(
                                targetValue = targetTopPadding,
                                label = "TimelineItemTopPadding"
                            )
                            val animatedBottomPadding by animateDpAsState(
                                targetValue = targetBottomPadding,
                                label = "TimelineItemBottomPadding"
                            )

                            // Dynamic Shape Assignment: Editing items pop out with full squircle corners.
                            // Neighboring items seamlessly adjust their outer boundaries around them.
                            val shape = if (isEditing) {
                                SquircleShape(CornerLargeIncrease)
                            } else {
                                val isFirstInBlock = index == 0 || timelineItems[index - 1].isEditing
                                val isLastInBlock = index == timelineItems.lastIndex || timelineItems[index + 1].isEditing

                                when {
                                    isFirstInBlock && isLastInBlock -> SquircleShape(CornerLargeIncrease)
                                    isFirstInBlock -> SquircleShape(CornerLargeIncrease, CornerLargeIncrease, CornerExtraSmall, CornerExtraSmall)
                                    isLastInBlock -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLargeIncrease, CornerLargeIncrease)
                                    else -> RoundedCornerShape(CornerExtraSmall)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .animateItem() // Built-in Compose transition engine handles reordering slide animations
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .padding(top = animatedTopPadding, bottom = animatedBottomPadding + 2.dp)
                                    .clip(shape)
                                    .background(SurfacePrimary)
                            ) {
                                TimeLineInput(
                                    item = item,
                                    onUpdate = { updatedItem ->
                                        val indexToUpdate = timelineItems.indexOfFirst { it.id == updatedItem.id }
                                        if (indexToUpdate != -1) {
                                            val oldItem = timelineItems[indexToUpdate]
                                            timelineItems[indexToUpdate] = updatedItem

                                            // Trigger chronological sort instantly when date is picked (even during editing)
                                            if (oldItem.date != updatedItem.date || (!updatedItem.isEditing && oldItem.isEditing)) {
                                                val parsedDates = timelineItems.associate { it.id to parseFormattedDate(it.dateString) }

                                                val sorted = timelineItems.sortedWith(
                                                    compareBy<SubEventItem> {
                                                        // Keep empty/undated/newly added items at the very top (index 0) so they can be edited cleanly
                                                        if (it.dateString.isBlank() || it.dateString == "Not yet decided" || it.dateString == "Select a date") 0 else 1
                                                    }.thenBy {
                                                        parsedDates[it.id] ?: LocalDate.MAX
                                                    }
                                                )

                                                timelineItems.clear()
                                                timelineItems.addAll(sorted)
                                            }
                                        }
                                    },
                                    onDelete = { itemToDelete ->
                                        timelineItems.remove(itemToDelete)
                                    },
                                    backgroundColor = SurfacePrimary,
                                    hasBorder = false,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    item{
                        Spacer(Modifier.height(46.dp))
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Icon(
                                painter = painterResource(R.drawable.ic_app),
                                contentDescription = null,
                                tint = ContentSecondary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Your smart way to celebrate.",
                                style = JasnifyTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                                color = ContentSecondary
                            )
                        }

                    }
                }
            }
        }

        // --- Screen-level CustomToast Display (Shown ONLY when Bottom Sheet is hidden) ---
        AnimatedVisibility(
            visible = toastData.message != null && !showBottomSheet,
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
                type = toastData.type
            )
        }
    }

    // ================================================= Dedicated Custom Date Picker Bottom Sheet ==========================================

    if (showDatePickerSheet) {
        DatePickerSheet(
            onDismiss = { showDatePickerSheet = false },
            onDateSelected = { localDate ->
                onDateSelectedCallback?.invoke(localDate)
            },
            initialDate = datePickerInitialDate
        )
    }

    // =================================================== Event Timeline Info Bottom Sheet =================================================

    if (isTimelineInfoSheetVisible) {
        EventTimeLineInfoSheet(
            onDismiss = { isTimelineInfoSheetVisible = false }
        )
    }

    // ============================================================= Bottom Sheet ============================================================

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
            dragHandle = null,
            sheetGesturesEnabled = true,
        ) {
            // --- Custom Window Setup for Sheet's Dialog Window ---
            val view = LocalView.current
            DisposableEffect(view) {
                var parent = view.parent
                var dialogWindow: android.view.Window? = null
                while (parent != null) {
                    if (parent is DialogWindowProvider) {
                        dialogWindow = parent.window
                        break
                    }
                    parent = parent.parent
                }

                dialogWindow?.let { w ->
                    // Set navigation bar color to blend with SurfacePrimary (bottom sheet container)
                    val colorInt = SurfacePrimary.toArgb()
                    w.navigationBarColor = colorInt

                    // Disable default gray tint scrim introduced in Android Q+ for light/colored system bars
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        w.isNavigationBarContrastEnforced = false
                    }

                    // Dynamically set dark or light system icon themes depending on background luminance
                    val isLightBackground = ColorUtils.calculateLuminance(colorInt) > 0.5
                    WindowCompat.getInsetsController(w, view).isAppearanceLightNavigationBars = isLightBackground
                }
                onDispose {}
            }

            // This outer column coordinates drawing the custom toast above the actual bottom sheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Aligns right above the sheet container ---
                androidx.compose.animation.AnimatedVisibility(
                    visible = toastData.message != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .zIndex(998f)
                ) {
                    CustomToast(
                        message = toastData.message ?: "",
                        type = toastData.type
                    )
                }

                // Actual visible bottom sheet container layout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(999f)
                        .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge, 0.dp, 0.dp))
                        .background(SurfacePrimary)
                        .navigationBarsPadding()
                ) {
                    // Custom Drag Handle inside the container
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                            .width(56.dp)
                            .height(4.dp)
                            .background(ContentTertiary, shape = SquircleShape(100))
                    )

                    when (bottomSheetStep) {
                        0 -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(12.dp, 12.dp, 12.dp, 0.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Event Timeline",
                                        style = JasnifyTheme.typography.displayLarge,
                                        color = ContentPrimary
                                    )

                                    TopBarIconButton(
                                        backgroundStyle = ButtonBackground.OPAQUE,
                                        icon = TopIcon.Predefined.CLOSE,
                                        onClick = {
                                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                                showBottomSheet = false
                                            }
                                        }
                                    )
                                }

                                // Steps Body content
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                    ) {
                                        val willBeDeleted = timelineType == "Multi-day" && tempTimelineType == "Single-day" && timelineItems.isNotEmpty()
                                        if (willBeDeleted) {
                                            InfoCard(
                                                message = "All your existing timelines will be removed.",
                                                nature = InfoCardNature.Negative,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        OptionSelector(
                                            label = "Multi-day",
                                            bodyText = "The event is spread across multiple days.",
                                            isSelected = tempTimelineType == "Multi-day",
                                            onClick = { tempTimelineType = "Multi-day" }
                                        )

                                        OptionSelector(
                                            label = "Single-day",
                                            bodyText = "The event is happening on one specific day.",
                                            isSelected = tempTimelineType == "Single-day",
                                            onClick = { tempTimelineType = "Single-day" }
                                        )
                                    }

                                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

                                    CustomTextButton(
                                        onClick = {
                                            if (tempTimelineType == "Single-day") {
                                                bottomSheetStep = 1
                                            } else {
                                                val wasSingleDay = timelineType == "Single-day"
                                                timelineType = "Multi-day"
                                                singleDaySelectedDate = null
                                                if (wasSingleDay) {
                                                    toastData = ToastData("Changed to Multi-day!", ToastType.SUCCESS)
                                                }
                                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                                    showBottomSheet = false
                                                }
                                            }
                                        },
                                        text = "Continue",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        size = ButtonSize.Medium,
                                        type = ButtonType.Primary,
                                        shapeStyle = ButtonShapeStyle.Square
                                    )
                                }
                            }
                        }

                        1 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                            ) {
                                Text(
                                    text = "Select your event date",
                                    style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                                    color = ContentPrimary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "Choose the date on which you are planning to host the single-day event.",
                                    style = JasnifyTheme.typography.bodyLarge,
                                    color = ContentSecondary,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(24.dp))
                                val options = listOf(true, false)

                                IosSegmentedControl(
                                    options = options,
                                    selectedOption = pickDateSegmentSelected,
                                    onOptionSelected = { isPickDate ->
                                        pickDateSegmentSelected = isPickDate
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    labelProvider = { isPickDate ->
                                        if (isPickDate) "Pick a date" else "Not yet decided"
                                    }
                                )
                                Spacer(Modifier.height(12.dp))

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    val hasSelectedValue = !pickDateSegmentSelected || tempSelectedDateString != null
                                    val inputTextColor = if (hasSelectedValue) ContentPrimary else ContentSecondary

                                    PrimaryInput(
                                        value = if (pickDateSegmentSelected) {
                                            tempSelectedDateString ?: ""
                                        } else {
                                            "Not yet decided"
                                        },
                                        onValueChange = {},
                                        placeholder = "Select a date",
                                        trailingIcon = painterResource(id = R.drawable.ic_calendar),
                                        trailingIconEnabled = hasSelectedValue,
                                        textStyle = JasnifyTheme.typography.labelXLarge.copy(color = inputTextColor),
                                        readOnly = true,
                                        modifier = Modifier.focusProperties { canFocus = false }
                                    )
                                    Spacer(Modifier.height(12.dp))

                                    if (pickDateSegmentSelected) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clip(SquircleShape(CornerLarge))
                                                .clickable {
                                                    if (timelineItems.isEmpty()) {
                                                        datePickerInitialDate = parseFormattedDate(tempSelectedDateString)
                                                        onDateSelectedCallback = { localDate ->
                                                            tempSelectedDateString = formatToOrdinalDate(localDate)
                                                        }
                                                        showDatePickerSheet = true
                                                    } else {
                                                        // Initialize separate draft trackers
                                                        draftSavedDateString = tempSelectedDateString
                                                        draftCustomDateString = tempSelectedDateString
                                                        bottomSheetStep = 2
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                            ) {
                                Text(
                                    text = "By proceeding, you allow us to delete any existing timeline.",
                                    style = JasnifyTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                                    color = ContentSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(12.dp))

                                CustomTextButton(
                                    onClick = {
                                        if (pickDateSegmentSelected && tempSelectedDateString.isNullOrBlank()) {
                                            toastData = ToastData("Please select a date!", ToastType.ERROR)
                                        } else {
                                            coroutineScope.launch {
                                                val wasMultiDay = timelineType == "Multi-day"
                                                timelineItems.clear()
                                                timelineType = "Single-day"
                                                singleDaySelectedDate = if (pickDateSegmentSelected) tempSelectedDateString else "Not yet decided"
                                                sheetState.hide()
                                                if (wasMultiDay) {
                                                    toastData = ToastData("Changed to Single-day!", ToastType.SUCCESS)
                                                }
                                            }.invokeOnCompletion {
                                                if (!sheetState.isVisible) {
                                                    showBottomSheet = false
                                                }
                                            }
                                        }
                                    },
                                    text = "Confirm",
                                    modifier = Modifier.fillMaxWidth(),
                                    size = ButtonSize.Medium,
                                    type = ButtonType.Primary,
                                    shapeStyle = ButtonShapeStyle.Square
                                )

                                CustomTextButton(
                                    onClick = { bottomSheetStep = 0 },
                                    text = "Cancel",
                                    modifier = Modifier.fillMaxWidth(),
                                    size = ButtonSize.Medium,
                                    type = ButtonType.Tertiary,
                                    shapeStyle = ButtonShapeStyle.Square
                                )
                            }
                        }

                        2 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { pickerActiveTab = 0 },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Saved Timelines",
                                            style = JasnifyTheme.typography.displaySmall,
                                            fontWeight = if (pickerActiveTab == 0) FontWeight.Medium else FontWeight.Normal,
                                            color = if (pickerActiveTab == 0) ContentPrimary else ContentSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                pickerActiveTab = 1
                                                // Sync the saved selection over to custom date so the slider coordinates starting point
                                                if (draftSavedDateString != null) {
                                                    draftCustomDateString = draftSavedDateString
                                                } else if (draftCustomDateString == null) {
                                                    // Fallback to today's date if no custom date was selected yet
                                                    draftCustomDateString = formatToOrdinalDate(LocalDate.now())
                                                }
                                                // Clear the draftSavedDateString selection so it resets completely when they slide/come back
                                                draftSavedDateString = null
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Custom Date",
                                            style = JasnifyTheme.typography.displaySmall,
                                            fontWeight = if (pickerActiveTab == 1) FontWeight.Medium else FontWeight.Normal,
                                            color = if (pickerActiveTab == 1) ContentPrimary else ContentSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .padding(horizontal = 12.dp)
                                ) {
                                    val containerWidth = maxWidth
                                    val indicatorWidth = 170.dp

                                    val targetOffset = if (pickerActiveTab == 0) {
                                        (containerWidth / 4) - (indicatorWidth / 2)
                                    } else {
                                        (containerWidth * 3f / 4f) - (indicatorWidth / 2)
                                    }

                                    val animatedOffset by animateDpAsState(
                                        targetValue = targetOffset,
                                        label = "TabIndicatorOffset"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .offset(x = animatedOffset)
                                            .width(indicatorWidth)
                                            .height(4.dp)
                                            .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge, CornerExtraSmall, CornerExtraSmall))
                                            .background(ContentBrand)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                ) {
                                    if (pickerActiveTab == 0) {
                                        if (timelineItems.isEmpty()) {
                                            Box(
                                                modifier = Modifier.fillMaxSize()
                                                    .padding(12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "No saved timelines available.",
                                                    color = ContentSecondary,
                                                    style = JasnifyTheme.typography.bodyLarge
                                                )
                                            }
                                        } else {
                                            LazyColumn(
                                                modifier = Modifier.fillMaxSize(),
                                                contentPadding = PaddingValues(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                itemsIndexed(timelineItems) { index, item ->
                                                    val isDateSelected = draftSavedDateString == item.dateString

                                                    // Dynamic Shape per element matching hierarchy rules
                                                    val rowShape = when {
                                                        timelineItems.size == 1 -> RoundedCornerShape(CornerLargeIncrease)
                                                        index == 0 -> SquircleShape(CornerLargeIncrease,CornerLargeIncrease,CornerExtraSmall,CornerExtraSmall)
                                                        index == timelineItems.lastIndex -> SquircleShape(CornerExtraSmall,CornerExtraSmall,CornerLargeIncrease,CornerLargeIncrease)
                                                        else -> RoundedCornerShape(CornerExtraSmall)
                                                    }

                                                    // Custom Dynamic Button Configuration for rows
                                                    val buttonText = if (isDateSelected) "Selected" else "Select date"
                                                    val buttonBgColor = when {
                                                        isDateSelected -> SurfaceInvPrimary
                                                        draftSavedDateString != null -> SurfaceInvSecondary
                                                        else -> SurfaceBrandPrimary
                                                    }
                                                    val buttonContentColor = ContentInvPrimary

                                                    // Dynamic container background based on selection
                                                    val rowBgColor = if (isDateSelected) SurfaceBrandSecondary else SurfaceSecondary

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(rowShape)
                                                            .background(rowBgColor)
                                                            .clickable {
                                                                draftSavedDateString = item.dateString
                                                            }
                                                            .padding(16.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(
                                                                text = item.dateString.ifEmpty { "Undated Ceremony" },
                                                                style = JasnifyTheme.typography.labelXLarge,
                                                                color = ContentBrandDark
                                                            )
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Text(
                                                                text = item.name.ifEmpty { "Ceremony description" },
                                                                style = JasnifyTheme.typography.labelLarge,
                                                                color = ContentSecondary
                                                            )
                                                        }

                                                        CustomTextButton(
                                                            onClick = {
                                                                draftSavedDateString = item.dateString
                                                            },
                                                            text = buttonText,
                                                            contentColor = buttonContentColor,
                                                            containerColor = buttonBgColor,
                                                            size = ButtonSize.Small,
                                                            shapeStyle = ButtonShapeStyle.Round
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // Custom Date picker Tab implementation
                                        val currentParsedDate = remember(draftCustomDateString) {
                                            parseFormattedDate(draftCustomDateString)
                                        }

                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            DatePickerSlider(
                                                selectedDate = currentParsedDate,
                                                onDateChanged = { newLocalDate ->
                                                    draftCustomDateString = formatToOrdinalDate(newLocalDate)
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CustomTextButton(
                                        onClick = {
                                            if (isDirectDateEdit) {
                                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                                    showBottomSheet = false
                                                }
                                            } else {
                                                bottomSheetStep = 1
                                            }
                                        },
                                        text = "Back",
                                        type = ButtonType.Tertiary,
                                        shapeStyle = ButtonShapeStyle.Square,
                                        containerColor = SurfacePrimary,
                                        contentColor = ContentPrimary,
                                        leadingIcon = painterResource(id = R.drawable.ic_left),
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Updated to CustomTextButton for Done Action
                                    CustomTextButton(
                                        onClick = {
                                            val resolvedDate = if (pickerActiveTab == 0) {
                                                draftSavedDateString
                                            } else {
                                                draftCustomDateString ?: formatToOrdinalDate(LocalDate.now())
                                            }

                                            if (isDirectDateEdit) {
                                                singleDaySelectedDate = resolvedDate
                                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                                    showBottomSheet = false
                                                }
                                            } else {
                                                tempSelectedDateString = resolvedDate
                                                bottomSheetStep = 1
                                            }
                                        },
                                        text = "Done",
                                        modifier = Modifier.weight(1f),
                                        shapeStyle = ButtonShapeStyle.Square,
                                        enabled = if (pickerActiveTab == 0) draftSavedDateString != null else true,
                                        disabledContainerColor = SurfaceInvSecondary,
                                        disabledContentColor = ContentInvPrimary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========================================================= Helper Functions & Utilities ===========================================================

@RequiresApi(Build.VERSION_CODES.O)
fun parseFormattedDate(dateStr: String?): LocalDate {
    if (dateStr.isNullOrBlank() || dateStr == "Not yet decided" || dateStr == "Select a date") {
        // Return maximum bound so undecided/empty items sort naturally to the bottom
        return LocalDate.MAX
    }
    return try {
        val dayPart = dateStr.take(2).filter { it.isDigit() }
        val remaining = dateStr.drop(2)

        val cleanedRemaining = remaining
            .trimStart()
            .replaceFirst("th", "")
            .replaceFirst("st", "")
            .replaceFirst("nd", "")
            .replaceFirst("rd", "")
            .replace("Sept", "Sep")
            .replace(",", "")
            .trim()

        val cleanedStr = "${dayPart.padStart(2, '0')} $cleanedRemaining"
        val formatterWithoutComma = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        LocalDate.parse(cleanedStr, formatterWithoutComma)
    } catch (e: Exception) {
        LocalDate.MAX
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatToOrdinalDate(date: LocalDate): String {
    val day = date.dayOfMonth
    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }

    val dayStr = day.toString().padStart(2, '0')
    val monthName = when (date.month) {
        Month.SEPTEMBER -> "Sept"
        else -> date.month.getDisplayName(JavaTextStyle.SHORT, Locale.ENGLISH)
    }

    return "$dayStr$suffix $monthName, ${date.year}"
}

@Composable
private fun EventInfoHalfCard(
    title: String,
    value: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(SquircleShape(CornerLargeIncrease))
            .background(SurfacePrimary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = JasnifyTheme.typography.labelLarge,
                color = ContentPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary
            )
        }
        CustomIconButton(
            onClick = onClick,
            icon = icon,
            containerColor = SurfacePrimary,
            contentColor = ContentPrimary,
            size = ButtonSize.Small
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EventDetailPreview() {
    EventDetailsScreen(
        onBackClick = { }
    )
}