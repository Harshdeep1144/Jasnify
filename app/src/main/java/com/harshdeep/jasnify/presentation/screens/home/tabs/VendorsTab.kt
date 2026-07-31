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
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.harshdeep.jasnify.presentation.components.chip.VendorTypeChip
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
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
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.TrendingAiSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.VendorCarousel
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

enum class VendorScreenState {
    MAIN, CATEGORY_DETAIL, ALL_SAVED, ROOM, VENDOR_DETAIL
}

data class VendorCategoryItem(val name: String, val icon: Int)

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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VendorsTab(
    mainNavController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    var currentScreenState by remember { mutableStateOf(VendorScreenState.MAIN) }
    var selectedCategory by remember { mutableStateOf<VendorCategoryItem?>(null) }
    var selectedVendor by remember { mutableStateOf<Vendor?>(null) }

    var recentSearchesNames by remember { mutableStateOf(getRecentSearches(context)) }

    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val savedVendorsFromCloud by vendorViewModel.savedVendors.collectAsStateWithLifecycle()
    val allVendorsFromRepo by vendorViewModel.allVendors.collectAsStateWithLifecycle()

    val vendorSavedDestinations = remember(savedVendorsFromCloud) {
        savedVendorsFromCloud.associate { "${it.vendorName}-${it.category}" to it.destination }
    }

    var toastData by remember { mutableStateOf(ToastData()) }

    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    LaunchedEffect(activeEventId) {
        activeEventId?.let { id ->
            roomViewModel.verifyAccess(id, "Vendors", FirebaseAuth.getInstance().currentUser?.uid ?: "")
            roomViewModel.loadRoomUsers(id, "Vendors")
            vendorViewModel.setEventId(id)
        }
    }

    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }

    val selectedCity by mainNavController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("selected_location", "City, State")
        ?.collectAsState() ?: remember { mutableStateOf("City, State") }

    val categories = listOf(
        VendorCategoryItem("Grooming", R.drawable.ill_vendor_grooming),
        VendorCategoryItem("Makeup", R.drawable.ill_vendor_makeup),
        VendorCategoryItem("Photography", R.drawable.ill_vendor_photographers),
        VendorCategoryItem("Mehendi", R.drawable.ill_vendor_mehendi),
        VendorCategoryItem("Jewellery", R.drawable.ill_vendor_jewellery),
        VendorCategoryItem("Outfits", R.drawable.ill_bride_and_groom),
        VendorCategoryItem("Entertainment", R.drawable.ill_vendor_entertainment),
        VendorCategoryItem("Food", R.drawable.ill_vendor_food),
        VendorCategoryItem("Gifts", R.drawable.ill_vendor_gifts)
    )

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
        currentScreenState = VendorScreenState.VENDOR_DETAIL
    }

    val handleFavoriteToggle: (Vendor) -> Unit = { vendor ->
        val alreadySaved = vendorSavedDestinations.containsKey("${vendor.name}-${vendor.category}")
        if (alreadySaved) {
            activeTargetVendor = vendor
            showSaveListBottomSheet = true
        } else {
            vendorViewModel.toggleSaveVendor(vendor, isViewer, "mysaved")
            toastData = ToastData("Added to My Saved List", ToastType.SUCCESS)
        }
    }

    LaunchedEffect(showMenuSheet, isSearchActive, currentScreenState, showSaveListBottomSheet) {
        val isBottomBarVisible = !showMenuSheet && !isSearchActive && !showSaveListBottomSheet && currentScreenState == VendorScreenState.MAIN
        onBottomBarVisibilityChange(isBottomBarVisible)
    }

    BackHandler {
        if (showSaveListBottomSheet) {
            showSaveListBottomSheet = false
        } else if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
            focusManager.clearFocus()
        } else {
            when (currentScreenState) {
                VendorScreenState.VENDOR_DETAIL -> {
                    if (selectedCategory != null) {
                        currentScreenState = VendorScreenState.CATEGORY_DETAIL
                    } else {
                        currentScreenState = VendorScreenState.MAIN
                    }
                }
                VendorScreenState.CATEGORY_DETAIL -> {
                    currentScreenState = VendorScreenState.MAIN
                    selectedCategory = null
                }
                VendorScreenState.ROOM, VendorScreenState.ALL_SAVED -> {
                    currentScreenState = VendorScreenState.MAIN
                }
                VendorScreenState.MAIN -> {
                    // Let the system handle it (exit tab/app)
                }
            }
        }
    }

    val isAnySheetVisible = showMenuSheet || showSaveListBottomSheet || userToRemove != null || showLeaveConfirmation
    val targetScale = if (isAnySheetVisible) 0.92f + (0.08f * sheetMotionProgress) else 1.0f
    val backdropScale by animateFloatAsState(targetValue = targetScale, animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f), label = "backdropScale")
    val backdropCornerRadius by animateDpAsState(targetValue = if (isAnySheetVisible) CornerExtraLarge else 0.dp, animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy), label = "backdropCornerRadius")

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        RoomAccessGuardian(
            hasAccess = hasAccess,
            roomName = "Vendors",
            onBackClick = { currentScreenState = VendorScreenState.MAIN }
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
                                onLocationClick = { mainNavController.navigate(Screen.LocationSelector.route) },
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
                                allVendors = allVendorsFromRepo
                            )
                        }
                        VendorScreenState.CATEGORY_DETAIL -> {
                            selectedCategory?.let { category ->
                                VendorCategoryDetailContent(
                                    category = category,
                                    selectedCity = selectedCity,
                                    onBackClick = { currentScreenState = VendorScreenState.MAIN },
                                    onLocationClick = { mainNavController.navigate(Screen.LocationSelector.route) },
                                    onMenuClick = { showMenuSheet = true },
                                    onVendorClick = handleVendorClick,
                                    onFavoriteToggle = handleFavoriteToggle,
                                    vendorSavedDestinations = vendorSavedDestinations,
                                    timelineEvents = timelineEvents,
                                    vendorViewModel = vendorViewModel,
                                    sharedTransitionScope = sharedTransitionScope
                                )
                            }
                        }
                        VendorScreenState.VENDOR_DETAIL -> {
                            selectedVendor?.let { vendor ->
                                VendorDetailScreen(
                                    vendorDetail = vendor,
                                    onBackClick = { currentScreenState = VendorScreenState.MAIN },
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
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                        VendorScreenState.ROOM -> {
                            activeEvent?.let { event ->
                                VendorRoomContent(
                                    eventId = event.id,
                                    roomViewModel = roomViewModel,
                                    onBackClick = { currentScreenState = VendorScreenState.MAIN },
                                    onRemove = { userToRemove = it },
                                    onLeave = { showLeaveConfirmation = true },
                                    onShowToast = { toastData = it }
                                )
                            }
                        }
                        else -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Screen for $state coming soon")
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = toastData.message != null && !isAnySheetVisible,
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
                        icon = painterResource(R.drawable.ic_heart),
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
                onDone = { showSaveListBottomSheet = false },
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
    allVendors: List<Vendor>
) {
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
                        onBackClick = {},
                        onMenuClick = onMenuClick,
                        onDropdownClick = onLocationClick,
                        isLargeTitle = true
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
                CustomSearchBar(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    onActiveChange = onSearchActiveChange,
                    placeholder = "Search Vendors",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (!isSearchActive) {
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
                        onVendorClick = onVendorClick,
                        onFavoriteToggle = onFavoriteToggle
                    )
                }
                item {
                    VendorCarousel(
                        title = "Best Photographers in $selectedCity",
                        vendors = allVendors.filter { it.category == "Photography" }.ifEmpty { MockData.sampleVendors.filter { it.category == "Photography" } },
                        onVendorClick = onVendorClick,
                        onFavoriteToggle = onFavoriteToggle
                    )
                }
                item {
                    VendorCarousel(
                        title = "Expert Mehendi Artists in $selectedCity",
                        vendors = allVendors.filter { it.category == "Mehendi" }.ifEmpty { MockData.sampleVendors.filter { it.category == "Mehendi" } },
                        onVendorClick = onVendorClick,
                        onFavoriteToggle = onFavoriteToggle
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
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var selectedTab by remember { mutableStateOf("explore") }

    var selectedViewType by remember { mutableStateOf("By List") }
    val viewOptions = listOf("By Timeline", "By List")
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }

    var recentSearchesNames by remember { mutableStateOf(getCategoryRecentSearches(context, category.name)) }

    val sortOptions = listOf("Relevance", "Price: Low to High", "Price: High to Low", "Rating: High to Low")
    var appliedSortOption by remember { mutableStateOf(sortOptions[0]) }
    val filterByOptions = listOf("Premium", "Top Rated", "Available Now")
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

    val filteredVendors = remember(allVendors, searchQuery, appliedSortOption, appliedFilterOptions, vendorSavedDestinations) {
        var result = allVendors.filter { it.name.contains(searchQuery, ignoreCase = true) }
        
        // Apply Filters
        if (appliedFilterOptions.contains("Top Rated")) {
            result = result.filter { it.rating >= 4.5 }
        }

        // Apply Sorting
        result = when (appliedSortOption) {
            "Price: Low to High" -> result.sortedBy { it.priceStartsFrom.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            "Price: High to Low" -> result.sortedByDescending { it.priceStartsFrom.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            "Rating: High to Low" -> result.sortedByDescending { it.rating }
            else -> result
        }

        result.map { it.copy(favorite = vendorSavedDestinations.containsKey("${it.name}-${it.category}")) }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = category.name,
                    subtitle = selectedCity,
                    onBackClick = onBackClick,
                    backIcon = TopIcon.Predefined.BACK,
                    onMenuClick = onMenuClick,
                    onDropdownClick = onLocationClick,
                    isLargeTitle = true
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
                            AnimatedVisibility(
                                visible = !isSearchActive,
                                enter = fadeIn(animationSpec = tween(200)) + expandHorizontally(expandFrom = Alignment.End, animationSpec = tween(250)),
                                exit = fadeOut(animationSpec = tween(150)) + shrinkHorizontally(shrinkTowards = Alignment.End, animationSpec = tween(250))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(Modifier.width(8.dp))
                                    FilterButton(onClick = { showFilterSheet = true })
                                }
                            }
                        }
                    }

                    if (!isSearchActive) {
                        item {
                            VendorCarousel(
                                title = "Top-Rated ${category.name}",
                                vendors = filteredVendors.filter { it.rating >= 4.5 },
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

                    if (selectedViewType == "By List") {
                        if (savedVendorsList.isEmpty()) {
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
                        if (savedTimelineEvents.isEmpty()) {
                            item { EmptySavedState() }
                        } else {
                            items(savedTimelineEvents) { timelineItem ->
                                val vendorsForEvent = allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name && it.destination == timelineItem.id } }.map { it.copy(favorite = true) }
                                VendorTimelineSection(
                                    date = timelineItem.date,
                                    event = timelineItem.event,
                                    vendors = vendorsForEvent,
                                    onVendorClick = onVendorClick,
                                    onFavoriteToggle = onFavoriteToggle,
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

    if (showFilterSheet) {
        SortFilterBottomSheet(
            sortOptions = sortOptions,
            initialSortOption = appliedSortOption,
            filterByOptions = filterByOptions,
            initialFilterOptions = appliedFilterOptions,
            onDismiss = { showFilterSheet = false },
            onApply = { sort, filters ->
                appliedSortOption = sort
                appliedFilterOptions = filters
                showFilterSheet = false
            }
        )
    }
}

@Composable
fun VendorTimelineSection(
    date: String,
    event: String,
    vendors: List<Vendor>,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(20.dp, CornerSmoothingDefault))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(20.dp, CornerSmoothingDefault)
            )
            .background(SurfaceSecondary),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = date, style = JasnifyTheme.typography.labelLarge, color = ContentSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event, style = JasnifyTheme.typography.labelXLarge, color = ContentBrandDark, fontWeight = FontWeight.Medium)
            }
        }

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            items(vendors) { vendor ->
                VendorCardCompact(
                    vendor = vendor,
                    onCardClick = { onVendorClick(vendor) },
                    onFavoriteToggle = { onFavoriteToggle(vendor) },
                    compactCardSize = CompactCardSize.SMALL,
                    sharedTransitionScope = sharedTransitionScope
                )
            }
        }
    }
}

@Composable
fun LazyItemScope.EmptySavedState() {
    Box(
        modifier = Modifier.fillParentMaxHeight(0.7f).fillMaxWidth(),
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AllSavedVendorsContent(
    onBackClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    vendorSavedDestinations: Map<String, String>,
    timelineEvents: List<TimelineEvent>,
    allVendors: List<Vendor>,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    var selectedViewType by remember { mutableStateOf("By List") }
    val viewOptions = listOf("By Timeline", "By List")

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

            if (selectedViewType == "By List") {
                if (savedVendorsList.isEmpty()) {
                    item {
                        Box(modifier = Modifier
                            .fillParentMaxHeight(0.7f)
                            .fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No saved vendors yet", style = JasnifyTheme.typography.bodyLarge, color = ContentPrimary)
                        }
                    }
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
                if (savedTimelineEvents.isEmpty()) {
                    item { EmptySavedState() }
                } else {
                    items(savedTimelineEvents) { timelineItem ->
                        val vendorsForEvent = allVendors.filter { v ->
                            vendorSavedDestinations["${v.name}-${v.category}"] == timelineItem.id
                        }.map { it.copy(favorite = true) }

                        VendorTimelineSection(
                            date = timelineItem.date,
                            event = timelineItem.event,
                            vendors = vendorsForEvent,
                            onVendorClick = onVendorClick,
                            onFavoriteToggle = onFavoriteToggle,
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
        onMenuClick = {}, // Handled internally in RoomScreen for non-admins
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

@Composable
fun VendorCategoryGrid(categories: List<VendorCategoryItem>, onCategoryClick: (VendorCategoryItem) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val rows = categories.chunked(3)
        rows.forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    VendorTypeChip(
                        label = item.name,
                        icon = item.icon,
                        isLarge = true,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onCategoryClick(item)
                        }
                    )
                }
                if (rowItems.size < 3) repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun ExploreCategoriesHorizontal(categories: List<VendorCategoryItem>, onCategoryClick: (VendorCategoryItem) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)) {
        Text(text = "Explore Categories", style = JasnifyTheme.typography.headingLarge, fontWeight = FontWeight.Medium, color = ContentPrimary, modifier = Modifier.padding(horizontal = 12.dp))
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { item ->
                VendorTypeChip(
                    label = item.name,
                    icon = item.icon,
                    subLabel = "Explore Now",
                    isLarge = false,
                    onClick = {
                        onCategoryClick(item)
                    }
                )
            }
        }
    }
}
