package com.harshdeep.jasnify.presentation.screens.catering

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.models.eventTypes
import com.harshdeep.jasnify.domain.model.Offer
import com.harshdeep.jasnify.domain.model.SubEvent
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomSuccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.room.RoomAccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.selection.OfferBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.selection.SaveListBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.filter.FilterBottomSheet
import com.harshdeep.jasnify.presentation.components.filter.FilterFoodTypeBottomSheet
import com.harshdeep.jasnify.presentation.components.filter.FoodTypeOption
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.sections.SavedTimelineItemsScreen
import com.harshdeep.jasnify.presentation.components.sections.vendorCategories
import com.harshdeep.jasnify.presentation.components.states.CateringLoadingState
import com.harshdeep.jasnify.presentation.navigation.ScreenTransitions
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorCategoryDetailContent
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorDetailScreen
import com.harshdeep.jasnify.presentation.screens.others.AiChatScreen
import com.harshdeep.jasnify.presentation.screens.others.LocationScreen
import com.harshdeep.jasnify.presentation.utils.SessionState
import com.harshdeep.jasnify.presentation.viewmodels.CateringViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

private val DEFAULT_CUISINES = listOf("Indian", "Japanese", "Mexican", "Italian", "Chinese", "French", "Thai", "Korean")
private val DEFAULT_TYPES = listOf("Starters", "Beverages", "Main Course", "Desserts")

private val CateringDateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CateringMenuScreen(
    onBackClick: () -> Unit,
    navController: NavHostController? = null,
    onBottomBarVisibilityChange: (Boolean) -> Unit = {},
    onChatClick: (Vendor) -> Unit = {},
    cateringViewModel: CateringViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    profileViewModel: com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel = hiltViewModel(),
) {
    // 1. ALL State Collections (Consistent order)
    val cateringItemsEntities by cateringViewModel.cateringItems.collectAsStateWithLifecycle()
    val isCateringLoading by cateringViewModel.isLoading.collectAsStateWithLifecycle()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId: String? by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val savedVendorsFromCloud by vendorViewModel.savedVendors.collectAsStateWithLifecycle()
    val allVendorsFromRepo by vendorViewModel.allVendors.collectAsStateWithLifecycle()
    val isVendorsLoading by vendorViewModel.isLoading.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()

    // 2. Explicit Entrance Delay
    var isEntering by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        roomViewModel.resetAccessState()
        eventViewModel.fetchUserEvents()
        delay(650.milliseconds) // Sufficient time for slide transition
        isEntering = false
    }

    // 3. ViewModel Driving Effects
    LaunchedEffect(activeEventId) {
        val id = activeEventId
        if (id != null) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
            cateringViewModel.setEventId(id)
            vendorViewModel.setEventId(id)
            roomViewModel.verifyAccess(id, "Catering", uid)
            roomViewModel.loadRoomUsers(id, "Catering")
        } else {
            roomViewModel.setAccessState(true)
        }
    }

    LaunchedEffect(activeEvent) {
        activeEvent?.let { event ->
            val eventTypeLabel = eventTypes.find { it.id == event.typeId }?.label ?: "Others"
            cateringViewModel.seedDefaultMenu(eventTypeLabel, event.id)
        }
    }

    // 4. SHELL UI: Immediate response
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if ((activeEvent == null) || isEntering) {
            CateringLoadingState()
        } else {
            // DEFER heavy composition to sub-composable
            CateringMenuContent(
                activeEvent = activeEvent!!,
                isCateringLoading = isCateringLoading,
                hasAccess = hasAccess,
                cateringItemsEntities = cateringItemsEntities,
                roomUsers = roomUsers,
                savedVendorsFromCloud = savedVendorsFromCloud,
                allVendorsFromRepo = allVendorsFromRepo,
                isVendorsLoading = isVendorsLoading,
                searchResults = searchResults,
                onBackClick = onBackClick,
                onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                onChatClick = onChatClick,
                cateringViewModel = cateringViewModel,
                eventViewModel = eventViewModel,
                roomViewModel = roomViewModel,
                vendorViewModel = vendorViewModel,
                profileViewModel = profileViewModel
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CateringMenuContent(
    activeEvent: com.harshdeep.jasnify.domain.model.Event,
    isCateringLoading: Boolean,
    hasAccess: Boolean?,
    cateringItemsEntities: List<com.harshdeep.jasnify.data.local.CateringItemEntity>,
    roomUsers: List<User>,
    savedVendorsFromCloud: List<com.harshdeep.jasnify.domain.model.SavedVendor>,
    allVendorsFromRepo: List<Vendor>,
    isVendorsLoading: Boolean,
    searchResults: List<User>,
    onBackClick: () -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onChatClick: (Vendor) -> Unit,
    cateringViewModel: CateringViewModel,
    eventViewModel: EventViewModel,
    roomViewModel: RoomViewModel,
    vendorViewModel: VendorViewModel,
    profileViewModel: com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    val vendorSavedDestinations = remember(savedVendorsFromCloud) {
        savedVendorsFromCloud.associate { "${it.vendorName}-${it.category}" to it.destination }
    }

    val exploreVendors = remember(allVendorsFromRepo, vendorSavedDestinations) {
        allVendorsFromRepo.map { vendor ->
            vendor.copy(favorite = vendorSavedDestinations.containsKey("${vendor.name}-${vendor.category}"))
        }
    }

    val foodCategoryItem = remember {
        vendorCategories.find {
            it.name.contains("Food", ignoreCase = true) || it.name.contains("Catering", ignoreCase = true)
        } ?: vendorCategories.first()
    }

    val foodCategoryVendors = remember(exploreVendors, foodCategoryItem.name) {
        exploreVendors.filter { it.category == foodCategoryItem.name }
    }

    val foodCategorySavedVendors = remember(savedVendorsFromCloud, foodCategoryItem.name) {
        savedVendorsFromCloud.filter { it.category == foodCategoryItem.name }
    }

    val auth = remember { FirebaseAuth.getInstance() }
    val currentUserUid = remember(auth.currentUser) { auth.currentUser?.uid.orEmpty() }

    val currentUserRole = remember(activeEvent, roomUsers, currentUserUid) {
        val isOwner = activeEvent.ownerId == currentUserUid
        val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
        when {
            isOwner -> UserRole.OWNER
            currentUserInRoom != null -> currentUserInRoom.role
            else -> UserRole.VIEWER
        }
    }
    val isOwner = currentUserRole == UserRole.OWNER
    val isViewer = currentUserRole == UserRole.VIEWER

    val allMenuItems = remember(cateringItemsEntities) {
        cateringItemsEntities.map { entity ->
            MenuItem(
                id = entity.id,
                name = entity.name,
                dietary = entity.dietary,
                type = entity.type,
                cuisine = entity.cuisine
            )
        }
    }

    val selectedCity = SessionState.currentLocation

    val timelineEvents by remember(activeEvent) {
        derivedStateOf {
            activeEvent.subEvents.map { subEvent ->
                val formattedDate = subEvent.date?.let { timestamp ->
                    CateringDateFormatter.format(Date(timestamp))
                } ?: "Date TBD"

                TimelineEvent(
                    id = subEvent.id,
                    date = formattedDate,
                    event = subEvent.name,
                    venues = emptyList()
                )
            }
        }
    }

    var searchText by remember { mutableStateOf("") }
    var showAiChat by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var isMultiSelectActive by remember { mutableStateOf(false) }
    var selectedItemIds by remember { mutableStateOf(emptySet<String>()) }
    val isSelectionMode = isMultiSelectActive || selectedItemIds.isNotEmpty()

    val mainListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val categoryListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val categorySavedGridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    var screenStack by remember { mutableStateOf(listOf(CateringMenuView.MENU)) }
    val currentView by remember(screenStack) { derivedStateOf { screenStack.last() } }

    var aiChatContext by remember { mutableStateOf("") }

    var selectedCategoryTab by remember { mutableStateOf("explore") }
    var selectedSavedViewType by remember { mutableStateOf("By Timeline") }
    var selectedVendor by remember { mutableStateOf<Vendor?>(null) }
    var selectedTimelineEventId by remember { mutableStateOf<String?>(null) }

    var showSaveListBottomSheet by remember { mutableStateOf(false) }
    var activeTargetVendor by remember { mutableStateOf<Vendor?>(null) }
    var showOfferSheet by remember { mutableStateOf(false) }
    var offersToShow by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var lastSavedVendor by remember { mutableStateOf<Vendor?>(null) }

    var toastData by remember { mutableStateOf(ToastData()) }
    var activeToastData by remember { mutableStateOf<ToastData?>(null) }

    val isSavedListToast = remember(toastData.message, lastSavedVendor) {
        toastData.message?.contains("Saved List") == true && lastSavedVendor != null
    }

    LaunchedEffect(toastData) {
        if (toastData.message != null) {
            when {
                toastData.message == "Please type or search a dish!" -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    delay(80.milliseconds)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                toastData.type == ToastType.ERROR -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            activeToastData = toastData
            delay(2500.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    val handleVendorClick: (Vendor) -> Unit = remember {
        { vendor ->
            selectedVendor = vendor
            screenStack = screenStack + CateringMenuView.VENDOR_DETAIL
        }
    }

    val handleFavoriteToggle: (Vendor) -> Unit = remember(vendorSavedDestinations, isViewer) {
        { vendor ->
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
    }

    val handleTimelineSeeAll: (TimelineEvent) -> Unit = remember {
        { event ->
            selectedTimelineEventId = event.id
            screenStack = screenStack + CateringMenuView.TIMELINE_DETAIL
        }
    }

    // Initialize SessionState.currentLocation if it's default
    val initializedLocation = remember(context) {
        SessionState.initializeLocation(context)
        true
    }

    val currentSelectedTimelineEvent = remember(selectedTimelineEventId, timelineEvents) {
        if (selectedTimelineEventId == "mysaved") {
            TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List")
        } else {
            timelineEvents.find { it.id == selectedTimelineEventId }
        }
    }

    val currentSelectedTimelineVendors = remember(selectedTimelineEventId, vendorSavedDestinations, foodCategoryVendors) {
        val targetId = selectedTimelineEventId ?: return@remember emptyList<Vendor>()
        foodCategoryVendors.filter { v ->
            vendorSavedDestinations["${v.name}-${v.category}"] == targetId
        }.map { it.copy(favorite = true) }
    }

    var selectedFilterTab by remember { mutableStateOf("All Items") }
    var selectedItemForDetails by remember { mutableStateOf<MenuItem?>(null) }
    var showDetailsBottomSheet by remember { mutableStateOf(false) }

    var showDeleteConfirmationSheet by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<MenuItem?>(null) }

    var showCuisineBottomSheet by remember { mutableStateOf(false) }
    var showTypeBottomSheet by remember { mutableStateOf(false) }

    var showAddItemSheet by remember { mutableStateOf(false) }
    var showSuccessSheet by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    var editingItem by remember { mutableStateOf<MenuItem?>(null) }

    var newItemName by remember { mutableStateOf("") }
    var newItemCuisine by remember { mutableStateOf("Indian") }
    var newItemType by remember { mutableStateOf("Starters") }
    var newItemDietary by remember { mutableStateOf(Dietary.Veg) }

    var showAddCuisineBottomSheet by remember { mutableStateOf(false) }
    var showAddTypeBottomSheet by remember { mutableStateOf(false) }

    var selectedCuisines by remember { mutableStateOf(emptySet<String>()) }
    var selectedTypes by remember { mutableStateOf(emptySet<String>()) }

    var showMenuBottomSheet by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showRoomAccessBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }

    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }


    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            showDetailsBottomSheet ||
                    showDeleteConfirmationSheet ||
                    showCuisineBottomSheet ||
                    showTypeBottomSheet ||
                    showAddItemSheet ||
                    showSuccessSheet ||
                    showAddCuisineBottomSheet ||
                    showAddTypeBottomSheet ||
                    showMenuBottomSheet ||
                    showRoomMenuBottomSheet ||
                    showRoomAccessBottomSheet ||
                    showSaveListBottomSheet ||
                    showOfferSheet ||
                    userToRemove != null ||
                    showLeaveConfirmation
        }
    }

    var isBottomBarVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember(mainListState, isAnyBottomSheetOpen, isSelectionMode, isSearchActive) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isAnyBottomSheetOpen || isSelectionMode || isSearchActive) return Offset.Zero

                val delta = available.y
                val canScroll = mainListState.canScrollForward || mainListState.canScrollBackward

                if (!canScroll) {
                    isBottomBarVisible = true
                    return Offset.Zero
                }

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

    LaunchedEffect(showAiChat, isBottomBarVisible) {
        onBottomBarVisibilityChange(!showAiChat && isBottomBarVisible)
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

    val cuisineOptions = remember(allMenuItems) {
        (DEFAULT_CUISINES + allMenuItems.map { it.cuisine }).distinct().sorted()
    }

    val typeOptions = remember(allMenuItems) {
        (DEFAULT_TYPES + allMenuItems.map { it.type }).distinct().sorted()
    }

    val foodTypeOptions = remember(typeOptions, allMenuItems) {
        typeOptions.map { typeName ->
            FoodTypeOption(
                name = typeName,
                count = allMenuItems.count { it.type.equals(typeName, ignoreCase = true) }
            )
        }
    }

    val filteredItems = remember(allMenuItems, searchText, selectedFilterTab, selectedCuisines, selectedTypes) {
        val query = searchText.trim()
        val hasQuery = query.isNotEmpty()
        val hasCuisineFilter = selectedCuisines.isNotEmpty()
        val hasTypeFilter = selectedTypes.isNotEmpty()

        allMenuItems.filter { item ->
            val matchesSearch = !hasQuery ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.type.contains(query, ignoreCase = true) ||
                    item.cuisine.contains(query, ignoreCase = true)

            val matchesTab = when (selectedFilterTab) {
                "Veg" -> item.dietary == Dietary.Veg
                "Non-Veg" -> item.dietary == Dietary.NonVeg
                else -> true
            }

            val matchesCuisine = !hasCuisineFilter || selectedCuisines.contains(item.cuisine)
            val matchesType = !hasTypeFilter || selectedTypes.contains(item.type)

            matchesSearch && matchesTab && matchesCuisine && matchesType
        }
    }

    val categorizedItems = remember(filteredItems) {
        filteredItems.groupBy { it.type }
    }

    BackHandler {
        when {
            showAiChat -> showAiChat = false
            showSaveListBottomSheet -> showSaveListBottomSheet = false
            showOfferSheet -> showOfferSheet = false
            isSelectionMode -> {
                selectedItemIds = emptySet()
                isMultiSelectActive = false
                focusManager.clearFocus()
            }
            isSearchActive -> {
                isSearchActive = false
                searchText = ""
                focusManager.clearFocus()
            }
            screenStack.size > 1 -> {
                screenStack = screenStack.dropLast(1)
            }
            else -> onBackClick()
        }
    }

    RoomAccessGuardian(
        hasAccess = hasAccess,
        roomName = "Catering",
        onBackClick = onBackClick
    ) {
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
                AnimatedContent(
                    targetState = currentView,
                    transitionSpec = {
                        when {
                            targetState == CateringMenuView.VENDOR_DETAIL ||
                                    targetState == CateringMenuView.LOCATION_SELECTOR ||
                                    targetState == CateringMenuView.TIMELINE_DETAIL ||
                                    targetState == CateringMenuView.HELP_FEEDBACK ->
                                ScreenTransitions.SlideBottomToTopFastTransition

                            initialState == CateringMenuView.VENDOR_DETAIL ||
                                    initialState == CateringMenuView.LOCATION_SELECTOR ||
                                    initialState == CateringMenuView.TIMELINE_DETAIL ||
                                    initialState == CateringMenuView.HELP_FEEDBACK ->
                                ScreenTransitions.SlideTopToBottomFastTransition

                            else -> fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                        }
                    },
                    label = "CateringMenuTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        CateringMenuView.MENU -> {
                            CateringMenuMainContent(
                                activeEvent = activeEvent,
                                isCateringLoading = isCateringLoading, 
                                allMenuItems = allMenuItems,
                                categorizedItems = categorizedItems,
                                foodCategoryVendors = foodCategoryVendors,
                                searchText = searchText,
                                onSearchTextChange = { searchText = it },
                                isSearchActive = isSearchActive,
                                onSearchActiveChange = { isSearchActive = it },
                                selectedFilterTab = selectedFilterTab,
                                onFilterTabChange = { selectedFilterTab = it },
                                selectedCuisines = selectedCuisines,
                                onCuisineChipClick = {
                                    focusManager.clearFocus()
                                    showCuisineBottomSheet = true
                                },
                                selectedTypes = selectedTypes,
                                onTypeChipClick = {
                                    focusManager.clearFocus()
                                    showTypeBottomSheet = true
                                },
                                onResetFilters = {
                                    focusManager.clearFocus()
                                    selectedFilterTab = "All Items"
                                    selectedCuisines = emptySet()
                                    selectedTypes = emptySet()
                                },
                                selectedItemIds = selectedItemIds,
                                onItemClick = { item ->
                                    focusManager.clearFocus()
                                    if (isSelectionMode) {
                                        selectedItemIds = if (selectedItemIds.contains(item.id)) {
                                            selectedItemIds - item.id
                                        } else {
                                            selectedItemIds + item.id
                                        }
                                    } else {
                                        selectedItemForDetails = item
                                        showDetailsBottomSheet = true
                                    }
                                },
                                onItemLongClick = { item ->
                                    focusManager.clearFocus()
                                    selectedItemIds = if (selectedItemIds.contains(item.id)) {
                                        selectedItemIds - item.id
                                    } else {
                                        selectedItemIds + item.id
                                    }
                                },
                                isMultiSelectActive = isMultiSelectActive,
                                onMultiSelectActiveChange = { isMultiSelectActive = it },
                                onDeleteSelectedClick = {
                                    focusManager.clearFocus()
                                    if (selectedItemIds.isEmpty()) {
                                        toastData = ToastData(
                                            message = "Please select at least 1 item",
                                            type = ToastType.ERROR
                                        )
                                    } else {
                                        showDeleteConfirmationSheet = true
                                    }
                                },
                                mainListState = mainListState,
                                nestedScrollConnection = nestedScrollConnection,
                                isBottomBarVisible = isBottomBarVisible,
                                onAddAnItemClick = {
                                    focusManager.clearFocus()
                                    editingItem = null
                                    newItemName = ""
                                    newItemCuisine = "Indian"
                                    newItemType = "Starters"
                                    newItemDietary = Dietary.Veg
                                    showAddItemSheet = true
                                },
                                onAiChatClick = {
                                    focusManager.clearFocus()
                                    aiChatContext = """
                                        Catering Menu for ${activeEvent.name}:
                                        Total Items: ${allMenuItems.size}
                                        
                                        Menu items:
                                        ${allMenuItems.joinToString("\n") { "- ${it.name} (${it.dietary}, ${it.cuisine}, ${it.type})" }}
                                    """.trimIndent()
                                    showAiChat = true
                                },
                                showAiChat = showAiChat,
                                isViewer = isViewer,
                                onBackClick = {
                                    focusManager.clearFocus()
                                    onBackClick()
                                },
                                onMenuClick = {
                                    focusManager.clearFocus()
                                    if (isSelectionMode) {
                                        selectedItemIds = emptySet()
                                        isMultiSelectActive = false
                                    } else {
                                        showMenuBottomSheet = true
                                    }
                                },
                                onVendorClick = handleVendorClick,
                                onFavoriteToggle = handleFavoriteToggle,
                                onOfferClick = { vendor ->
                                    offersToShow = vendor.offers
                                    showOfferSheet = true
                                },
                                onViewAllVendorsClick = {
                                    focusManager.clearFocus()
                                    screenStack = screenStack + CateringMenuView.VENDOR_CATEGORY_DETAIL
                                }
                            )
                        }

                        CateringMenuView.VENDOR_CATEGORY_DETAIL -> {
                            VendorCategoryDetailContent(
                                category = foodCategoryItem,
                                allVendors = foodCategoryVendors,
                                savedVendorsForCategory = foodCategorySavedVendors,
                                selectedCity = selectedCity,
                                onBackClick = {
                                    if (screenStack.size > 1) {
                                        screenStack = screenStack.dropLast(1)
                                    } else {
                                        onBackClick()
                                    }
                                },
                                onLocationClick = {
                                    screenStack = screenStack + CateringMenuView.LOCATION_SELECTOR
                                },
                                onMenuClick = { showMenuBottomSheet = true },
                                onVendorClick = handleVendorClick,
                                onFavoriteToggle = handleFavoriteToggle,
                                vendorSavedDestinations = vendorSavedDestinations,
                                timelineEvents = timelineEvents,
                                selectedTab = selectedCategoryTab,
                                onSelectedTabChange = { selectedCategoryTab = it },
                                selectedViewType = selectedSavedViewType,
                                onSelectedViewTypeChange = { selectedSavedViewType = it },
                                isLoading = isVendorsLoading,
                                onTimelineSeeAll = handleTimelineSeeAll,
                                onOfferClick = { vendor ->
                                    offersToShow = vendor.offers
                                    showOfferSheet = true
                                },
                                listState = categoryListState,
                                gridState = categorySavedGridState
                            )
                        }

                        CateringMenuView.VENDOR_DETAIL -> {
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

                        CateringMenuView.TIMELINE_DETAIL -> {
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

                        CateringMenuView.LOCATION_SELECTOR -> {
                            LocationScreen(
                                initialSearches = emptyList(),
                                currentAddress = selectedCity,
                                onAddressSelected = {
                                    SessionState.updateLocation(context, it)
                                    if (screenStack.size > 1) {
                                        screenStack = screenStack.dropLast(1)
                                    } else {
                                        onBackClick()
                                    }
                                },
                                onBackClick = {
                                    if (screenStack.size > 1) {
                                        screenStack = screenStack.dropLast(1)
                                    } else {
                                        onBackClick()
                                    }
                                },
                                backIcon = TopIcon.Predefined.DOWN
                            )
                        }

                        CateringMenuView.MANAGE_ROOM_ACCESS -> {
                            CateringRoomContent(
                                eventId = activeEvent.id,
                                roomViewModel = roomViewModel,
                                currentUserRole = currentUserRole,
                                onBackClick = {
                                    if (screenStack.size > 1) screenStack = screenStack.dropLast(1)
                                    focusManager.clearFocus()
                                },
                                onMenuClick = {
                                    showRoomMenuBottomSheet = true
                                    focusManager.clearFocus()
                                },
                                onRemove = { targetUser ->
                                    userToRemove = targetUser
                                },
                                onLeave = {
                                    showLeaveConfirmation = true
                                },
                                      onShowToast = { toastData = it }
                            )
                        }

                        CateringMenuView.HELP_FEEDBACK -> {
                            com.harshdeep.jasnify.presentation.screens.main.tabs.profile.HelpFeedbackScreen(
                                profileViewModel = profileViewModel,
                                onBack = {
                                    if (screenStack.size > 1) screenStack = screenStack.dropLast(1)
                                },
                                onShowAiChat = { showAiChat = true }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = toastData.message != null && !isSavedListToast && !isAnyBottomSheetOpen,
                enter = slideInVertically(initialOffsetY = { -it - 500 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it - 500 }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .zIndex(99f)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                activeToastData?.let { data ->
                    CustomToast(
                        message = data.message.orEmpty(),
                        type = data.type
                    )
                }
            }

            AnimatedVisibility(
                visible = toastData.message != null && isSavedListToast && !isAnyBottomSheetOpen,
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
                    buttonText = if (activeEvent.multiDay) "Change" else null,
                    onButtonClick = if (activeEvent.multiDay) {
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

            AnimatedVisibility(
                visible = showAiChat,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.zIndex(200f)
            ) {
                AiChatScreen(
                    eventId = activeEvent.id,
                    initialContext = aiChatContext,
                    shouldStartNewSession = true,
                    onBackClick = {
                        showAiChat = false
                        focusManager.clearFocus()
                    }
                )
            }
        }
    }

    if (showOfferSheet) {
        OfferBottomSheet(
            offers = offersToShow,
            onDismiss = { showOfferSheet = false },
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
                val newSubEvent = SubEvent(
                    id = subEventItem.id,
                    name = subEventItem.name,
                    date = subEventItem.date,
                    completed = subEventItem.isCompleted
                )
                eventViewModel.updateEvent(activeEvent.copy(subEvents = activeEvent.subEvents + newSubEvent))

                activeTargetVendor?.let { vendor ->
                    vendorViewModel.toggleSaveVendor(vendor, isViewer, subEventItem.id)
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

    if (showDetailsBottomSheet && selectedItemForDetails != null) {
        val currentItem = selectedItemForDetails!!
        val itemCategoryStyle = remember(currentItem.type) { getCategoryStyle(currentItem.type) }

        CustomBottomSheet(
            heading = "Item Details",
            sheetHeight = null,
            onProgress = { progress -> sheetMotionProgress = progress },
            onDismiss = {
                focusManager.clearFocus()
                showDetailsBottomSheet = false
                selectedItemForDetails = null
            },
            containerBrush = itemCategoryStyle.sheetBrush,
            showDragHandle = false,
            closeButtonBackgroundStyle = ButtonBackground.TRANSLUCENT
        ) {
            ItemDetailsSheetContent(
                item = currentItem,
                canEdit = !isViewer,
                onFetchImages = { query ->
                    cateringViewModel.fetchDishImages(query)
                },
                onDeleteClick = {
                    focusManager.clearFocus()
                    itemToDelete = currentItem
                    showDetailsBottomSheet = false
                    showDeleteConfirmationSheet = true
                },
                onEditClick = {
                    focusManager.clearFocus()
                    editingItem = currentItem
                    newItemName = currentItem.name
                    newItemCuisine = currentItem.cuisine
                    newItemType = currentItem.type
                    newItemDietary = currentItem.dietary

                    showDetailsBottomSheet = false
                    showAddItemSheet = true
                }
            )
        }
    }
    if (showDeleteConfirmationSheet) {
        val count = selectedItemIds.size
        val deleteHeading = if (isSelectionMode) {
            if (count == 1) "Remove 1 item?" else "Remove $count items?"
        } else {
            "Remove item?"
        }

        val deleteSubheading = if (isSelectionMode) {
            "The selected items will be removed from the Catering Menu."
        } else {
            "The item will be removed from the Catering Menu."
        }

        ConfirmationBottomSheet(
            heading = deleteHeading,
            subHeading = deleteSubheading,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showDeleteConfirmationSheet = false
                if (!isSelectionMode) itemToDelete = null
            },
            onConfirm = {
                focusManager.clearFocus()
                if (isSelectionMode) {
                    val removedCount = selectedItemIds.size
                    selectedItemIds.forEach { id -> cateringViewModel.deleteItem(id) }
                    selectedItemIds = emptySet()
                    isMultiSelectActive = false
                    toastData = ToastData("$removedCount item${if (removedCount > 1) "s" else ""} removed from menu", ToastType.ERROR)
                } else {
                    itemToDelete?.let { cateringViewModel.deleteItem(it.id) }
                    itemToDelete = null
                    toastData = ToastData("Item removed from menu", ToastType.ERROR)
                }
                showDeleteConfirmationSheet = false
            }
        )
    }

    if (showCuisineBottomSheet) {
        FilterBottomSheet(
            title = "Select Cuisine",
            options = cuisineOptions,
            initialSelectedOptions = selectedCuisines,
            showSearchBar = true,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showCuisineBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                selectedCuisines = selectedOptions
                showCuisineBottomSheet = false
            }
        )
    }

    if (showTypeBottomSheet) {
        FilterFoodTypeBottomSheet(
            options = foodTypeOptions,
            initialSelectedOptions = selectedTypes,
            isMultiSelect = true,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showTypeBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                selectedTypes = selectedOptions
                showTypeBottomSheet = false
            }
        )
    }

    if (showAddItemSheet) {
        CustomBottomSheet(
            heading = if (editingItem != null) "Edit menu item" else "Add an item to menu",
            onDismiss = { showAddItemSheet = false },
            onProgress = { sheetMotionProgress = it },
            sheetHeight = null,
            showDragHandle = false,
            showCloseButton = true,
            hasToast = toastData.message != null,
            toast = {
                AnimatedVisibility(
                    visible = toastData.message != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    activeToastData?.let { data ->
                        CustomToast(
                            message = data.message.orEmpty(),
                            type = data.type
                        )
                    }
                }
            }
        ) {
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
                    val colorInt = SurfacePrimary.toArgb()
                    w.navigationBarColor = colorInt

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        w.isNavigationBarContrastEnforced = false
                    }

                    val isLightBackground = ColorUtils.calculateLuminance(colorInt) > 0.5
                    WindowCompat.getInsetsController(w, view).isAppearanceLightNavigationBars = isLightBackground
                }
                onDispose {}
            }

            AddItemSheetContent(
                itemName = newItemName,
                onItemNameChange = { newItemName = it },
                cuisine = newItemCuisine,
                onCuisineClick = {
                    focusManager.clearFocus()
                    showAddCuisineBottomSheet = true
                },
                type = newItemType,
                onTypeClick = {
                    focusManager.clearFocus()
                    showAddTypeBottomSheet = true
                },
                dietary = newItemDietary,
                onDietaryChange = {
                    focusManager.clearFocus()
                    newItemDietary = it
                },
                isEditMode = editingItem != null,
                onSubmitClick = {
                    focusManager.clearFocus()
                    if (newItemName.isNotBlank()) {
                        successMessage = if (editingItem != null) {
                            "Item has been updated"
                        } else {
                            "Item added to menu"
                        }

                        if (editingItem != null) {
                            cateringViewModel.updateItem(
                                id = editingItem!!.id,
                                name = newItemName,
                                dietary = newItemDietary,
                                type = newItemType,
                                cuisine = newItemCuisine
                            )
                        } else {
                            cateringViewModel.addItem(
                                name = newItemName,
                                dietary = newItemDietary,
                                type = newItemType,
                                cuisine = newItemCuisine
                            )
                        }

                        showAddItemSheet = false
                        showSuccessSheet = true

                        newItemName = ""
                        newItemCuisine = "Indian"
                        newItemType = "Starters"
                        newItemDietary = Dietary.Veg
                        editingItem = null
                    } else {
                        toastData = ToastData(
                            message = "Please type or search a dish!",
                            type = ToastType.ERROR
                        )
                    }
                }
            )
        }
    }

    if (showSuccessSheet) {
        CustomSuccessBottomSheet(
            message = successMessage,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showSuccessSheet = false
            }
        )
    }

    if (showAddCuisineBottomSheet) {
        FilterBottomSheet(
            title = "Select Cuisine",
            options = cuisineOptions,
            initialSelectedOptions = if (newItemCuisine.isNotEmpty()) setOf(newItemCuisine) else emptySet(),
            showSearchBar = true,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showAddCuisineBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                newItemCuisine = selectedOptions.firstOrNull() ?: "Indian"
                showAddCuisineBottomSheet = false
            },
            isMultiSelect = false
        )
    }

    if (showAddTypeBottomSheet) {
        FilterFoodTypeBottomSheet(
            options = foodTypeOptions,
            initialSelectedOptions = if (newItemType.isNotEmpty()) setOf(newItemType) else emptySet(),
            isMultiSelect = false,
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                focusManager.clearFocus()
                showAddTypeBottomSheet = false
            },
            onApply = { selectedOptions ->
                focusManager.clearFocus()
                newItemType = selectedOptions.firstOrNull() ?: "Starters"
                showAddTypeBottomSheet = false
            }
        )
    }

    if (showMenuBottomSheet) {
        val plusPainter = painterResource(R.drawable.ic_add_circle)
        val checkPainter = painterResource(R.drawable.ic_multi_select)
        val userDefaultPainter = painterResource(R.drawable.ic_user_default)
        val helpPainter = painterResource(R.drawable.ic_help_feedback)

        val menuItems = remember(isViewer, isOwner, plusPainter, checkPainter, userDefaultPainter, helpPainter) {
            listOfNotNull(
                if (!isViewer) {
                    listOf(
                        MenuSheetActionItem(
                            text = "Add an item",
                            icon = plusPainter,
                            onClick = {
                                showMenuBottomSheet = false
                                showAddItemSheet = true
                            },
                            iconPlacement = IconPlacement.Top
                        ),
                        MenuSheetActionItem(
                            text = "Multi Select",
                            icon = checkPainter,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showMenuBottomSheet = false
                                isMultiSelectActive = true
                            },
                            iconPlacement = IconPlacement.Top
                        )
                    )
                } else null,
                listOf(
                    MenuSheetActionItem(
                        text = if (isOwner) "Manage Room Access" else "Room Members",
                        icon = userDefaultPainter,
                        onClick = {
                            showMenuBottomSheet = false
                            screenStack = screenStack + CateringMenuView.MANAGE_ROOM_ACCESS
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = helpPainter,
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuBottomSheet = false
                            screenStack = screenStack + CateringMenuView.HELP_FEEDBACK
                        }
                    )
                )
            )
        }

        MenuBottomSheet(
            items = menuItems,
            onProgress = { sheetMotionProgress = it },
            onCancelClick = {
                showMenuBottomSheet = false
            }
        )
    }

    if (showRoomMenuBottomSheet) {
        val logoutPainter = painterResource(R.drawable.ic_logout)
        val helpPainter = painterResource(R.drawable.ic_help_feedback)
        val errorColor = MaterialTheme.colorScheme.error

        val roomMenuItems = remember(logoutPainter, errorColor, helpPainter) {
            listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Leave Room",
                        icon = logoutPainter,
                        contentColor = errorColor,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            showLeaveConfirmation = true
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = helpPainter,
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            screenStack = screenStack + CateringMenuView.HELP_FEEDBACK
                        }
                    )
                )
            )
        }

        MenuBottomSheet(
            items = roomMenuItems,
            onProgress = { sheetMotionProgress = it },
            onCancelClick = {
                showRoomMenuBottomSheet = false
            }
        )
    }

    if (showRoomAccessBottomSheet) {
        RoomAccessBottomSheet(
            onDismissRequest = { showRoomAccessBottomSheet = false },
            onGrantAccess = { email, role ->
                roomViewModel.grantAccess(activeEvent.id, "Catering", email, role)
            },
            onProgress = { sheetMotionProgress = it },
            searchResults = searchResults,
            onSearch = { roomViewModel.searchUsers(it) }
        )
    }

    userToRemove?.let { user ->
        ConfirmationBottomSheet(
            heading = "Remove ${user.name} from Catering Menu?",
            subHeading = "They will not be able to access this room anymore.",
            confirmButtonText = "Remove",
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                userToRemove = null
            },
            onConfirm = {
                val target = userToRemove
                if (target != null) {
                    roomViewModel.removeAccess(activeEvent.id, "Catering", target.uid)
                    toastData = ToastData("${target.name} removed from room", ToastType.SUCCESS)
                }
                userToRemove = null
            }
        )
    }

    if (showLeaveConfirmation) {
        ConfirmationBottomSheet(
            heading = "Leaving Catering Room?",
            subHeading = "You will lose access to this room and won't be able to see updates.",
            confirmButtonText = "Leave",
            onProgress = { sheetMotionProgress = it },
            onDismiss = {
                showLeaveConfirmation = false
            },
            onConfirm = {
                roomViewModel.removeAccess(activeEvent.id, "Catering", currentUserUid)
                toastData = ToastData("You left the room", ToastType.DEFAULT)
                screenStack = listOf(CateringMenuView.MENU)
                showLeaveConfirmation = false
            }
        )
    }
}
