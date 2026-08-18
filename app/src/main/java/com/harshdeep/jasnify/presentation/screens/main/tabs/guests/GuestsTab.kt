package com.harshdeep.jasnify.presentation.screens.main.tabs.guests

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Contact
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.model.GuestType
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddGuestInfoBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddGuestTypeBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ContactPickerBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomSuccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.GuestDetailsBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.RecentActivityBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SelectGuestTypeBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.GuestCard
import com.harshdeep.jasnify.presentation.components.cards.GuestCardType
import com.harshdeep.jasnify.presentation.components.cards.ImportContactsBanner
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.GuestViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.utils.ContactHelper
import com.harshdeep.jasnify.utils.SearchHistoryManager
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

enum class GuestsView {
    MAIN,
    MANAGE_GUEST_TYPES,
    GUEST_TYPE_DETAIL,
    ROOM_ACCESS
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GuestsTab(
    onBottomBarVisibilityChange: (Boolean) -> Unit = {},
    roomViewModel: RoomViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    guestViewModel: GuestViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current

    val auth = FirebaseAuth.getInstance()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val guestsFromCloud by guestViewModel.guests.collectAsStateWithLifecycle()
    val isLoading by guestViewModel.isLoading.collectAsStateWithLifecycle()

    val currentUserUid = auth.currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        roomViewModel.resetAccessState()
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEvent) {
        val event = activeEvent
        if (event != null) {
            roomViewModel.verifyAccess(event.id, "Guest", currentUserUid)
            roomViewModel.loadRoomUsers(event.id, "Guest")
            guestViewModel.setEventId(event.id)
        } else {
            roomViewModel.setAccessState(true)
        }
    }

    val searchHistoryManager = remember { SearchHistoryManager(context) }
    var recentSearches by remember { mutableStateOf(searchHistoryManager.getRecentSearches()) }
    var currentView by remember { mutableStateOf(GuestsView.MAIN) }
    var selectedGuestTypeForDetail by remember { mutableStateOf<String?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            recentSearches = searchHistoryManager.getRecentSearches()
        }
    }

    val guests = guestsFromCloud

    var selectedGuestForInfo by remember { mutableStateOf<Guest?>(null) }
    var selectedGuestForRecentActivity by remember { mutableStateOf<Guest?>(null) }
    var guestToDeleteForInfo by remember { mutableStateOf<Guest?>(null) }
    var selectedGuestForEdit by remember { mutableStateOf<Guest?>(null) }
    var selectedGuestTypeForEdit by remember { mutableStateOf<String?>(null) }
    var showAddGuestSheet by remember { mutableStateOf(false) }
    var showAddTypeSheet by remember { mutableStateOf(false) }
    var showTypeFilterSheet by remember { mutableStateOf(false) }
    var selectedTypesFilter by remember { mutableStateOf<List<String>>(emptyList()) }

    var phoneContacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var showContactPicker by remember { mutableStateOf(false) }
    var inviteFilter by remember { mutableStateOf<String?>(null) }
    var showMenuSheet by remember { mutableStateOf(false) }

    var isMultiSelectMode by remember { mutableStateOf(false) }
    val selectedGuestIds = remember { mutableStateListOf<String>() }
    var showMultiDeleteConfirmation by remember { mutableStateOf(false) }
    var showSuccessSheet by remember { mutableStateOf(false) }
    var importedCount by remember { mutableIntStateOf(0) }

    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }

    var isHeaderVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }
    val mainListState = rememberLazyListState()

    var isBannerDismissed by remember { mutableStateOf(false) }

    val topBarMaxScrollPx = with(density) { 56.dp.toPx() }
    val topBarScrollProgress by remember {
        derivedStateOf {
            if (mainListState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (mainListState.firstVisibleItemScrollOffset / topBarMaxScrollPx).coerceIn(0f, 1f)
            }
        }
    }

    val guestsNestedScrollConnection = remember(mainListState, currentView, isSearchActive, isMultiSelectMode) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (currentView != GuestsView.MAIN || isSearchActive || isMultiSelectMode) return Offset.Zero

                val delta = available.y
                val canScroll = mainListState.canScrollForward || mainListState.canScrollBackward
                if (!canScroll) {
                    isHeaderVisible = true
                    return Offset.Zero
                }

                if (delta > 0) {
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) {
                    if (scrollAccumulator > 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                }

                if (scrollAccumulator > 150f && !isHeaderVisible) {
                    isHeaderVisible = true
                    scrollAccumulator = 0f
                } else if (scrollAccumulator < -150f && isHeaderVisible) {
                    isHeaderVisible = false
                    scrollAccumulator = 0f
                }

                return Offset.Zero
            }
        }
    }

    var toastData by remember { mutableStateOf<ToastData?>(null) }
    var activeToastData by remember { mutableStateOf<ToastData?>(null) }

    LaunchedEffect(toastData?.message) {
        if (toastData?.message != null) {
            activeToastData = toastData
            delay(3000.milliseconds)
            toastData = null
        }
    }

    var hasContactPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        )
    }

    val isOwner = activeEvent?.ownerId == currentUserUid
    val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
    val currentUserRole = when {
        isOwner -> UserRole.OWNER
        currentUserInRoom != null -> currentUserInRoom.role
        else -> UserRole.VIEWER
    }
    val isViewer = currentUserRole == UserRole.VIEWER

    LaunchedEffect(inviteFilter, selectedTypesFilter) {
        selectedGuestIds.clear()
    }

    var sheetMotionProgress by remember { mutableFloatStateOf(0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            selectedGuestForInfo != null ||
                    selectedGuestForRecentActivity != null ||
                    selectedGuestForEdit != null ||
                    showAddGuestSheet ||
                    showAddTypeSheet ||
                    showTypeFilterSheet ||
                    showContactPicker ||
                    showMenuSheet ||
                    guestToDeleteForInfo != null ||
                    showMultiDeleteConfirmation ||
                    showSuccessSheet ||
                    showRoomMenuBottomSheet ||
                    userToRemove != null ||
                    showLeaveConfirmation
        }
    }

    val isBottomBarVisible by remember {
        derivedStateOf {
            hasAccess == true && !isAnyBottomSheetOpen && currentView == GuestsView.MAIN && !isMultiSelectMode && toastData?.message == null && isHeaderVisible
        }
    }

    LaunchedEffect(isBottomBarVisible) {
        onBottomBarVisibilityChange(isBottomBarVisible)
    }

    val targetScale by remember {
        derivedStateOf {
            if (isAnyBottomSheetOpen) {
                0.92f + (0.08f * sheetMotionProgress)
            } else {
                1.0f
            }
        }
    }

    val backdropScaleState = animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadiusState = animateDpAsState(
        targetValue = if (isAnyBottomSheetOpen) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactPermission = isGranted
        if (isGranted) {
            phoneContacts = ContactHelper.fetchContacts(context)
            showContactPicker = true
        }
    }

    val guestTypes = remember {
        mutableStateListOf(
            "Family", "Close Friend", "Office", "College", "Apartment", "Others"
        )
    }

    val typeColors = remember { mutableStateMapOf<String, Color>() }
    val colorPalette = remember {
        listOf(
            Color(0xFF4CAF50), Color(0xFF635994), Color(0xFF2196F3),
            Color(0xFFFF9800), Color(0xFFE91E63), Color(0xFF9C27B0),
            Color(0xFF00BCD4), Color(0xFF009688), Color(0xFFFF5722)
        )
    }

    LaunchedEffect(guestTypes.size) {
        guestTypes.forEachIndexed { _, type ->
            if (!typeColors.containsKey(type)) {
                typeColors[type] = colorPalette[typeColors.size % colorPalette.size]
            }
        }
    }

    val onInviteToggle = { guestId: String ->
        val guest = guests.find { it.id == guestId }
        if (guest != null && !isViewer) {
            val nameFromAuth = auth.currentUser?.displayName
            val nameFromRoom = currentUserInRoom?.name
            val currentUserName = when {
                !nameFromRoom.isNullOrBlank() -> nameFromRoom
                !nameFromAuth.isNullOrBlank() -> nameFromAuth
                else -> "User"
            }

            val isInviting = !guest.invited
            val timestamp = SimpleDateFormat("MMM dd, yyyy, hh:mma", Locale.getDefault()).format(Date())

            guestViewModel.updateGuest(
                guest.copy(
                    invited = isInviting,
                    invitedBy = if (isInviting) currentUserName else null,
                    invitedAt = if (isInviting) timestamp else null
                )
            )
        }
    }

    val filteredGuests by remember(searchQuery, selectedTypesFilter, inviteFilter, guests) {
        derivedStateOf {
            val query = searchQuery.trim()
            guests.filter { guest ->
                val matchesSearch = query.isEmpty() || guest.name.contains(query, ignoreCase = true)
                val matchesType = selectedTypesFilter.isEmpty() || selectedTypesFilter.contains(guest.type)
                val matchesInvite = when (inviteFilter) {
                    "Yet to invite" -> !guest.invited
                    "Already invited" -> guest.invited
                    else -> true
                }
                matchesSearch && matchesType && matchesInvite
            }
        }
    }

    val onAddGuestClick = {
        val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        hasContactPermission = isGranted
        if (isGranted) {
            phoneContacts = ContactHelper.fetchContacts(context)
            showContactPicker = true
        } else {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    BackHandler(enabled = currentView != GuestsView.MAIN || isSearchActive || isMultiSelectMode) {
        if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
        } else if (isMultiSelectMode) {
            isMultiSelectMode = false
            selectedGuestIds.clear()
        } else {
            when (currentView) {
                GuestsView.GUEST_TYPE_DETAIL -> currentView = GuestsView.MANAGE_GUEST_TYPES
                GuestsView.MANAGE_GUEST_TYPES -> currentView = GuestsView.MAIN
                GuestsView.ROOM_ACCESS -> currentView = GuestsView.MAIN
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .noRippleClickable {
                focusManager.clearFocus()
            }
            .nestedScroll(guestsNestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = backdropScaleState.value
                    scaleY = backdropScaleState.value
                    val radius = backdropCornerRadiusState.value
                    clip = isAnyBottomSheetOpen || radius > 0.dp
                    shape = RoundedCornerShape(radius.coerceAtLeast(0.dp))
                }
        ) {
            RoomAccessGuardian(
                hasAccess = hasAccess,
                roomName = "Guest",
                onBackClick = onBackClick
            ) {
                AnimatedContent(
                    targetState = currentView,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ViewTransition"
                ) { view ->
                    when (view) {
                        GuestsView.MAIN -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(BackgroundPrimary)
                            ) {
                                if (isSearchActive) {
                                    GuestSearchScreen(
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { searchQuery = it },
                                        onBackClick = {
                                            if (searchQuery.isNotEmpty()) {
                                                searchHistoryManager.addSearch(searchQuery)
                                                recentSearches = searchHistoryManager.getRecentSearches()
                                            }
                                            isSearchActive = false
                                            searchQuery = ""
                                        },
                                        recentSearches = recentSearches,
                                        onClearRecent = {
                                            searchHistoryManager.clearAll()
                                            recentSearches = emptyList()
                                        },
                                        onRemoveRecent = { search ->
                                            searchHistoryManager.removeSearch(search)
                                            recentSearches = searchHistoryManager.getRecentSearches()
                                        },
                                        searchResults = filteredGuests,
                                        onGuestClick = { guest ->
                                            selectedGuestForInfo = guest
                                            searchHistoryManager.addSearch(guest.name)
                                            recentSearches = searchHistoryManager.getRecentSearches()
                                        },
                                        onAddGuestClick = onAddGuestClick,
                                        getGuestTypeColor = { type -> typeColors.getOrDefault(type, Color.Gray) },
                                        onInviteToggle = onInviteToggle
                                    )
                                } else if (isLoading) {
                                    Box(modifier = Modifier.fillMaxSize())
                                } else {
                                    var expandedGuestId by remember { mutableStateOf<String?>(null) }

                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .statusBarsPadding(),
                                        state = mainListState,
                                    ) {
                                        item(key = "top_bar", contentType = "header") {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .graphicsLayer {
                                                        alpha = (1f - topBarScrollProgress).coerceIn(0f, 1f)
                                                        translationY = -topBarScrollProgress * 30f
                                                    }
                                            ) {
                                                if (!isMultiSelectMode) {
                                                    CustomTopBar(
                                                        title = "Guests",
                                                        titleIcon = painterResource(R.drawable.ic_guests),
                                                        menuIcon = TopIcon.Predefined.MENU_MODERN,
                                                        isLeftAligned = true,
                                                        isLargeTitle = true,
                                                        onMenuClick = {
                                                            focusManager.clearFocus()
                                                            showMenuSheet = true
                                                        },
                                                        buttonStyle = ButtonBackground.OPAQUE
                                                    )
                                                } else {
                                                    CustomTopBar(
                                                        title = "${selectedGuestIds.size} selected",
                                                        onBackClick = {
                                                            isMultiSelectMode = false
                                                            selectedGuestIds.clear()
                                                        },
                                                        menuIcon = TopIcon.CustomPainter(painterResource(R.drawable.ic_delete)),
                                                        onMenuClick = if (selectedGuestIds.isNotEmpty()) {
                                                            {
                                                                focusManager.clearFocus()
                                                                showMultiDeleteConfirmation = true
                                                            }
                                                        } else null,
                                                        buttonStyle = ButtonBackground.OPAQUE
                                                    )
                                                }
                                            }
                                        }

                                        if (guests.isNotEmpty()) {
                                            stickyHeader(key = "search_and_filters", contentType = "sticky_filter_header") {
                                                Column(
                                                    modifier = Modifier
                                                        .background(BackgroundPrimary)
                                                        .fillMaxWidth()
                                                        .zIndex(10f)
                                                ) {
                                                    if (!isMultiSelectMode) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(start = 12.dp, top = 12.dp, bottom = 0.dp, end = 12.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            CustomSearchBar(
                                                                value = searchQuery,
                                                                onValueChange = { searchQuery = it },
                                                                placeholder = "Search Guests",
                                                                modifier = Modifier.weight(1f),
                                                                onActiveChange = { if (it) isSearchActive = true }
                                                            )

                                                            if (!isViewer) {
                                                                CustomTextButton(
                                                                    onClick = onAddGuestClick,
                                                                    text = "Add",
                                                                    leadingIcon = painterResource(id = R.drawable.ic_plus),
                                                                    shapeStyle = ButtonShapeStyle.Round
                                                                )
                                                            }
                                                        }
                                                    }

                                                    LazyRow(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(top = 12.dp),
                                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        item(key = "type_filter_chip", contentType = "filter_chip") {
                                                            FilterChip(
                                                                label = when {
                                                                    selectedTypesFilter.isEmpty() -> "Guest Type"
                                                                    selectedTypesFilter.size == 1 -> selectedTypesFilter.first()
                                                                    else -> "Guest Types (${selectedTypesFilter.size})"
                                                                },
                                                                hasDropdown = true,
                                                                isSelected = selectedTypesFilter.isNotEmpty(),
                                                                onClick = { showTypeFilterSheet = true },
                                                                shapeStyle = ChipShapeStyle.Round,
                                                                hasStroke = true
                                                            )
                                                        }
                                                        item(key = "yet_to_invite_chip", contentType = "filter_chip") {
                                                            FilterChip(
                                                                label = "Yet to invite",
                                                                isSelected = inviteFilter == "Yet to invite",
                                                                onClick = {
                                                                    inviteFilter = if (inviteFilter == "Yet to invite") null else "Yet to invite"
                                                                },
                                                                shapeStyle = ChipShapeStyle.Round,
                                                                hasStroke = true
                                                            )
                                                        }
                                                        item(key = "already_invited_chip", contentType = "filter_chip") {
                                                            FilterChip(
                                                                label = "Already invited",
                                                                isSelected = inviteFilter == "Already invited",
                                                                onClick = {
                                                                    inviteFilter = if (inviteFilter == "Already invited") null else "Already invited"
                                                                },
                                                                shapeStyle = ChipShapeStyle.Round,
                                                                hasStroke = true
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                }
                                            }
                                        }

                                        if (guests.isEmpty()) {
                                            item(key = "empty_state", contentType = "empty_state") {
                                                GuestEmptyState(
                                                    modifier = Modifier.fillParentMaxHeight(0.80f),
                                                    hasContactPermission = hasContactPermission,
                                                    onAddGuestClick = onAddGuestClick,
                                                    onAddManuallyClick = {
                                                        selectedGuestForEdit = null
                                                        showAddGuestSheet = true
                                                    }
                                                )
                                            }
                                        } else if (filteredGuests.isEmpty()) {
                                            item(key = "no_guests_found", contentType = "empty_state") {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .fillParentMaxHeight(0.80f),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center,
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.ic_cross_arrow),
                                                            contentDescription = null,
                                                            tint = ContentTertiary,
                                                            modifier = Modifier.size(84.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                            text = "No guests found",
                                                            style = JasnifyTheme.typography.displayMedium.copy(
                                                                textAlign = TextAlign.Center,
                                                                fontWeight = FontWeight.Medium
                                                            ),
                                                            color = ContentTertiary
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            if (!hasContactPermission && !isBannerDismissed) {
                                                item(key = "contacts_banner", contentType = "banner") {
                                                    ImportContactsBanner(
                                                        onAllowAccessClick = {
                                                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                                                        },
                                                        onDismissClick = {
                                                            isBannerDismissed = true
                                                        },
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }

                                            itemsIndexed(
                                                items = filteredGuests,
                                                key = { _, guest -> guest.id },
                                                contentType = { _, _ -> "guest_card" }
                                            ) { index, guest ->

                                                val isExpanded = expandedGuestId == guest.id
                                                val isSelected = selectedGuestIds.contains(guest.id)

                                                val topRadius = if (index == 0) CornerLargeIncrease else CornerExtraSmall
                                                val bottomRadius =
                                                    if (index == filteredGuests.lastIndex) CornerLargeIncrease else CornerExtraSmall

                                                val itemShape = SquircleShape(
                                                    topStart = topRadius,
                                                    topEnd = topRadius,
                                                    bottomStart = bottomRadius,
                                                    bottomEnd = bottomRadius,
                                                    cornerSmoothing = CornerSmoothingDefault
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 12.dp, vertical = 1.dp)
                                                ) {
                                                    GuestCard(
                                                        name = guest.name,
                                                        label = guest.type,
                                                        imageUrl = guest.imageUrl,
                                                        isInvited = guest.invited,
                                                        invitedBy = guest.invitedBy,
                                                        invitedAt = guest.invitedAt,
                                                        type = when {
                                                            isMultiSelectMode -> GuestCardType.SELECTABLE
                                                            isViewer -> GuestCardType.DEFAULT
                                                            else -> GuestCardType.INVITE_ACTION
                                                        },
                                                        isSelected = isSelected,
                                                        showActions = isExpanded,
                                                        labelColor = typeColors.getOrDefault(guest.type, Color.Gray),
                                                        onCardClick = {
                                                            if (isMultiSelectMode) {
                                                                if (isSelected) selectedGuestIds.remove(guest.id) else selectedGuestIds.add(guest.id)
                                                            } else {
                                                                expandedGuestId = if (isExpanded) null else guest.id
                                                            }
                                                        },
                                                        onSelectToggle = { selected ->
                                                            if (selected) {
                                                                selectedGuestIds.add(guest.id)
                                                            } else {
                                                                selectedGuestIds.remove(guest.id)
                                                            }
                                                        },
                                                        onViewDetailsClick = {
                                                            selectedGuestForInfo = guest
                                                        },
                                                        onInviteClick = {
                                                            onInviteToggle(guest.id)
                                                        },
                                                        cardShape = itemShape
                                                    )
                                                }
                                            }

                                            if (guests.size > 10) {
                                                item(key = "footer", contentType = "footer") {
                                                    FooterJansify(modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
                                                }
                                            }
                                        }
                                    }

                                    if (isMultiSelectMode && selectedGuestIds.isNotEmpty() && inviteFilter != "Already invited") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        colorStops = arrayOf(
                                                            0.00f to Color.Transparent,
                                                            0.25f to BackgroundPrimary.copy(alpha = 0.15f),
                                                            0.55f to BackgroundPrimary.copy(alpha = 0.65f),
                                                            0.80f to BackgroundPrimary.copy(alpha = 0.92f),
                                                            1.00f to BackgroundPrimary
                                                        )
                                                    )
                                                )
                                                .navigationBarsPadding()
                                                .padding(horizontal = 12.dp, vertical = 12.dp)
                                        ) {
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(62.dp)
                                                    .pill360Shadow(
                                                        ambientColor = Color.Black.copy(alpha = 0.10f),
                                                        ambientBlur = 12.dp,
                                                        ambientSpread = 2.dp,
                                                        spotColor = Color.Black.copy(alpha = 0.15f),
                                                        spotBlur = 18.dp,
                                                        spotOffsetY = 4.dp
                                                    ),
                                                color = SurfacePrimary,
                                                shape = CircleShape
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    CustomTextButton(
                                                        onClick = {
                                                            val nameFromAuth = auth.currentUser?.displayName
                                                            val nameFromRoom = currentUserInRoom?.name
                                                            val currentUserName = when {
                                                                !nameFromRoom.isNullOrBlank() -> nameFromRoom
                                                                !nameFromAuth.isNullOrBlank() -> nameFromAuth
                                                                else -> "User"
                                                            }

                                                            val selectedGuests = filteredGuests.filter { it.id in selectedGuestIds }
                                                            selectedGuests.forEach { guest ->
                                                                val timestamp = SimpleDateFormat("MMM dd, yyyy, hh:mma", Locale.getDefault()).format(Date())
                                                                guestViewModel.updateGuest(
                                                                    guest.copy(
                                                                        invited = true,
                                                                        invitedBy = currentUserName,
                                                                        invitedAt = timestamp
                                                                    )
                                                                )
                                                            }
                                                            selectedGuestIds.clear()
                                                            isMultiSelectMode = false
                                                        },
                                                        text = "Mark all as invited",
                                                        leadingIcon = painterResource(id = R.drawable.ic_users_tick),
                                                        modifier = Modifier.fillMaxWidth(),
                                                        containerColor = ContentPrimary,
                                                        contentColor = ContentInvPrimary,
                                                        shapeStyle = ButtonShapeStyle.Round
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        GuestsView.MANAGE_GUEST_TYPES -> {
                            GuestTypeScreen(
                                isViewer = isViewer,
                                guestTypes = guestTypes.map { typeName ->
                                    GuestType(
                                        name = typeName,
                                        guestCount = guests.count { it.type == typeName }
                                    )
                                },
                                onBackClick = { currentView = GuestsView.MAIN },
                                onAddTypeClick = { showAddTypeSheet = true },
                                onTypeClick = { type ->
                                    selectedGuestTypeForDetail = type.name
                                    currentView = GuestsView.GUEST_TYPE_DETAIL
                                },
                                getGuestThumbnails = { typeName ->
                                    guests.filter { it.type == typeName }.mapNotNull { it.imageUrl }.take(3)
                                }
                            )
                        }
                        GuestsView.GUEST_TYPE_DETAIL -> {
                            GuestTypeDetailScreen(
                                isViewer = isViewer,
                                typeName = selectedGuestTypeForDetail ?: "",
                                guests = guests.filter { it.type == selectedGuestTypeForDetail },
                                onBackClick = { currentView = GuestsView.MANAGE_GUEST_TYPES },
                                getGuestTypeColor = { type -> typeColors.getOrDefault(type, Color.Gray) },
                                onViewDetails = { guest -> selectedGuestForInfo = guest },
                                onEditTypeClick = {
                                    selectedGuestTypeForEdit = selectedGuestTypeForDetail
                                    showAddTypeSheet = true
                                },
                                onInviteToggle = onInviteToggle
                            )
                        }
                        GuestsView.ROOM_ACCESS -> {
                            activeEvent?.id?.let { id ->
                                GuestRoomContent(
                                    eventId = id,
                                    roomViewModel = roomViewModel,
                                    onBackClick = { currentView = GuestsView.MAIN },
                                    onMenuClick = {
                                        focusManager.clearFocus()
                                        showRoomMenuBottomSheet = true
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
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = toastData?.message != null && !isAnyBottomSheetOpen,
                enter = slideInVertically(initialOffsetY = { -it - 500 }),
                exit = slideOutVertically(targetOffsetY = { -it - 500 }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .statusBarsPadding()
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
        }

        if (selectedGuestForInfo != null) {
            val currentGuest = guests.find { it.id == selectedGuestForInfo?.id } ?: selectedGuestForInfo!!

            GuestDetailsBottomSheet(
                isViewer = isViewer,
                guest = currentGuest,
                onDismiss = { selectedGuestForInfo = null },
                onProgress = { sheetMotionProgress = it },
                onEditClick = {
                    selectedGuestForEdit = currentGuest
                    selectedGuestForInfo = null
                },
                onInviteClick = {
                    onInviteToggle(currentGuest.id)
                },
                onDeleteClick = {
                    guestToDeleteForInfo = currentGuest
                    selectedGuestForInfo = null
                },
                onRecentActivityClick = {
                    selectedGuestForRecentActivity = currentGuest
                }
            )
        }

        if (selectedGuestForRecentActivity != null) {
            RecentActivityBottomSheet(
                guest = selectedGuestForRecentActivity!!,
                onDismiss = { selectedGuestForRecentActivity = null },
                onProgress = { sheetMotionProgress = it }
            )
        }

        guestToDeleteForInfo?.let { guest ->
            val formattedName = remember(guest.name) {
                val parts = guest.name.trim().split(" ").filter { it.isNotBlank() }
                if (parts.size >= 2) "${parts[0]} ${parts[1].take(1)}." else parts.firstOrNull() ?: ""
            }
            ConfirmationBottomSheet(
                heading = "Are you sure?",
                subHeading = "${guest.name} will be removed from the guest list.",
                confirmButtonText = "Remove $formattedName",
                onDismiss = { guestToDeleteForInfo = null },
                onConfirm = {
                    guestViewModel.deleteGuest(guest.id)
                    guestToDeleteForInfo = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showMultiDeleteConfirmation) {
            val selectedGuests = guests.filter { it.id in selectedGuestIds }
            val subHeading = when {
                selectedGuests.size == 1 -> "${selectedGuests[0].name} will be removed from the guest list."
                selectedGuests.size == 2 -> "${selectedGuests[0].name} and ${selectedGuests[1].name} will be removed from the guest list."
                selectedGuests.size > 2 -> {
                    val n1 = selectedGuests[0].name.trim().split(" ").firstOrNull { it.isNotBlank() } ?: ""
                    val n2 = selectedGuests[1].name.trim().split(" ").drop(1).firstOrNull { it.isNotBlank() } ?: ""
                    "$n1, $n2 & ${selectedGuests.size - 2} other guests will be removed from the guest list."
                }
                else -> ""
            }

            ConfirmationBottomSheet(
                heading = "Are you sure?",
                subHeading = subHeading,
                confirmButtonText = "Remove All ${selectedGuestIds.size} Guests",
                onDismiss = { showMultiDeleteConfirmation = false },
                onConfirm = {
                    guestViewModel.deleteMultipleGuests(selectedGuestIds.toList())
                    selectedGuestIds.clear()
                    isMultiSelectMode = false
                    showMultiDeleteConfirmation = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showAddGuestSheet || selectedGuestForEdit != null) {
            AddGuestInfoBottomSheet(
                onDismiss = {
                    showAddGuestSheet = false
                    selectedGuestForEdit = null
                },
                onProgress = { sheetMotionProgress = it },
                onUploadPhoto = { uri, guestId, onUrlReady ->
                    guestViewModel.uploadGuestPhoto(uri, guestId, onUrlReady) { error ->
                        toastData = ToastData(error, ToastType.ERROR)
                    }
                },
                onRemovePhoto = { url ->
                    guestViewModel.deleteGuestPhoto(url)
                },
                onAddClick = { newGuest ->
                    val nameFromAuth = auth.currentUser?.displayName
                    val nameFromRoom = currentUserInRoom?.name
                    val currentUserName = when {
                        !nameFromRoom.isNullOrBlank() -> nameFromRoom
                        !nameFromAuth.isNullOrBlank() -> nameFromAuth
                        else -> "User"
                    }
                    val timestamp = SimpleDateFormat("MMM dd, yyyy, hh:mma", Locale.getDefault()).format(Date())

                    if (selectedGuestForEdit != null) {
                        guestViewModel.updateGuest(
                            newGuest.copy(
                                id = selectedGuestForEdit!!.id,
                                updatedBy = currentUserName,
                                updatedAt = timestamp
                            )
                        )
                        selectedGuestForEdit = null
                        toastData = ToastData("Update successfully!", ToastType.SUCCESS)
                    } else {
                        val exists = guests.any {
                            it.name.equals(newGuest.name, ignoreCase = true) &&
                                    it.contactNo == newGuest.contactNo &&
                                    it.contactNo.isNotBlank()
                        }
                        if (exists) {
                            toastData = ToastData("${newGuest.name} already exists", ToastType.DEFAULT)
                        } else {
                            guestViewModel.addGuest(
                                newGuest.copy(
                                    addedBy = currentUserName,
                                    addedAt = timestamp
                                )
                            )
                            showAddGuestSheet = false
                            toastData = ToastData("Guest added successfully!", ToastType.SUCCESS)
                        }
                    }
                },
                onAddNewTypeClick = { showAddTypeSheet = true },
                guestTypes = guestTypes.map { GuestType(name = it) },
                initialGuest = selectedGuestForEdit,
                onError = { message ->
                    toastData = ToastData(message, ToastType.ERROR)
                },
                hasToast = toastData != null,
                toast = {
                    AnimatedVisibility(
                        visible = toastData?.message != null,
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
            )
        }

        if (showAddTypeSheet) {
            AddGuestTypeBottomSheet(
                onDismiss = {
                    showAddTypeSheet = false
                    selectedGuestTypeForEdit = null
                },
                onProgress = { sheetMotionProgress = it },
                initialType = selectedGuestTypeForEdit,
                onAddType = { newType ->
                    if (selectedGuestTypeForEdit != null) {
                        val index = guestTypes.indexOf(selectedGuestTypeForEdit)
                        if (index != -1) {
                            guestTypes[index] = newType
                        }

                        guests.forEachIndexed { _, guest ->
                            if (guest.type == selectedGuestTypeForEdit) {
                                guestViewModel.updateGuest(guest.copy(type = newType))
                            }
                        }

                        if (selectedGuestTypeForDetail == selectedGuestTypeForEdit) {
                            selectedGuestTypeForDetail = newType
                        }
                        selectedGuestTypeForEdit = null
                    } else if (!guestTypes.contains(newType)) {
                        guestTypes.add(newType)
                    }
                    showAddTypeSheet = false
                },
                hasToast = toastData != null,
                toast = {
                    AnimatedVisibility(
                        visible = toastData?.message != null,
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
            )
        }

        if (showTypeFilterSheet) {
            SelectGuestTypeBottomSheet(
                guestTypes = guestTypes.map { typeName ->
                    GuestType(
                        name = typeName,
                        guestCount = guests.count { it.type == typeName }
                    )
                },
                initialSelectedTypes = selectedTypesFilter,
                onDismiss = { showTypeFilterSheet = false },
                onProgress = { sheetMotionProgress = it },
                onApply = { types ->
                    selectedTypesFilter = types
                    showTypeFilterSheet = false
                },
                imageUrls = guests.mapNotNull { it.imageUrl }.distinct()
            )
        }

        if (showContactPicker) {
            ContactPickerBottomSheet(
                contacts = phoneContacts,
                onDismiss = { showContactPicker = false },
                onProgress = { sheetMotionProgress = it },
                onAddManuallyClick = {
                    showContactPicker = false
                    showAddGuestSheet = true
                },
                existingGuestIdentifiers = guests.map { it.name.lowercase() + it.contactNo }.toSet(),
                onContactsSelected = { selectedContacts, includePhoneNo ->
                    if (selectedContacts.isEmpty()) {
                        toastData = ToastData("Please select at least 1 guest!", ToastType.ERROR)
                    } else {
                        val nameFromAuth = auth.currentUser?.displayName
                        val nameFromRoom = currentUserInRoom?.name
                        val currentUserName = when {
                            !nameFromRoom.isNullOrBlank() -> nameFromRoom
                            !nameFromAuth.isNullOrBlank() -> nameFromAuth
                            else -> "User"
                        }
                        val timestamp = SimpleDateFormat("MMM dd, yyyy, hh:mma", Locale.getDefault()).format(Date())

                        var duplicateCount = 0
                        selectedContacts.forEach { contact ->
                            val phoneNumber = if (includePhoneNo) contact.phoneNumber else ""
                            val exists = guests.any {
                                it.name.equals(contact.name, ignoreCase = true) && it.contactNo == phoneNumber
                            }
                            if (!exists) {
                                guestViewModel.addGuest(
                                    Guest(
                                        name = contact.name,
                                        contactNo = phoneNumber,
                                        imageUrl = contact.photoUri,
                                        type = "Others",
                                        addedBy = currentUserName,
                                        addedAt = timestamp
                                    )
                                )
                            } else {
                                duplicateCount++
                            }
                        }
                        if (duplicateCount > 0) {
                            toastData = ToastData(
                                if (duplicateCount == 1) "Guest already exists!" else "$duplicateCount Guests already exist!",
                                ToastType.DEFAULT
                            )
                        }

                        if (selectedContacts.size > duplicateCount) {
                            importedCount = selectedContacts.size - duplicateCount
                            showSuccessSheet = true
                        }

                        showContactPicker = false
                    }
                },
                hasToast = toastData != null,
                toast = {
                    AnimatedVisibility(
                        visible = toastData?.message != null,
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
            )
        }

        if (showSuccessSheet) {
            CustomSuccessBottomSheet(
                message = "$importedCount Contacts Imported",
                onDismiss = { showSuccessSheet = false },
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

        if (showLeaveConfirmation) {
            ConfirmationBottomSheet(
                heading = "Leaving Guest Room?",
                subHeading = "You will lose access to this room and won't be able to see updates.",
                confirmButtonText = "Leave",
                onDismiss = {
                    showLeaveConfirmation = false
                },
                onConfirm = {
                    activeEvent?.id?.let { eventId ->
                        roomViewModel.removeAccess(eventId, "Guest", currentUserUid)
                    }
                    toastData = ToastData("You left the room", ToastType.DEFAULT)
                    currentView = GuestsView.MAIN
                    showLeaveConfirmation = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        userToRemove?.let { user ->
            ConfirmationBottomSheet(
                heading = "Remove ${user.name} from Guest Room?",
                subHeading = "They will not be able to access this room anymore.",
                confirmButtonText = "Remove",
                onDismiss = {
                    userToRemove = null
                },
                onConfirm = {
                    val target = userToRemove
                    val eventId = activeEvent?.id
                    if (target != null && eventId != null) {
                        roomViewModel.removeAccess(eventId, "Guest", target.uid)
                        toastData = ToastData("${target.name} removed from Room!", ToastType.SUCCESS)
                    }
                    userToRemove = null
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showMenuSheet) {
            val menuItems = listOf(
                listOfNotNull(
                    if (!isViewer) {
                        MenuSheetActionItem(
                            text = "Add Guests",
                            icon = painterResource(id = R.drawable.ic_add_circle),
                            iconPlacement = IconPlacement.Top,
                            onClick = {
                                showMenuSheet = false
                                onAddGuestClick()
                            }
                        )
                    } else null,
                    if (guests.isNotEmpty() && !isViewer) {
                        MenuSheetActionItem(
                            text = "Multi-Select",
                            icon = painterResource(id = R.drawable.ic_multi_select),
                            iconPlacement = IconPlacement.Top,
                            onClick = {
                                showMenuSheet = false
                                isMultiSelectMode = true
                                inviteFilter = "Yet to invite"
                            }
                        )
                    } else null
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Manage Guest Type",
                        icon = painterResource(id = R.drawable.ic_category),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuSheet = false
                            currentView = GuestsView.MANAGE_GUEST_TYPES
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = if (isOwner) "Manage Room Access" else "Room Members",
                        icon = painterResource(id = R.drawable.ic_user_default),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenuSheet = false
                            currentView = GuestsView.ROOM_ACCESS
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Help & Feedback",
                        icon = painterResource(id = R.drawable.ic_help_feedback),
                        iconPlacement = IconPlacement.Left,
                        onClick = { showMenuSheet = false }
                    )
                )
            )

            MenuBottomSheet(
                items = menuItems,
                onCancelClick = { showMenuSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }
}