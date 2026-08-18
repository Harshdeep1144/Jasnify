package com.harshdeep.jasnify.presentation.screens.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.models.eventTypes
import com.harshdeep.jasnify.domain.model.SubEvent
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSlider
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DeleteTimelineWarningSheet
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
import com.harshdeep.jasnify.presentation.components.others.InfoTooltip
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.OptionSelector
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.SubEventItem
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import java.time.format.TextStyle as JavaTextStyle

private val DateParserFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
private val DateStandardFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
private val PickDateOptions = listOf(true, false)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailsScreen(
    onBackClick: () -> Unit,
    onReviewVenues: () -> Unit = {},
    onReviewVendors: () -> Unit = {},
    eventViewModel: EventViewModel = hiltViewModel(),
    venueViewModel: VenueViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activeEvent by eventViewModel.activeEvent.collectAsState()
    val savedVenues by venueViewModel.savedVenues.collectAsState()
    val savedVendors by vendorViewModel.savedVendors.collectAsState()

    val isAdmin by remember(activeEvent) {
        derivedStateOf { activeEvent?.ownerId == FirebaseAuth.getInstance().currentUser?.uid }
    }

    LaunchedEffect(Unit) {
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEvent) {
        activeEvent?.id?.let { id ->
            venueViewModel.setEventId(id)
            vendorViewModel.setEventId(id)
        }
    }

    var eventId by remember { mutableStateOf("...") }
    var eventType by remember { mutableStateOf("...") }

    var timelineType by remember { mutableStateOf("Multi-day") }
    var primaryEventName by remember { mutableStateOf("...") }
    var singleDaySelectedDate by remember { mutableStateOf<String?>(null) }

    var showEventTypeTooltip by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }
    var isTimelineInfoSheetVisible by remember { mutableStateOf(false) }

    var showDatePickerSheet by remember { mutableStateOf(false) }
    var showTimelineDatePicker by remember { mutableStateOf(false) }
    var selectedTimelineItem by remember { mutableStateOf<SubEventItem?>(null) }
    var datePickerInitialDate by remember { mutableStateOf(LocalDate.now()) }
    var onDateSelectedCallback by remember { mutableStateOf<((LocalDate) -> Unit)?>(null) }

    var bottomSheetStep by remember { mutableIntStateOf(0) }
    var tempTimelineType by remember { mutableStateOf(timelineType) }
    var pickDateSegmentSelected by remember { mutableStateOf(true) }
    var tempSelectedDateString by remember { mutableStateOf<String?>(null) }

    var draftSavedDateString by remember { mutableStateOf<String?>(null) }
    var draftCustomDateString by remember { mutableStateOf<String?>(null) }

    var isDirectDateEdit by remember { mutableStateOf(false) }
    var pickerActiveTab by remember { mutableIntStateOf(0) }

    var showDeleteWarningSheet by remember { mutableStateOf(false) }
    var venueCountForDelete by remember { mutableIntStateOf(0) }
    var vendorCountForDelete by remember { mutableIntStateOf(0) }
    var itemPendingDelete by remember { mutableStateOf<SubEventItem?>(null) }

    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            showBottomSheet ||
                    isTimelineInfoSheetVisible ||
                    showDatePickerSheet ||
                    showTimelineDatePicker ||
                    showDeleteWarningSheet
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

    var isEditingEventName by remember { mutableStateOf(false) }
    var isExistingEventName by remember { mutableStateOf(true) }

    val timelineItems = remember {
        mutableStateListOf<SubEventItem>()
    }

    var isSyncing by remember { mutableStateOf(false) }

    val isEditingAnyItem by remember {
        derivedStateOf {
            timelineItems.any { it.isEditing } || isEditingEventName
        }
    }

    var isInitialLoad by remember { mutableStateOf(true) }

    val hasUnsavedEditingItem by remember {
        derivedStateOf {
            timelineItems.any { it.isEditing && !it.isExisting }
        }
    }

    LaunchedEffect(activeEvent) {
        activeEvent?.let { event ->
            eventId = event.id.take(8).uppercase()
            primaryEventName = event.name
            eventType = eventTypes.find { it.id == event.typeId }?.label ?: "Others"

            if (!showBottomSheet && !isEditingAnyItem && !isSyncing) {
                timelineType = if (event.multiDay) "Multi-day" else "Single-day"

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
                        isCompleted = subEvent.completed
                    )
                }

                timelineItems.clear()
                timelineItems.addAll(uiSubEvents)
                val parsed = timelineItems.associate { it.id to parseFormattedDate(it.dateString) }
                timelineItems.sortBy { parsed[it.id] }

                if (!event.multiDay && event.date != null) {
                    singleDaySelectedDate = formatToOrdinalDate(Instant.ofEpochMilli(event.date).atZone(ZoneId.systemDefault()).toLocalDate())
                }

                if (isInitialLoad) {
                    coroutineScope.launch {
                        delay(500.milliseconds)
                        isInitialLoad = false
                    }
                }
            }
        }
    }

    var toastData by remember { mutableStateOf(ToastData()) }
    var activeToastData by remember { mutableStateOf<ToastData?>(null) }

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            activeToastData = toastData
            delay(2000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    fun syncEvent() {
        val current = activeEvent ?: return
        val isMulti = timelineType == "Multi-day"

        isSyncing = true

        if (!isMulti) {
            timelineItems.clear()
        }

        val updated = current.copy(
            name = primaryEventName,
            multiDay = isMulti,
            date = if (!isMulti) {
                val ld = parseFormattedDate(singleDaySelectedDate)
                if (ld == LocalDate.MAX) null else ld.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } else null,
            subEvents = if (isMulti) {
                timelineItems.filter { it.name.isNotBlank() && it.date != null }.map {
                    SubEvent(
                        id = it.id,
                        name = it.name,
                        date = it.date,
                        completed = it.isCompleted
                    )
                }
            } else {
                emptyList()
            }
        )
        eventViewModel.updateEvent(updated)

        coroutineScope.launch {
            delay(1000.milliseconds)
            isSyncing = false
        }
    }

    val infoPainter = painterResource(id = R.drawable.ic_info)
    val copyPainter = painterResource(id = R.drawable.ic_copy)
    val editPainter = painterResource(id = R.drawable.ic_edit)
    val calendarPainter = painterResource(id = R.drawable.ic_calendar)
    val plusPainter = painterResource(id = R.drawable.ic_plus)
    val appPainter = painterResource(id = R.drawable.ic_app)
    val leftPainter = painterResource(id = R.drawable.ic_left)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = backdropScale
                        scaleY = backdropScale
                        clip = isAnyBottomSheetOpen || backdropCornerRadius > 0.dp
                        shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                    }
            ) {
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
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            item(key = "event_info_half_cards", contentType = "header_cards") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp, 12.dp, 12.dp, 0.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    EventInfoHalfCard(
                                        title = "Event Type :",
                                        value = eventType,
                                        icon = infoPainter,
                                        modifier = Modifier.weight(1f),
                                        onClick = { showEventTypeTooltip = true },
                                        tooltipContent = {
                                            InfoTooltip(
                                                visible = showEventTypeTooltip,
                                                tooltipText = "Event type can't be changed",
                                                onDismiss = { showEventTypeTooltip = false }
                                            )
                                        }
                                    )

                                    EventInfoHalfCard(
                                        title = "Event ID :",
                                        value = eventId,
                                        icon = copyPainter,
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

                            item(key = "event_name_and_timeline_type", contentType = "primary_inputs") {
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

                                    EventNameInput(
                                        item = eventNameInputItem,
                                        onUpdate = { updatedItem ->
                                            val wasEditing = isEditingEventName
                                            primaryEventName = updatedItem.name
                                            isEditingEventName = updatedItem.isEditing
                                            isExistingEventName = updatedItem.isExisting

                                            if (wasEditing && !updatedItem.isEditing) {
                                                syncEvent()
                                            }
                                        },
                                        onDelete = {
                                            primaryEventName = ""
                                            isEditingEventName = false
                                            isExistingEventName = false
                                            syncEvent()
                                        },
                                        backgroundColor = SurfacePrimary,
                                        hasBorder = false,
                                        isEditable = isAdmin,
                                        modifier = Modifier
                                            .clip(SquircleShape(CornerLargeIncrease, CornerLargeIncrease, CornerExtraSmall, CornerExtraSmall))
                                            .background(SurfacePrimary)
                                    )

                                    val timelineTypeShape = if (timelineType == "Single-day") {
                                        SquircleShape(CornerExtraSmall)
                                    } else {
                                        SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLargeIncrease, CornerLargeIncrease)
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
                                        if (isAdmin) {
                                            CustomIconButton(
                                                onClick = {
                                                    isDirectDateEdit = false
                                                    bottomSheetStep = 0
                                                    tempTimelineType = timelineType
                                                    tempSelectedDateString = singleDaySelectedDate
                                                    pickDateSegmentSelected = singleDaySelectedDate != "Not yet decided"
                                                    showBottomSheet = true
                                                },
                                                icon = editPainter,
                                                containerColor = SurfacePrimary,
                                                contentColor = ContentPrimary,
                                                size = ButtonSize.Small
                                            )
                                        }
                                    }

                                    if (timelineType == "Single-day") {
                                        val displayDate = singleDaySelectedDate ?: "Not yet decided"
                                        val isDateAdded = displayDate != "Not yet decided"

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(
                                                    SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLargeIncrease, CornerLargeIncrease)
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

                                            if (isAdmin) {
                                                if (isDateAdded) {
                                                    CustomIconButton(
                                                        onClick = {
                                                            if (timelineItems.isEmpty()) {
                                                                datePickerInitialDate = parseFormattedDate(singleDaySelectedDate)
                                                                onDateSelectedCallback = { localDate ->
                                                                    singleDaySelectedDate = formatToOrdinalDate(localDate)
                                                                    syncEvent()
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
                                                        icon = editPainter,
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
                                                                    syncEvent()
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
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (timelineType == "Multi-day") {
                                item(key = "timeline_header", contentType = "section_header") {
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
                                                icon = infoPainter,
                                                containerColor = BackgroundSecondary,
                                                contentColor = ContentPrimary,
                                                size = ButtonSize.Small
                                            )
                                        }

                                        if (isAdmin) {
                                            Row(
                                                modifier = Modifier
                                                    .clickable(enabled = !hasUnsavedEditingItem) {
                                                        timelineItems.add(
                                                            0,
                                                            SubEventItem(
                                                                id = UUID.randomUUID().toString(),
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
                                                    painter = plusPainter,
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
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                itemsIndexed(
                                    items = timelineItems,
                                    key = { _, item -> item.id },
                                    contentType = { _, _ -> "timeline_input_item" }
                                ) { index, item ->
                                    val isEditing = item.isEditing

                                    val targetTopPadding = if (isEditing && index > 0 && !timelineItems[index - 1].isEditing) 12.dp else 0.dp
                                    val targetBottomPadding = if (isEditing && index == 0) {
                                        12.dp
                                    } else if (isEditing && index < timelineItems.lastIndex && !timelineItems[index + 1].isEditing) {
                                        12.dp
                                    } else {
                                        0.dp
                                    }

                                    val animatedTopPadding by animateDpAsState(
                                        targetValue = targetTopPadding,
                                        label = "TimelineItemTopPadding"
                                    )
                                    val animatedBottomPadding by animateDpAsState(
                                        targetValue = targetBottomPadding,
                                        label = "TimelineItemBottomPadding"
                                    )

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
                                            .then(if (isInitialLoad) Modifier else Modifier.animateItem())
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

                                                    if (oldItem.date != updatedItem.date || (!updatedItem.isEditing && oldItem.isEditing)) {
                                                        val parsedDates = timelineItems.associate { it.id to parseFormattedDate(it.dateString) }

                                                        val sorted = timelineItems.sortedWith(
                                                            compareBy<SubEventItem> {
                                                                if (it.dateString.isBlank() || it.dateString == "Not yet decided" || it.dateString == "Select a date") 0 else 1
                                                            }.thenBy {
                                                                parsedDates[it.id] ?: LocalDate.MAX
                                                            }
                                                        )

                                                        timelineItems.clear()
                                                        timelineItems.addAll(sorted)
                                                    }

                                                    if (!updatedItem.isEditing && oldItem.isEditing) {
                                                        syncEvent()
                                                    }
                                                }
                                            },
                                            onDelete = { itemToDelete ->
                                                val venueCount = savedVenues.count { it.destination == itemToDelete.id }
                                                val vendorCount = savedVendors.count { it.destination == itemToDelete.id }

                                                if (venueCount > 0 || vendorCount > 0) {
                                                    venueCountForDelete = venueCount
                                                    vendorCountForDelete = vendorCount
                                                    itemPendingDelete = itemToDelete
                                                    showDeleteWarningSheet = true
                                                } else {
                                                    timelineItems.remove(itemToDelete)
                                                    syncEvent()
                                                }
                                            },
                                            backgroundColor = SurfacePrimary,
                                            hasBorder = false,
                                            isEditable = isAdmin,
                                            onShowDatePicker = { targetItem ->
                                                selectedTimelineItem = targetItem
                                                showTimelineDatePicker = true
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            }

                            item(key = "spacer_end", contentType = "spacer") {
                                Spacer(Modifier.height(46.dp))
                            }

                            item(key = "footer_branding", contentType = "footer") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = appPainter,
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
            }
        }

        AnimatedVisibility(
            visible = toastData.message != null && !isAnyBottomSheetOpen,
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

        CustomBottomSheet(
            isVisible = showBottomSheet,
            onDismiss = { showBottomSheet = false },
            onProgress = { sheetMotionProgress = it },
            sheetHeight = null,
            showDragHandle = false,
            showCloseButton = false,
            hasToast = toastData.message != null,
            toast = {
                AnimatedVisibility(
                    visible = toastData.message != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(12.dp)
                ) {
                    activeToastData?.let { data ->
                        CustomToast(
                            message = data.message ?: "",
                            type = data.type
                        )
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(999f)
                        .background(SurfacePrimary)
                ) {
                    when (bottomSheetStep) {
                        0 -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
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
                                        iconSize = 18.dp,
                                        onClick = { showBottomSheet = false }
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
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

                                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

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
                                                syncEvent()
                                                showBottomSheet = false
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
                                    .padding(12.dp)
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

                                IosSegmentedControl(
                                    options = PickDateOptions,
                                    selectedOption = pickDateSegmentSelected,
                                    onOptionSelected = { isPickDate ->
                                        pickDateSegmentSelected = isPickDate
                                    },
                                    modifier = Modifier.fillMaxWidth(),
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
                                        trailingIcon = calendarPainter,
                                        trailingIconEnabled = hasSelectedValue,
                                        textStyle = JasnifyTheme.typography.labelXLarge.copy(color = inputTextColor),
                                        readOnly = true,
                                        modifier = Modifier.focusProperties { canFocus = false }
                                    )
                                    Spacer(Modifier.height(12.dp))

                                    if (pickDateSegmentSelected) {
                                        val validSavedTimelines = remember(timelineItems) {
                                            timelineItems.filter {
                                                it.dateString.isNotBlank() &&
                                                        it.dateString != "Not yet decided" &&
                                                        it.dateString != "Select a date" &&
                                                        it.name.isNotBlank()
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clip(SquircleShape(CornerLarge))
                                                .clickable {
                                                    if (timelineItems.isEmpty() || validSavedTimelines.isEmpty()) {
                                                        datePickerInitialDate = parseFormattedDate(tempSelectedDateString)
                                                        onDateSelectedCallback = { localDate ->
                                                            tempSelectedDateString = formatToOrdinalDate(localDate)
                                                        }
                                                        showDatePickerSheet = true
                                                    } else {
                                                        draftSavedDateString = tempSelectedDateString
                                                        draftCustomDateString = tempSelectedDateString
                                                        bottomSheetStep = 2
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "By proceeding, you allow us to delete any existing timeline.",
                                    style = JasnifyTheme.typography.bodyMedium,
                                    color = ContentSecondary,
                                    fontWeight = FontWeight.Light,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(12.dp))

                                CustomTextButton(
                                    onClick = {
                                        if (pickDateSegmentSelected && tempSelectedDateString.isNullOrBlank()) {
                                            toastData = ToastData("Please select a date!", ToastType.ERROR)
                                        } else {
                                            val wasMultiDay = timelineType == "Multi-day"
                                            timelineItems.clear()
                                            timelineType = "Single-day"
                                            singleDaySelectedDate = if (pickDateSegmentSelected) tempSelectedDateString else "Not yet decided"
                                            syncEvent()
                                            showBottomSheet = false
                                            if (wasMultiDay) {
                                                toastData = ToastData("Changed to Single-day!", ToastType.SUCCESS)
                                            }
                                        }
                                    },
                                    text = "Switch to Single-day",
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
                                                if (draftSavedDateString != null) {
                                                    draftCustomDateString = draftSavedDateString
                                                } else if (draftCustomDateString == null) {
                                                    draftCustomDateString = formatToOrdinalDate(LocalDate.now())
                                                }
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
                                            .clip(RoundedCornerShape(CornerExtraLarge, CornerExtraLarge, CornerExtraSmall, CornerExtraSmall))
                                            .background(ContentBrand)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                ) {
                                    if (pickerActiveTab == 0) {
                                        val validSavedTimelines = remember(timelineItems) {
                                            timelineItems.filter {
                                                it.dateString.isNotBlank() &&
                                                        it.dateString != "Not yet decided" &&
                                                        it.dateString != "Select a date" &&
                                                        it.name.isNotBlank()
                                            }
                                        }

                                        if (validSavedTimelines.isEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
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
                                                itemsIndexed(
                                                    items = validSavedTimelines,
                                                    key = { _, item -> item.id },
                                                    contentType = { _, _ -> "saved_timeline_picker_item" }
                                                ) { index, item ->
                                                    val isDateSelected = draftSavedDateString == item.dateString

                                                    val rowShape = when {
                                                        validSavedTimelines.size == 1 -> RoundedCornerShape(CornerLargeIncrease)
                                                        index == 0 -> SquircleShape(CornerLargeIncrease, CornerLargeIncrease, CornerExtraSmall, CornerExtraSmall)
                                                        index == validSavedTimelines.lastIndex -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLargeIncrease, CornerLargeIncrease)
                                                        else -> RoundedCornerShape(CornerExtraSmall)
                                                    }

                                                    val buttonText = if (isDateSelected) "Selected" else "Select date"
                                                    val buttonBgColor = when {
                                                        isDateSelected -> SurfaceInvPrimary
                                                        draftSavedDateString != null -> SurfaceInvSecondary
                                                        else -> SurfaceBrandPrimary
                                                    }
                                                    val buttonContentColor = ContentInvPrimary
                                                    val rowBgColor = if (isDateSelected) SurfaceBrandSecondary else SurfaceSecondary

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(rowShape)
                                                            .background(rowBgColor)
                                                            .clickable { draftSavedDateString = item.dateString }
                                                            .padding(16.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(
                                                                text = item.dateString,
                                                                style = JasnifyTheme.typography.labelXLarge,
                                                                color = ContentBrandDark
                                                            )
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Text(
                                                                text = item.name,
                                                                style = JasnifyTheme.typography.labelLarge,
                                                                color = ContentSecondary
                                                            )
                                                        }

                                                        CustomTextButton(
                                                            onClick = { draftSavedDateString = item.dateString },
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

                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

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
                                                showBottomSheet = false
                                            } else {
                                                bottomSheetStep = 1
                                            }
                                        },
                                        text = "Back",
                                        type = ButtonType.Tertiary,
                                        shapeStyle = ButtonShapeStyle.Square,
                                        containerColor = SurfacePrimary,
                                        contentColor = ContentPrimary,
                                        leadingIcon = leftPainter,
                                        modifier = Modifier.weight(1f)
                                    )

                                    CustomTextButton(
                                        onClick = {
                                            val resolvedDate = if (pickerActiveTab == 0) {
                                                draftSavedDateString
                                            } else {
                                                draftCustomDateString ?: formatToOrdinalDate(LocalDate.now())
                                            }

                                            if (isDirectDateEdit) {
                                                singleDaySelectedDate = resolvedDate
                                                syncEvent()
                                                showBottomSheet = false
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
                                        disabledContentColor = ContentInvPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isTimelineInfoSheetVisible) {
            EventTimeLineInfoSheet(
                onDismiss = { isTimelineInfoSheetVisible = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showDeleteWarningSheet) {
            DeleteTimelineWarningSheet(
                onDismiss = { showDeleteWarningSheet = false },
                venueCount = venueCountForDelete,
                vendorCount = vendorCountForDelete,
                onReviewVenues = {
                    showDeleteWarningSheet = false
                    onReviewVenues()
                },
                onReviewVendors = {
                    showDeleteWarningSheet = false
                    onReviewVendors()
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        DatePickerSheet(
            isVisible = showDatePickerSheet,
            onDismiss = { showDatePickerSheet = false },
            onDateSelected = { localDate ->
                onDateSelectedCallback?.invoke(localDate)
            },
            initialDate = datePickerInitialDate,
            onProgress = { sheetMotionProgress = it }
        )

        DatePickerSheet(
            isVisible = showTimelineDatePicker,
            onDismiss = { showTimelineDatePicker = false },
            onDateSelected = { date ->
                selectedTimelineItem?.let { item ->
                    val formattedDate = date.format(DateStandardFormatter)
                    val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    val indexToUpdate = timelineItems.indexOfFirst { it.id == item.id }
                    if (indexToUpdate != -1) {
                        timelineItems[indexToUpdate] = timelineItems[indexToUpdate].copy(
                            date = timestamp,
                            dateString = formattedDate
                        )
                    }
                }
                showTimelineDatePicker = false
            },
            initialDate = selectedTimelineItem?.date?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            } ?: LocalDate.now(),
            onProgress = { sheetMotionProgress = it }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun parseFormattedDate(dateStr: String?): LocalDate {
    if (dateStr.isNullOrBlank() || dateStr == "Not yet decided" || dateStr == "Select a date") {
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
        LocalDate.parse(cleanedStr, DateParserFormatter)
    } catch (_: Exception) {
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
    onClick: () -> Unit,
    tooltipContent: @Composable (() -> Unit)? = null
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
                color = ContentSecondary,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(
                        iterations = Int.MAX_VALUE
                    )
            )
        }
        Box {
            CustomIconButton(
                onClick = onClick,
                icon = icon,
                containerColor = SurfacePrimary,
                contentColor = ContentPrimary,
                size = ButtonSize.Small
            )
            tooltipContent?.invoke()
        }
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