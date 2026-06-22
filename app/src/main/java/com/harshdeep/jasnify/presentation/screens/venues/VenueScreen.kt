package com.harshdeep.jasnify.presentation.screens.venues

import com.harshdeep.jasnify.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.data.mock.MockData
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
import com.harshdeep.jasnify.presentation.components.buttons.CustomRadioButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueScreen(
    selectedLocation: String = "Patna, Bihar",
    onVenueClick: (VendorCardData) -> Unit,
    onLocationSelectorClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
                    isLargeTitle = true
                )

                if (selectedTab == "saved") {
                    IosSegmentedControl(
                        options = viewOptions,
                        selectedOption = selectedViewType,
                        onOptionSelected = { selectedViewType = it },
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        bottomBar = {
            if(!isSearchActive){
                BottomTab(
                    items = bottomTabs,
                    selectedValue = selectedTab,
                    onItemSelected = { selectedTab = it }
                )
            }
        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == "explore") {
                item {
                    if(!isSearchActive){
                        Spacer(Modifier.height(12.dp))
                        LocationSelectorPill(location = selectedLocation, onLocationSelectorClick)
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
                            modifier = Modifier.weight(1f)
                        )

                        if(!isSearchActive){
                            Spacer(Modifier.width(8.dp))
                            FilterButton(onClick = { showFilterDialog = true })
                        }
                    }
                }

                if(!isSearchActive){
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
                item{Spacer(Modifier.height(6.dp))}
            } else {
                if (savedVenuesList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
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
                item{Spacer(Modifier.height(6.dp))}
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
            events = timelineEvents,
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
                timelineEvents = timelineEvents + TimelineEvent(newId, date, name, emptyList())
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveListBottomSheet(
    sheetState: SheetState,
    events: List<TimelineEvent>,
    isMySavedListChecked: Boolean,
    onMySavedListToggled: (Boolean) -> Unit,
    selectedEventId: String?,
    onEventSelected: (String?) -> Unit,
    onAddNewEvent: (name: String, date: String) -> Unit,
    onDismiss: () -> Unit,
    onDone: () -> Unit
) {
    var showCreateEventDialog by remember { mutableStateOf(false) }
    var newEventName by remember { mutableStateOf("") }
    var newEventDate by remember { mutableStateOf("") }

    CustomBottomSheet(
        heading = "Manage Saved List",
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetHeight = 480.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEBECEB))
                            .clickable { onMySavedListToggled(!isMySavedListChecked) }
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "My Saved List",
                            style = JasnifyTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = ContentPrimary
                        )

                        CustomChecker(
                            checked = isMySavedListChecked,
                            onCheckedChange = { onMySavedListToggled(it) }
                        )
                    }
                }

                item {
                    OrDivider(text = "OR", dividerGap = 4.dp)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Save for an event",
                                style = JasnifyTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = ContentPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info panel",
                                tint = ContentTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.clickable { showCreateEventDialog = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = ContentBrandDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "New Event",
                                style = JasnifyTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ContentBrandDark
                            )
                        }
                    }
                }

                items(events) { eventItem ->
                    val isSelected = selectedEventId == eventItem.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEBECEB))
                            .clickable { onEventSelected(if (isSelected) null else eventItem.id) }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = eventItem.date,
                                style = JasnifyTheme.typography.labelLarge,
                                color = ContentTertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = eventItem.event,
                                style = JasnifyTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = ContentPrimary
                            )
                        }

                        CustomChecker(
                            checked = isSelected,
                            onCheckedChange = { onEventSelected(if (it) eventItem.id else null) }
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = SurfacePrimary,
                tonalElevation = 0.dp
            ) {
                Column {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        CustomTextButton(
                            onClick = onDone,
                            text = "Done",
                            size = ButtonSize.Medium,
                            type = ButtonType.Primary,
                            shapeStyle = ButtonShapeStyle.Square,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (showCreateEventDialog) {
        AlertDialog(
            onDismissRequest = { showCreateEventDialog = false },
            title = { Text("Create New Event", style = JasnifyTheme.typography.headingSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newEventName,
                        onValueChange = { newEventName = it },
                        label = { Text("Event Title") },
                        placeholder = { Text("e.g. Reception Party") }
                    )
                    OutlinedTextField(
                        value = newEventDate,
                        onValueChange = { newEventDate = it },
                        label = { Text("Event Date") },
                        placeholder = { Text("e.g. 18th Sept, 2025") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newEventName.isNotBlank() && newEventDate.isNotBlank()) {
                            onAddNewEvent(newEventName, newEventDate)
                            newEventName = ""
                            newEventDate = ""
                            showCreateEventDialog = false
                        }
                    }
                ) {
                    Text("Create", color = ContentBrandDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateEventDialog = false }) {
                    Text("Cancel", color = ContentTertiary)
                }
            }
        )
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
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
            Text("Venues in ", style = JasnifyTheme.typography.bodyXLarge, color = ContentBrandDark)

            // Replaced multiline wrap with premium marquee scrolls
            Text(
                text = location,
                style = JasnifyTheme.typography.headingMedium,
                fontWeight = FontWeight.Medium,
                color = ContentBrandDark,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee()
            )

            Icon(Icons.Outlined.LocationOn, null, tint = ContentBrandDark, modifier = Modifier.size(24.dp).padding(start = 4.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Outlined.KeyboardArrowRight, null, tint = ContentBrandDark)
        }
    }
}

@Composable
fun TimelineHeader(date: String, event: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
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

@Preview(showBackground = true)
@Composable
fun PreviewVenueScreen() {
    JasnifyTheme {
        VenueScreen(selectedLocation = "Patna, Bihar", onVenueClick = {}, onLocationSelectorClick = {}, onBackClick = {})
    }
}