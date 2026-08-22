package com.harshdeep.jasnify.presentation.screens.main.tabs.vendors

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Offer
import com.harshdeep.jasnify.domain.model.SubEvent
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.bottomdrawer.LocationAccessBottomSheet
import com.harshdeep.jasnify.presentation.utils.LocationHelper
import com.harshdeep.jasnify.presentation.utils.SessionState
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.OfferBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SaveListBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.sections.SavedTimelineItemsScreen
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.components.sections.vendorCategories
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.navigation.ScreenTransitions
import com.harshdeep.jasnify.presentation.screens.others.LocationScreen
import com.harshdeep.jasnify.presentation.components.states.VendorsLoadingState
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
    MAIN,
    CATEGORY_DETAIL,
    ALL_SAVED,
    ROOM,
    VENDOR_DETAIL,
    LOCATION_SELECTOR,
    TIMELINE_DETAIL
}

@SuppressLint("ConstantLocale")
private val VendorDateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsTab(
    mainNavController: NavHostController,
    internalNavController: NavHostController? = null,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onChatClick: (Vendor) -> Unit = {},
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    initialCategory: VendorCategoryItem? = null,
    onBackClick: () -> Unit = {}
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()

    if (activeEvent == null) {
        VendorsLoadingState()
        return
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var showOfferSheet by remember { mutableStateOf(false) }
    var offersToShow by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }
    var showLocationAccessSheet by remember { mutableStateOf(false) }

    var recentSearchesNames by remember { mutableStateOf(getRecentSearches(context)) }
    val recentLocations = remember { LocationHelper.getRecentLocations(context) }

    val selectedCityFromNav by mainNavController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("selected_location", "City, State")
        ?.collectAsState() ?: remember { mutableStateOf("City, State") }

    var localSelectedCity by remember(selectedCityFromNav, recentLocations) {
        mutableStateOf(
            if (selectedCityFromNav == "City, State" && recentLocations.isNotEmpty()) {
                recentLocations.first()
            } else {
                selectedCityFromNav
            }
        )
    }

    val selectedCity = localSelectedCity

    val gpsResolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            LocationHelper.fetchLocationAndResolveAddress(context, coroutineScope, { city ->
                localSelectedCity = city
                mainNavController.currentBackStackEntry?.savedStateHandle?.set("selected_location", city)
            })
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationGranted || coarseLocationGranted) {
            LocationHelper.checkSettingsAndFetchLocation(context, gpsResolutionLauncher) {
                LocationHelper.fetchLocationAndResolveAddress(context, coroutineScope, { city ->
                    localSelectedCity = city
                    mainNavController.currentBackStackEntry?.savedStateHandle?.set("selected_location", city)
                })
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!SessionState.hasShownVendorLocationAccess) {
            delay(500.milliseconds)
            showLocationAccessSheet = true
        }
    }

    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var screenStack by remember {
        mutableStateOf(if (initialCategory != null) listOf(VendorScreenState.CATEGORY_DETAIL) else listOf(VendorScreenState.MAIN))
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

    val navEntry = internalNavController ?: mainNavController
    val selectedCategoryNameFromHome by navEntry.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("selected_category_name", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(selectedCategoryNameFromHome) {
        selectedCategoryNameFromHome?.let { name ->
            val cat = vendorCategories.find { it.name == name }
            if (cat != null) {
                selectedCategory = cat
                screenStack = screenStack + VendorScreenState.CATEGORY_DETAIL
                navEntry.currentBackStackEntry?.savedStateHandle?.remove<String>("selected_category_name")
            }
        }
    }

    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val savedVendorsFromCloud by vendorViewModel.savedVendors.collectAsStateWithLifecycle()
    val exploreVendors by vendorViewModel.exploreVendors.collectAsStateWithLifecycle()
    val isLoading by vendorViewModel.isLoading.collectAsStateWithLifecycle()

    val vendorSavedDestinations = remember(savedVendorsFromCloud) {
        savedVendorsFromCloud.associate { "${it.vendorName}-${it.category}" to it.destination }
    }

    var toastData by remember { mutableStateOf(ToastData()) }
    var lastSavedVendor by remember { mutableStateOf<Vendor?>(null) }

    val isSavedListToast = remember(toastData.message, lastSavedVendor) {
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
        val currentEventId = activeEventId
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        if (currentEventId != null) {
            roomViewModel.verifyAccess(currentEventId, "Vendors", uid)
            roomViewModel.loadRoomUsers(currentEventId, "Vendors")
            vendorViewModel.setEventId(currentEventId)
        } else {
            roomViewModel.setAccessState(true)
        }
    }

    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }

    val categories = vendorCategories

    val recentVendorsList = remember(recentSearchesNames, exploreVendors) {
        if (recentSearchesNames.isEmpty()) emptyList<Vendor>()
        else {
            val vendorMap = exploreVendors.associateBy { it.name }
            recentSearchesNames.mapNotNull { name -> vendorMap[name] }
        }
    }

    var showSaveListBottomSheet by remember { mutableStateOf(false) }
    var activeTargetVendor by remember { mutableStateOf<Vendor?>(null) }
    var autoFocusLocationSearch by remember { mutableStateOf(false) }

    val timelineEvents by remember(activeEvent) {
        derivedStateOf {
            activeEvent?.subEvents?.map { subEvent ->
                val formattedDate = subEvent.date?.let { timestamp ->
                    VendorDateFormatter.format(Date(timestamp))
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
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isOwner = activeEvent?.ownerId == currentUserId
    val currentUserInRoom = roomUsers.find { it.uid == currentUserId }
    val currentUserRole = when {
        isOwner -> UserRole.OWNER
        currentUserInRoom != null -> currentUserInRoom.role
        else -> UserRole.VIEWER
    }
    val isViewer = currentUserRole == UserRole.VIEWER

    val currentSelectedTimelineEvent = remember(selectedTimelineEventId, timelineEvents) {
        if (selectedTimelineEventId == "mysaved") {
            TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List")
        } else {
            timelineEvents.find { it.id == selectedTimelineEventId }
        }
    }

    val currentSelectedTimelineVendors = remember(selectedTimelineEventId, selectedCategory, savedVendorsFromCloud, exploreVendors) {
        val targetId = selectedTimelineEventId ?: return@remember emptyList<Vendor>()

        val categoryFiltered = if (selectedCategory != null) {
            exploreVendors.filter { it.category == selectedCategory?.name }
        } else {
            exploreVendors
        }

        val savedSet = savedVendorsFromCloud.filter { it.destination == targetId }
            .map { "${it.vendorName}-${it.category}" }
            .toSet()

        categoryFiltered.filter { v ->
            savedSet.contains("${v.name}-${v.category}")
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

                if (currentScreenState != VendorScreenState.MAIN) return Offset.Zero
                val canScroll = mainListState.canScrollForward || mainListState.canScrollBackward
                if (!canScroll) return Offset.Zero

                if (delta > 0) {
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) {
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

    LaunchedEffect(showMenuSheet, showRoomMenuBottomSheet, isSearchActive, currentScreenState, showSaveListBottomSheet, hasAccess, isBottomBarVisible, showLocationAccessSheet) {
        val isBottomBarVisibleEffective = isBottomBarVisible &&
                hasAccess == true &&
                !showMenuSheet &&
                !showRoomMenuBottomSheet &&
                !isSearchActive &&
                !showSaveListBottomSheet &&
                !showLocationAccessSheet &&
                currentScreenState == VendorScreenState.MAIN
        onBottomBarVisibilityChange(isBottomBarVisibleEffective)
    }

    BackHandler {
        when {
            showSaveListBottomSheet -> showSaveListBottomSheet = false
            showRoomMenuBottomSheet -> showRoomMenuBottomSheet = false
            isSearchActive -> {
                isSearchActive = false
                searchQuery = ""
                focusManager.clearFocus()
            }
            screenStack.size > 1 -> {
                if (currentScreenState == VendorScreenState.CATEGORY_DETAIL) {
                    selectedCategory = null
                }
                screenStack = screenStack.dropLast(1)
            }
            else -> onBackClick()
        }
    }

    val isAnySheetVisible = showMenuSheet || showRoomMenuBottomSheet || showSaveListBottomSheet || userToRemove != null || showLeaveConfirmation || showOfferSheet || showLocationAccessSheet
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
                                onOfferClick = { vendor ->
                                    offersToShow = vendor.offers
                                    showOfferSheet = true
                                },
                                recentVendorsList = recentVendorsList,
                                focusManager = focusManager,
                                context = context,
                                onRecentSearchesUpdate = { recentSearchesNames = it },
                                allVendors = exploreVendors,
                                isLoading = isLoading,
                                listState = mainListState
                            )
                        }
                        VendorScreenState.CATEGORY_DETAIL -> {
                            selectedCategory?.let { category ->
                                val categoryVendors = remember(exploreVendors, category.name) {
                                    exploreVendors.filter { it.category == category.name }
                                }
                                val categorySavedVendors = remember(savedVendorsFromCloud, category.name) {
                                    savedVendorsFromCloud.filter { it.category == category.name }
                                }

                                VendorCategoryDetailContent(
                                    category = category,
                                    allVendors = categoryVendors,
                                    savedVendorsForCategory = categorySavedVendors,
                                    selectedCity = selectedCity,
                                    onBackClick = {
                                        if (screenStack.size > 1) {
                                            screenStack = screenStack.dropLast(1)
                                        } else {
                                            onBackClick()
                                        }
                                    },
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
                                    onOfferClick = { vendor ->
                                        offersToShow = vendor.offers
                                        showOfferSheet = true
                                    },
                                    listState = categoryListState,
                                    gridState = categorySavedGridState
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
                                    onBackClick = {
                                        if (screenStack.size > 1) {
                                            screenStack = screenStack.dropLast(1)
                                        } else {
                                            onBackClick()
                                        }
                                    },
                                    onFavoriteToggle = { handleFavoriteToggle(it) },
                                    onChatClick = { onChatClick(it) }
                                )
                            }
                        }
                        VendorScreenState.ALL_SAVED -> {
                            AllSavedVendorsContent(
                                onBackClick = {
                                    if (screenStack.size > 1) {
                                        screenStack = screenStack.dropLast(1)
                                    } else {
                                        onBackClick()
                                    }
                                },
                                onVendorClick = handleVendorClick,
                                onFavoriteToggle = handleFavoriteToggle,
                                vendorSavedDestinations = vendorSavedDestinations,
                                timelineEvents = timelineEvents,
                                allVendors = exploreVendors,
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
                                    onBackClick = {
                                        if (screenStack.size > 1) {
                                            screenStack = screenStack.dropLast(1)
                                        } else {
                                            onBackClick()
                                        }
                                    }
                                )
                            }
                        }
                        VendorScreenState.ROOM -> {
                            activeEvent?.let { event ->
                                VendorRoomContent(
                                    eventId = event.id,
                                    roomViewModel = roomViewModel,
                                    onBackClick = {
                                        if (screenStack.size > 1) {
                                            screenStack = screenStack.dropLast(1)
                                        } else {
                                            onBackClick()
                                        }
                                    },
                                    onMenuClick = { showRoomMenuBottomSheet = true },
                                    onRemove = { userToRemove = it },
                                    onLeave = { showLeaveConfirmation = true },
                                    onShowToast = { toastData = it }
                                )
                            }
                        }
                        VendorScreenState.LOCATION_SELECTOR -> {
                            LocationScreen(
                                initialSearches = recentLocations,
                                currentAddress = selectedCity,
                                onAddressSelected = {
                                    localSelectedCity = it
                                    mainNavController.currentBackStackEntry?.savedStateHandle?.set("selected_location", it)
                                    autoFocusLocationSearch = false
                                    if (screenStack.size > 1) {
                                        screenStack = screenStack.dropLast(1)
                                    } else {
                                        onBackClick()
                                    }
                                },
                                onBackClick = {
                                    autoFocusLocationSearch = false
                                    if (screenStack.size > 1) {
                                        screenStack = screenStack.dropLast(1)
                                    } else {
                                        onBackClick()
                                    }
                                },
                                backIcon = TopIcon.Predefined.DOWN,
                                autoFocusSearch = autoFocusLocationSearch
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
                iconColor = Color.Unspecified,
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

        if (showOfferSheet) {
            OfferBottomSheet(
                offers = offersToShow,
                onDismiss = { showOfferSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showLocationAccessSheet) {
            LocationAccessBottomSheet(
                title = "Discover the best \n vendors around you",
                subtitle = "Allow location permissions for best \n recommendations around you",
                onDismiss = {
                    showLocationAccessSheet = false
                    SessionState.hasShownVendorLocationAccess = true
                },
                onAllowClick = {
                    showLocationAccessSheet = false
                    SessionState.hasShownVendorLocationAccess = true

                    if (LocationHelper.hasLocationPermission(context)) {
                        LocationHelper.checkSettingsAndFetchLocation(context, gpsResolutionLauncher) {
                            LocationHelper.fetchLocationAndResolveAddress(context, coroutineScope, { city ->
                                localSelectedCity = city
                                mainNavController.currentBackStackEntry?.savedStateHandle?.set("selected_location", city)
                            })
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                onManualClick = {
                    showLocationAccessSheet = false
                    SessionState.hasShownVendorLocationAccess = true
                    autoFocusLocationSearch = true
                    screenStack = screenStack + VendorScreenState.LOCATION_SELECTOR
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showMenuSheet) {
            val menuItems = listOf(
                buildList {
                    if (currentScreenState == VendorScreenState.MAIN) {
                        add(
                            MenuSheetActionItem(
                                text = "Saved Vendors",
                                icon = painterResource(R.drawable.ic_top_bar_heart),
                                iconPlacement = IconPlacement.Top,
                                onClick = {
                                    showMenuSheet = false
                                    screenStack = screenStack + VendorScreenState.ALL_SAVED
                                },
                            )
                        )
                    }

                    add(
                        MenuSheetActionItem(
                            text = "Change Location",
                            icon = painterResource(R.drawable.ic_location_marker),
                            iconPlacement = if (currentScreenState == VendorScreenState.MAIN) IconPlacement.Top else IconPlacement.Left,
                            onClick = {
                                showMenuSheet = false
                                mainNavController.navigate(Screen.LocationSelector.route)
                            }
                        )
                    )
                },
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
                        onClick = {
                            showMenuSheet = false
                        }
                    )
                )
            )

            MenuBottomSheet(
                items = menuItems,
                onCancelClick = {
                    showMenuSheet = false
                },
                onProgress = {
                    sheetMotionProgress = it
                }
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

        userToRemove?.let { targetUser ->
            ConfirmationBottomSheet(
                heading = "Remove ${targetUser.name} from Vendor Room?",
                subHeading = "They will not be able to access this room anymore.",
                confirmButtonText = "Remove",
                onDismiss = {
                    userToRemove = null
                },
                onConfirm = {
                    val activeId = activeEvent?.id
                    if (activeId != null) {
                        roomViewModel.removeAccess(activeId, "Vendors", targetUser.uid)
                        toastData = ToastData("${targetUser.name} removed from room", ToastType.SUCCESS)
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
                        roomViewModel.removeAccess(eventId, "Vendors", FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
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
