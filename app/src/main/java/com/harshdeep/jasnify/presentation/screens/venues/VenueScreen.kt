package com.harshdeep.jasnify.presentation.screens.venues

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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.edit
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SaveListBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.filter.SortFilterBottomSheet
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.sections.SavedTimelineItemsScreen
import com.harshdeep.jasnify.presentation.navigation.ScreenTransitions
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

enum class VenueScreenState {
    MAIN,
    LOCATION_PICKER,
    ROOM_ACCESS,
    VENUE_DETAIL,
    TIMELINE_DETAIL
}

const val PREFS_NAME = "venue_prefs"
const val KEY_RECENT_SEARCHES = "recent_searches"

fun getRecentSearches(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val saved = prefs.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
    return saved.split("|||").filter { it.isNotBlank() }
}

fun saveRecentSearch(context: Context, query: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val current = getRecentSearches(context).toMutableList()
    current.remove(query)
    current.add(0, query)
    val limited = current.take(10)
    prefs.edit {
        putString(KEY_RECENT_SEARCHES, limited.joinToString("|||"))
    }
}

fun clearRecentSearches(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit { remove(KEY_RECENT_SEARCHES) }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
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
    var screenStack by remember { mutableStateOf(listOf(VenueScreenState.MAIN)) }
    val screenState = screenStack.last()
    var selectedTab by remember { mutableStateOf(initialTab) }
    var selectedViewType by remember { mutableStateOf("By Timeline") }
    var selectedVenueForDetail by remember { mutableStateOf<Venue?>(null) }
    var selectedTimelineEventId by remember { mutableStateOf<String?>(null) }

    val mainListState = rememberLazyListState()

    val auth = FirebaseAuth.getInstance()
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()
    val savedVenuesFromCloud by venueViewModel.savedVenues.collectAsStateWithLifecycle()
    val allVenues by venueViewModel.allVenues.collectAsStateWithLifecycle()
    val venueReviews by venueViewModel.venueReviews.collectAsStateWithLifecycle()
    val isLoading by venueViewModel.isLoading.collectAsStateWithLifecycle()

    val venueSavedDestinations = remember(savedVenuesFromCloud) {
        savedVenuesFromCloud.associate { it.venueName to it.destination }
    }

    val currentUserUid = auth.currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        roomViewModel.resetAccessState()
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEvent) {
        if (activeEvent != null) {
            roomViewModel.verifyAccess(activeEvent!!.id, "Venue", currentUserUid)
            roomViewModel.loadRoomUsers(activeEvent!!.id, "Venue")
            venueViewModel.setEventId(activeEvent!!.id)
        } else {
            roomViewModel.setAccessState(true)
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

    val exploreVenues = remember(allVenues) {
        allVenues.ifEmpty { MockData.sampleVenues1 + MockData.sampleVenues2 }
    }

    val savedTimelineEvents = remember(venueSavedDestinations, timelineEvents, exploreVenues) {
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

    val currentSelectedTimelineEvent = remember(selectedTimelineEventId, timelineEvents, venueSavedDestinations, exploreVenues) {
        val baseEvent = if (selectedTimelineEventId == "mysaved") {
            TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List")
        } else {
            timelineEvents.find { it.id == selectedTimelineEventId }
        }

        baseEvent?.let { event ->
            val eventVenues = exploreVenues.filter { venue ->
                venueSavedDestinations[venue.name] == event.id
            }.map { venue ->
                venue.copy(favorite = true)
            }
            event.copy(venues = eventVenues)
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
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    BackHandler(enabled = screenStack.size > 1) {
        when (screenState) {
            VenueScreenState.VENUE_DETAIL -> {
                selectedVenueForDetail = null
                venueViewModel.setSelectedVenueId(null)
            }
            VenueScreenState.TIMELINE_DETAIL -> {
                selectedTimelineEventId = null
            }
            else -> {}
        }
        screenStack = screenStack.dropLast(1)
    }

    val isAnySheetVisible = showRoomMenuBottomSheet || (userToRemove != null) ||
            showFilterDialog || showSaveListBottomSheet || showMenuSheet || showLeaveConfirmation

    val targetScale = if (isAnySheetVisible) 0.92f + (0.08f * sheetMotionProgress) else 1.0f

    val backdropScaleState = animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadiusState = animateDpAsState(
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

    val handleFavoriteToggle: (Venue) -> Unit = { venue ->
        val alreadySaved = venueSavedDestinations.containsKey(venue.name)
        if (alreadySaved) {
            if (activeEvent?.multiDay == true) {
                activeTargetVenue = venue
                val currentDestination = venueSavedDestinations[venue.name]
                isMySavedListChecked = currentDestination == "mysaved"
                selectedSaveEventId = if (currentDestination != "mysaved" && currentDestination != null) currentDestination else null
                showSaveListBottomSheet = true
            } else {
                venueViewModel.toggleSaveVenue(venue.name, venue.id, isViewer, null)
                toastData = ToastData("Removed from Saved List", ToastType.DEFAULT)
            }
        } else {
            venueViewModel.toggleSaveVenue(venue.name, venue.id, isViewer, "mysaved")
            lastSavedVenue = venue
            toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = backdropScaleState.value
                    scaleY = backdropScaleState.value
                    val radius = backdropCornerRadiusState.value
                    clip = isAnySheetVisible || radius > 0.dp
                    shape = RoundedCornerShape(radius.coerceAtLeast(0.dp))
                }
                .background(BackgroundPrimary)
        ) {
            RoomAccessGuardian(
                hasAccess = hasAccess,
                roomName = "Venue",
                onBackClick = onBackClick
            ) {
                AnimatedContent(
                    targetState = screenState,
                    transitionSpec = {
                        when {
                           // Venue Detail Screen (Fast bottom-to-top & top-to-bottom)
                            targetState == VenueScreenState.VENUE_DETAIL -> ScreenTransitions.SlideBottomToTopFastTransition
                            initialState == VenueScreenState.VENUE_DETAIL -> ScreenTransitions.SlideTopToBottomFastTransition

                            else -> ScreenTransitions.FadeInOutDefaultTransition
                        }
                    },
                    label = "venue_screen_transition",
                    modifier = Modifier.fillMaxSize()
                ) { targetState ->
                    when (targetState) {
                        VenueScreenState.LOCATION_PICKER -> {
                            LocationScreen(
                                initialSearches = emptyList(),
                                currentAddress = currentAddress,
                                onAddressSelected = {
                                    currentAddress = it
                                    screenStack = screenStack.dropLast(1)
                                },
                                onBackClick = { screenStack = screenStack.dropLast(1) },
                            )
                        }
                        VenueScreenState.ROOM_ACCESS -> {
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
                                    onBackClick = { screenStack = screenStack.dropLast(1) },
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
                        VenueScreenState.VENUE_DETAIL -> {
                            selectedVenueForDetail?.let { venue ->
                                val detailData = remember(venue, allVenues, venueReviews, venueSavedDestinations) {
                                    val base = allVenues.find { it.id == venue.id } ?:
                                    MockData.venueDetailsMap[venue.name] ?:
                                    MockData.getDetailsForVenue(venue)

                                    val reactiveBase = base.copy(favorite = venueSavedDestinations.containsKey(base.name))

                                    if (venueReviews.isNotEmpty()) {
                                        reactiveBase.copy(
                                            reviewsData = reactiveBase.reviewsData?.copy(reviews = venueReviews)
                                                ?: VenueReviewsData(reviews = venueReviews)
                                        )
                                    } else {
                                        reactiveBase
                                    }
                                }
                                VenueDetailScreen(
                                    venueDetail = detailData,
                                    onBackClick = {
                                        selectedVenueForDetail = null
                                        venueViewModel.setSelectedVenueId(null)
                                        screenStack = screenStack.dropLast(1)
                                    },
                                    onFavoriteToggle = {
                                        handleFavoriteToggle(detailData)
                                    },
                                    onChatClick = { venueChat ->
                                        onChatClick(venueChat)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        VenueScreenState.MAIN -> {
                            VenueMainContent(
                                exploreVenues = exploreVenues,
                                savedTimelineEvents = savedTimelineEvents,
                                selectedLocation = currentAddress,
                                onVenueClick = { venue ->
                                    selectedVenueForDetail = venue
                                    venueViewModel.setSelectedVenueId(venue.id)
                                    screenStack = screenStack + VenueScreenState.VENUE_DETAIL
                                    onVenueClick(venue)
                                },
                                onLocationSelectorClick = {
                                    screenStack = screenStack + VenueScreenState.LOCATION_PICKER
                                },
                                onBackClick = onBackClick,
                                onShowMenuSheetChange = { showMenuSheet = it },
                                venueSavedDestinations = venueSavedDestinations,
                                onFavoriteToggle = handleFavoriteToggle,
                                selectedTab = selectedTab,
                                onSelectedTabChange = { selectedTab = it },
                                selectedViewType = selectedViewType,
                                onSelectedViewTypeChange = { selectedViewType = it },
                                appliedSortOption = appliedSortOption,
                                appliedFilterOptions = appliedFilterOptions,
                                onShowFilterDialogChange = { showFilterDialog = it },
                                isLoading = isLoading,
                                onTimelineSeeAll = { event ->
                                    selectedTimelineEventId = event.id
                                    screenStack = screenStack + VenueScreenState.TIMELINE_DETAIL
                                },
                                listState = mainListState
                            )
                        }
                        VenueScreenState.TIMELINE_DETAIL -> {
                            currentSelectedTimelineEvent?.let { event ->
                                SavedTimelineItemsScreen(
                                    title = "Saved Venues",
                                    date = event.date,
                                    event = event.event,
                                    venues = event.venues,
                                    onVenueClick = { venue ->
                                        selectedVenueForDetail = venue
                                        venueViewModel.setSelectedVenueId(venue.id)
                                        screenStack = screenStack + VenueScreenState.VENUE_DETAIL
                                    },
                                    onVenueFavoriteToggle = handleFavoriteToggle,
                                    onBackClick = { screenStack = screenStack.dropLast(1) }
                                )
                            }
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
                                screenStack = screenStack + VenueScreenState.LOCATION_PICKER
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
                                screenStack = screenStack + VenueScreenState.ROOM_ACCESS
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

    userToRemove?.let { user ->
        ConfirmationBottomSheet(
            heading = "Remove ${user.name} from Venue Room?",
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
                screenStack = listOf(VenueScreenState.MAIN)
                showLeaveConfirmation = false
            },
            onProgress = { sheetMotionProgress = it }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueMainContent(
    exploreVenues: List<Venue>,
    savedTimelineEvents: List<TimelineEvent>,
    selectedLocation: String,
    onVenueClick: (Venue) -> Unit,
    onLocationSelectorClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowMenuSheetChange: (Boolean) -> Unit,
    venueSavedDestinations: Map<String, String>,
    onFavoriteToggle: (Venue) -> Unit,
    selectedTab: String,
    onSelectedTabChange: (String) -> Unit,
    selectedViewType: String,
    onSelectedViewTypeChange: (String) -> Unit,
    appliedSortOption: String,
    appliedFilterOptions: Set<String>,
    onShowFilterDialogChange: (Boolean) -> Unit,
    isLoading: Boolean = false,
    onTimelineSeeAll: (TimelineEvent) -> Unit = {},
    listState: LazyListState = rememberLazyListState()
) {
    val focusManager = LocalFocusManager.current

    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val savedVenuesList = remember(venueSavedDestinations, exploreVenues) {
        exploreVenues.filter { venue ->
            venueSavedDestinations.containsKey(venue.name)
        }.map { venue ->
            venue.copy(favorite = true)
        }
    }

    val filteredAndSortedExploreVenues = remember(exploreVenues, venueSavedDestinations, appliedSortOption, appliedFilterOptions, text) {
        var result = exploreVenues.map { venue ->
            venue.copy(favorite = venueSavedDestinations.containsKey(venue.name))
        }

        if (text.isNotEmpty()) {
            result = result.filter {
                it.name.contains(text, ignoreCase = true) ||
                        it.location.contains(text, ignoreCase = true) ||
                        it.locality.contains(text, ignoreCase = true) ||
                        it.city.contains(text, ignoreCase = true) ||
                        it.type?.contains(text, ignoreCase = true) == true
            }
        }

        if (appliedFilterOptions.isNotEmpty()) {
            result = result.filter { venue ->
                appliedFilterOptions.any { filter ->
                    venue.type?.equals(filter, ignoreCase = true) == true
                }
            }
        }

        result = when (appliedSortOption) {
            "Highest to Lowest Amount" -> result.sortedByDescending { parsePrice(it.priceStartsFrom) }
            "Lowest to Highest Amount" -> result.sortedBy { parsePrice(it.priceStartsFrom) }
            "Oldest First" -> result.sortedBy { it.timestamp }
            "Newest First (Default)" -> result.sortedByDescending { it.timestamp }
            else -> result
        }

        result
    }

    val bottomTabs = remember(filteredAndSortedExploreVenues.size, savedVenuesList.size) {
        listOf(
            TabItem("Explore", "explore", badgeCount = filteredAndSortedExploreVenues.size),
            TabItem("Saved", "saved", badgeCount = savedVenuesList.size)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.statusBarsPadding()) {
                    AnimatedContent(
                        targetState = isSearchActive,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                        },
                        label = "VenueTopBarSearchTransition"
                    ) { active ->
                        CustomTopBar(
                            title = if (active) "Search Venues" else "Venue",
                            onBackClick = {
                                if (active) {
                                    isSearchActive = false
                                    text = ""
                                    focusManager.clearFocus()
                                } else {
                                    onBackClick()
                                }
                            },
                            onMenuClick = if (active) null else { { onShowMenuSheetChange(true) } },
                            backIcon = if (active) TopIcon.Predefined.DOWN else TopIcon.Predefined.BACK,
                            buttonStyle = ButtonBackground.OPAQUE,
                            isLargeTitle = true,
                        )
                    }
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
                    VenueExploreContent(
                        exploreVenues = exploreVenues,
                        filteredAndSortedExploreVenues = filteredAndSortedExploreVenues,
                        selectedLocation = selectedLocation,
                        onVenueClick = onVenueClick,
                        onLocationSelectorClick = onLocationSelectorClick,
                        onFavoriteToggle = onFavoriteToggle,
                        onShowFilterDialogChange = onShowFilterDialogChange,
                        isLoading = isLoading,
                        listState = listState,
                        text = text,
                        onTextChange = { text = it },
                        isSearchActive = isSearchActive,
                        onSearchActiveChange = { isSearchActive = it }
                    )
                } else {
                    VenueSavedContent(
                        savedTimelineEvents = savedTimelineEvents,
                        savedVenuesList = savedVenuesList,
                        selectedViewType = selectedViewType,
                        onSelectedViewTypeChange = onSelectedViewTypeChange,
                        onVenueClick = onVenueClick,
                        onFavoriteToggle = onFavoriteToggle,
                        onTimelineSeeAll = onTimelineSeeAll,
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}

@Composable
fun LocationSelectorPill(
    location: String,
    onLocationSelectorClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(16.dp))
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
