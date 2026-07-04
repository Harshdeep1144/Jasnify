package com.harshdeep.jasnify.presentation.screens.venues

import android.os.Build
import androidx.annotation.RequiresApi
import com.harshdeep.jasnify.R
import androidx.compose.animation.*
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.CalendarToday
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale
import com.harshdeep.jasnify.presentation.components.bottomdrawer.DatePickerSheet
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.data.models.SubEventItem
import com.harshdeep.jasnify.presentation.components.inputfield.TimeLineInput
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardData
import com.harshdeep.jasnify.presentation.components.cards.VendorCardFull
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

data class TimelineEvent(
    val id: String,
    val date: String,
    val event: String,
    val venues: List<VendorCardData>
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VenueScreen(
    selectedLocation: String = "City, State",
    onVenueClick: (VendorCardData) -> Unit,
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    isScreenActive: Boolean = true
) {
    var currentAddress by remember { mutableStateOf(selectedLocation) }
    var isLocationPickerVisible by remember { mutableStateOf(false) }

    BackHandler(enabled = isLocationPickerVisible) {
        isLocationPickerVisible = false
    }

    AnimatedContent(
        targetState = isLocationPickerVisible,
        transitionSpec = {
            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
        },
        label = "venue_location_transition"
    ) { showPicker ->
        if (showPicker) {
            LocationScreen(
                initialSearches = listOf("Patna", "Delhi", "Mumbai"),
                currentAddress = currentAddress,
                onAddressSelected = {
                    currentAddress = it
                    isLocationPickerVisible = false
                },
                onBackClick = { isLocationPickerVisible = false },
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = this
            )
        } else {
            VenueMainContent(
                selectedLocation = currentAddress,
                onVenueClick = onVenueClick,
                onLocationSelectorClick = { isLocationPickerVisible = true },
                onBackClick = onBackClick,
                sharedTransitionScope = sharedTransitionScope,
                outerAnimatedVisibilityScope = animatedVisibilityScope,
                innerAnimatedVisibilityScope = this,
                isScreenActive = isScreenActive
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VenueMainContent(
    selectedLocation: String,
    onVenueClick: (VendorCardData) -> Unit,
    onLocationSelectorClick: () -> Unit,
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    outerAnimatedVisibilityScope: AnimatedVisibilityScope? = null,
    innerAnimatedVisibilityScope: AnimatedVisibilityScope? = null,
    isScreenActive: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    var selectedTab by remember { mutableStateOf("explore") }

    val viewOptions = listOf("By Timeline", "By List")
    var selectedViewType by remember { mutableStateOf(viewOptions[0]) }

    var timelineEvents by remember {
        mutableStateOf(
            listOf(
                TimelineEvent("1", "09th Sept, 2025", "Mehendi Ceremony", emptyList()),
                TimelineEvent("2", "10th Sept, 2025", "Haldi & Sangeet Ceremony", emptyList()),
                TimelineEvent("3", "12th Sept, 2025", "The Wedding Day", emptyList()),
                TimelineEvent("4", "15th Sept, 2025", "Reception Dinner", emptyList())
            )
        )
    }

    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        text = ""
        focusManager.clearFocus()
    }

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFilterDialog by remember { mutableStateOf(false) }

    val saveListSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSaveListBottomSheet by remember { mutableStateOf(false) }

    var activeTargetVenue by remember { mutableStateOf<VendorCardData?>(null) }
    var isMySavedListChecked by remember { mutableStateOf(true) }
    var selectedSaveEventId by remember { mutableStateOf<String?>(null) }

    var venueSavedDestinations by remember { mutableStateOf(mapOf<String, String>()) }

    val exploreVenues = remember { MockData.sampleVenues1 }

    val savedVenuesList = remember(venueSavedDestinations) {
        exploreVenues.filter { venue ->
            venueSavedDestinations.containsKey(venue.vendorName)
        }.map { venue ->
            venue.copy(isFavorite = true)
        }
    }

    val bottomTabs = remember(savedVenuesList.size) {
        listOf(
            TabItem("Explore", "explore", badgeCount = 24),
            TabItem("Saved", "saved", badgeCount = savedVenuesList.size)
        )
    }

    val sortOptions = listOf(
        "Newest First (Default)",
        "Oldest First",
        "Highest to Lowest Amount",
        "Lowest to Highest Amount"
    )
    var appliedSortOption by remember { mutableStateOf(sortOptions[0]) }

    val filterByOptions = listOf(
        "Venue", "Catering", "Gifts", "Staff & Crew",
        "Costumes", "Vendors", "Transportation", "Entertainment"
    )
    var appliedFilterOptions by remember { mutableStateOf(setOf<String>()) }

    val filteredAndSortedExploreVenues = remember(exploreVenues, venueSavedDestinations, appliedSortOption, appliedFilterOptions) {
        var result = exploreVenues.map { venue ->
            venue.copy(isFavorite = venueSavedDestinations.containsKey(venue.vendorName))
        }

        if (appliedFilterOptions.isNotEmpty()) {
            result = result.filter { venue ->
                appliedFilterOptions.any { filter ->
                    venue.services.any { it.equals(filter, ignoreCase = true) } ||
                            venue.vendorType?.equals(filter, ignoreCase = true) == true
                }
            }
        }

        result = when (appliedSortOption) {
            "Highest to Lowest Amount" -> {
                result.sortedByDescending { parsePrice(it.priceStartsFrom) }
            }
            "Lowest to Highest Amount" -> {
                result.sortedBy { parsePrice(it.priceStartsFrom) }
            }
            "Oldest First" -> {
                result.sortedBy { it.timestamp }
            }
            "Newest First (Default)" -> {
                result.sortedByDescending { it.timestamp }
            }
            else -> result
        }

        result
    }

    val savedTimelineEvents = remember(venueSavedDestinations, timelineEvents) {
        val list = mutableListOf<TimelineEvent>()

        val defaultSavedVenues = exploreVenues.filter { venue ->
            venueSavedDestinations[venue.vendorName] == "mysaved"
        }.map { venue ->
            venue.copy(isFavorite = true)
        }

        if (defaultSavedVenues.isNotEmpty()) {
            list.add(
                TimelineEvent(
                    id = "mysaved",
                    date = "Default List",
                    event = "My Saved List",
                    venues = defaultSavedVenues
                )
            )
        }

        val eventSections = timelineEvents.map { event ->
            val eventVenues = exploreVenues.filter { venue ->
                venueSavedDestinations[venue.vendorName] == event.id
            }.map { venue ->
                venue.copy(isFavorite = true)
            }
            event.copy(venues = eventVenues)
        }.filter { it.venues.isNotEmpty() }

        list.addAll(eventSections)
        list
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .statusBarsPadding()
                ) {
                    CustomTopBar(
                        title = "Venue",
                        onBackClick = { onBackClick() },
                        onMenuClick = if (!isSearchActive) { { } } else null,
                        isLargeTitle = true,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = outerAnimatedVisibilityScope
                    )
                }
            },
            bottomBar = {
                if (!isSearchActive) {
                    BottomTab(
                        items = bottomTabs,
                        selectedValue = selectedTab,
                        onItemSelected = { selectedTab = it }
                    )
                }
            },
        ) { paddingValues ->

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val isSaved = targetState == "saved"
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { fullWidth -> if (isSaved) fullWidth else -fullWidth }
                    ) + fadeIn(animationSpec = tween(300)) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { fullWidth -> if (isSaved) -fullWidth else fullWidth }
                            ) + fadeOut(animationSpec = tween(300))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundPrimary)
                    .padding(paddingValues),
                label = "explore_saved_slide_transition"
            ) { currentTab ->
                if (currentTab == "explore") {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                            .background(Color.Transparent),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            if (!isSearchActive) {
                                Spacer(Modifier.height(12.dp))
                                with(sharedTransitionScope) {
                                    LocationSelectorPill(
                                        location = selectedLocation,
                                        onLocationSelectorClick = onLocationSelectorClick,
                                        modifier = if (this != null && innerAnimatedVisibilityScope != null) {
                                            Modifier.sharedBounds(
                                                rememberSharedContentState(key = "location_picker"),
                                                animatedVisibilityScope = innerAnimatedVisibilityScope,
                                                boundsTransform = { _, _ ->
                                                    spring(
                                                        dampingRatio = 0.85f,
                                                        stiffness = 380f
                                                    )
                                                },
                                                resizeMode = ResizeMode.scaleToBounds(ContentScale.FillWidth, Alignment.Center),
                                                renderInOverlayDuringTransition = isScreenActive
                                            )
                                        } else Modifier
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomSearchBar(
                                    value = text,
                                    onValueChange = { text = it },
                                    onActiveChange = { active -> isSearchActive = active },
                                    modifier = Modifier.weight(1f),
                                    isAiSearch = true,
                                    placeholder = "Type your choices"
                                )

                                if (!isSearchActive) {
                                    Spacer(Modifier.width(8.dp))
                                    FilterButton(onClick = { showFilterDialog = true })
                                }
                            }
                        }

                        if (!isSearchActive) {
                            items(filteredAndSortedExploreVenues) { venue ->
                                VendorCardFull(
                                    vendor = venue,
                                    onBookCallClick = {},
                                    onFavoriteToggle = {
                                        if (venueSavedDestinations.containsKey(venue.vendorName)) {
                                            venueSavedDestinations = venueSavedDestinations - venue.vendorName
                                        } else {
                                            activeTargetVenue = venue
                                            isMySavedListChecked = true
                                            selectedSaveEventId = null
                                            showSaveListBottomSheet = true
                                        }
                                    },
                                    onCardClick = {},
                                    onChatClick = {},
                                )
                            }
                        } else {
                            item {
                                RecentSearchesSection(
                                    onVenueClick = {},
                                    recentVenues = MockData.sampleVenues1,
                                    onClearAll = {}
                                )
                            }
                        }
                        item { Spacer(Modifier.height(6.dp)) }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                            .background(Color.Transparent),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (savedVenuesList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_filter),
                                            contentDescription = "Empty States",
                                            tint = ContentTertiary,
                                            modifier = Modifier.size(56.dp)
                                        )
                                        Text(
                                            text = "Your Saved List is empty",
                                            style = JasnifyTheme.typography.headingMedium,
                                            color = ContentSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Tap the heart on venues to organize them for events.",
                                            style = JasnifyTheme.typography.bodyMedium,
                                            color = ContentTertiary,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            item{
                                IosSegmentedControl(
                                    options = viewOptions,
                                    selectedOption = selectedViewType,
                                    onOptionSelected = { selectedViewType = it },
                                    modifier = Modifier.padding(vertical = 12.dp)
                                        .height(44.dp)
                                )
                            }

                            if (selectedViewType == "By Timeline") {
                                if (savedTimelineEvents.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 80.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No events configured yet. Items saved to 'My Saved List'.",
                                                style = JasnifyTheme.typography.bodyLarge,
                                                color = ContentTertiary,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    items(savedTimelineEvents) { timelineItem ->
                                        TimelineSection(
                                            date = timelineItem.date,
                                            event = timelineItem.event,
                                            venues = timelineItem.venues,
                                            onVenueClick = { },
                                            onFavoriteToggle = { venue ->
                                                venueSavedDestinations = venueSavedDestinations - venue.vendorName
                                            }
                                        )
                                    }
                                }
                            } else {
                                items(savedVenuesList) { venue ->
                                    VendorCardFull(
                                        vendor = venue,
                                        onBookCallClick = {},
                                        onFavoriteToggle = {
                                            venueSavedDestinations = venueSavedDestinations - venue.vendorName
                                        },
                                        onCardClick = {},
                                        onChatClick = {},
                                    )
                                }
                            }
                        }
                        item { Spacer(Modifier.height(6.dp)) }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        SortFilterBottomSheet(
            sheetState = filterSheetState,
            sortOptions = sortOptions,
            initialSortOption = appliedSortOption,
            filterByOptions = filterByOptions,
            initialFilterOptions = appliedFilterOptions,
            onDismiss = { showFilterDialog = false },
            onApply = { sortOption, filterSet ->
                appliedSortOption = sortOption
                appliedFilterOptions = filterSet
                showFilterDialog = false
            }
        )
    }

    if (showSaveListBottomSheet) {
        SaveListBottomSheet(
            sheetState = saveListSheetState,
            timelineEvents = timelineEvents,
            isMySavedListChecked = isMySavedListChecked,
            onMySavedListToggled = { checked ->
                isMySavedListChecked = checked
                if (checked) {
                    selectedSaveEventId = null
                }
            },
            selectedEventId = selectedSaveEventId,
            onEventSelected = { eventId ->
                selectedSaveEventId = eventId
                if (eventId != null) {
                    isMySavedListChecked = false
                }
            },
            onAddNewEvent = { name, date ->
                val newId = (timelineEvents.size + 1).toString()
                timelineEvents = listOf(TimelineEvent(newId, date, name, emptyList())) + timelineEvents
                selectedSaveEventId = newId
                isMySavedListChecked = false
            },
            onDismiss = { showSaveListBottomSheet = false },
            onDone = {
                activeTargetVenue?.let { venue ->
                    val destination = if (isMySavedListChecked) "mysaved" else selectedSaveEventId
                    if (destination != null) {
                        venueSavedDestinations = venueSavedDestinations + (venue.vendorName to destination)
                    }
                }
                showSaveListBottomSheet = false
                activeTargetVenue = null
            }
        )
    }
}

@Composable
fun CustomCircleIndicator(
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (checked) ContentBrandDark else Color.Transparent)
            .border(
                width = if (checked) 0.dp else 1.5.dp,
                color = if (checked) Color.Transparent else ContentTertiary,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SaveListBottomSheet(
    sheetState: SheetState,
    timelineEvents: List<TimelineEvent>,
    isMySavedListChecked: Boolean,
    onMySavedListToggled: (Boolean) -> Unit,
    selectedEventId: String?,
    onEventSelected: (String?) -> Unit,
    onAddNewEvent: (name: String, date: String) -> Unit,
    onDismiss: () -> Unit,
    onDone: () -> Unit
) {
    // Hold a dynamic draft SubEventItem inside our SaveListBottomSheet to directly integrate TimeLineInput
    var draftNewEvent by remember { mutableStateOf<SubEventItem?>(null) }

    CustomBottomSheet(
        heading = "Manage Saved List",
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetHeight = 560.dp,
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfacePrimary)
        ) {
            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(SquircleShape(CornerLargeIncrease))
                            .background(SurfaceSecondary)
                            .clickable { onMySavedListToggled(!isMySavedListChecked) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "My Saved List",
                            style = JasnifyTheme.typography.labelXLarge,
                            color = ContentPrimary
                        )

                        CustomCircleIndicator(checked = isMySavedListChecked)
                    }
                }

                item {
                    OrDivider(
                        text = "OR",
                        dividerGap = 24.dp,
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Save for an event",
                                style = JasnifyTheme.typography.headingLarge,
                                fontWeight = FontWeight.Medium,
                                color = ContentPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(R.drawable.ic_info),
                                contentDescription = "Info panel",
                                tint = ContentPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.clickable {
                                // Directly initialize our unified SubEventItem to trigger the editing mode
                                draftNewEvent = SubEventItem(
                                    id = "temp-new-item",
                                    date = "",
                                    name = "",
                                    isExisting = false,
                                    isEditing = true
                                )
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus),
                                contentDescription = null,
                                tint = ContentBrandDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "New Event",
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentBrandDark
                            )
                        }
                    }
                }

                // Render the unified TimeLineInput component when adding a new event
                if (draftNewEvent != null) {
                    item {
                        TimeLineInput(
                            item = draftNewEvent!!,
                            onUpdate = { updatedItem ->
                                if (!updatedItem.isEditing) {
                                    if (updatedItem.isExisting) {
                                        // Save standard event when "Done" clicked
                                        onAddNewEvent(updatedItem.name, updatedItem.date)
                                    }
                                    draftNewEvent = null
                                } else {
                                    draftNewEvent = updatedItem
                                }
                            },
                            onDelete = {
                                draftNewEvent = null
                            },
                            backgroundColor = SurfaceSecondary,
                            hasBorder = true
                        )
                    }
                }

                itemsIndexed(timelineEvents) { index, eventItem ->
                    val isSelected = selectedEventId == eventItem.id

                    val cardShape = when {
                        timelineEvents.size == 1 -> RoundedCornerShape(CornerLargeIncrease)
                        index == 0 -> SquircleShape(CornerLargeIncrease, CornerLargeIncrease, CornerExtraSmall, CornerExtraSmall)
                        index == timelineEvents.lastIndex -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLargeIncrease, CornerLargeIncrease)
                        else -> RoundedCornerShape(CornerExtraSmall)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(cardShape)
                            .background(SurfaceSecondary)
                            .clickable { onEventSelected(if (isSelected) null else eventItem.id) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = eventItem.date,
                                style = JasnifyTheme.typography.labelLarge,
                                color = ContentSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = eventItem.event,
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary
                            )
                        }

                        CustomCircleIndicator(checked = isSelected)
                    }

                    // To inject a 2.dp gap between timeline cards
                    if (index < timelineEvents.lastIndex) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfacePrimary,
                tonalElevation = 0.dp
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(12.dp)
                    ) {
                        CustomTextButton(
                            onClick = onDone,
                            text = "Done",
                            shapeStyle = ButtonShapeStyle.Square,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineSection(
    date: String,
    event: String,
    venues: List<VendorCardData>,
    onVenueClick: (VendorCardData) -> Unit,
    onFavoriteToggle: (VendorCardData) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val accentColors = listOf(CloudWhisper, SoftMint, PaleLavender, LightSkyBlue, SoftPeach)
    val randomBackgroundColor = remember { accentColors.random() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(20.dp, 0f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(20.dp, 0f)
            )
            .background(randomBackgroundColor),
    ) {
        TimelineHeader(date = date, event = event)

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(venues) { venue ->
                VendorCardCompact(
                    vendor = venue,
                    removeBg = true,
                    onCardClick = { onVenueClick(venue) },
                    onFavoriteToggle = { onFavoriteToggle(venue) }
                )
            }
        }

        CarouselIndicator(
            listState = listState,
            totalItems = venues.size,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun CarouselIndicator(
    listState: LazyListState,
    totalItems: Int,
    modifier: Modifier = Modifier
) {
    if (totalItems <= 1) return
    val progress by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 0f
            else {
                val firstItem = visibleItems.first()
                val firstItemOffset = -firstItem.offset.toFloat()
                val totalScrollableRange = layoutInfo.totalItemsCount * firstItem.size - layoutInfo.viewportSize.width
                val currentScroll = (firstItem.index * firstItem.size) + firstItemOffset
                if (totalScrollableRange <= 0) 0f
                else (currentScroll / totalScrollableRange).coerceIn(0f, 1f)
            }
        }
    }
    val trackWidth = 28.dp
    val thumbWidth = trackWidth / totalItems

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(4.dp)
            .background(color = SurfaceSecondary, shape = CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(thumbWidth)
                .offset(x = (trackWidth - thumbWidth) * progress)
                .background(color = ContentBrandDark, shape = CircleShape)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationSelectorPill(
    location: String,
    onLocationSelectorClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(16.dp, 0f))
            .clickable { onLocationSelectorClick() },
        color = SurfaceBrandSecondary,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Venues in ",
                style = JasnifyTheme.typography.bodyXLarge,
                color = ContentBrandDark
            )

            Text(
                text = location,
                style = JasnifyTheme.typography.headingMedium,
                fontWeight = FontWeight.Medium,
                color = ContentBrandDark,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )

            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = ContentBrandDark,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = ContentBrandDark
            )
        }
    }
}

@Composable
fun TimelineHeader(date: String, event: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(date, style = JasnifyTheme.typography.labelLarge, color = ContentSecondary)
            Spacer(Modifier.height(4.dp))
            Text(event, style = JasnifyTheme.typography.labelXLarge, color = ContentBrandDark, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.MoreVert, "Options", tint = ContentPrimary)
    }
}

private fun parsePrice(priceString: String): Int {
    val clean = priceString
        .replace("₹", "")
        .replace(",", "")
        .replace(" ", "")
        .trim()
    return clean.toIntOrNull() ?: 0
}


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Preview(showBackground = true)
@Composable
fun PreviewVenueScreen() {
    JasnifyTheme {
        VenueScreen(selectedLocation = "Patna, Bihar", onVenueClick = {}, onBackClick = {})
    }
}