package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.domain.model.SubEvent
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SaveListBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardFull
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.sections.ExploreCategoriesHorizontal
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.SavedTimelineItemsScreen
import com.harshdeep.jasnify.presentation.components.sections.TimelineSection
import com.harshdeep.jasnify.presentation.components.sections.TrendingAiSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.VendorCarousel
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryGrid
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.components.sections.vendorCategories
import com.harshdeep.jasnify.presentation.components.states.EmptyState
import com.harshdeep.jasnify.presentation.components.states.SearchSuggestionItem
import com.harshdeep.jasnify.presentation.components.states.StandaloneEmptyState
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.navigation.ScreenTransitions
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.screens.venues.LocationScreen
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

enum class VendorScreenState {
    MAIN, CATEGORY_DETAIL, ALL_SAVED, ROOM, VENDOR_DETAIL, LOCATION_SELECTOR, TIMELINE_DETAIL
}

private const val PREFS_NAME = "vendor_search_prefs"
private const val KEY_RECENT_SEARCHES = "recent_searches"

private fun getRecentSearches(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
    return if (raw.isEmpty()) emptyList() else raw.split("|||")
}

private fun saveRecentSearch(context: Context, name: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val current = getRecentSearches(context).toMutableList()
    current.remove(name)
    current.add(0, name)
    val limited = current.take(8)
    prefs.edit { putString(KEY_RECENT_SEARCHES, limited.joinToString("|||")) }
}

private fun getCategoryRecentSearches(context: Context, category: String): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString("${KEY_RECENT_SEARCHES}_$category", null) ?: return emptyList()
    return if (raw.isEmpty()) emptyList() else raw.split("|||")
}

private fun saveCategoryRecentSearch(context: Context, category: String, name: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val current = getCategoryRecentSearches(context, category).toMutableList()
    current.remove(name)
    current.add(0, name)
    val limited = current.take(8)
    prefs.edit { putString("${KEY_RECENT_SEARCHES}_$category", limited.joinToString("|||")) }
}

private fun parsePrice(priceString: String): Int {
    return priceString
        .replace(Regex("[^0-9]"), "")
        .toIntOrNull() ?: 0
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsTab(
    mainNavController: NavHostController,
    internalNavController: NavHostController? = null,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    initialCategory: VendorCategoryItem? = null,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var screenStack by remember {
        mutableStateOf(if (initialCategory != null) listOf(VendorScreenState.MAIN, VendorScreenState.CATEGORY_DETAIL) else listOf(VendorScreenState.MAIN))
    }
    val currentScreenState by remember(screenStack) { derivedStateOf { screenStack.last() } }

    val mainListState = rememberLazyListState()
    val categoryListState = remember(selectedCategory) { LazyListState() }
    val allSavedGridState = rememberLazyGridState()
    val categorySavedGridState = remember(selectedCategory) { LazyGridState() }

    var selectedVendor by remember { mutableStateOf<Vendor?>(null) }
    var selectedTimelineEventId by remember { mutableStateOf<String?>(null) }

    var selectedCategoryTab by remember(selectedCategory) { mutableStateOf("explore") }
    var selectedSavedViewType by remember(selectedCategory) { mutableStateOf("By Timeline") }

    val selectedCategoryNameFromHome by (internalNavController ?: mainNavController).currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("selected_category_name", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(selectedCategoryNameFromHome) {
        selectedCategoryNameFromHome?.let { name ->
            val cat = vendorCategories.find { it.name == name }
            if (cat != null) {
                selectedCategory = cat
                screenStack += VendorScreenState.CATEGORY_DETAIL
                (internalNavController ?: mainNavController).currentBackStackEntry?.savedStateHandle?.remove<String>("selected_category_name")
            }
        }
    }

    var recentSearchesNames by remember { mutableStateOf(getRecentSearches(context)) }

    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val savedVendorsFromCloud by vendorViewModel.savedVendors.collectAsStateWithLifecycle()
    val allVendorsFromRepo by vendorViewModel.allVendors.collectAsStateWithLifecycle()
    val isLoading by vendorViewModel.isLoading.collectAsStateWithLifecycle()

    val vendorSavedDestinations = remember(savedVendorsFromCloud) {
        savedVendorsFromCloud.associate { "${it.vendorName}-${it.category}" to it.destination }
    }

    var toastData by remember { mutableStateOf(ToastData()) }
    var lastSavedVendor by remember { mutableStateOf<Vendor?>(null) }

    val isSavedListToast = remember(toastData, lastSavedVendor) {
        toastData.message?.contains("Saved List") == true && lastSavedVendor != null
    }

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    LaunchedEffect(Unit) {
        roomViewModel.resetAccessState()
    }

    LaunchedEffect(activeEventId) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (activeEventId != null) {
            roomViewModel.verifyAccess(activeEventId!!, "Vendors", uid)
            roomViewModel.loadRoomUsers(activeEventId!!, "Vendors")
            vendorViewModel.setEventId(activeEventId!!)
        } else {
            roomViewModel.setAccessState(true)
        }
    }

    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }

    val selectedCity by mainNavController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("selected_location", "City, State")
        ?.collectAsState() ?: remember { mutableStateOf("City, State") }

    val categories = vendorCategories
    val allSampleVendors = MockData.sampleVendors

    val recentVendorsList = remember(recentSearchesNames, allSampleVendors) {
        recentSearchesNames.mapNotNull { name ->
            allSampleVendors.find { it.name == name }
        }
    }

    var showSaveListBottomSheet by remember { mutableStateOf(false) }
    var activeTargetVendor by remember { mutableStateOf<Vendor?>(null) }

    val timelineEvents by remember(activeEvent) {
        derivedStateOf {
            activeEvent?.subEvents?.map { subEvent ->
                val formattedDate = subEvent.date?.let { timestamp ->
                    val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                    sdf.format(Date(timestamp))
                } ?: "Date TBD"

                TimelineEvent(
                    id = subEvent.id,
                    date = formattedDate,
                    event = subEvent.name,
                    venues = emptyList()
                )
            } ?: emptyList()
        }
    }

    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val isOwner = activeEvent?.ownerId == FirebaseAuth.getInstance().currentUser?.uid
    val currentUserInRoom = roomUsers.find { it.uid == FirebaseAuth.getInstance().currentUser?.uid }
    val currentUserRole = when {
        isOwner -> UserRole.OWNER
        currentUserInRoom != null -> currentUserInRoom.role
        else -> UserRole.VIEWER
    }
    val isViewer = currentUserRole == UserRole.VIEWER

    val exploreVendors = remember(allVendorsFromRepo) {
        allVendorsFromRepo.ifEmpty { MockData.sampleVendors }
    }

    val currentSelectedTimelineEvent = remember(selectedTimelineEventId, timelineEvents) {
        val baseEvent = if (selectedTimelineEventId == "mysaved") {
            TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List")
        } else {
            timelineEvents.find { it.id == selectedTimelineEventId }
        }
        baseEvent
    }

    val currentSelectedTimelineVendors = remember(selectedTimelineEventId, selectedCategory, vendorSavedDestinations, exploreVendors) {
        if (selectedTimelineEventId == null) return@remember emptyList<Vendor>()

        val categoryFiltered = if (selectedCategory != null) {
            exploreVendors.filter { it.category == selectedCategory!!.name }
        } else {
            exploreVendors
        }

        categoryFiltered.filter { v ->
            vendorSavedDestinations["${v.name}-${v.category}"] == selectedTimelineEventId
        }.map { it.copy(favorite = true) }
    }

    val handleVendorClick: (Vendor) -> Unit = { vendor ->
        saveRecentSearch(context, vendor.name)
        recentSearchesNames = getRecentSearches(context)
        selectedVendor = vendor
        screenStack = screenStack + VendorScreenState.VENDOR_DETAIL
    }

    val handleFavoriteToggle: (Vendor) -> Unit = { vendor ->
        val alreadySaved = vendorSavedDestinations.containsKey("${vendor.name}-${vendor.category}")
        if (alreadySaved) {
            activeTargetVendor = vendor
            showSaveListBottomSheet = true
        } else {
            vendorViewModel.toggleSaveVendor(vendor, isViewer, "mysaved")
            lastSavedVendor = vendor
            toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
        }
    }

    val handleTimelineSeeAll: (TimelineEvent) -> Unit = { event ->
        selectedTimelineEventId = event.id
        screenStack = screenStack + VendorScreenState.TIMELINE_DETAIL
    }

    var isBottomBarVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }
    val vendorsTabNestedScrollConnection = remember(currentScreenState, mainListState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // Only react if we are in MAIN screen and it's scrollable
                if (currentScreenState != VendorScreenState.MAIN) return Offset.Zero
                val canScroll = mainListState.canScrollForward || mainListState.canScrollBackward
                if (!canScroll) return Offset.Zero

                if (delta > 0) { // Scrolling up (showing)
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) { // Scrolling down (hiding)
                    if (scrollAccumulator > 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                }

                if (scrollAccumulator > 150f && !isBottomBarVisible) {
                    isBottomBarVisible = true
                    scrollAccumulator = 0f
                } else if (scrollAccumulator < -150f && isBottomBarVisible) {
                    isBottomBarVisible = false
                    scrollAccumulator = 0f
                }

                return Offset.Zero
            }
        }
    }

    LaunchedEffect(showMenuSheet, showRoomMenuBottomSheet, isSearchActive, currentScreenState, showSaveListBottomSheet, hasAccess, isBottomBarVisible) {
        val isBottomBarVisibleEffective = isBottomBarVisible && hasAccess == true && !showMenuSheet && !showRoomMenuBottomSheet && !isSearchActive && !showSaveListBottomSheet && currentScreenState == VendorScreenState.MAIN
        onBottomBarVisibilityChange(isBottomBarVisibleEffective)
    }

    BackHandler {
        if (showSaveListBottomSheet) {
            showSaveListBottomSheet = false
        } else if (showRoomMenuBottomSheet) {
            showRoomMenuBottomSheet = false
        } else if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
            focusManager.clearFocus()
        } else {
            if (screenStack.size > 1) {
                if (currentScreenState == VendorScreenState.CATEGORY_DETAIL) {
                    selectedCategory = null
                }
                screenStack = screenStack.dropLast(1)
            } else {
                onBackClick()
            }
        }
    }

    val isAnySheetVisible = showMenuSheet || showRoomMenuBottomSheet || showSaveListBottomSheet || userToRemove != null || showLeaveConfirmation
    val targetScale = if (isAnySheetVisible) 0.92f + (0.08f * sheetMotionProgress) else 1.0f
    val backdropScale by animateFloatAsState(targetValue = targetScale, animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f), label = "backdropScale")
    val backdropCornerRadius by animateDpAsState(targetValue = if (isAnySheetVisible) CornerExtraLarge else 0.dp, animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy), label = "backdropCornerRadius")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        RoomAccessGuardian(
            hasAccess = hasAccess,
            roomName = "Vendors",
            onBackClick = onBackClick
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = backdropScale
                        scaleY = backdropScale
                        clip = isAnySheetVisible || backdropCornerRadius > 0.dp
                        shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                    }
                    .background(BackgroundPrimary)
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                    .nestedScroll(vendorsTabNestedScrollConnection)
            ) {
                AnimatedContent(
                    targetState = currentScreenState,
                    transitionSpec = {
                        when {
                            targetState == VendorScreenState.VENDOR_DETAIL || 
                            targetState == VendorScreenState.LOCATION_SELECTOR || 
                            targetState == VendorScreenState.TIMELINE_DETAIL -> 
                                ScreenTransitions.SlideBottomToTopFastTransition
                            
                            initialState == VendorScreenState.VENDOR_DETAIL || 
                            initialState == VendorScreenState.LOCATION_SELECTOR || 
                            initialState == VendorScreenState.TIMELINE_DETAIL -> 
                                ScreenTransitions.SlideTopToBottomFastTransition

                            else -> ScreenTransitions.FadeInOutDefaultTransition
                        }
                    },
                    label = "VendorTabTransition"
                ) { state ->
                    when (state) {
                        VendorScreenState.MAIN -> {
                            VendorMainContent(
                                selectedCity = selectedCity,
                                categories = categories,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it },
                                isSearchActive = isSearchActive,
                                onSearchActiveChange = { isSearchActive = it },
                                onMenuClick = { showMenuSheet = true },
                                onLocationClick = {
                                    screenStack = screenStack + VendorScreenState.LOCATION_SELECTOR
                                },
                                onCategoryClick = { category ->
                                    selectedCategory = category
                                    screenStack = screenStack + VendorScreenState.CATEGORY_DETAIL
                                },
                                onVendorClick = handleVendorClick,
                                onFavoriteToggle = handleFavoriteToggle,
                                recentVendorsList = recentVendorsList,
                                focusManager = focusManager,
                                context = context,
                                onRecentSearchesUpdate = { recentSearchesNames = it },
                                allVendors = allVendorsFromRepo,
                                isLoading = isLoading,
                                listState = mainListState
                            )
                        }
                        VendorScreenState.CATEGORY_DETAIL -> {
                            selectedCategory?.let { category ->
                                val categoryVendors = remember(allVendorsFromRepo, category.name) {
                                    allVendorsFromRepo.filter { it.category == category.name }
                                }
                                val categorySavedVendors = remember(savedVendorsFromCloud, category.name) {
                                    savedVendorsFromCloud.filter { it.category == category.name }
                                }

                                VendorCategoryDetailContent(
                                    category = category,
                                    allVendors = categoryVendors,
                                    savedVendorsForCategory = categorySavedVendors,
                                    selectedCity = selectedCity,
                                    onBackClick = { screenStack = screenStack.dropLast(1) },
                                    onLocationClick = {
                                        screenStack = screenStack + VendorScreenState.LOCATION_SELECTOR
                                    },
                                    onMenuClick = { showMenuSheet = true },
                                    onVendorClick = handleVendorClick,
                                    onFavoriteToggle = handleFavoriteToggle,
                                    vendorSavedDestinations = vendorSavedDestinations,
                                    timelineEvents = timelineEvents,
                                    selectedTab = selectedCategoryTab,
                                    onSelectedTabChange = { selectedCategoryTab = it },
                                    selectedViewType = selectedSavedViewType,
                                    onSelectedViewTypeChange = { selectedSavedViewType = it },
                                    isLoading = isLoading,
                                    onTimelineSeeAll = handleTimelineSeeAll,
                                    listState = categoryListState,
                                    gridState = categorySavedGridState,
                                    isBottomBarVisible = isBottomBarVisible
                                )
                            }
                        }
                        VendorScreenState.VENDOR_DETAIL -> {
                            selectedVendor?.let { vendor ->
                                val detailData = remember(vendor, exploreVendors, vendorSavedDestinations) {
                                    val base = exploreVendors.find { it.name == vendor.name && it.category == vendor.category } ?: vendor
                                    base.copy(favorite = vendorSavedDestinations.containsKey("${base.name}-${base.category}"))
                                }
                                VendorDetailScreen(
                                    vendorDetail = detailData,
                                    onBackClick = { screenStack = screenStack.dropLast(1) },
                                    onFavoriteToggle = { handleFavoriteToggle(it) }
                                )
                            }
                        }
                        VendorScreenState.ALL_SAVED -> {
                            AllSavedVendorsContent(
                                onBackClick = { screenStack = screenStack.dropLast(1) },
                                onVendorClick = handleVendorClick,
                                onFavoriteToggle = handleFavoriteToggle,
                                vendorSavedDestinations = vendorSavedDestinations,
                                timelineEvents = timelineEvents,
                                allVendors = allVendorsFromRepo,
                                selectedViewType = selectedSavedViewType,
                                onSelectedViewTypeChange = { selectedSavedViewType = it },
                                isLoading = isLoading,
                                onTimelineSeeAll = handleTimelineSeeAll,
                                gridState = allSavedGridState
                            )
                        }
                        VendorScreenState.TIMELINE_DETAIL -> {
                            currentSelectedTimelineEvent?.let { event ->
                                SavedTimelineItemsScreen(
                                    title = "Saved Vendors",
                                    date = event.date,
                                    event = event.event,
                                    vendors = currentSelectedTimelineVendors,
                                    onVendorClick = handleVendorClick,
                                    onVendorFavoriteToggle = handleFavoriteToggle,
                                    onBackClick = { screenStack = screenStack.dropLast(1) }
                                )
                            }
                        }
                        VendorScreenState.ROOM -> {
                            activeEvent?.let { event ->
                                VendorRoomContent(
                                    eventId = event.id,
                                    roomViewModel = roomViewModel,
                                    onBackClick = { screenStack = screenStack.dropLast(1) },
                                    onMenuClick = { showRoomMenuBottomSheet = true },
                                    onRemove = { userToRemove = it },
                                    onLeave = { showLeaveConfirmation = true },
                                    onShowToast = { toastData = it }
                                )
                            }
                        }
                        VendorScreenState.LOCATION_SELECTOR -> {
                            LocationScreen(
                                initialSearches = emptyList(),
                                currentAddress = selectedCity,
                                onAddressSelected = {
                                    mainNavController.currentBackStackEntry?.savedStateHandle?.set("selected_location", it)
                                    screenStack = screenStack.dropLast(1)
                                },
                                onBackClick = {
                                    screenStack = screenStack.dropLast(1)
                                },
                                backIcon = TopIcon.Predefined.DOWN
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = toastData.message != null && !isSavedListToast && !isAnySheetVisible,
            enter = slideInVertically(initialOffsetY = { -it - 500 }),
            exit = slideOutVertically(targetOffsetY = { -it - 500 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .zIndex(100f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            CustomToast(
                message = toastData.message ?: "",
                type = toastData.type
            )
        }

        AnimatedVisibility(
            visible = toastData.message != null && isSavedListToast && !isAnySheetVisible,
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
            CustomToast(
                message = toastData.message ?: "",
                type = toastData.type,
                leadingIcon = painterResource(id = R.drawable.ic_heart_filled),
                buttonText = if (activeEvent?.multiDay == true) "Change" else null,
                onButtonClick = if (activeEvent?.multiDay == true) {
                    {
                        toastData = ToastData()
                        lastSavedVendor?.let { vendor ->
                            activeTargetVendor = vendor
                            showSaveListBottomSheet = true
                        }
                    }
                } else null
            )
        }

        if (showMenuSheet) {
            val commonMenuItems = listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Change Location",
                        icon = painterResource(R.drawable.ic_location_marker),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuSheet = false
                            mainNavController.navigate(Screen.LocationSelector.route)
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Manage Room Access",
                        icon = painterResource(R.drawable.ic_user_default),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuSheet = false
                            screenStack = screenStack + VendorScreenState.ROOM
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = painterResource(R.drawable.ic_help_feedback),
                        iconPlacement = IconPlacement.Left,
                        onClick = { showMenuSheet = false }
                    )
                )
            )

            val menuItems = if (currentScreenState == VendorScreenState.MAIN) {
                val savedVendorsItem = listOf(
                    MenuSheetActionItem(
                        text = "Saved Vendors",
                        icon = painterResource(R.drawable.ic_top_bar_heart),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuSheet = false
                            screenStack = screenStack + VendorScreenState.ALL_SAVED
                        }
                    )
                )
                listOf(savedVendorsItem) + commonMenuItems
            } else commonMenuItems

            MenuBottomSheet(
                items = menuItems,
                onCancelClick = { showMenuSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showRoomMenuBottomSheet) {
            MenuBottomSheet(
                items = listOf(
                    listOf(
                        MenuSheetActionItem(
                            text = "Leave Room",
                            icon = painterResource(R.drawable.ic_logout),
                            iconPlacement = IconPlacement.Left,
                            contentColor = MaterialTheme.colorScheme.error,
                            onClick = {
                                showRoomMenuBottomSheet = false
                                showLeaveConfirmation = true
                            }
                        )
                    )
                ),
                onCancelClick = {
                    showRoomMenuBottomSheet = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showSaveListBottomSheet) {
            val currentDest = activeTargetVendor?.let { vendorSavedDestinations["${it.name}-${it.category}"] }
            SaveListBottomSheet(
                timelineEvents = timelineEvents,
                isMySavedListChecked = currentDest == "mysaved",
                onMySavedListToggled = { checked ->
                    activeTargetVendor?.let { vendor ->
                        if (checked) {
                            vendorViewModel.toggleSaveVendor(vendor, isViewer, "mysaved")
                        } else {
                            vendorViewModel.toggleSaveVendor(vendor, isViewer, null)
                        }
                    }
                },
                selectedEventId = if (currentDest != "mysaved") currentDest else null,
                onEventSelected = { eventId ->
                    activeTargetVendor?.let { vendor ->
                        vendorViewModel.toggleSaveVendor(vendor, isViewer, eventId)
                    }
                },
                onAddNewEvent = { subEventItem ->
                    activeEvent?.let { event ->
                        val newSubEvent = SubEvent(
                            id = subEventItem.id,
                            name = subEventItem.name,
                            date = subEventItem.date,
                            completed = subEventItem.isCompleted
                        )
                        eventViewModel.updateEvent(event.copy(subEvents = event.subEvents + newSubEvent))

                        activeTargetVendor?.let { vendor ->
                            vendorViewModel.toggleSaveVendor(vendor, isViewer, subEventItem.id)
                        }
                    }
                },
                isViewer = isViewer,
                onDismiss = { showSaveListBottomSheet = false },
                onDone = {
                    activeTargetVendor?.let { vendor ->
                        val isSaved = vendorSavedDestinations.containsKey("${vendor.name}-${vendor.category}")
                        if (isSaved) {
                            lastSavedVendor = vendor
                            toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
                        } else {
                            toastData = ToastData("Removed from Saved List", ToastType.DEFAULT)
                        }
                    }
                    showSaveListBottomSheet = false
                    activeTargetVendor = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        userToRemove?.let {
            ConfirmationBottomSheet(
                heading = "Remove ${it.name} from Vendor Room?",
                subHeading = "They will not be able to access this room anymore.",
                confirmButtonText = "Remove",
                onDismiss = {
                    userToRemove = null
                },
                onConfirm = {
                    val target = userToRemove
                    if (target != null && activeEvent != null) {
                        roomViewModel.removeAccess(activeEvent!!.id, "Vendors", target.uid)
                        toastData = ToastData("${target.name} removed from room", ToastType.SUCCESS)
                    }
                    userToRemove = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showLeaveConfirmation) {
            ConfirmationBottomSheet(
                heading = "Leaving Vendor Room?",
                subHeading = "You will lose access to this room and won't be able to see updates.",
                confirmButtonText = "Leave",
                onDismiss = {
                    showLeaveConfirmation = false
                },
                onConfirm = {
                    activeEvent?.id?.let { eventId ->
                        roomViewModel.removeAccess(eventId, "Vendors", FirebaseAuth.getInstance().currentUser?.uid ?: "")
                    }
                    toastData = ToastData("You left the room", ToastType.DEFAULT)
                    screenStack = listOf(VendorScreenState.MAIN)
                    showLeaveConfirmation = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorMainContent(
    selectedCity: String,
    categories: List<VendorCategoryItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    onMenuClick: () -> Unit,
    onLocationClick: () -> Unit,
    onCategoryClick: (VendorCategoryItem) -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    recentVendorsList: List<Vendor>,
    focusManager: androidx.compose.ui.focus.FocusManager,
    context: Context,
    onRecentSearchesUpdate: (List<String>) -> Unit,
    allVendors: List<Vendor>,
    isLoading: Boolean,
    listState: LazyListState = rememberLazyListState()
) {
    val filteredAllVendors = remember(allVendors, searchQuery) {
        val baseList = allVendors.ifEmpty { MockData.sampleVendors }
        baseList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true) ||
                    it.locality.contains(searchQuery, ignoreCase = true) ||
                    it.city.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(isSearchActive) {
        if (!isSearchActive && searchQuery.isNotEmpty() && filteredAllVendors.isEmpty()) {
            onSearchQueryChange("")
        }
    }

    val searchBarTopPadding by animateDpAsState(
        targetValue = if (isSearchActive) 0.dp else 12.dp,
        label = "searchBarTopPadding"
    )

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                AnimatedContent(
                    targetState = isSearchActive,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    label = "TopBarSearchTransition"
                ) { active ->
                    CustomTopBar(
                        title = if (active) "Search Vendors" else "Vendors",
                        subtitle = if (active) null else selectedCity,
                        onBackClick = if (active) {
                            {
                                onSearchActiveChange(false)
                                onSearchQueryChange("")
                                focusManager.clearFocus()
                            }
                        } else null,
                        onMenuClick = if (active) null else onMenuClick,
                        onDropdownClick = if (active) null else onLocationClick,
                        titleIcon = if (active) null else painterResource(R.drawable.ic_vendor),
                        backIcon = TopIcon.Predefined.DOWN,
                        isLargeTitle = true,
                        isLeftAligned = !active,
                        buttonStyle = ButtonBackground.OPAQUE
                    )
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            state = listState,
            contentPadding = PaddingValues(bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(searchBarTopPadding))
                CustomSearchBar(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    onActiveChange = onSearchActiveChange,
                    placeholder = "Search Vendors",
                    isAiSearch = true,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (isSearchActive && searchQuery.isNotEmpty()) {
                if (filteredAllVendors.isEmpty()) {
                    item(key = "empty_search") {
                        EmptyState(message = "No matches for \"$searchQuery\"")
                    }
                } else {
                    items(
                        items = filteredAllVendors.take(8),
                        key = { "search_${it.name}_${it.category}" }
                    ) { vendor ->
                        SearchSuggestionItem(
                            title = vendor.name,
                            subtitle = "${vendor.category} • ${vendor.locality}, ${vendor.city}",
                            onClick = {
                                onVendorClick(vendor)
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            } else if (!isSearchActive && searchQuery.isNotEmpty()) {
                items(
                    items = filteredAllVendors,
                    key = { "filtered_${it.name}_${it.category}" }
                ) { vendor ->
                    VendorCardFull(
                        vendor = vendor,
                        onCardClick = { onVendorClick(vendor) },
                        onFavoriteToggle = { onFavoriteToggle(vendor) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            } else if (!isSearchActive) {
                item(key = "categories_grid") {
                    VendorCategoryGrid(categories = categories, onCategoryClick = onCategoryClick)
                }
                item(key = "explore_divider") {
                    OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                }
                item(key = "carousel_makeup") {
                    VendorCarousel(
                        title = "Top Makeup Artists in $selectedCity",
                        vendors = allVendors.filter { it.category == "Makeup" }.ifEmpty { MockData.sampleVendors.filter { it.category == "Makeup" } },
                        isLoading = isLoading,
                        onVendorClick = { vendor ->
                            saveRecentSearch(context, vendor.name)
                            onRecentSearchesUpdate(getRecentSearches(context))
                            onVendorClick(vendor)
                        },
                        onFavoriteToggle = onFavoriteToggle,
                        cardSize = CompactCardSize.MEDIUM
                    )
                }
                item(key = "carousel_photography") {
                    VendorCarousel(
                        title = "Best Photographers in $selectedCity",
                        vendors = allVendors.filter { it.category == "Photography" }.ifEmpty { MockData.sampleVendors.filter { it.category == "Photography" } },
                        isLoading = isLoading,
                        onVendorClick = { vendor ->
                            saveRecentSearch(context, vendor.name)
                            onRecentSearchesUpdate(getRecentSearches(context))
                            onVendorClick(vendor)
                        },
                        onFavoriteToggle = onFavoriteToggle,
                        cardSize = CompactCardSize.MEDIUM
                    )
                }
                item(key = "carousel_mehendi") {
                    VendorCarousel(
                        title = "Expert Mehendi Artists in $selectedCity",
                        vendors = allVendors.filter { it.category == "Mehendi" }.ifEmpty { MockData.sampleVendors.filter { it.category == "Mehendi" } },
                        isLoading = isLoading,
                        onVendorClick = { vendor ->
                            saveRecentSearch(context, vendor.name)
                            onRecentSearchesUpdate(getRecentSearches(context))
                            onVendorClick(vendor)
                        },
                        onFavoriteToggle = onFavoriteToggle,
                        cardSize = CompactCardSize.MEDIUM
                    )
                }
                item(key = "explore_horizontal") {
                    DashedDivider()
                    ExploreCategoriesHorizontal(categories = categories, onCategoryClick = onCategoryClick)
                }
                item(key = "footer") { FooterJansify() }
            } else {
                item(key = "trending_searches") {
                    TrendingAiSearchesSection(onTrendingClick = { query ->
                        onSearchQueryChange(query)
                        focusManager.clearFocus()
                    })
                }
                if (recentVendorsList.isNotEmpty()) {
                    item(key = "recent_searches_section") {
                        RecentSearchesSection(
                            recentVendors = recentVendorsList,
                            onVendorClick = onVendorClick,
                            onRemoveVendor = { vendor ->
                                val current = getRecentSearches(context).toMutableList()
                                current.remove(vendor.name)
                                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit { putString(KEY_RECENT_SEARCHES, current.joinToString("|||")) }
                                onRecentSearchesUpdate(getRecentSearches(context))
                            }
                        )
                    }
                }
                item(key = "spacer_bottom") { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorCategoryDetailContent(
    category: VendorCategoryItem,
    allVendors: List<Vendor>,
    savedVendorsForCategory: List<com.harshdeep.jasnify.domain.model.SavedVendor>,
    selectedCity: String,
    onBackClick: () -> Unit,
    onLocationClick: () -> Unit,
    onMenuClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    vendorSavedDestinations: Map<String, String>,
    timelineEvents: List<TimelineEvent>,
    selectedTab: String,
    onSelectedTabChange: (String) -> Unit,
    selectedViewType: String,
    onSelectedViewTypeChange: (String) -> Unit,
    isLoading: Boolean = false,
    onTimelineSeeAll: (TimelineEvent) -> Unit = { _ -> },
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState(),
    isBottomBarVisible: Boolean = true
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val viewOptions = listOf("By Timeline", "All Saved")
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var recentSearchesNames by remember { mutableStateOf(getCategoryRecentSearches(context, category.name)) }

    val filters = listOf("Most Relevant", "Top-Rated", "Price: Highest First", "Price: Lowest First")
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var appliedFilterOptions by remember { mutableStateOf(setOf<String>()) }

    val filteredVendors = remember(allVendors, searchQuery, selectedFilterIndex, appliedFilterOptions, vendorSavedDestinations) {
        val baseList = allVendors.ifEmpty { MockData.sampleVendors.filter { it.category == category.name } }
        var result = baseList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.locality.contains(searchQuery, ignoreCase = true) ||
                    it.city.contains(searchQuery, ignoreCase = true)
        }

        result = when (selectedFilterIndex) {
            1 -> result.sortedByDescending { it.rating }
            2 -> result.sortedByDescending { parsePrice(it.priceStartsFrom) }
            3 -> result.sortedBy { parsePrice(it.priceStartsFrom) }
            else -> result
        }

        if (appliedFilterOptions.contains("Top Rated")) {
            result = result.filter { it.rating >= 4.5 }
        }

        result.map { it.copy(favorite = vendorSavedDestinations.containsKey("${it.name}-${it.category}")) }
    }

    LaunchedEffect(isSearchActive) {
        if (!isSearchActive && searchQuery.isNotEmpty() && filteredVendors.isEmpty()) {
            searchQuery = ""
        }
    }

    var internalBottomBarVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }
    val categoryDetailNestedScrollConnection = remember(selectedTab, listState, gridState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // Determine scrollability based on active tab
                val canScroll = if (selectedTab == "explore") {
                    listState.canScrollForward || listState.canScrollBackward
                } else {
                    gridState.canScrollForward || gridState.canScrollBackward
                }
                if (!canScroll) return Offset.Zero

                if (delta > 0) {
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) {
                    if (scrollAccumulator > 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                }

                if (scrollAccumulator > 150f && !internalBottomBarVisible) {
                    internalBottomBarVisible = true
                    scrollAccumulator = 0f
                } else if (scrollAccumulator < -150f && internalBottomBarVisible) {
                    internalBottomBarVisible = false
                    scrollAccumulator = 0f
                }
                return Offset.Zero
            }
        }
    }

    val searchBarTopPadding by animateDpAsState(
        targetValue = if (isSearchActive) 0.dp else 12.dp,
        label = "searchBarTopPadding"
    )

    val bottomTabs = remember(allVendors.size, savedVendorsForCategory.size) {
        listOf(
            TabItem("Explore", "explore", badgeCount = allVendors.size),
            TabItem("Saved", "saved", badgeCount = savedVendorsForCategory.size)
        )
    }

    val recentVendorsList = remember(recentSearchesNames, allVendors) {
        recentSearchesNames.mapNotNull { name ->
            allVendors.find { it.name == name }
        }
    }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchQuery = ""
        focusManager.clearFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .nestedScroll(categoryDetailNestedScrollConnection)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                Column(modifier = Modifier.statusBarsPadding()) {
                    AnimatedContent(
                        targetState = isSearchActive,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                        },
                        label = "CategoryTopBarSearchTransition"
                    ) { active ->
                        CustomTopBar(
                            title = if (active) "Search ${category.name}" else category.name,
                            subtitle = if (active) null else selectedCity,
                            onBackClick = if (active) {
                                {
                                    isSearchActive = false
                                    searchQuery = ""
                                    focusManager.clearFocus()
                                }
                            } else onBackClick,
                            backIcon = if (active) TopIcon.Predefined.DOWN else TopIcon.Predefined.BACK,
                            onMenuClick = if (active) null else onMenuClick,
                            onDropdownClick = if (active) null else onLocationClick,
                            buttonStyle = ButtonBackground.OPAQUE
                        )
                    }
                }
            },
        ) { paddingValues ->
            val topPadding = paddingValues.calculateTopPadding()

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val isSaved = targetState == "saved"
                        if (isSaved) {
                            ScreenTransitions.SlideInFromRightTransition togetherWith ScreenTransitions.SlideOutToLeftTransition
                        } else {
                            ScreenTransitions.SlideInFromLeftTransition togetherWith ScreenTransitions.SlideOutToRightTransition
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = topPadding),
                    label = "CategoryTabTransition"
                ) { currentTab ->
                    if (currentTab == "explore") {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 12.dp,
                                            end = 12.dp,
                                            top = 12.dp,
                                            bottom = 12.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CustomSearchBar(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        onActiveChange = { isSearchActive = it },
                                        modifier = Modifier.weight(1f),
                                        isAiSearch = true,
                                        placeholder = "Search ${category.name}"
                                    )
                                }
                            }

                            if (isSearchActive && searchQuery.isNotEmpty()) {
                                if (filteredVendors.isEmpty()) {
                                    item(key = "empty_category_search") {
                                        EmptyState(message = "No matches for \"$searchQuery\"")
                                    }
                                } else {
                                    items(
                                        items = filteredVendors.take(8),
                                        key = { "cat_search_${it.name}" }
                                    ) { vendor ->
                                        SearchSuggestionItem(
                                            title = vendor.name,
                                            subtitle = "${vendor.locality}, ${vendor.city}",
                                            onClick = {
                                                onVendorClick(vendor)
                                                focusManager.clearFocus()
                                            }
                                        )
                                    }
                                }
                            } else if (!isSearchActive && searchQuery.isNotEmpty()) {
                                items(
                                    items = filteredVendors,
                                    key = { "cat_filtered_${it.name}" }
                                ) { vendor ->
                                    VendorCardFull(
                                        vendor = vendor,
                                        onCardClick = {
                                            saveCategoryRecentSearch(context, category.name, vendor.name)
                                            recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                            onVendorClick(vendor)
                                        },
                                        onFavoriteToggle = { onFavoriteToggle(vendor) },
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                }
                            } else if (!isSearchActive) {
                                item(key = "top_rated_carousel") {
                                    VendorCarousel(
                                        title = "Top-Rated ${category.name}",
                                        vendors = filteredVendors.filter { it.rating >= 4.5 },
                                        isLoading = isLoading,
                                        onVendorClick = { vendor ->
                                            saveCategoryRecentSearch(context, category.name, vendor.name)
                                            recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                            onVendorClick(vendor)
                                        },
                                        onFavoriteToggle = onFavoriteToggle
                                    )
                                }

                                item(key = "cat_explore_divider") {
                                    OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                                }

                                @OptIn(ExperimentalFoundationApi::class)
                                stickyHeader(key = "filters_header") {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = BackgroundPrimary
                                    ) {
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            itemsIndexed(
                                                items = filters,
                                                key = { _, filter -> "filter_$filter" }
                                            ) { index, filter ->
                                                val isSelected = selectedFilterIndex == index
                                                FilterChip(
                                                    label = filter,
                                                    isSelected = isSelected,
                                                    hasStroke = true,
                                                    shapeStyle = ChipShapeStyle.Round,
                                                    onClick = { selectedFilterIndex = index }
                                                )
                                            }
                                        }
                                    }
                                }

                                if (isLoading && filteredVendors.isEmpty()) {
                                    items(5, key = { "loading_$it" }) {
                                        VendorCardFull(
                                            vendor = Vendor(),
                                            isLoading = true,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                    }
                                } else if (filteredVendors.isEmpty()) {
                                    item(key = "no_vendors_found") { EmptyState(message = "No vendors found in this category") }
                                } else {
                                    items(
                                        items = filteredVendors,
                                        key = { "vendor_${it.name}_${it.category}" }
                                    ) { vendor ->
                                        VendorCardFull(
                                            vendor = vendor,
                                            onCardClick = {
                                                saveCategoryRecentSearch(context, category.name, vendor.name)
                                                recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                                onVendorClick(vendor)
                                            },
                                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                    }
                                }

                                item(key = "cat_footer") { FooterJansify() }
                            } else {
                                item(key = "cat_trending") {
                                    TrendingAiSearchesSection(onTrendingClick = { query ->
                                        searchQuery = query
                                        focusManager.clearFocus()
                                    })
                                }
                                if (recentVendorsList.isNotEmpty()) {
                                    item(key = "cat_recent_searches") {
                                        RecentSearchesSection(
                                            recentVendors = recentVendorsList,
                                            onVendorClick = { vendor ->
                                                saveCategoryRecentSearch(context, category.name, vendor.name)
                                                recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                                onVendorClick(vendor)
                                            },
                                            onRemoveVendor = { vendor ->
                                                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                                val current = getCategoryRecentSearches(context, category.name).toMutableList()
                                                current.remove(vendor.name)
                                                prefs.edit { putString("${KEY_RECENT_SEARCHES}_${category.name}", current.joinToString("|||")) }
                                                recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                            }
                                        )
                                    }
                                }
                                item(key = "cat_bottom_spacer") { Spacer(Modifier.height(24.dp)) }
                                item(key = "cat_extra_spacer") { Spacer(Modifier.height(100.dp)) }
                            }
                        }
                    } else {
                        val savedVendorsList = remember(savedVendorsForCategory, allVendors) {
                            allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name } }.map { it.copy(favorite = true) }
                        }

                        val savedTimelineEvents = remember(savedVendorsForCategory, timelineEvents, allVendors) {
                            val list = mutableListOf<TimelineEvent>()
                            val defaultSaved = allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name && it.destination == "mysaved" } }.map { it.copy(favorite = true) }

                            if (defaultSaved.isNotEmpty()) {
                                list.add(TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List", venues = emptyList()))
                            }

                            timelineEvents.forEach { event ->
                                val eventVendors = allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name && it.destination == event.id } }.map { it.copy(favorite = true) }
                                if (eventVendors.isNotEmpty()) {
                                    list.add(event.copy(venues = emptyList()))
                                }
                            }
                            list
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            state = gridState,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(span = { GridItemSpan(2) }) {
                                IosSegmentedControl(
                                    options = viewOptions,
                                    selectedOption = selectedViewType,
                                    onOptionSelected = onSelectedViewTypeChange,
                                    modifier = Modifier.padding(top = 12.dp).height(44.dp)
                                )
                            }

                            if (selectedViewType == "All Saved") {
                                if (isLoading) {
                                    items(6) {
                                        VendorCardCompact(
                                            vendor = Vendor(),
                                            isLoading = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            compactCardSize = CompactCardSize.SMALL
                                        )
                                    }
                                } else if (savedVendorsList.isEmpty()) {
                                    item(span = { GridItemSpan(2) }) {
                                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                                    }
                                } else {
                                    items(savedVendorsList) { vendor ->
                                        VendorCardCompact(
                                            vendor = vendor,
                                            onCardClick = { onVendorClick(vendor) },
                                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                                            modifier = Modifier.fillMaxWidth(),
                                            compactCardSize = CompactCardSize.SMALL
                                        )
                                    }
                                }
                            } else {
                                if (isLoading) {
                                    items(3, span = { GridItemSpan(2) }) {
                                        TimelineSection(
                                            date = "Loading...",
                                            event = "Fetching your plans",
                                            isLoading = true
                                        )
                                    }
                                } else if (savedTimelineEvents.isEmpty()) {
                                    item(span = { GridItemSpan(2) }) {
                                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                                    }
                                } else {
                                    items(savedTimelineEvents, span = { GridItemSpan(2) }) { timelineItem ->
                                        val vendorsForEvent = allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name && it.destination == timelineItem.id } }.map { it.copy(favorite = true) }
                                        TimelineSection(
                                            date = timelineItem.date,
                                            event = timelineItem.event,
                                            vendors = vendorsForEvent,
                                            onVendorClick = onVendorClick,
                                            onVendorFavoriteToggle = onFavoriteToggle,
                                            onSeeAllClick = { onTimelineSeeAll(timelineItem) }
                                        )
                                    }
                                }
                            }
                            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(24.dp)) }
                            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(100.dp)) }
                        }
                    }
                }

                if (!isSearchActive) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        AnimatedVisibility(
                            visible = internalBottomBarVisible,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it }),
                            label = "CategoryBottomTabVisibility"
                        ) {
                            BottomTab(
                                items = bottomTabs,
                                selectedValue = selectedTab,
                                onItemSelected = onSelectedTabChange
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllSavedVendorsContent(
    onBackClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    vendorSavedDestinations: Map<String, String>,
    timelineEvents: List<TimelineEvent>,
    allVendors: List<Vendor>,
    selectedViewType: String,
    onSelectedViewTypeChange: (String) -> Unit,
    isLoading: Boolean = false,
    onTimelineSeeAll: (TimelineEvent) -> Unit = { _ -> },
    gridState: LazyGridState = rememberLazyGridState()
) {
    val viewOptions = listOf("By Timeline", "All Saved")

    val savedVendorsList = remember(vendorSavedDestinations, allVendors) {
        allVendors.filter { v -> vendorSavedDestinations.containsKey("${v.name}-${v.category}") }
            .map { it.copy(favorite = true) }
    }

    val savedTimelineEvents = remember(vendorSavedDestinations, timelineEvents, allVendors) {
        val list = mutableListOf<TimelineEvent>()
        val defaultSaved = allVendors.filter { v -> vendorSavedDestinations["${v.name}-${v.category}"] == "mysaved" }
            .map { it.copy(favorite = true) }

        if (defaultSaved.isNotEmpty()) {
            list.add(TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List", venues = emptyList()))
        }

        timelineEvents.forEach { event ->
            val eventVendors = allVendors.filter { v -> vendorSavedDestinations["${v.name}-${v.category}"] == event.id }
                .map { it.copy(favorite = true) }
            if (eventVendors.isNotEmpty()) {
                list.add(event.copy(venues = emptyList()))
            }
        }
        list
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Saved Vendors",
                    onBackClick = onBackClick,
                    backIcon = TopIcon.Predefined.BACK,
                    isLargeTitle = true
                )
            }
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 12.dp),
            state = gridState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                IosSegmentedControl(
                    options = viewOptions,
                    selectedOption = selectedViewType,
                    onOptionSelected = onSelectedViewTypeChange,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .height(44.dp)
                )
            }

            if (selectedViewType == "All Saved") {
                if (isLoading) {
                    items(6) {
                        VendorCardCompact(
                            vendor = Vendor(),
                            isLoading = true,
                            modifier = Modifier.fillMaxWidth(),
                            compactCardSize = CompactCardSize.SMALL
                        )
                    }
                } else if (savedVendorsList.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                    }
                } else {
                    items(savedVendorsList) { vendor ->
                        VendorCardCompact(
                            vendor = vendor,
                            onCardClick = { onVendorClick(vendor) },
                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                            modifier = Modifier.fillMaxWidth(),
                            compactCardSize = CompactCardSize.SMALL
                        )
                    }
                }
            } else {
                if (isLoading) {
                    items(3, span = { GridItemSpan(2) }) {
                        TimelineSection(
                            date = "Loading...",
                            event = "Fetching your plans",
                            isLoading = true,
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                    }
                } else if (savedTimelineEvents.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                    }
                } else {
                    items(savedTimelineEvents, span = { GridItemSpan(2) }) { timelineItem ->
                        val vendorsForEvent = allVendors.filter { v ->
                            vendorSavedDestinations["${v.name}-${v.category}"] == timelineItem.id
                        }.map { it.copy(favorite = true) }

                        TimelineSection(
                            date = timelineItem.date,
                            event = timelineItem.event,
                            vendors = vendorsForEvent,
                            onVendorClick = onVendorClick,
                            onVendorFavoriteToggle = onFavoriteToggle,
                            onSeeAllClick = { onTimelineSeeAll(timelineItem) },
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                    }
                }
            }
            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun VendorRoomContent(
    eventId: String,
    roomViewModel: RoomViewModel,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onRemove: (User) -> Unit,
    onLeave: () -> Unit,
    onShowToast: (ToastData) -> Unit
) {
    val roomUsers by roomViewModel.roomUsers.collectAsState()
    val searchResults by roomViewModel.searchResults.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val currentUserRole = roomUsers.find { it.uid == currentUser?.uid }?.role ?: UserRole.VIEWER

    val displayUsers = remember(roomUsers, currentUser) {
        if (currentUser == null) return@remember roomUsers

        val self = User(
            uid = currentUser.uid,
            name = currentUser.displayName ?: "Me",
            email = currentUser.email ?: "",
            role = roomUsers.find { it.uid == currentUser.uid }?.role ?: currentUserRole,
            username = currentUser.email?.substringBefore("@") ?: "me"
        )

        val baseList = if (roomUsers.any { it.uid == currentUser.uid }) {
            roomUsers.map { if (it.uid == currentUser.uid) self.copy(role = it.role) else it }
        } else {
            listOf(self) + roomUsers
        }
        baseList.distinctBy { it.uid }
    }

    RoomScreen(
        allUsers = displayUsers,
        currentUserRole = currentUserRole,
        isSelf = { it.uid == currentUser?.uid },
        onBackClick = onBackClick,
        onMenuClick = onMenuClick,
        onRoleChange = { user, newRole ->
            roomViewModel.updateRole(eventId, "Vendors", user, newRole)
        },
        onRemove = onRemove,
        onReport = { user -> onShowToast(ToastData("${user.name} reported", ToastType.DEFAULT)) },
        onLeave = onLeave,
        searchResults = searchResults,
        onSearch = { query -> roomViewModel.searchUsers(query) },
        onGrantAccess = { email, role ->
            roomViewModel.grantAccess(eventId, "Vendors", email, role)
            onShowToast(ToastData("Access granted to $email", ToastType.SUCCESS))
        }
    )
}