package com.harshdeep.jasnify.presentation.screens.venues

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.harshdeep.jasnify.R
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.vector.ImageVector
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomDeleteSheet
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

// --- SharedPreferences Helpers for Search History ---
private const val PREFS_NAME = "venue_search_prefs"
private const val KEY_RECENT_SEARCHES = "recent_searches"

private fun getRecentSearches(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
    return if (raw.isEmpty()) emptyList() else raw.split("|||")
}

private fun saveRecentSearch(context: Context, vendorName: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val current = getRecentSearches(context).toMutableList()
    current.remove(vendorName)
    current.add(0, vendorName)
    val limited = current.take(8)
    prefs.edit().putString(KEY_RECENT_SEARCHES, limited.joinToString("|||")).apply()
}

private fun clearRecentSearches(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().remove(KEY_RECENT_SEARCHES).apply()
}

data class TimelineEvent(
    val id: String,
    val date: String,
    val event: String,
    val venues: List<VendorCardData>
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueScreen(
    selectedLocation: String = "City, State",
    onVenueClick: (VendorCardData) -> Unit,
    onBackClick: () -> Unit,
    isScreenActive: Boolean = true
) {
    var currentAddress by remember { mutableStateOf(selectedLocation) }
    var isLocationPickerVisible by remember { mutableStateOf(false) }
    var showRoomAccess by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("explore") }

    // User Directory State
    var venueRoomUsers by remember {
        mutableStateOf(
            listOf(
                User("Anand K.", "viratanand", UserRole.OWNER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Steve R.", "captainamerica", UserRole.EDITOR, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Tony S.", "ironman", UserRole.EDITOR, "https://images.unsplash.com/photo-1531427186611-ecfd6d936c79?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Bruce B.", "hulk", UserRole.VIEWER, "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Thor O.", "thor", UserRole.EDITOR, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Natasha R.", "blackwidow", UserRole.VIEWER, "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Clint B.", "hawkeye", UserRole.VIEWER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80")
            )
        )
    }

    // Hoisted bottom sheets and dialog states to build correct 'isAnySheetVisible' overlays
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSaveListBottomSheet by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }

    // Hoisted dynamic details states for target venue saving interactions
    var activeTargetVenue by remember { mutableStateOf<VendorCardData?>(null) }
    var isMySavedListChecked by remember { mutableStateOf(true) }
    var selectedSaveEventId by remember { mutableStateOf<String?>(null) }
    var venueSavedDestinations by remember { mutableStateOf(mapOf<String, String>()) }
    var lastSavedVenue by remember { mutableStateOf<VendorCardData?>(null) }

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

    // Unified screen-level Toast State
    var toastData by remember { mutableStateOf<ToastData?>(null) }

    LaunchedEffect(toastData?.message) {
        if (toastData?.message != null) {
            delay(3000.milliseconds)
            toastData = null
        }
    }

    val focusManager = LocalFocusManager.current

    BackHandler(enabled = isLocationPickerVisible || showRoomAccess) {
        if (isLocationPickerVisible) {
            isLocationPickerVisible = false
        } else if (showRoomAccess) {
            showRoomAccess = false
        }
    }

    // Determine overlay occlusion layers to mask global toast alerts cleanly
    val isAnySheetVisible = showRoomMenuBottomSheet || (userToRemove != null) ||
            showFilterDialog || showSaveListBottomSheet || showMenuSheet

    val isSavedListToast = remember(toastData, lastSavedVenue) {
        toastData?.message?.contains("Saved List") == true && lastSavedVenue != null
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedContent(
            targetState = when {
                isLocationPickerVisible -> "picker"
                showRoomAccess -> "room"
                else -> "main"
            },
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
            },
            label = "venue_screen_transition"
        ) { state ->
            when (state) {
                "picker" -> {
                    LocationScreen(
                        initialSearches = emptyList(),
                        currentAddress = currentAddress,
                        onAddressSelected = {
                            currentAddress = it
                            isLocationPickerVisible = false
                        },
                        onBackClick = { isLocationPickerVisible = false },
                    )
                }
                "room" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SurfaceSecondary)
                    ) {
                        RoomScreen(
                            allUsers = venueRoomUsers,
                            currentUserRole = UserRole.OWNER,
                            isSelf = { it.username == "viratanand" },
                            onBackClick = { showRoomAccess = false },
                            onMenuClick = {
                                focusManager.clearFocus()
                                showRoomMenuBottomSheet = true
                            },
                            onRoleChange = { targetUser, newRole ->
                                venueRoomUsers = venueRoomUsers.map { user ->
                                    if (user.username == targetUser.username) user.copy(role = newRole) else user
                                }
                            },
                            onRemove = { targetUser ->
                                userToRemove = targetUser
                            },
                            onReport = { targetUser ->
                                toastData = ToastData("${targetUser.name} reported", ToastType.DEFAULT)
                            },
                            onLeave = {
                                toastData = ToastData("You left the room", ToastType.DEFAULT)
                                showRoomAccess = false
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                else -> {
                    VenueMainContent(
                        selectedLocation = currentAddress,
                        onVenueClick = onVenueClick,
                        onLocationSelectorClick = { isLocationPickerVisible = true },
                        onManageRoomAccessClick = { showRoomAccess = true },
                        onBackClick = onBackClick,
                        isScreenActive = isScreenActive,
                        toastData = toastData,
                        onShowToast = { toastData = it },
                        showFilterDialog = showFilterDialog,
                        onShowFilterDialogChange = { showFilterDialog = it },
                        showSaveListBottomSheet = showSaveListBottomSheet,
                        onShowSaveListBottomSheetChange = { showSaveListBottomSheet = it },
                        showMenuSheet = showMenuSheet,
                        onShowMenuSheetChange = { showMenuSheet = it },
                        timelineEvents = timelineEvents,
                        onTimelineEventsChange = { timelineEvents = it },
                        activeTargetVenue = activeTargetVenue,
                        onActiveTargetVenueChange = { activeTargetVenue = it },
                        isMySavedListChecked = isMySavedListChecked,
                        onMySavedListCheckedChange = { isMySavedListChecked = it },
                        selectedSaveEventId = selectedSaveEventId,
                        onSelectedSaveEventIdChange = { selectedSaveEventId = it },
                        venueSavedDestinations = venueSavedDestinations,
                        onVenueSavedDestinationsChange = { venueSavedDestinations = it },
                        lastSavedVenue = lastSavedVenue,
                        onLastSavedVenueChange = { lastSavedVenue = it },
                        selectedTab = selectedTab,
                        onSelectedTabChange = { selectedTab = it }
                    )
                }
            }
        }

        // --- Standard Alerts: Top-Center Toast Animation ---
        AnimatedVisibility(
            visible = toastData?.message != null && !isSavedListToast && !isAnySheetVisible,
            enter = slideInVertically(initialOffsetY = { -it - 500 }),
            exit = slideOutVertically(targetOffsetY = { -it - 500 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .zIndex(100f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            toastData?.let { data ->
                CustomToast(
                    message = data.message ?: "",
                    type = data.type
                )
            }
        }

        // --- Interactive Saved List Notification: Bottom-Up Toast Animation ---
        AnimatedVisibility(
            visible = toastData?.message != null && isSavedListToast && !isAnySheetVisible,
            enter = slideInVertically(initialOffsetY = { it + 500 }),
            exit = slideOutVertically(targetOffsetY = { it + 500 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 80.dp)
                .fillMaxWidth()
                .zIndex(100f)
                .padding(horizontal = 12.dp)
        ) {
            toastData?.let { data ->
                CustomToast(
                    message = data.message ?: "",
                    type = data.type,
                    leadingIcon = painterResource(id = R.drawable.ic_heart_filled),
                    buttonText = "Change",
                    onButtonClick = {
                        toastData = null
                        lastSavedVenue?.let { venue ->
                            activeTargetVenue = venue
                            val currentDest = venueSavedDestinations[venue.vendorName]
                            isMySavedListChecked = currentDest == "mysaved"
                            selectedSaveEventId = if (currentDest != "mysaved" && currentDest != null) currentDest else null
                            showSaveListBottomSheet = true
                        }
                    }
                )
            }
        }
    }

    if (showRoomMenuBottomSheet) {
        MenuBottomSheet(
            items = listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Copy Link",
                        icon = painterResource(R.drawable.ic_link),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            toastData = ToastData("Link Copied!", ToastType.SUCCESS)
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Add New Members",
                        icon = painterResource(R.drawable.ic_plus),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showRoomMenuBottomSheet = false
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Leave Room",
                        icon = painterResource(R.drawable.ic_logout),
                        iconPlacement = IconPlacement.Left,
                        contentColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            showRoomAccess = false
                        }
                    )
                )
            ),
            onCancelClick = {
                showRoomMenuBottomSheet = false
            }
        )
    }

    if (userToRemove != null) {
        CustomDeleteSheet(
            heading = "Remove Member from Venue Room?",
            subHeading = "They will not be able to access this room anymore.",
            confirmButtonText = "Remove",
            onDismiss = {
                userToRemove = null
            },
            onConfirmRemove = {
                val target = userToRemove
                if (target != null) {
                    venueRoomUsers = venueRoomUsers.filter { it.username != target.username }
                    toastData = ToastData("${target.name} removed from Room!", ToastType.SUCCESS)
                }
                userToRemove = null
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueMainContent(
    selectedLocation: String,
    onVenueClick: (VendorCardData) -> Unit,
    onLocationSelectorClick: () -> Unit,
    onManageRoomAccessClick: () -> Unit,
    onBackClick: () -> Unit,
    isScreenActive: Boolean = true,
    // Unified Hoisted States
    toastData: ToastData?,
    onShowToast: (ToastData?) -> Unit,
    showFilterDialog: Boolean,
    onShowFilterDialogChange: (Boolean) -> Unit,
    showSaveListBottomSheet: Boolean,
    onShowSaveListBottomSheetChange: (Boolean) -> Unit,
    showMenuSheet: Boolean,
    onShowMenuSheetChange: (Boolean) -> Unit,
    timelineEvents: List<TimelineEvent>,
    onTimelineEventsChange: (List<TimelineEvent>) -> Unit,
    activeTargetVenue: VendorCardData?,
    onActiveTargetVenueChange: (VendorCardData?) -> Unit,
    isMySavedListChecked: Boolean,
    onMySavedListCheckedChange: (Boolean) -> Unit,
    selectedSaveEventId: String?,
    onSelectedSaveEventIdChange: (String?) -> Unit,
    venueSavedDestinations: Map<String, String>,
    onVenueSavedDestinationsChange: (Map<String, String>) -> Unit,
    lastSavedVenue: VendorCardData?,
    onLastSavedVenueChange: (VendorCardData?) -> Unit,
    selectedTab: String,
    onSelectedTabChange: (String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val viewOptions = listOf("By Timeline", "By List")
    var selectedViewType by remember { mutableStateOf(viewOptions[0]) }

    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var recentSearches by remember {
        mutableStateOf(getRecentSearches(context))
    }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        text = ""
        focusManager.clearFocus()
    }

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val saveListSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val exploreVenues = remember { MockData.sampleVenues1 }

    val recentVenuesList = remember(recentSearches, exploreVenues) {
        recentSearches.mapNotNull { name ->
            exploreVenues.find { it.vendorName == name }
        }
    }

    val handleVenueClick: (VendorCardData) -> Unit = { venue ->
        saveRecentSearch(context, venue.vendorName)
        recentSearches = getRecentSearches(context)
        onVenueClick(venue)
    }

    // Interactive helper for when the user clicks the like/favorite button directly
    val handleFavoriteToggle: (VendorCardData) -> Unit = { venue ->
        val alreadySaved = venueSavedDestinations.containsKey(venue.vendorName)
        if (alreadySaved) {
            // Already Saved: Unlike action triggers the "Manage Saved List" bottom sheet
            onActiveTargetVenueChange(venue)
            val currentDestination = venueSavedDestinations[venue.vendorName]
            onMySavedListCheckedChange(currentDestination == "mysaved")
            onSelectedSaveEventIdChange(if (currentDestination != "mysaved" && currentDestination != null) currentDestination else null)
            onShowSaveListBottomSheetChange(true)
        } else {
            // Not Saved: Clicking like directly adds to the "My Saved List"
            onVenueSavedDestinationsChange(venueSavedDestinations + (venue.vendorName to "mysaved"))
            onLastSavedVenueChange(venue)
            onShowToast(ToastData("Added to Saved List!", ToastType.DEFAULT))
        }
    }

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .statusBarsPadding()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusManager.clearFocus()
                        }
                ) {
                    CustomTopBar(
                        title = "Venue",
                        onBackClick = { onBackClick() },
                        onMenuClick = if (!isSearchActive) { { onShowMenuSheetChange(true) } } else null,
                        isLargeTitle = true,
                    )
                }
            },
            bottomBar = {
                if (!isSearchActive) {
                    BottomTab(
                        items = bottomTabs,
                        selectedValue = selectedTab,
                        onItemSelected = { onSelectedTabChange(it) }
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
                            .background(Color.Transparent),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            AnimatedVisibility(
                                visible = !isSearchActive,
                                enter = fadeIn(animationSpec = tween(250)) + expandVertically(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(250))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Spacer(Modifier.height(12.dp))
                                    LocationSelectorPill(
                                        location = selectedLocation,
                                        onLocationSelectorClick = onLocationSelectorClick,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
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

                                AnimatedVisibility(
                                    visible = !isSearchActive,
                                    enter = fadeIn(animationSpec = tween(200)) +
                                            expandHorizontally(expandFrom = Alignment.End, animationSpec = tween(250)),
                                    exit = fadeOut(animationSpec = tween(150)) +
                                            shrinkHorizontally(shrinkTowards = Alignment.End, animationSpec = tween(250))
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Spacer(Modifier.width(8.dp))
                                        FilterButton(onClick = { onShowFilterDialogChange(true) })
                                    }
                                }
                            }
                        }

                        if (!isSearchActive) {
                            items(filteredAndSortedExploreVenues) { venue ->
                                VendorCardFull(
                                    vendor = venue,
                                    onFavoriteToggle = { handleFavoriteToggle(venue) },
                                    onCardClick = { handleVenueClick(venue) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp),
                                )
                            }
                        } else {
                            item {
                                TrendingAiSearchesSection(
                                    onTrendingClick = { query ->
                                        text = query
                                        focusManager.clearFocus()
                                    }
                                )
                            }
                            if (recentVenuesList.isNotEmpty()) {
                                item {
                                    RecentSearchesSection(
                                        onVenueClick = handleVenueClick,
                                        recentVenues = recentVenuesList,
                                        onClearAll = {
                                            clearRecentSearches(context)
                                            recentSearches = emptyList()
                                        }
                                    )
                                }
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
                        item {
                            IosSegmentedControl(
                                options = viewOptions,
                                selectedOption = selectedViewType,
                                onOptionSelected = { selectedViewType = it },
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .height(44.dp)
                            )
                        }

                        if (selectedViewType == "By Timeline") {
                            if (savedTimelineEvents.isEmpty()) {
                                item {
                                    EmptySavedState()
                                }
                            } else {
                                items(savedTimelineEvents) { timelineItem ->
                                    TimelineSection(
                                        date = timelineItem.date,
                                        event = timelineItem.event,
                                        venues = timelineItem.venues,
                                        onVenueClick = handleVenueClick,
                                        onFavoriteToggle = { venue -> handleFavoriteToggle(venue) },
                                    )
                                }
                            }
                        } else {
                            if (savedVenuesList.isEmpty()) {
                                item {
                                    EmptySavedState()
                                }
                            } else {
                                items(savedVenuesList) { venue ->
                                    VendorCardFull(
                                        vendor = venue,
                                        onFavoriteToggle = { handleFavoriteToggle(venue) },
                                        onCardClick = { handleVenueClick(venue) },
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
            onDismiss = { onShowFilterDialogChange(false) },
            onApply = { sortOption, filterSet ->
                appliedSortOption = sortOption
                appliedFilterOptions = filterSet
                onShowFilterDialogChange(false)
            }
        )
    }

    if (showSaveListBottomSheet) {
        SaveListBottomSheet(
            sheetState = saveListSheetState,
            timelineEvents = timelineEvents,
            isMySavedListChecked = isMySavedListChecked,
            onMySavedListToggled = { checked ->
                onMySavedListCheckedChange(checked)
                if (checked) {
                    onSelectedSaveEventIdChange(null)
                }
            },
            selectedEventId = selectedSaveEventId,
            onEventSelected = { eventId ->
                onSelectedSaveEventIdChange(eventId)
                if (eventId != null) {
                    onMySavedListCheckedChange(false)
                }
            },
            onAddNewEvent = { name, date ->
                val newId = (timelineEvents.size + 1).toString()
                onTimelineEventsChange(listOf(TimelineEvent(newId, date, name, emptyList())) + timelineEvents)
                onSelectedSaveEventIdChange(newId)
                onMySavedListCheckedChange(false)
            },
            onDismiss = { onShowSaveListBottomSheetChange(false) },
            onDone = {
                activeTargetVenue?.let { venue ->
                    val destination = if (isMySavedListChecked) "mysaved" else selectedSaveEventId
                    if (destination != null) {
                        onVenueSavedDestinationsChange(venueSavedDestinations + (venue.vendorName to destination))
                        onLastSavedVenueChange(venue)
                        onShowToast(ToastData("Added to Saved List!", ToastType.DEFAULT))
                    } else {
                        // User unchecked all lists -> This confirms the "unlike" behavior
                        onVenueSavedDestinationsChange(venueSavedDestinations - venue.vendorName)
                        onShowToast(ToastData("Removed from Saved List", ToastType.DEFAULT))
                    }
                }
                onShowSaveListBottomSheetChange(false)
                onActiveTargetVenueChange(null)
            }
        )
    }

    if (showMenuSheet) {
        MenuBottomSheet(
            items = listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Change Location",
                        icon = painterResource(R.drawable.ic_location_marker),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            onShowMenuSheetChange(false)
                            onLocationSelectorClick()
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Manage Room Access",
                        icon = painterResource(R.drawable.ic_user_default),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            onShowMenuSheetChange(false)
                            onManageRoomAccessClick()
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = painterResource(R.drawable.ic_help_feedback),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            onShowMenuSheetChange(false)
                        }
                    )
                )
            ),
            onCancelClick = { onShowMenuSheetChange(false) }
        )
    }
}

@Composable
fun LazyItemScope.EmptySavedState() {
    Box(
        modifier = Modifier
            .fillParentMaxHeight(0.7f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_receipt),
                contentDescription = "No plans here yet",
                tint = ContentTertiary,
                modifier = Modifier.size(84.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No plans here yet",
                style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                color = ContentTertiary,
                textAlign = TextAlign.Center
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

                        CustomChecker(
                            checked = isMySavedListChecked,
                            onCheckedChange = null
                        )
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

                if (draftNewEvent != null) {
                    item {
                        TimeLineInput(
                            item = draftNewEvent!!,
                            onUpdate = { updatedItem ->
                                if (!updatedItem.isEditing) {
                                    if (updatedItem.isExisting) {
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

                        CustomChecker(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                    }

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

        if (venues.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No venues saved yet.",
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
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
                painter = painterResource(R.drawable.ic_location_marker),
                contentDescription = null,
                tint = ContentBrandDark,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(20.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrendingAiSearchesSection(
    onTrendingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val aiIcon = ImageVector.vectorResource(id = R.drawable.ic_ai)
    val trendingQueries = listOf(
        "4.5+ Rated",
        "Hotels for 800 guests",
        "Vintage Themed Hotels"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trend_up),
                    contentDescription = "Trending",
                    tint = ContentPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Trending AI Searches",
                    style = JasnifyTheme.typography.headingMedium,
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary
                )
            }

            CustomIconButton(
                onClick = { },
                icon = painterResource(R.drawable.ic_info),
                contentColor = ContentPrimary,
                containerColor = SurfacePrimary,
                size = ButtonSize.Small
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            trendingQueries.forEach { query ->
                FilterChip(
                    label = query,
                    isSelected = false,
                    shapeStyle = ChipShapeStyle.Round,
                    size = ChipSize.Small,
                    leadingIcon = aiIcon,
                    onClick = { onTrendingClick(query) },
                    hasStroke = true,
                    isAiMode = true
                )
            }
        }
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