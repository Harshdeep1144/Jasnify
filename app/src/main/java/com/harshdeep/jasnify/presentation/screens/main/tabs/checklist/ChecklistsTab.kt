package com.harshdeep.jasnify.presentation.screens.main.tabs.checklist

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.profile.NavBarStyleOption
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.ChecklistCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.buttons.AskAiButton
import com.harshdeep.jasnify.presentation.screens.others.AiChatScreen
import com.harshdeep.jasnify.presentation.components.states.ChecklistLoadingState
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.presentation.viewmodels.ChecklistViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.UIViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.TopBrandGradientBrush
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

sealed interface ChecklistScreenState {
    data object List : ChecklistScreenState
    data class Detail(val checklist: Checklist?, val isAddingNew: Boolean) : ChecklistScreenState
    data object Archives : ChecklistScreenState
    data object ManageRoomAccess : ChecklistScreenState
    data object HelpFeedback : ChecklistScreenState
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun ChecklistsTab(
    mainNavController: NavHostController,
    viewModel: ChecklistViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    onBottomBarVisibilityChange: (Boolean) -> Unit = {},
    onBackClick: () -> Unit = {},
    profileViewModel: com.harshdeep.jasnify.presentation.viewmodels.ProfileViewModel = hiltViewModel()
) {
    val mainGraphEntry = remember(mainNavController) {
        mainNavController.getBackStackEntry(Screen.MainAppGraph.route)
    }
    val uiViewModel: UIViewModel = hiltViewModel(mainGraphEntry)
    val navBarStyle by uiViewModel.navBarStyle.collectAsStateWithLifecycle()

    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()

    if (activeEvent == null) {
        ChecklistLoadingState(
            navBarStyle = navBarStyle
        )
        return
    }

    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val hasAccess by roomViewModel.hasAccess.collectAsStateWithLifecycle()

    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""

    val currentUserInRoom = roomUsers.find { it.uid == currentUserUid }
    val isOwner = activeEvent?.ownerId == currentUserUid
    val currentUserRole = when {
        isOwner -> UserRole.OWNER
        currentUserInRoom != null -> currentUserInRoom.role
        else -> UserRole.VIEWER
    }
    val isViewer = currentUserRole == UserRole.VIEWER

    LaunchedEffect(Unit) {
        roomViewModel.resetAccessState()
        eventViewModel.fetchUserEvents()
    }

    LaunchedEffect(activeEventId) {
        viewModel.setEventId(activeEventId)
        activeEventId?.let { id ->
            roomViewModel.verifyAccess(id, "Checklist", currentUserUid)
            roomViewModel.loadRoomUsers(id, "Checklist")
        }
    }

    val checklists by viewModel.checklists.collectAsStateWithLifecycle()
    val archivedChecklists by viewModel.archivedChecklists.collectAsStateWithLifecycle()
    val recentColorsHex by viewModel.recentColors.collectAsStateWithLifecycle()
    val recentColors = remember(recentColorsHex) { recentColorsHex.map { Color(it.toLong(16)) } }

    var isGridView by remember { mutableStateOf(true) }
    var showAiChat by remember { mutableStateOf(false) }
    var aiChatContext by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showMenuSheet by remember { mutableStateOf(false) }
    var selectedChecklist by remember { mutableStateOf<Checklist?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var showArchives by remember { mutableStateOf(false) }
    var showRoomAccess by remember { mutableStateOf(false) }
    var showHelpFeedback by remember { mutableStateOf(false) }
    var navigatedFromArchives by remember { mutableStateOf(false) }
    var showDiscardToast by remember { mutableStateOf(false) }

    var toastData by remember { mutableStateOf(ToastData()) }
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }

    var showDetailColorPicker by remember { mutableStateOf(false) }
    var showDetailMenu by remember { mutableStateOf(false) }
    var showDetailDeleteConfirmation by remember { mutableStateOf(false) }
    var detailColorBeforePicker by remember { mutableStateOf(Color.Transparent) }
    var currentDetailBgColor by remember { mutableStateOf(Color.Transparent) }

    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            showMenuSheet ||
                    showRoomMenuBottomSheet ||
                    (userToRemove != null) ||
                    showLeaveConfirmation ||
                    showDetailColorPicker ||
                    showDetailMenu ||
                    showDetailDeleteConfirmation
        }
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

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }
    var wasFocused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val currentScreen = remember(selectedChecklist, isAddingNew, showArchives, showRoomAccess, showHelpFeedback) {
        when {
            selectedChecklist != null || isAddingNew -> {
                ChecklistScreenState.Detail(selectedChecklist, isAddingNew)
            }
            showArchives -> {
                ChecklistScreenState.Archives
            }
            showRoomAccess -> {
                ChecklistScreenState.ManageRoomAccess
            }
            showHelpFeedback -> {
                ChecklistScreenState.HelpFeedback
            }
            else -> {
                ChecklistScreenState.List
            }
        }
    }

    LaunchedEffect(currentScreen, hasAccess, isAnyBottomSheetOpen, showAiChat) {
        onBottomBarVisibilityChange(hasAccess == true && currentScreen is ChecklistScreenState.List && !isAnyBottomSheetOpen && !showAiChat)
    }

    LaunchedEffect(showDiscardToast) {
        if (showDiscardToast) {
            delay(2000.milliseconds)
            showDiscardToast = false
        }
    }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            wasFocused = false
            delay(100.milliseconds)
            searchFocusRequester.requestFocus()
        }
    }

    BackHandler(enabled = showArchives || isSearchActive || showRoomAccess || showAiChat || showHelpFeedback) {
        focusManager.clearFocus()
        if (showAiChat) {
            showAiChat = false
        } else if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
            wasFocused = false
        } else if (showHelpFeedback) {
            showHelpFeedback = false
        } else if (showArchives) {
            showArchives = false
        } else if (showRoomAccess) {
            showRoomAccess = false
        }
    }

    RoomAccessGuardian(
        hasAccess = hasAccess,
        roomName = "Checklist",
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
                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300)))
                                .using(SizeTransform(clip = false))
                        },
                        label = "screen_navigation_transition"
                    ) { targetScreenState ->
                        when (targetScreenState) {
                            is ChecklistScreenState.Detail -> {
                                ChecklistDetailScreen(
                                    checklist = targetScreenState.checklist,
                                    isAddingNew = targetScreenState.isAddingNew,
                                    isViewer = isViewer,
                                    onBackClick = { updatedChecklist ->
                                        focusManager.clearFocus()
                                        showDetailColorPicker = false
                                        showDetailMenu = false
                                        showDetailDeleteConfirmation = false
                                        if (updatedChecklist != null) {
                                            val isEmpty =
                                                updatedChecklist.title.isBlank() && updatedChecklist.items.isEmpty()

                                            if (isEmpty && targetScreenState.isAddingNew) {
                                                showDiscardToast = true
                                            } else if (updatedChecklist.title.isNotBlank() || updatedChecklist.items.isNotEmpty()) {
                                                if (isViewer) {
                                                    viewModel.saveChecklistLocally(updatedChecklist)
                                                } else {
                                                    viewModel.saveChecklist(updatedChecklist)
                                                }
                                            }
                                        }
                                        selectedChecklist = null
                                        isAddingNew = false
                                        if (navigatedFromArchives) {
                                            showArchives = true
                                            navigatedFromArchives = false
                                        }
                                    },
                                    onDelete = { id ->
                                        focusManager.clearFocus()
                                        showDetailColorPicker = false
                                        showDetailMenu = false
                                        showDetailDeleteConfirmation = false
                                        viewModel.deleteChecklist(id)
                                        selectedChecklist = null
                                        isAddingNew = false
                                        if (navigatedFromArchives) {
                                            showArchives = true
                                            navigatedFromArchives = false
                                        }
                                    },
                                    onTogglePin = { checklist ->
                                        viewModel.togglePin(checklist, isViewer)
                                    },
                                    onArchive = { checklist ->
                                        focusManager.clearFocus()
                                        showDetailColorPicker = false
                                        showDetailMenu = false
                                        showDetailDeleteConfirmation = false
                                        viewModel.toggleArchive(checklist, isViewer)
                                        selectedChecklist = null
                                        isAddingNew = false
                                        if (navigatedFromArchives) {
                                            showArchives = true
                                            navigatedFromArchives = false
                                        }
                                    },
                                    isArchived = archivedChecklists.any { it.id == targetScreenState.checklist?.id },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this@AnimatedContent,
                                    onOpenColorPicker = { initialColor ->
                                        detailColorBeforePicker = initialColor
                                        currentDetailBgColor = initialColor
                                        showDetailColorPicker = true
                                    },
                                    onOpenMenu = {
                                        showDetailMenu = true
                                    },
                                    currentBgColor = currentDetailBgColor
                                )
                            }

                            ChecklistScreenState.Archives -> {
                                ChecklistArchivesScreen(
                                    archivedChecklists = archivedChecklists,
                                    onBackClick = {
                                        focusManager.clearFocus()
                                        showArchives = false
                                    },
                                    onChecklistClick = { checklist ->
                                        focusManager.clearFocus()
                                        selectedChecklist = checklist
                                        showArchives = false
                                        navigatedFromArchives = true
                                    }
                                )
                            }

                            ChecklistScreenState.ManageRoomAccess -> {
                                activeEvent?.id?.let { id ->
                                    ChecklistRoomContent(
                                        eventId = id,
                                        roomViewModel = roomViewModel,
                                        onBackClick = {
                                            focusManager.clearFocus()
                                            showRoomAccess = false
                                        },
                                        onMenuClick = {
                                            focusManager.clearFocus()
                                            showRoomMenuBottomSheet = true
                                        },
                                        onRemove = { targetUser ->
                                            focusManager.clearFocus()
                                            userToRemove = targetUser
                                        },
                                        onLeave = {
                                            showLeaveConfirmation = true
                                        },
                                        onShowToast = { toastData = it }
                                    )
                                }
                            }

                            ChecklistScreenState.List -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(BackgroundPrimary)
                                ) {
                                    Scaffold(
                                        topBar = {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .statusBarsPadding()
                                            ) {
                                                AnimatedContent(
                                                    targetState = isSearchActive,
                                                    transitionSpec = {
                                                        if (targetState) {
                                                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                                                slideOutHorizontally { width -> -width } + fadeOut()
                                                            )
                                                        } else {
                                                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                                                slideOutHorizontally { width -> width } + fadeOut()
                                                            )
                                                        }
                                                    },
                                                    label = "SearchBarTransition"
                                                ) { active ->
                                                    if (active) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(
                                                                    start = 12.dp,
                                                                    end = 12.dp,
                                                                    top = 12.dp
                                                                ),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            CustomSearchBar(
                                                                value = searchQuery,
                                                                onValueChange = { searchQuery = it },
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .focusRequester(searchFocusRequester)
                                                                    .onFocusChanged { focusState ->
                                                                        if (focusState.isFocused) {
                                                                            wasFocused = true
                                                                        } else if (wasFocused) {
                                                                            isSearchActive = false
                                                                            searchQuery = ""
                                                                            wasFocused = false
                                                                        }
                                                                    },
                                                                onActiveChange = {}
                                                            )
                                                        }
                                                    } else {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .noRippleClickable {
                                                                    focusManager.clearFocus()
                                                                }
                                                        ) {
                                                            CustomTopBar(
                                                                title = "Checklist",
                                                                titleIcon = painterResource(R.drawable.ill_checklists),
                                                                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                                                                isLeftAligned = true,
                                                                isLargeTitle = true,
                                                                secondaryIcon = TopIcon.Predefined.SEARCH,
                                                                onSecondaryClick = {
                                                                    isSearchActive = true
                                                                },
                                                                onMenuClick = {
                                                                    focusManager.clearFocus()
                                                                    showMenuSheet = true
                                                                },
                                                                buttonStyle = ButtonBackground.OPAQUE
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        floatingActionButton = {
                                            if (!isViewer) {
                                                val fabOffset = if (navBarStyle == NavBarStyleOption.PILL_SHAPED) (-104).dp else (-12).dp

                                                CustomIconButton(
                                                    onClick = {
                                                        focusManager.clearFocus()
                                                        isAddingNew = true
                                                    },
                                                    icon = painterResource(R.drawable.ic_plus),
                                                    size = ButtonSize.Large,
                                                    modifier = Modifier
                                                        .offset(x = (-24).dp, y = fabOffset)
                                                        .shadow(16.dp, CircleShape)
                                                )
                                            }
                                        },
                                        containerColor = Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .noRippleClickable {
                                                focusManager.clearFocus()
                                            }
                                    ) { paddingValues ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = paddingValues.calculateTopPadding())
                                                .noRippleClickable {
                                                    focusManager.clearFocus()
                                                }
                                        ) {
                                            Spacer(Modifier.height(16.dp))

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                FilterChip(
                                                    label = "All",
                                                    isSelected = selectedFilter == "All",
                                                    onClick = {
                                                        focusManager.clearFocus()
                                                        selectedFilter = "All"
                                                    },
                                                    hasStroke = true,
                                                    shapeStyle = ChipShapeStyle.Round
                                                )
                                                FilterChip(
                                                    label = "Recent First",
                                                    isSelected = selectedFilter == "Recent First",
                                                    onClick = {
                                                        focusManager.clearFocus()
                                                        selectedFilter = "Recent First"
                                                    },
                                                    hasStroke = true,
                                                    shapeStyle = ChipShapeStyle.Round
                                                )
                                                FilterChip(
                                                    label = "Oldest First",
                                                    isSelected = selectedFilter == "Oldest First",
                                                    onClick = {
                                                        focusManager.clearFocus()
                                                        selectedFilter = "Oldest First"
                                                    },
                                                    hasStroke = true,
                                                    shapeStyle = ChipShapeStyle.Round
                                                )
                                            }

                                            val filteredAndSortedChecklists = remember(searchQuery, checklists, selectedFilter) {
                                                val query = searchQuery.trim()
                                                val searched = if (query.isEmpty()) {
                                                    checklists
                                                } else {
                                                    checklists.filter {
                                                        it.title.contains(query, ignoreCase = true) ||
                                                                it.items.any { item -> item.text.contains(query, ignoreCase = true) }
                                                    }
                                                }

                                                when (selectedFilter) {
                                                    "Recent First" -> searched.sortedWith(
                                                        compareByDescending<Checklist> { it.pinned }.thenByDescending { it.lastUpdated }
                                                    )
                                                    "Oldest First" -> searched.sortedWith(
                                                        compareByDescending<Checklist> { it.pinned }.thenBy { it.lastUpdated }
                                                    )
                                                    else -> searched.sortedWith(
                                                        compareByDescending<Checklist> { it.pinned }.thenByDescending { it.createdAt }
                                                    )
                                                }
                                            }

                                            val boundsTransformSpec = remember {
                                                BoundsTransform { _, _ ->
                                                    tween(
                                                        durationMillis = 500,
                                                        easing = FastOutSlowInEasing
                                                    )
                                                }
                                            }

                                            if (filteredAndSortedChecklists.isEmpty()) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(320.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Top
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.ic_receipt),
                                                            contentDescription = null,
                                                            tint = ContentTertiary,
                                                            modifier = Modifier.size(84.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                            text = if (checklists.isEmpty()) "Your checklists will \n appear here" else "No checklists Found",
                                                            style = JasnifyTheme.typography.displayMedium.copy(
                                                                fontWeight = FontWeight.Medium
                                                            ),
                                                            color = ContentTertiary,
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                }
                                            } else {
                                                if (isGridView) {
                                                    LazyVerticalGrid(
                                                        columns = GridCells.Fixed(2),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                                        contentPadding = PaddingValues(bottom = 12.dp),
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(
                                                                start = 12.dp,
                                                                end = 12.dp,
                                                                top = 12.dp
                                                            )
                                                    ) {
                                                        items(
                                                            items = filteredAndSortedChecklists,
                                                            key = { it.id },
                                                            contentType = { "checklist_card" }
                                                        ) { checklist ->
                                                            Box(
                                                                modifier = Modifier.sharedBounds(
                                                                    sharedContentState = rememberSharedContentState(
                                                                        key = "bounds-${checklist.id}"
                                                                    ),
                                                                    animatedVisibilityScope = this@AnimatedContent,
                                                                    boundsTransform = boundsTransformSpec
                                                                )
                                                            ) {
                                                                ChecklistCard(
                                                                    checklist = checklist,
                                                                    onClick = {
                                                                        focusManager.clearFocus()
                                                                        selectedChecklist = checklist
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    LazyColumn(
                                                        contentPadding = PaddingValues(bottom = 12.dp),
                                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(
                                                                start = 12.dp,
                                                                end = 12.dp,
                                                                top = 12.dp
                                                            )
                                                    ) {
                                                        items(
                                                            items = filteredAndSortedChecklists,
                                                            key = { it.id },
                                                            contentType = { "checklist_card" }
                                                        ) { checklist ->
                                                            Box(
                                                                modifier = Modifier.sharedBounds(
                                                                    sharedContentState = rememberSharedContentState(
                                                                        key = "bounds-${checklist.id}"
                                                                    ),
                                                                    animatedVisibilityScope = this@AnimatedContent,
                                                                    boundsTransform = boundsTransformSpec
                                                                )
                                                            ) {
                                                                ChecklistCard(
                                                                    checklist = checklist,
                                                                    onClick = {
                                                                        focusManager.clearFocus()
                                                                        selectedChecklist = checklist
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!isAnyBottomSheetOpen && !showAiChat && checklists.isNotEmpty()) {
                                        AskAiButton(
                                            onClick = {
                                                focusManager.clearFocus()
                                                aiChatContext = """
                                            Checklist for ${activeEvent?.name ?: "Event"}:
                                            Total Checklists: ${checklists.size}
                                            
                                            Active Checklists:
                                            ${checklists.joinToString("\n") { "- ${it.title}: ${it.items.count { it.checked }}/${it.items.size} items done" }}
                                        """.trimIndent()
                                                showAiChat = true
                                            },
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(bottom = 250.dp)
                                                .zIndex(150f)
                                        )
                                    }
                                }

                                AnimatedVisibility(
                                    visible = showAiChat,
                                    enter = slideInVertically(initialOffsetY = { it }),
                                    exit = slideOutVertically(targetOffsetY = { it }),
                                    modifier = Modifier.zIndex(200f)
                                ) {
                                    AiChatScreen(
                                        eventId = activeEventId,
                                        initialContext = aiChatContext,
                                        shouldStartNewSession = true,
                                        onBackClick = {
                                            showAiChat = false
                                            focusManager.clearFocus()
                                        }
                                    )
                                }
                            }

                            ChecklistScreenState.HelpFeedback -> {
                                com.harshdeep.jasnify.presentation.screens.main.tabs.profile.HelpFeedbackScreen(
                                    profileViewModel = profileViewModel,
                                    onBack = { showHelpFeedback = false },
                                    onShowAiChat = { showAiChat = true }
                                )
                            }
                        }
                    }
                }
            }

            if (showMenuSheet) {
                MenuBottomSheet(
                    items = listOf(
                        listOf(
                            MenuSheetActionItem(
                                text = if (isGridView) "List View" else "Grid View",
                                icon = if (isGridView) painterResource(R.drawable.ic_list) else painterResource(
                                    R.drawable.ic_grid
                                ),
                                iconPlacement = IconPlacement.Top,
                                onClick = {
                                    isGridView = !isGridView
                                    showMenuSheet = false
                                }
                            ),
                            MenuSheetActionItem(
                                text = "View Archives",
                                icon = painterResource(R.drawable.ic_box),
                                iconPlacement = IconPlacement.Top,
                                onClick = {
                                    showArchives = true
                                    showMenuSheet = false
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
                                    showHelpFeedback = true
                                }
                            )
                        )
                    ),
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
                        ),
                        listOf(
                            MenuSheetActionItem(
                                text = "Help & Feedback",
                                icon = painterResource(R.drawable.ic_help_feedback),
                                iconPlacement = IconPlacement.Left,
                                onClick = {
                                    showRoomMenuBottomSheet = false
                                    showHelpFeedback = true
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

            userToRemove?.let { targetUser ->
                ConfirmationBottomSheet(
                    heading = "Remove ${targetUser.name} from Checklist Room?",
                    subHeading = "They will not be able to access this room anymore.",
                    confirmButtonText = "Remove",
                    onDismiss = {
                        userToRemove = null
                    },
                    onConfirm = {
                        val eventId = activeEvent?.id
                        if (eventId != null) {
                            roomViewModel.removeAccess(
                                eventId,
                                "Checklist",
                                targetUser.uid
                            )
                            toastData = ToastData(
                                "${targetUser.name} removed from Room!",
                                ToastType.SUCCESS
                            )
                        }
                        userToRemove = null
                    },
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showLeaveConfirmation) {
                ConfirmationBottomSheet(
                    heading = "Leaving Checklist Room?",
                    subHeading = "You will lose access to this room and won't be able to see updates.",
                    confirmButtonText = "Leave",
                    onDismiss = {
                        showLeaveConfirmation = false
                    },
                    onConfirm = {
                        activeEvent?.id?.let { id ->
                            roomViewModel.removeAccess(id, "Checklist", currentUserUid)
                        }
                        toastData = ToastData("You left the room", ToastType.DEFAULT)
                        showRoomAccess = false
                        showLeaveConfirmation = false
                    },
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showDetailColorPicker) {
                ColorPickerBottomSheet(
                    initialColor = detailColorBeforePicker,
                    recentColors = recentColors,
                    onColorPreview = { previewColor ->
                        currentDetailBgColor = previewColor
                    },
                    onConfirm = { finalColor ->
                        currentDetailBgColor = finalColor
                        detailColorBeforePicker = finalColor
                        viewModel.addRecentColor(String.format("%08X", finalColor.toArgb()))
                        showDetailColorPicker = false
                    },
                    onDismiss = {
                        currentDetailBgColor = detailColorBeforePicker
                        showDetailColorPicker = false
                    },
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showDetailMenu) {
                val isDetailArchived = archivedChecklists.any { it.id == selectedChecklist?.id }
                MenuBottomSheet(
                    items = listOfNotNull(
                        listOf(
                            MenuSheetActionItem(
                                text = if (isDetailArchived) "Unarchive" else "Archive",
                                icon = painterResource(R.drawable.ic_box),
                                iconPlacement = IconPlacement.Left,
                                onClick = {
                                    showDetailMenu = false
                                    selectedChecklist?.let { checklist ->
                                        viewModel.toggleArchive(checklist, isViewer)
                                    }
                                }
                            )
                        ),
                        if (!isViewer) {
                            listOf(
                                MenuSheetActionItem(
                                    text = "Delete",
                                    icon = painterResource(R.drawable.ic_delete),
                                    iconPlacement = IconPlacement.Left,
                                    contentColor = MaterialTheme.colorScheme.error,
                                    onClick = {
                                        showDetailMenu = false
                                        showDetailDeleteConfirmation = true
                                    }
                                )
                            )
                        } else null
                    ),
                    onCancelClick = { showDetailMenu = false },
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showDetailDeleteConfirmation) {
                ConfirmationBottomSheet(
                    heading = "Are you sure?",
                    subHeading = "The checklist will be deleted permanently.",
                    confirmButtonText = "Delete Checklist",
                    onDismiss = { showDetailDeleteConfirmation = false },
                    onConfirm = {
                        showDetailDeleteConfirmation = false
                        selectedChecklist?.id?.let { id ->
                            viewModel.deleteChecklist(id)
                            selectedChecklist = null
                            isAddingNew = false
                        }
                    },
                    onProgress = { sheetMotionProgress = it }
                )
            }

            AnimatedVisibility(
                visible = (showDiscardToast || toastData.message != null) && !isAnyBottomSheetOpen,
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
                    message = toastData.message ?: "Empty List Discarded!",
                    type = toastData.type
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistsTabPreview() {
    JasnifyTheme {
        val testNavController = rememberNavController()
        ChecklistsTab(
            mainNavController = testNavController
        )
    }
}