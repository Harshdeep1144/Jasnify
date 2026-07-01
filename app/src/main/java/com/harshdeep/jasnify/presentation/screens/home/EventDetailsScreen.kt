package com.harshdeep.jasnify.presentation.screens.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.models.SubEventItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSlider
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSheet
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
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.OptionSelector
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
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailsScreen(
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val eventId = "OBFEQO2"
    val eventType = "Wedding"

    // Core dynamic values driven by state
    var timelineType by remember { mutableStateOf("Multi-day") }
    var primaryEventName by remember { mutableStateOf("Taylor & Travis's Wedding") }
    var singleDaySelectedDate by remember { mutableStateOf<String?>(null) }

    // Bottom Sheet Control States
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Dedicated Custom Date Picker Sheet States (Triggers directly if no timelines available)
    var showDatePickerSheet by remember { mutableStateOf(false) }
    var datePickerInitialDate by remember { mutableStateOf(LocalDate.now()) }
    var onDateSelectedCallback by remember { mutableStateOf<((LocalDate) -> Unit)?>(null) }

    // Secondary navigation steps inside bottom sheet
    var bottomSheetStep by remember { mutableStateOf(0) }
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
        mutableStateListOf(
            SubEventItem(id = "1", date = "09th Sept, 2025", name = "Mehendi Ceremony", isExisting = true),
            SubEventItem(id = "3", date = "12th Sept, 2025", name = "The Wedding Day", isExisting = true),
            SubEventItem(id = "2", date = "10th Sept, 2025", name = "Haldi & Sangeet Ceremony", isExisting = true)
        ).apply {
            sortBy { parseFormattedDate(it.date) }
        }
    }

    val nextNewId = remember { mutableIntStateOf(4) }

    // Check if any item is currently unsaved and being edited to avoid duplicate blank inserts
    val hasUnsavedEditingItem by remember {
        derivedStateOf {
            timelineItems.any { it.isEditing && !it.isExisting }
        }
    }

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
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            icon = painterResource(R.drawable.ic_info),
                            modifier = Modifier.weight(1f),
                            onClick = { }
                        )

                        // Event ID Card
                        EventInfoHalfCard(
                            title = "Event ID :",
                            value = eventId,
                            icon = painterResource(R.drawable.ic_copy),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Event ID", eventId)
                                clipboard.setPrimaryClip(clip)

                                Toast.makeText(context, "Event ID copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
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
                                icon = painterResource(R.drawable.ic_edit),
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
                                        icon = painterResource(R.drawable.ic_edit),
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_info),
                                    contentDescription = "Timeline Info",
                                    tint = ContentPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Dynamically active "Add" button linked to lists
                            Row(
                                modifier = Modifier
                                    .clickable(enabled = !hasUnsavedEditingItem) {
                                        timelineItems.add(
                                            SubEventItem(
                                                id = "new-${nextNewId.value++}",
                                                date = "",
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
                    }

                    // Event Timeline Stack rendering custom TimeLineInput
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            timelineItems.forEachIndexed { index, item ->
                                val shape = when {
                                    timelineItems.size == 1 -> RoundedCornerShape(CornerLargeIncrease)
                                    index == 0 -> SquircleShape(CornerLargeIncrease, CornerLargeIncrease, CornerExtraSmall, CornerExtraSmall)
                                    index == timelineItems.lastIndex -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLargeIncrease, CornerLargeIncrease)
                                    else -> RoundedCornerShape(CornerExtraSmall)
                                }

                                TimeLineInput(
                                    item = item,
                                    onUpdate = { updatedItem ->
                                        val indexToUpdate = timelineItems.indexOfFirst { it.id == updatedItem.id }
                                        if (indexToUpdate != -1) {
                                            timelineItems[indexToUpdate] = updatedItem

                                            // Automatically sort the timeline items chronologically when an item is finished editing
                                            if (!updatedItem.isEditing) {
                                                val sorted = timelineItems.sortedWith(
                                                    compareBy<SubEventItem> {
                                                        // Push empty/undecided items to the very end of the list
                                                        it.date.isBlank() || it.date == "Not yet decided"
                                                    }.thenBy {
                                                        parseFormattedDate(it.date)
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
                                    modifier = Modifier
                                        .clip(shape)
                                        .background(SurfacePrimary)
                                )
                            }
                        }
                    }
                }

                // Footer branding logo area
                item{
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_app),
                            contentDescription = "Jasnify App Logo",
                            tint = ContentTertiary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your smart way to celebrate.",
                            color = ContentSecondary,
                            style = JasnifyTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light)
                        )
                    }
                }
            }
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

    // ============================================================= Bottom Sheet ============================================================

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = SurfacePrimary,
            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(56.dp)
                        .height(4.dp)
                        .background(ContentTertiary, shape = SquircleShape(100))
                )
            },
            sheetGesturesEnabled = true,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfacePrimary)
                    .statusBarsPadding()
            ) {
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
                                            timelineType = "Multi-day"
                                            singleDaySelectedDate = null
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
                                    trailingIcon = painterResource(R.drawable.ic_calendar),
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
                                    // Custom Update: Validate that if "Pick a date" is active, a date must be selected first
                                    if (pickDateSegmentSelected && tempSelectedDateString.isNullOrBlank()) {
                                        Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
                                    } else {
                                        coroutineScope.launch {
                                            timelineItems.clear()
                                            timelineType = "Single-day"
                                            singleDaySelectedDate = if (pickDateSegmentSelected) tempSelectedDateString else "Not yet decided"
                                            sheetState.hide()
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
                                    .height(12.dp)
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
                                    .padding(12.dp)
                                    .height(250.dp)
                            ) {
                                if (pickerActiveTab == 0) {
                                    if (timelineItems.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
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
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            itemsIndexed(timelineItems) { index, item ->
                                                val isDateSelected = draftSavedDateString == item.date

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
                                                            draftSavedDateString = item.date
                                                        }
                                                        .padding(16.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = item.date.ifEmpty { "Undated Ceremony" },
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
                                                            draftSavedDateString = item.date
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

                            // Added Horizontal Divider above the action buttons
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
                                    leadingIcon = painterResource(R.drawable.ic_left),
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

// ========================================================= Helper Functions & Utilities ===========================================================

@RequiresApi(Build.VERSION_CODES.O)
fun parseFormattedDate(dateStr: String?): LocalDate {
    if (dateStr.isNullOrBlank() || dateStr == "Not yet decided" || dateStr == "Select a date") {
        return LocalDate.now()
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
            .trim()

        val cleanedStr = "${dayPart.padStart(2, '0')} $cleanedRemaining"
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH)
        LocalDate.parse(cleanedStr, formatter)
    } catch (e: Exception) {
        LocalDate.now()
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