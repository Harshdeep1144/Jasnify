package com.harshdeep.jasnify.presentation.screens.venues

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.domain.model.SubEvent
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.model.VenueReviewsData
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.EventTimeLineInfoSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VenueCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VenueCardFull
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.inputfield.TimeLineInput
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.SubEventItem
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

private const val PREFS_NAME = "venue_search_prefs"
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
    prefs.edit().putString(KEY_RECENT_SEARCHES, limited.joinToString("|||")).apply()
}

private fun clearRecentSearches(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().remove(KEY_RECENT_SEARCHES).apply()
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VenueScreen(
    selectedLocation: String = "City, State",
    initialTab: String = "explore",
    onVenueClick: (Venue) -> Unit,
    onChatClick: (Venue) -> Unit = {},
    onBackClick: () -> Unit,
    isScreenActive: Boolean = true,
    roomViewModel: RoomViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    venueViewModel: VenueViewModel = hiltViewModel(),
    enquiryViewModel: EnquiryViewModel = hiltViewModel()
) {
    var currentAddress by remember { mutableStateOf(selectedLocation) }
    var isLocationPickerVisible by remember { mutableStateOf(false) }
    var showRoomAccess by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(initialTab) }
    var selectedVenueForDetail by remember { mutableStateOf<Venue?>(null) }

    val auth = FirebaseAuth.getInstance()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val savedVenuesFromCloud by venueViewModel.savedVenues.collectAsStateWithLifecycle()
    val allVenues by venueViewModel.allVenues.collectAsStateWithLifecycle()
    val venueReviews by venueViewModel.venueReviews.collectAsStateWithLifecycle()

    val venueSavedDestinations = remember(savedVenuesFromCloud) {
        savedVenuesFromCloud.associate { it.venueName to it.destination }
    }

    val currentUserUid = auth.currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEvent) {
        activeEvent?.let { event ->
            roomViewModel.verifyAccess(event.id, "Venue", currentUserUid)
            roomViewModel.loadRoomUsers(event.id, "Venue")
            venueViewModel.setEventId(event.id)
        }
    }

    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSaveListBottomSheet by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }

    var activeTargetVenue by remember { mutableStateOf<Venue?>(null) }
    var isMySavedListChecked by remember { mutableStateOf(true) }
    var selectedSaveEventId by remember { mutableStateOf<String?>(null) }
    var lastSavedVenue by remember { mutableStateOf<Venue?>(null) }

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

    var toastData by remember { mutableStateOf<ToastData?>(null) }

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

    LaunchedEffect(toastData?.message) {
        if (toastData?.message != null) {
            delay(3000.milliseconds)
            toastData = null
        }
    }

    val focusManager = LocalFocusManager.current

    // Real-time drag progress ratio (0.0f = fully open sheet, 1.0f = fully dismissed sheet)
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    BackHandler(enabled = isLocationPickerVisible || showRoomAccess || selectedVenueForDetail != null) {
        if (isLocationPickerVisible) {
            isLocationPickerVisible = false
        } else if (showRoomAccess) {
            showRoomAccess = false
        } else if (selectedVenueForDetail != null) {
            selectedVenueForDetail = null
        }
    }

    val isAnySheetVisible = showRoomMenuBottomSheet || (userToRemove != null) ||
            showFilterDialog || showSaveListBottomSheet || showMenuSheet || showLeaveConfirmation

    val targetScale = if (isAnySheetVisible) {
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
        targetValue = if (isAnySheetVisible) CornerExtraLarge else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    val isSavedListToast = remember(toastData, lastSavedVenue) {
        toastData?.message?.contains("Saved List") == true && lastSavedVenue != null
    }

    val isOwner = activeEvent?.ownerId == currentUserUid
    val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
    val currentUserRole = when {
        isOwner -> UserRole.OWNER
        currentUserInRoom != null -> currentUserInRoom.role
        else -> UserRole.VIEWER
    }
    val isViewer = currentUserRole == UserRole.VIEWER

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        SharedTransitionLayout {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = backdropScale
                        scaleY = backdropScale
                        clip = isAnySheetVisible || backdropCornerRadius > 0.dp
                        shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                    }
            ) {
                RoomAccessGuardian(
                    hasAccess = hasAccess,
                    roomName = "Venue",
                    onBackClick = onBackClick
                ) {
                    AnimatedContent(
                        targetState = when {
                            isLocationPickerVisible -> "picker"
                            showRoomAccess -> "room"
                            selectedVenueForDetail != null -> "detail"
                            else -> "main"
                        },
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                        },
                        label = "venue_screen_transition",
                        modifier = Modifier.fillMaxSize()
                    ) { state ->
                        // ... (rest of AnimatedContent)
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
                            val displayUsers = if (currentUserInRoom == null && currentUserUid.isNotEmpty()) {
                                val self = User(
                                    uid = currentUserUid,
                                    name = auth.currentUser?.displayName ?: "Me",
                                    email = auth.currentUser?.email ?: "",
                                    role = currentUserRole,
                                    username = auth.currentUser?.email?.substringBefore("@") ?: "me"
                                )
                                (listOf(self) + roomUsers).distinctBy { it.uid }
                            } else {
                                roomUsers
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SurfaceSecondary)
                            ) {
                                RoomScreen(
                                    allUsers = displayUsers,
                                    currentUserRole = currentUserRole,
                                    isSelf = { it.uid == currentUserUid },
                                    onBackClick = { showRoomAccess = false },
                                    onMenuClick = {
                                        focusManager.clearFocus()
                                        showRoomMenuBottomSheet = true
                                    },
                                    onRoleChange = { targetUser, newRole ->
                                        activeEvent?.id?.let { eventId ->
                                            roomViewModel.updateRole(eventId, "Venue", targetUser, newRole)
                                        }
                                    },
                                    onRemove = { targetUser ->
                                        userToRemove = targetUser
                                    },
                                    onReport = { targetUser ->
                                        toastData = ToastData("${targetUser.name} reported", ToastType.DEFAULT)
                                    },
                                    onLeave = {
                                        showLeaveConfirmation = true
                                    },
                                    searchResults = searchResults,
                                    onSearch = { roomViewModel.searchUsers(it) },
                                    onGrantAccess = { email, role ->
                                        activeEvent?.id?.let { eventId ->
                                            roomViewModel.grantAccess(eventId, "Venue", email, role)
                                            toastData = ToastData("Access granted to $email", ToastType.SUCCESS)
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        "detail" -> {
                            selectedVenueForDetail?.let { venue ->
                                val detailData = remember(venue, allVenues, venueReviews) {
                                    val base = allVenues.find { it.id == venue.id } ?:
                                    MockData.venueDetailsMap[venue.name] ?:
                                    MockData.getDetailsForVenue(venue)

                                    if (venueReviews.isNotEmpty()) {
                                        base.copy(reviewsData = base.reviewsData?.copy(reviews = venueReviews) ?: VenueReviewsData(
                                            reviews = venueReviews
                                        )
                                        )
                                    } else {
                                        base
                                    }
                                }
                                VenueDetailScreen(
                                    venueDetail = detailData,
                                    onBackClick = {
                                        selectedVenueForDetail = null
                                        venueViewModel.setSelectedVenueId(null)
                                    },
                                    onChatClick = { venue ->
                                        onChatClick(venue)
                                    },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this@AnimatedContent,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        else -> {
                            VenueMainContent(
                                allVenues = allVenues,
                                selectedLocation = currentAddress,
                                onVenueClick = { venue ->
                                    selectedVenueForDetail = venue
                                    venueViewModel.setSelectedVenueId(venue.id)
                                    onVenueClick(venue)
                                },
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
                                onActiveTargetVenueChange = { activeTargetVenue = it },
                                isMySavedListChecked = isMySavedListChecked,
                                onMySavedListCheckedChange = { isMySavedListChecked = it },
                                selectedSaveEventId = selectedSaveEventId,
                                onSelectedSaveEventIdChange = { selectedSaveEventId = it },
                                venueSavedDestinations = venueSavedDestinations,
                                onToggleSaveVenue = { venue, destination ->
                                    venueViewModel.toggleSaveVenue(venue.name, venue.id, isViewer, destination)
                                },
                                lastSavedVenue = lastSavedVenue,
                                onLastSavedVenueChange = { lastSavedVenue = it },
                                isMultiDay = activeEvent?.multiDay ?: false,
                                isViewer = isViewer,
                                isOwner = isOwner,
                                selectedTab = selectedTab,
                                onSelectedTabChange = { selectedTab = it },
                                appliedSortOption = appliedSortOption,
                                appliedFilterOptions = appliedFilterOptions,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent,
                                onProgress = { sheetMotionProgress = it }
                            )
                        }
                    }
                }
            }
        }

        if (showFilterDialog) {
            SortFilterBottomSheet(
                sortOptions = sortOptions,
                initialSortOption = appliedSortOption,
                filterByOptions = filterByOptions,
                initialFilterOptions = appliedFilterOptions,
                onDismiss = { showFilterDialog = false },
                onApply = { sortOption, filterSet ->
                    appliedSortOption = sortOption
                    appliedFilterOptions = filterSet
                    showFilterDialog = false
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showSaveListBottomSheet) {
            SaveListBottomSheet(
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
                onAddNewEvent = { subEventItem ->
                    activeEvent?.let { event ->
                        val newSubEvent = SubEvent(
                            id = subEventItem.id,
                            name = subEventItem.name,
                            date = subEventItem.date,
                            completed = subEventItem.isCompleted
                        )
                        val updatedEvent = event.copy(subEvents = event.subEvents + newSubEvent)
                        eventViewModel.updateEvent(updatedEvent)

                        activeTargetVenue?.let { venue ->
                            venueViewModel.toggleSaveVenue(venue.name, venue.id, isViewer, subEventItem.id)
                        }

                        selectedSaveEventId = subEventItem.id
                        isMySavedListChecked = false
                    }
                },
                isViewer = isViewer,
                onDismiss = { showSaveListBottomSheet = false },
                onDone = {
                    activeTargetVenue?.let { venue ->
                        val destination = if (isMySavedListChecked) "mysaved" else selectedSaveEventId
                        if (destination != null) {
                            venueViewModel.toggleSaveVenue(venue.name, venue.id, isViewer, destination)
                            lastSavedVenue = venue
                            toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
                        } else {
                            venueViewModel.toggleSaveVenue(venue.name, venue.id, isViewer, null)
                            toastData = ToastData("Removed from Saved List", ToastType.DEFAULT)
                        }
                    }
                    showSaveListBottomSheet = false
                    activeTargetVenue = null
                },
                onProgress = { sheetMotionProgress = it }
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
                                showMenuSheet = false
                                isLocationPickerVisible = true
                            }
                        )
                    ),
                    listOf(
                        MenuSheetActionItem(
                            text = if (isOwner) "Manage Room Access" else "Room Members",
                            icon = painterResource(R.drawable.ic_user_default),
                            iconPlacement = IconPlacement.Left,
                            onClick = {
                                showMenuSheet = false
                                showRoomAccess = true
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
                ),
                onCancelClick = { showMenuSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }
    }

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
                    buttonText = if (activeEvent?.multiDay == true) "Change" else null,
                    onButtonClick = if (activeEvent?.multiDay == true) {
                        {
                            toastData = null
                            lastSavedVenue?.let { venue ->
                                activeTargetVenue = venue
                                val currentDest = venueSavedDestinations[venue.name]
                                isMySavedListChecked = currentDest == "mysaved"
                                selectedSaveEventId = if (currentDest != "mysaved" && currentDest != null) currentDest else null
                                showSaveListBottomSheet = true
                            }
                        }
                    } else null
                )
            }
        }
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

    userToRemove?.let {
        ConfirmationBottomSheet(
            heading = "Remove ${it.name} from Venue Room?",
            subHeading = "They will not be able to access this room anymore.",
            confirmButtonText = "Remove",
            onDismiss = {
                userToRemove = null
            },
            onConfirm = {
                val target = userToRemove
                if (target != null && activeEvent != null) {
                    roomViewModel.removeAccess(activeEvent!!.id, "Venue", target.uid)
                    toastData = ToastData("${target.name} removed from Room!", ToastType.SUCCESS)
                }
                userToRemove = null
            },
            onProgress = { sheetMotionProgress = it }
        )
    }

    if (showLeaveConfirmation) {
        ConfirmationBottomSheet(
            heading = "Leaving Venue Room?",
            subHeading = "You will lose access to this room and won't be able to see updates.",
            confirmButtonText = "Leave",
            onDismiss = {
                showLeaveConfirmation = false
            },
            onConfirm = {
                activeEvent?.id?.let { eventId ->
                    roomViewModel.removeAccess(eventId, "Venue", currentUserUid)
                }
                toastData = ToastData("You left the room", ToastType.DEFAULT)
                showRoomAccess = false
                showLeaveConfirmation = false
            },
            onProgress = { sheetMotionProgress = it }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VenueMainContent(
    allVenues: List<Venue>,
    selectedLocation: String,
    onVenueClick: (Venue) -> Unit,
    onLocationSelectorClick: () -> Unit,
    onManageRoomAccessClick: () -> Unit,
    onBackClick: () -> Unit,
    isScreenActive: Boolean = true,
    toastData: ToastData?,
    onShowToast: (ToastData?) -> Unit,
    showFilterDialog: Boolean,
    onShowFilterDialogChange: (Boolean) -> Unit,
    showSaveListBottomSheet: Boolean,
    onShowSaveListBottomSheetChange: (Boolean) -> Unit,
    showMenuSheet: Boolean,
    onShowMenuSheetChange: (Boolean) -> Unit,
    timelineEvents: List<TimelineEvent>,
    onActiveTargetVenueChange: (Venue?) -> Unit,
    isMySavedListChecked: Boolean,
    onMySavedListCheckedChange: (Boolean) -> Unit,
    selectedSaveEventId: String?,
    onSelectedSaveEventIdChange: (String?) -> Unit,
    venueSavedDestinations: Map<String, String>,
    onToggleSaveVenue: (Venue, String?) -> Unit,
    lastSavedVenue: Venue?,
    onLastSavedVenueChange: (Venue?) -> Unit,
    isMultiDay: Boolean,
    isViewer: Boolean,
    isOwner: Boolean,
    selectedTab: String,
    onSelectedTabChange: (String) -> Unit,
    appliedSortOption: String,
    appliedFilterOptions: Set<String>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onProgress: (Float) -> Unit = {}
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

    val exploreVenues = remember(allVenues) {
        allVenues.ifEmpty { MockData.sampleVenues1 }
    }

    val recentVenuesList = remember<List<Venue>>(recentSearches, exploreVenues) {
        recentSearches.mapNotNull { name ->
            exploreVenues.find { it.name == name }
        }
    }

    val handleVenueClick: (Venue) -> Unit = { venue ->
        saveRecentSearch(context, venue.name)
        recentSearches = getRecentSearches(context)
        onVenueClick(venue)
    }

    val handleFavoriteToggle: (Venue) -> Unit = { venue ->
        val alreadySaved = venueSavedDestinations.containsKey(venue.name)
        if (alreadySaved) {
            if (isMultiDay) {
                onActiveTargetVenueChange(venue)
                val currentDestination = venueSavedDestinations[venue.name]
                onMySavedListCheckedChange(currentDestination == "mysaved")
                onSelectedSaveEventIdChange(if (currentDestination != "mysaved" && currentDestination != null) currentDestination else null)
                onShowSaveListBottomSheetChange(true)
            } else {
                onToggleSaveVenue(venue, null)
                onShowToast(ToastData("Removed from Saved List", ToastType.DEFAULT))
            }
        } else {
            onToggleSaveVenue(venue, "mysaved")
            onLastSavedVenueChange(venue)
            onShowToast(ToastData("Added to Saved List!", ToastType.DEFAULT))
        }
    }

    val savedVenuesList = remember<List<Venue>>(venueSavedDestinations) {
        exploreVenues.filter { venue ->
            venueSavedDestinations.containsKey(venue.name)
        }.map { venue ->
            venue.copy(favorite = true)
        }
    }

    val bottomTabs = remember(savedVenuesList.size) {
        listOf(
            TabItem("Explore", "explore", badgeCount = 24),
            TabItem("Saved", "saved", badgeCount = savedVenuesList.size)
        )
    }

    val filteredAndSortedExploreVenues = remember<List<Venue>>(exploreVenues, venueSavedDestinations, appliedSortOption, appliedFilterOptions) {
        var result = exploreVenues.map { venue ->
            venue.copy(favorite = venueSavedDestinations.containsKey(venue.name))
        }

        if (appliedFilterOptions.isNotEmpty()) {
            result = result.filter { venue ->
                appliedFilterOptions.any { filter ->
                    venue.type?.equals(filter, ignoreCase = true) == true
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
            venueSavedDestinations[venue.name] == "mysaved"
        }.map { venue ->
            venue.copy(favorite = true)
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
                venueSavedDestinations[venue.name] == event.id
            }.map { venue ->
                venue.copy(favorite = true)
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
                            items(
                                items = filteredAndSortedExploreVenues,
                                key = { it.name }
                            ) { venueItem ->
                                VenueCardFull(
                                    venue = venueItem,
                                    onFavoriteToggle = { handleFavoriteToggle(venueItem) },
                                    onCardClick = { handleVenueClick(venueItem) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp),
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope
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
                                        sharedTransitionScope = sharedTransitionScope,
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                }
                            }
                        } else {
                            if (savedVenuesList.isEmpty()) {
                                item {
                                    EmptySavedState()
                                }
                            } else {
                                items(
                                    items = savedVenuesList,
                                    key = { it.name }
                                ) { venueItem ->
                                    VenueCardFull(
                                        venue = venueItem,
                                        onFavoriteToggle = { handleFavoriteToggle(venueItem) },
                                        onCardClick = { handleVenueClick(venueItem) },
                                        sharedTransitionScope = sharedTransitionScope,
                                        animatedVisibilityScope = animatedVisibilityScope
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
    timelineEvents: List<TimelineEvent>,
    isMySavedListChecked: Boolean,
    onMySavedListToggled: (Boolean) -> Unit,
    selectedEventId: String?,
    onEventSelected: (String?) -> Unit,
    onAddNewEvent: (SubEventItem) -> Unit,
    isViewer: Boolean,
    onDismiss: () -> Unit,
    onDone: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    var draftNewEvent by remember { mutableStateOf<SubEventItem?>(null) }
    var showInfoSheet by remember { mutableStateOf(false) }

    if (showInfoSheet) {
        EventTimeLineInfoSheet(
            onDismiss = { showInfoSheet = false },
            onProgress = onProgress
        )
    }

    CustomBottomSheet(
        heading = "Manage Saved List",
        onDismiss = onDismiss,
        onProgress = onProgress,
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
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        showInfoSheet = true
                                    }
                            )
                        }

                        if (!isViewer) {
                            Row(
                                modifier = Modifier.clickable {
                                    draftNewEvent = SubEventItem(
                                        dateString = "",
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
                }

                if (draftNewEvent != null) {
                    item {
                        TimeLineInput(
                            item = draftNewEvent!!,
                            onUpdate = { updatedItem ->
                                if (!updatedItem.isEditing) {
                                    if (updatedItem.isExisting) {
                                        onAddNewEvent(updatedItem)
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
                        Spacer(Modifier.height(12.dp))
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TimelineSection(
    date: String,
    event: String,
    venues: List<Venue>,
    onVenueClick: (Venue) -> Unit,
    onFavoriteToggle: (Venue) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(20.dp, 0f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(20.dp, 0f)
            )
            .background(SurfaceSecondary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = date,
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentBrandDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }

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
                    VenueCardCompact(
                        venue = venue,
                        onCardClick = { onVenueClick(venue) },
                        onFavoriteToggle = { onFavoriteToggle(venue) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        compactCardSize = CompactCardSize.SMALL
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