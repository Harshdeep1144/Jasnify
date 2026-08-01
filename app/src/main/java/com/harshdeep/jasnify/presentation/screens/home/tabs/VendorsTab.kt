package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.harshdeep.jasnify.presentation.components.sections.TimelineSection
import com.harshdeep.jasnify.presentation.components.sections.TrendingAiSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.VendorCarousel
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryGrid
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.components.sections.vendorCategories
import com.harshdeep.jasnify.presentation.components.states.EmptySavedState
import com.harshdeep.jasnify.presentation.components.states.EmptyState
import com.harshdeep.jasnify.presentation.components.states.SearchSuggestionItem
import com.harshdeep.jasnify.presentation.navigation.Screen
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
    MAIN, CATEGORY_DETAIL, ALL_SAVED, ROOM, VENDOR_DETAIL, LOCATION_SELECTOR
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
    prefs.edit { putString(KEY_RECENT_SEARCHES, limited.joinToString("|||"))}
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
    prefs.edit { putString("${KEY_RECENT_SEARCHES}_$category", limited.joinToString("|||"))}
}

private fun parsePrice(priceString: String): Int {
    return priceString
        .replace(Regex("[^0-9]"), "")
        .toIntOrNull() ?: 0
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VendorsTab(
    mainNavController: NavHostController,
    internalNavController: NavHostController? = null,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
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
    var currentScreenState by remember { 
        mutableStateOf(if (initialCategory != null) VendorScreenState.CATEGORY_DETAIL else VendorScreenState.MAIN) 
    }
    var previousScreenState by remember { mutableStateOf<VendorScreenState?>(null) }
    var selectedVendor by remember { mutableStateOf<Vendor?>(null) }

    // Handle navigation from Home screen category clicks via NavController if not passed directly
    val selectedCategoryNameFromHome by (internalNavController ?: mainNavController).currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("selected_category_name", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(selectedCategoryNameFromHome) {
        selectedCategoryNameFromHome?.let { name ->
            val cat = vendorCategories.find { it.name == name }
            if (cat != null) {
                selectedCategory = cat
                currentScreenState = VendorScreenState.CATEGORY_DETAIL
                // Clear it so it doesn't reopen on every recomposition
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
            // If no active event, we allow browsing by setting hasAccess to true
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
                    venues = emptyList() // Not used here
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

    val handleVendorClick: (Vendor) -> Unit = { vendor ->
        saveRecentSearch(context, vendor.name)
        recentSearchesNames = getRecentSearches(context)
        selectedVendor = vendor
        previousScreenState = currentScreenState
        currentScreenState = VendorScreenState.VENDOR_DETAIL
    }

    val handleFavoriteToggle: (Vendor) -> Unit = { vendor ->
        val alreadySaved = vendorSavedDestinations.containsKey("${vendor.name}-${vendor.category}")
        if (alreadySaved) {
            // When user clicks heart on an already saved vendor (wants to dislike/manage), open bottom sheet directly
            activeTargetVendor = vendor
            showSaveListBottomSheet = true
        } else {
            // First time like: add to default list and show bottom toast with "Change" button
            vendorViewModel.toggleSaveVendor(vendor, isViewer, "mysaved")
            lastSavedVendor = vendor
            toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
        }
    }

    LaunchedEffect(showMenuSheet, showRoomMenuBottomSheet, isSearchActive, currentScreenState, showSaveListBottomSheet, hasAccess) {
        val isBottomBarVisible = hasAccess == true && !showMenuSheet && !showRoomMenuBottomSheet && !isSearchActive && !showSaveListBottomSheet && currentScreenState == VendorScreenState.MAIN
        onBottomBarVisibilityChange(isBottomBarVisible)
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
            when (currentScreenState) {
                VendorScreenState.VENDOR_DETAIL -> {
                    currentScreenState = previousScreenState ?: VendorScreenState.MAIN
                }
                VendorScreenState.CATEGORY_DETAIL -> {
                    currentScreenState = VendorScreenState.MAIN
                    selectedCategory = null
                }
                VendorScreenState.ROOM, VendorScreenState.ALL_SAVED, VendorScreenState.LOCATION_SELECTOR -> {
                    currentScreenState = previousScreenState ?: VendorScreenState.MAIN
                }
                VendorScreenState.MAIN -> {
                    onBackClick()
                }
            }
        }
    }

    val isAnySheetVisible = showMenuSheet || showRoomMenuBottomSheet || showSaveListBottomSheet || userToRemove != null || showLeaveConfirmation
    val targetScale = if (isAnySheetVisible) 0.92f + (0.08f * sheetMotionProgress) else 1.0f
    val backdropScale by animateFloatAsState(targetValue = targetScale, animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f), label = "backdropScale")
    val backdropCornerRadius by animateDpAsState(targetValue = if (isAnySheetVisible) CornerExtraLarge else 0.dp, animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy), label = "backdropCornerRadius")

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
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
            ) {
                AnimatedContent(
                    targetState = currentScreenState,
                    transitionSpec = { fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300)) },
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
                                    previousScreenState = VendorScreenState.MAIN
                                    currentScreenState = VendorScreenState.LOCATION_SELECTOR 
                                },
                                onCategoryClick = { category ->
                                    selectedCategory = category
                                    currentScreenState = VendorScreenState.CATEGORY_DETAIL
                                },
                                onVendorClick = handleVendorClick,
                                onFavoriteToggle = handleFavoriteToggle,
                                recentVendorsList = recentVendorsList,
                                focusManager = focusManager,
                                context = context,
                                onRecentSearchesUpdate = { recentSearchesNames = it },
                                allVendors = allVendorsFromRepo,
                                isLoading = isLoading
                            )
                        }
                        VendorScreenState.CATEGORY_DETAIL -> {
                            selectedCategory?.let { category ->
                                VendorCategoryDetailContent(
                                    category = category,
                                    selectedCity = selectedCity,
                                    onBackClick = { currentScreenState = VendorScreenState.MAIN },
                                    onLocationClick = { 
                                        previousScreenState = VendorScreenState.CATEGORY_DETAIL
                                        currentScreenState = VendorScreenState.LOCATION_SELECTOR 
                                    },
                                    onMenuClick = { showMenuSheet = true },
                                    onVendorClick = handleVendorClick,
                                    onFavoriteToggle = handleFavoriteToggle,
                                    vendorSavedDestinations = vendorSavedDestinations,
                                    timelineEvents = timelineEvents,
                                    vendorViewModel = vendorViewModel,
                                    isLoading = isLoading,
                                    sharedTransitionScope = sharedTransitionScope
                                )
                            }
                        }
                        VendorScreenState.VENDOR_DETAIL -> {
                            selectedVendor?.let { vendor ->
                                VendorDetailScreen(
                                    vendorDetail = vendor,
                                    onBackClick = { currentScreenState = previousScreenState ?: VendorScreenState.MAIN },
                                    onFavoriteToggle = { handleFavoriteToggle(it) },
                                    sharedTransitionScope = sharedTransitionScope
                                )
                            }
                        }
                        VendorScreenState.ALL_SAVED -> {
                            AllSavedVendorsContent(
                                onBackClick = { currentScreenState = VendorScreenState.MAIN },
                                onVendorClick = handleVendorClick,
                                onFavoriteToggle = handleFavoriteToggle,
                                vendorSavedDestinations = vendorSavedDestinations,
                                timelineEvents = timelineEvents,
                                allVendors = allVendorsFromRepo,
                                isLoading = isLoading,
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                        VendorScreenState.ROOM -> {
                            activeEvent?.let { event ->
                                VendorRoomContent(
                                    eventId = event.id,
                                    roomViewModel = roomViewModel,
                                    onBackClick = { currentScreenState = VendorScreenState.MAIN },
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
                                    currentScreenState = previousScreenState ?: VendorScreenState.MAIN
                                },
                                onBackClick = {
                                    currentScreenState = previousScreenState ?: VendorScreenState.MAIN
                                },
                                backIcon = TopIcon.Predefined.DOWN
                            )
                        }
                    }
                }
            }
        }

        // Standard Top Toast
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

        // Bottom Saved List Toast with "Change" Action Button
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
                leadingIcon = painterResource(id = R.drawable.ic_top_bar_heart),
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
                            currentScreenState = VendorScreenState.ROOM
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
                            currentScreenState = VendorScreenState.ALL_SAVED
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
                    currentScreenState = VendorScreenState.MAIN
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
    isLoading: Boolean
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

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                if (isSearchActive) {
                    CustomTopBar(
                        title = "Search Vendors",
                        onBackClick = {
                            onSearchActiveChange(false)
                            onSearchQueryChange("")
                            focusManager.clearFocus()
                        },
                        backIcon = TopIcon.Predefined.DOWN,
                        buttonStyle = ButtonBackground.OPAQUE,
                        isLargeTitle = true
                    )
                } else {
                    CustomTopBar(
                        title = "Vendors",
                        subtitle = selectedCity,
                        onMenuClick = onMenuClick,
                        onDropdownClick = onLocationClick,
                        titleIcon = painterResource(R.drawable.ic_vendor),
                        isLargeTitle = true,
                        isLeftAligned = true
                    )
                }
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(12.dp))
                CustomSearchBar(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    onActiveChange = onSearchActiveChange,
                    placeholder = "Search Vendors",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (isSearchActive && searchQuery.isNotEmpty()) {
                if (filteredAllVendors.isEmpty()) {
                    item {
                        EmptyState(message = "No matches for \"$searchQuery\"")
                    }
                } else {
                    items(filteredAllVendors) { vendor ->
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
                items(filteredAllVendors) { vendor ->
                    VendorCardFull(
                        vendor = vendor,
                        onCardClick = { onVendorClick(vendor) },
                        onFavoriteToggle = { onFavoriteToggle(vendor) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            } else if (!isSearchActive) {
                item {
                    VendorCategoryGrid(categories = categories, onCategoryClick = onCategoryClick)
                }
                    item {
                        OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                    }
                    item {
                        VendorCarousel(
                            title = "Top Makeup Artists in $selectedCity",
                            vendors = allVendors.filter { it.category == "Makeup" }.ifEmpty { MockData.sampleVendors.filter { it.category == "Makeup" } },
                            isLoading = isLoading,
                            onVendorClick = onVendorClick,
                            onFavoriteToggle = onFavoriteToggle,
                            cardSize = CompactCardSize.MEDIUM
                        )
                    }
                    item {
                        VendorCarousel(
                            title = "Best Photographers in $selectedCity",
                            vendors = allVendors.filter { it.category == "Photography" }.ifEmpty { MockData.sampleVendors.filter { it.category == "Photography" } },
                            isLoading = isLoading,
                            onVendorClick = onVendorClick,
                            onFavoriteToggle = onFavoriteToggle,
                            cardSize = CompactCardSize.MEDIUM
                        )
                    }
                    item {
                        VendorCarousel(
                            title = "Expert Mehendi Artists in $selectedCity",
                            vendors = allVendors.filter { it.category == "Mehendi" }.ifEmpty { MockData.sampleVendors.filter { it.category == "Mehendi" } },
                            isLoading = isLoading,
                            onVendorClick = onVendorClick,
                            onFavoriteToggle = onFavoriteToggle,
                            cardSize = CompactCardSize.MEDIUM
                        )
                    }
                    item {
                        DashedDivider()
                        ExploreCategoriesHorizontal(categories = categories, onCategoryClick = onCategoryClick)
                    }
                    item { FooterJansify() }
            } else {
                item {
                    TrendingAiSearchesSection(onTrendingClick = { query ->
                        onSearchQueryChange(query)
                        focusManager.clearFocus()
                    })
                }
                if (recentVendorsList.isNotEmpty()) {
                    item {
                        RecentSearchesSection(
                            recentVendors = recentVendorsList,
                            onVendorClick = onVendorClick,
                            onRemoveVendor = { vendor ->
                                val current = getRecentSearches(context).toMutableList()
                                current.remove(vendor.name)
                                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit { putString(KEY_RECENT_SEARCHES, current.joinToString("|||")) }
                                onRecentSearchesUpdate(getRecentSearches(context))
                            },
                        )
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VendorCategoryDetailContent(
    category: VendorCategoryItem,
    selectedCity: String,
    onBackClick: () -> Unit,
    onLocationClick: () -> Unit,
    onMenuClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    vendorSavedDestinations: Map<String, String>,
    timelineEvents: List<TimelineEvent>,
    vendorViewModel: VendorViewModel,
    isLoading: Boolean = false,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var selectedTab by remember { mutableStateOf("explore") }

    var selectedViewType by remember { mutableStateOf("By Timeline") }
    val viewOptions = listOf("By Timeline", "All Saved")
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var recentSearchesNames by remember { mutableStateOf(getCategoryRecentSearches(context, category.name)) }

    val filters = listOf("Most Relevant", "Top-Rated", "Price: Highest First", "Price: Lowest First")
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    var appliedFilterOptions by remember { mutableStateOf(setOf<String>()) }

    val allVendors by vendorViewModel.getVendorsByCategory(category.name).collectAsStateWithLifecycle(initialValue = emptyList())
    val savedVendorsForCategory by vendorViewModel.getSavedVendorsByCategory(category.name).collectAsStateWithLifecycle(initialValue = emptyList())

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

    val filteredVendors = remember(allVendors, searchQuery, selectedFilterIndex, appliedFilterOptions, vendorSavedDestinations) {
        val baseList = allVendors.ifEmpty { MockData.sampleVendors.filter { it.category == category.name } }
        var result = baseList.filter { 
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.locality.contains(searchQuery, ignoreCase = true) ||
            it.city.contains(searchQuery, ignoreCase = true)
        }

        // Apply Sorting/Filtering based on selectedFilterIndex
        result = when (selectedFilterIndex) {
            1 -> result.sortedByDescending { it.rating }
            2 -> result.sortedByDescending { parsePrice(it.priceStartsFrom) }
            3 -> result.sortedBy { parsePrice(it.priceStartsFrom) }
            else -> result // "Most Relevant" - default order
        }

        // Apply Filters
        if (appliedFilterOptions.contains("Top Rated")) {
            result = result.filter { it.rating >= 4.5 }
        }

        result.map { it.copy(favorite = vendorSavedDestinations.containsKey("${it.name}-${it.category}")) }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                if(!isSearchActive){
                    CustomTopBar(
                        title = category.name,
                        subtitle = selectedCity,
                        onBackClick = onBackClick,
                        backIcon = TopIcon.Predefined.BACK,
                        onMenuClick = onMenuClick,
                        onDropdownClick = onLocationClick,
                    )
                }else{
                    CustomTopBar(
                        title = "Search ${category.name}",
                        onBackClick = onBackClick,
                        backIcon = TopIcon.Predefined.BACK,
                    )
                }
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
        containerColor = BackgroundPrimary
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
                .padding(paddingValues),
            label = "CategoryTabTransition"
        ) { currentTab ->
            if (currentTab == "explore") {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
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
                            item {
                                EmptyState(message = "No matches for \"$searchQuery\"")
                            }
                        } else {
                            items(filteredVendors) { vendor ->
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
                        items(filteredVendors) { vendor ->
                            VendorCardFull(
                                vendor = vendor,
                                onCardClick = {
                                    saveCategoryRecentSearch(context, category.name, vendor.name)
                                    recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                    onVendorClick(vendor)
                                },
                                onFavoriteToggle = { onFavoriteToggle(vendor) },
                                modifier = Modifier.padding(horizontal = 12.dp),
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                    } else if (!isSearchActive) {
                        // DISCOVERY MODE: Regular UI
                        item {
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

                        item {
                            OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                        }

                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                itemsIndexed(filters) { index, filter ->
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

                        if (isLoading && filteredVendors.isEmpty()) {
                            items(5) {
                                VendorCardFull(
                                    vendor = Vendor(), // Mock empty vendor
                                    isLoading = true,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        } else if (filteredVendors.isEmpty()) {
                            item { EmptyState(message = "No vendors found in this category") }
                        } else {
                            items(filteredVendors) { vendor ->
                                VendorCardFull(
                                    vendor = vendor,
                                    onCardClick = {
                                        saveCategoryRecentSearch(context, category.name, vendor.name)
                                        recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                        onVendorClick(vendor)
                                    },
                                    onFavoriteToggle = { onFavoriteToggle(vendor) },
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    sharedTransitionScope = sharedTransitionScope
                                )
                            }
                        }

                        item { FooterJansify() }
                    } else {
                        item {
                            TrendingAiSearchesSection(onTrendingClick = { query ->
                                searchQuery = query
                                focusManager.clearFocus()
                            })
                        }
                        if (recentVendorsList.isNotEmpty()) {
                            item {
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
                                    },
                                )
                            }
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            } else {
                // SAVED TAB
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
                            list.add(event.copy(venues = emptyList())) // We'll handle vendor list separately
                        }
                    }
                    list
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        IosSegmentedControl(
                            options = viewOptions,
                            selectedOption = selectedViewType,
                            onOptionSelected = { selectedViewType = it },
                            modifier = Modifier.padding(top = 12.dp).height(44.dp)
                        )
                    }

                    if (selectedViewType == "All Saved") {
                        if (isLoading) {
                            items(5) {
                                VendorCardFull(
                                    vendor = Vendor(),
                                    isLoading = true,
                                )
                            }
                        } else if (savedVendorsList.isEmpty()) {
                            item { EmptySavedState() }
                        } else {
                            items(savedVendorsList) { vendor ->
                                VendorCardFull(
                                    vendor = vendor,
                                    onCardClick = { onVendorClick(vendor) },
                                    onFavoriteToggle = { onFavoriteToggle(vendor) },
                                    sharedTransitionScope = sharedTransitionScope
                                )
                            }
                        }
                    } else {
                        // TIMELINE VIEW
                        if (isLoading) {
                            items(3) {
                                TimelineSection(
                                    date = "Loading...",
                                    event = "Fetching your plans",
                                    isLoading = true
                                )
                            }
                        } else if (savedTimelineEvents.isEmpty()) {
                            item { EmptySavedState() }
                        } else {
                            items(savedTimelineEvents) { timelineItem ->
                                val vendorsForEvent = allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name && it.destination == timelineItem.id } }.map { it.copy(favorite = true) }
                                TimelineSection(
                                    date = timelineItem.date,
                                    event = timelineItem.event,
                                    vendors = vendorsForEvent,
                                    onVendorClick = onVendorClick,
                                    onVendorFavoriteToggle = onFavoriteToggle,
                                    sharedTransitionScope = sharedTransitionScope
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AllSavedVendorsContent(
    onBackClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    vendorSavedDestinations: Map<String, String>,
    timelineEvents: List<TimelineEvent>,
    allVendors: List<Vendor>,
    isLoading: Boolean = false,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    var selectedViewType by remember { mutableStateOf("By Timeline") }
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
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                IosSegmentedControl(
                    options = viewOptions,
                    selectedOption = selectedViewType,
                    onOptionSelected = { selectedViewType = it },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .height(44.dp)
                )
            }

            if (selectedViewType == "All Saved") {
                if (isLoading) {
                    items(5) {
                        VendorCardFull(
                            vendor = Vendor(),
                            isLoading = true,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                } else if (savedVendorsList.isEmpty()) {
                    item {EmptySavedState()}
                } else {
                    items(savedVendorsList) { vendor ->
                        VendorCardFull(
                            vendor = vendor,
                            onCardClick = { onVendorClick(vendor) },
                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            sharedTransitionScope = sharedTransitionScope
                        )
                    }
                }
            } else {
                // Timeline implementation
                if (isLoading) {
                    items(3) {
                        TimelineSection(
                            date = "Loading...",
                            event = "Fetching your plans",
                            isLoading = true,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                } else if (savedTimelineEvents.isEmpty()) {
                    item { EmptySavedState() }
                } else {
                    items(savedTimelineEvents) { timelineItem ->
                        val vendorsForEvent = allVendors.filter { v ->
                            vendorSavedDestinations["${v.name}-${v.category}"] == timelineItem.id
                        }.map { it.copy(favorite = true) }

                        TimelineSection(
                            date = timelineItem.date,
                            event = timelineItem.event,
                            vendors = vendorsForEvent,
                            onVendorClick = onVendorClick,
                            onVendorFavoriteToggle = onFavoriteToggle,
                            modifier = Modifier.padding(horizontal = 12.dp),
                            sharedTransitionScope = sharedTransitionScope
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
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

    // Determine current user's role in this specific room
    val currentUserRole = roomUsers.find { it.uid == currentUser?.uid }?.role ?: UserRole.VIEWER

    // Ensure current user is in the list shown with correct info
    val displayUsers = remember(roomUsers, currentUser) {
        if (currentUser == null) return@remember roomUsers

        val self = User(
            uid = currentUser.uid,
            name = currentUser.displayName ?: "Me",
            email = currentUser.email ?: "",
            role = roomUsers.find { it.uid == currentUser.uid }?.role ?: currentUserRole,
            username = currentUser.email?.substringBefore("@") ?: "me"
        )

        // If current user is already in list but missing info, replace with 'self'
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
