package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import com.harshdeep.jasnify.domain.model.ChecklistItem
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleOption
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.ChecklistCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.ChecklistItem
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.RoomAccessGuardian
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import com.harshdeep.jasnify.presentation.viewmodels.ChecklistViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.UIViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CloudWhisper
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.LightSkyBlue
import com.harshdeep.jasnify.theme.PaleLavender
import com.harshdeep.jasnify.theme.SoftMint
import com.harshdeep.jasnify.theme.SoftPeach
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

sealed interface ChecklistScreenState {
    object List : ChecklistScreenState
    data class Detail(val checklist: Checklist?, val isAddingNew: Boolean) : ChecklistScreenState
    object Archives : ChecklistScreenState
    object ManageRoomAccess : ChecklistScreenState
}

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun ChecklistsTab(
    mainNavController: NavHostController,
    viewModel: ChecklistViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    onBottomBarVisibilityChange: (Boolean) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val mainGraphEntry =
        remember(mainNavController) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
    val uiViewModel: UIViewModel = hiltViewModel(mainGraphEntry)
    val navBarStyle by uiViewModel.navBarStyle.collectAsStateWithLifecycle()

    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val activeEventId by eventViewModel.activeEventId.collectAsStateWithLifecycle()
    val roomUsers by roomViewModel.roomUsers.collectAsStateWithLifecycle()
    val searchResults by roomViewModel.searchResults.collectAsStateWithLifecycle()
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

    var isGridView by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("All") }
    var showMenuSheet by remember { mutableStateOf(false) }
    var selectedChecklist by remember { mutableStateOf<Checklist?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var showArchives by remember { mutableStateOf(false) }
    var showRoomAccess by remember { mutableStateOf(false) }
    var navigatedFromArchives by remember { mutableStateOf(false) }
    var showDiscardToast by remember { mutableStateOf(false) }

    // Toast State Management
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

    // Detail Screen Bottom Sheets (Hoisted to root level to prevent scale issues)
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

    val currentScreen = remember(selectedChecklist, isAddingNew, showArchives, showRoomAccess) {
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

            else -> {
                ChecklistScreenState.List
            }
        }
    }

    LaunchedEffect(currentScreen, hasAccess, isAnyBottomSheetOpen) {
        onBottomBarVisibilityChange(hasAccess == true && currentScreen is ChecklistScreenState.List && !isAnyBottomSheetOpen)
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

    BackHandler(enabled = showArchives || isSearchActive || showRoomAccess) {
        focusManager.clearFocus()
        if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
            wasFocused = false
        } else if (showArchives) {
            showArchives = false
        } else if (showRoomAccess) {
            showRoomAccess = false
        }
    }

    val isAnySheetVisible = isAnyBottomSheetOpen

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
                                val currentUid = auth.currentUser?.uid ?: ""
                                val ownerState = activeEvent?.ownerId == currentUid
                                val currentUserInRoomState = roomUsers.find { it.uid == currentUid }
                                val currentRoleState = when {
                                    ownerState -> UserRole.OWNER
                                    currentUserInRoomState != null -> currentUserInRoomState.role
                                    else -> UserRole.VIEWER
                                }

                                val displayUsers =
                                    if (currentUserInRoomState == null && currentUid.isNotEmpty()) {
                                        val self = User(
                                            uid = currentUid,
                                            name = auth.currentUser?.displayName ?: "User",
                                            email = auth.currentUser?.email ?: "",
                                            role = currentRoleState,
                                            username = auth.currentUser?.email?.substringBefore("@")
                                                ?: "Username"
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
                                        currentUserRole = currentRoleState,
                                        isSelf = { it.uid == currentUid },
                                        onBackClick = {
                                            focusManager.clearFocus()
                                            showRoomAccess = false
                                        },
                                        onMenuClick = {
                                            focusManager.clearFocus()
                                            showRoomMenuBottomSheet = true
                                        },
                                        onRoleChange = { targetUser, newRole ->
                                            activeEvent?.id?.let { id ->
                                                roomViewModel.updateRole(
                                                    id,
                                                    "Checklist",
                                                    targetUser,
                                                    newRole
                                                )
                                            }
                                        },
                                        onRemove = { targetUser ->
                                            focusManager.clearFocus()
                                            userToRemove = targetUser
                                        },
                                        onReport = { targetUser ->
                                            focusManager.clearFocus()
                                            toastData = ToastData(
                                                "${targetUser.name} reported",
                                                ToastType.DEFAULT
                                            )
                                        },
                                        onLeave = {
                                            showLeaveConfirmation = true
                                        },
                                        searchResults = searchResults,
                                        onSearch = { roomViewModel.searchUsers(it) },
                                        onGrantAccess = { email, role ->
                                            activeEvent?.id?.let { id ->
                                                roomViewModel.grantAccess(
                                                    id,
                                                    "Checklist",
                                                    email,
                                                    role
                                                )
                                                toastData = ToastData(
                                                    "Access granted to $email",
                                                    ToastType.SUCCESS
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            ChecklistScreenState.List -> {
                                Scaffold(
                                    topBar = {
                                        Column(
                                            modifier = Modifier
                                                .background(BackgroundPrimary)
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
                                                            onActiveChange = {},
                                                        )
                                                    }
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable(
                                                                interactionSource = remember { MutableInteractionSource() },
                                                                indication = null
                                                            ) {
                                                                focusManager.clearFocus()
                                                            }
                                                    ) {
                                                        CustomTopBar(
                                                            title = "Checklist",
                                                            titleIcon = TopIcon.Predefined.CHECKLIST,
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
                                            val fabOffset =
                                                if (navBarStyle == NavBarStyleOption.PILL_SHAPED) (-104).dp else (-12).dp

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
                                    containerColor = BackgroundPrimary,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                        }
                                ) { paddingValues ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = paddingValues.calculateTopPadding())
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                focusManager.clearFocus()
                                            }
                                    ) {
                                        Spacer(Modifier.height(12.dp))

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

                                        val filteredAndSortedChecklists =
                                            remember(searchQuery, checklists, selectedFilter) {
                                                checklists.filter {
                                                    it.title.contains(
                                                        searchQuery,
                                                        ignoreCase = true
                                                    ) ||
                                                            it.items.any { item ->
                                                                item.text.contains(
                                                                    searchQuery,
                                                                    ignoreCase = true
                                                                )
                                                            }
                                                }.let { list ->
                                                    when (selectedFilter) {
                                                        "Recent First" -> list.sortedWith(
                                                            compareByDescending<Checklist> { it.pinned }.thenByDescending { it.lastUpdated })

                                                        "Oldest First" -> list.sortedWith(
                                                            compareByDescending<Checklist> { it.pinned }.thenBy { it.lastUpdated })

                                                        else -> list.sortedWith(
                                                            compareByDescending<Checklist> { it.pinned }.thenByDescending { it.createdAt })
                                                    }
                                                }
                                            }

                                        val boundsTransformSpec = BoundsTransform { _, _ ->
                                            tween(
                                                durationMillis = 500,
                                                easing = FastOutSlowInEasing
                                            )
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
                                                        key = { it.id }) { checklist ->
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
                                                        key = { it.id }) { checklist ->
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
                                onClick = { showMenuSheet = false }
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
                    heading = "Remove ${it.name} from Checklist Room?",
                    subHeading = "They will not be able to access this room anymore.",
                    confirmButtonText = "Remove",
                    onDismiss = {
                        userToRemove = null
                    },
                    onConfirm = {
                        val target = userToRemove
                        if (target != null && activeEvent != null) {
                            roomViewModel.removeAccess(
                                activeEvent!!.id,
                                "Checklist",
                                target.uid
                            )
                            toastData = ToastData(
                                "${target.name} removed from Room!",
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

            // Detail Screen Bottom Sheets Hoisted Outside Scaled Container
            if (showDetailColorPicker) {
                ColorPickerBottomSheet(
                    initialColor = detailColorBeforePicker,
                    onColorPreview = { previewColor ->
                        currentDetailBgColor = previewColor
                    },
                    onConfirm = { finalColor ->
                        currentDetailBgColor = finalColor
                        detailColorBeforePicker = finalColor
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
                    items = listOf(
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
                    ).filterNotNull(),
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
                visible = (showDiscardToast || toastData.message != null) && !isAnySheetVisible,
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

// ========================================== DETAIL SCREEN ==========================================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ChecklistDetailScreen(
    checklist: Checklist? = null,
    isAddingNew: Boolean = false,
    onBackClick: (Checklist?) -> Unit,
    onDelete: (String) -> Unit = {},
    onTogglePin: (Checklist) -> Unit = {},
    onArchive: (Checklist) -> Unit = {},
    isArchived: Boolean = false,
    isViewer: Boolean = false,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    onOpenColorPicker: (Color) -> Unit = {},
    onOpenMenu: () -> Unit = {},
    currentBgColor: Color = Color.Transparent
) {
    var title by remember { mutableStateOf(checklist?.title ?: "") }

    var items by remember {
        mutableStateOf(
            if (isAddingNew && (checklist?.items == null || checklist.items.isEmpty())) {
                listOf(ChecklistItem(id = UUID.randomUUID().toString()))
            } else {
                checklist?.items ?: emptyList()
            }
        )
    }

    val checklistColors = remember {
        listOf(
            CloudWhisper, SoftMint, PaleLavender, LightSkyBlue, SoftPeach,
            Color(0xFFE1F5FE), Color(0xFFE8F5E9), Color(0xFFFFF3E0), Color(0xFFFCE4EC),
            Color(0xFFEDE7F6), Color(0xFFE0F7FA), Color(0xFFFFFDE7)
        )
    }

    var bgColor by remember(checklist?.id) {
        mutableStateOf(
            if (checklist != null) {
                Color(checklist.bgColorHex)
            } else {
                checklistColors.random()
            }
        )
    }

    LaunchedEffect(currentBgColor) {
        if (currentBgColor != Color.Transparent) {
            bgColor = currentBgColor
        }
    }

    var pinned by remember { mutableStateOf(checklist?.pinned ?: false) }
    var archived by remember { mutableStateOf(checklist?.archived ?: isArchived) }

    LaunchedEffect(isArchived) {
        archived = isArchived
    }

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    var isFormattingActive by remember { mutableStateOf(false) }
    var isBoldActive by remember { mutableStateOf(false) }
    var isItalicActive by remember { mutableStateOf(false) }
    var isUnderlineActive by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val firstItemFocusRequester = remember { FocusRequester() }
    var itemToFocusId by remember { mutableStateOf<String?>(null) }

    val cardDateTimeString = remember {
        checklist?.dateTime ?: run {
            val sdf = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
            sdf.format(Date())
        }
    }

    val history = remember { mutableStateListOf<Pair<String, List<ChecklistItem>>>() }
    var historyIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(checklist) {
        if (history.isEmpty()) {
            history.add(Pair(title, items))
            historyIndex = 0
        }
    }

    LaunchedEffect(isAddingNew) {
        if (isAddingNew) {
            delay(250.milliseconds)
            firstItemFocusRequester.requestFocus()
        }
    }

    fun saveToHistory(newTitle: String, newItems: List<ChecklistItem>) {
        if (historyIndex >= 0 && historyIndex < history.size) {
            val current = history[historyIndex]
            if (current.first == newTitle && current.second == newItems) return
        }
        while (history.size > historyIndex + 1) {
            history.removeAt(history.size - 1)
        }
        history.add(Pair(newTitle, newItems.toList()))
        historyIndex = history.size - 1
    }

    fun performUndo() {
        if (historyIndex > 0) {
            historyIndex--
            val snapshot = history[historyIndex]
            title = snapshot.first
            items = snapshot.second
        }
    }

    fun performRedo() {
        if (historyIndex < history.size - 1) {
            historyIndex++
            val snapshot = history[historyIndex]
            title = snapshot.first
            items = snapshot.second
        }
    }

    BackHandler {
        focusManager.clearFocus()
        val cleanedItems = items.filter { it.text.isNotBlank() }
        val result = Checklist(
            id = checklist?.id ?: UUID.randomUUID().toString(),
            title = title,
            dateTime = cardDateTimeString,
            items = cleanedItems,
            bgColorHex = bgColor.toArgb().toLong(),
            pinned = pinned,
            archived = archived,
            lastUpdated = System.currentTimeMillis(),
            createdAt = checklist?.createdAt ?: System.currentTimeMillis()
        )
        onBackClick(result)
    }

    val sharedBoundsModifier =
        if (sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "bounds-${checklist?.id ?: "new_checklist_bounds"}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = BoundsTransform { _, _ ->
                        tween(durationMillis = 350, easing = FastOutSlowInEasing)
                    }
                )
            }
        } else {
            Modifier
        }

    val sharedTitleModifier =
        if (sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = "title-${checklist?.id ?: "new_title_element"}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        } else {
            Modifier
        }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    CustomTopBar(
                        onBackClick = {
                            focusManager.clearFocus()
                            val cleanedItems = items.filter { it.text.isNotBlank() }
                            val result = Checklist(
                                id = checklist?.id ?: UUID.randomUUID().toString(),
                                title = title,
                                dateTime = cardDateTimeString,
                                items = cleanedItems,
                                bgColorHex = bgColor.toArgb().toLong(),
                                pinned = pinned,
                                archived = archived,
                                lastUpdated = System.currentTimeMillis(),
                                createdAt = checklist?.createdAt ?: System.currentTimeMillis()
                            )
                            onBackClick(result)
                        },
                        backIcon = TopIcon.CustomPainter(painterResource(R.drawable.ic_check)),
                        secondaryIcon = if (pinned) TopIcon.Predefined.PIN_FILLED else TopIcon.Predefined.PIN,
                        onSecondaryClick = {
                            focusManager.clearFocus()
                            pinned = !pinned
                            checklist?.let { onTogglePin(it) }
                        },
                        onMenuClick = {
                            focusManager.clearFocus()
                            onOpenMenu()
                        },
                        buttonStyle = ButtonBackground.TRANSLUCENT,
                        translucentAlpha = 0.5f
                    )
                }
            },
            containerColor = bgColor,
            modifier = Modifier
                .fillMaxSize()
                .then(sharedBoundsModifier)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    }
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    Text(
                        text = cardDateTimeString,
                        style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = ContentSecondary,
                    )
                    Spacer(Modifier.height(8.dp))

                    BasicTextField(
                        value = title,
                        onValueChange = {
                            if (!isViewer) {
                                title = it
                                saveToHistory(it, items)
                            }
                        },
                        readOnly = isViewer,
                        textStyle = JasnifyTheme.typography.headingXLarge.copy(
                            fontWeight = if (isBoldActive) FontWeight.Bold else FontWeight.Medium,
                            fontStyle = if (isItalicActive) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderlineActive) TextDecoration.Underline else TextDecoration.None
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(sharedTitleModifier),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    "Title",
                                    style = JasnifyTheme.typography.headingXLarge.copy(
                                        fontWeight = if (isBoldActive) FontWeight.Bold else FontWeight.Medium,
                                        fontStyle = if (isItalicActive) FontStyle.Italic else FontStyle.Normal,
                                        textDecoration = if (isUnderlineActive) TextDecoration.Underline else TextDecoration.None
                                    ),
                                    color = ContentSecondary
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                            val focusRequester =
                                if (index == 0) firstItemFocusRequester else remember { FocusRequester() }

                            LaunchedEffect(itemToFocusId) {
                                if (itemToFocusId == item.id) {
                                    focusRequester.requestFocus()
                                    itemToFocusId = null
                                }
                            }

                            val isDragging = draggedItemIndex == index
                            val currentIndex by rememberUpdatedState(index)
                            val density = LocalDensity.current
                            val itemHeightPx = with(density) { 56.dp.toPx() }

                            Box(
                                modifier = Modifier
                                    .animateItem()
                                    .graphicsLayer {
                                        translationY = if (isDragging) dragOffset else 0f
                                        scaleX = if (isDragging) 1.05f else 1f
                                        scaleY = if (isDragging) 1.05f else 1f
                                        alpha = if (isDragging) 0.8f else 1f
                                    }
                                    .zIndex(if (isDragging) 1f else 0f)
                            ) {
                                ChecklistItem(
                                    item = item,
                                    focusRequester = focusRequester,
                                    isViewer = isViewer,
                                    onTextChanged = { newText ->
                                        val updated = items.map { if (it.id == item.id) it.copy(text = newText) else it }
                                        items = updated
                                        saveToHistory(title, updated)
                                    },
                                    onCheckedChange = { isChecked ->
                                        val updated = items.map {
                                            if (it.id == item.id) it.copy(checked = isChecked) else it
                                        }
                                        items = updated
                                        saveToHistory(title, updated)
                                    },
                                    onRemove = {
                                        val updated = items.filter { it.id != item.id }
                                        items = updated
                                        saveToHistory(title, updated)
                                    },
                                    onEnterPressed = {
                                        val newItem = ChecklistItem(id = UUID.randomUUID().toString())
                                        val newItems = items.toMutableList()
                                        newItems.add(index + 1, newItem)
                                        items = newItems
                                        itemToFocusId = newItem.id
                                        saveToHistory(title, newItems)
                                    },
                                    modifier = Modifier.pointerInput(item.id) {
                                        if (!isViewer) {
                                            detectDragGesturesAfterLongPress(
                                                onDragStart = {
                                                    focusManager.clearFocus()
                                                    draggedItemIndex = currentIndex
                                                    dragOffset = 0f
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    dragOffset += dragAmount.y

                                                    val activeIndex = draggedItemIndex
                                                    if (activeIndex != null) {
                                                        val targetIndex =
                                                            if (dragOffset > itemHeightPx) {
                                                                activeIndex + 1
                                                            } else if (dragOffset < -itemHeightPx) {
                                                                activeIndex - 1
                                                            } else {
                                                                activeIndex
                                                            }

                                                        if (targetIndex in items.indices && targetIndex != activeIndex) {
                                                            val newList = items.toMutableList()
                                                            val movingItem = newList.removeAt(activeIndex)
                                                            newList.add(targetIndex, movingItem)
                                                            items = newList

                                                            if (targetIndex > activeIndex) {
                                                                dragOffset -= itemHeightPx
                                                            } else {
                                                                dragOffset += itemHeightPx
                                                            }
                                                            draggedItemIndex = targetIndex
                                                        }
                                                    }
                                                },
                                                onDragEnd = {
                                                    draggedItemIndex = null
                                                    dragOffset = 0f
                                                },
                                                onDragCancel = {
                                                    draggedItemIndex = null
                                                    dragOffset = 0f
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        item {
                            if (!isViewer) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val updated = items + ChecklistItem(id = UUID.randomUUID().toString())
                                            items = updated
                                            saveToHistory(title, updated)
                                        }
                                        .padding(start = 32.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = ContentPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Add item",
                                        style = JasnifyTheme.typography.headingMedium,
                                        color = ContentSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                if (!isViewer) {
                    ChecklistDetailToolbar(
                        isFormattingActive = isFormattingActive,
                        onFormattingActiveChange = { isFormattingActive = it },
                        isBoldActive = isBoldActive,
                        onBoldChange = { isBoldActive = it },
                        isItalicActive = isItalicActive,
                        onItalicChange = { isItalicActive = it },
                        isUnderlineActive = isUnderlineActive,
                        onUnderlineChange = { isUnderlineActive = it },
                        canUndo = historyIndex > 0,
                        canRedo = historyIndex < history.size - 1,
                        onUndo = { performUndo() },
                        onRedo = { performRedo() },
                        onColorClick = {
                            focusManager.clearFocus()
                            onOpenColorPicker(bgColor)
                        }
                    )
                }
            }
        }
    }
}

// ========================================== ARCHIVES SCREEN ==========================================

@Composable
fun ChecklistArchivesScreen(
    archivedChecklists: List<Checklist>,
    onBackClick: () -> Unit,
    onChecklistClick: (Checklist) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val focusManager = LocalFocusManager.current

    val filteredChecklists = remember(searchQuery, selectedFilter, archivedChecklists) {
        archivedChecklists.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.items.any { item -> item.text.contains(searchQuery, ignoreCase = true) }
        }.let { list ->
            when (selectedFilter) {
                "Recent First" -> list.sortedByDescending { it.lastUpdated }
                "Oldest First" -> list.sortedBy { it.lastUpdated }
                else -> list.sortedByDescending { it.createdAt }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(BackgroundPrimary)
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Archives",
                    onBackClick = {
                        focusManager.clearFocus()
                        onBackClick()
                    },
                    backIcon = TopIcon.Predefined.BACK,
                    buttonStyle = ButtonBackground.OPAQUE,
                    isLargeTitle = true
                )
            }
        },
        containerColor = BackgroundPrimary,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Spacer(Modifier.height(12.dp))

            CustomSearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                onActiveChange = { }
            )

            Spacer(Modifier.height(12.dp))

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

            if (filteredChecklists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_receipt),
                            contentDescription = "Nothing Archived Yet",
                            tint = ContentTertiary,
                            modifier = Modifier.size(84.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nothing Archived Yet",
                            style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                            color = ContentTertiary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                ) {
                    items(items = filteredChecklists, key = { it.id }) { checklist ->
                        ChecklistCard(
                            checklist = checklist,
                            onClick = { onChecklistClick(checklist) }
                        )
                    }
                }
            }
        }
    }
}

// ========================================== HELPER COMPONENTS ==========================================

@Composable
fun ChecklistDetailToolbar(
    isFormattingActive: Boolean,
    onFormattingActiveChange: (Boolean) -> Unit,
    isBoldActive: Boolean,
    onBoldChange: (Boolean) -> Unit,
    isItalicActive: Boolean,
    onItalicChange: (Boolean) -> Unit,
    isUnderlineActive: Boolean,
    onUnderlineChange: (Boolean) -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onColorClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isFormattingActive) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_bold)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = { onBoldChange(!isBoldActive) }
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_italic)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = { onItalicChange(!isItalicActive) }
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_underline)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = { onUnderlineChange(!isUnderlineActive) }
                )
            }
            TopBarIconButton(
                icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_cross)),
                backgroundStyle = ButtonBackground.TRANSLUCENT,
                translucentAlpha = 0.5f,
                iconSize = 18.dp,
                onClick = { onFormattingActiveChange(false) }
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_paint)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = onColorClick
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_a_text)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    onClick = { onFormattingActiveChange(true) }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_undo)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    iconColor = if (canUndo) ContentPrimary else ContentTertiary,
                    onClick = onUndo
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_redo)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    iconColor = if (canRedo) ContentPrimary else ContentTertiary,
                    onClick = onRedo
                )
            }
        }
    }
}

private fun areColorsEqual(c1: Color, c2: Color): Boolean {
    val threshold = 0.005f
    return abs(c1.red - c2.red) < threshold &&
            abs(c1.green - c2.green) < threshold &&
            abs(c1.blue - c2.blue) < threshold &&
            abs(c1.alpha - c2.alpha) < threshold
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    initialColor: Color,
    onColorPreview: (Color) -> Unit,
    onConfirm: (Color) -> Unit,
    onDismiss: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    val colors = remember(initialColor) {
        val basePalette = listOf(
            Color(0xFFE1F5FE),
            Color(0xFFE8F5E9),
            Color(0xFFFFF3E0),
            Color(0xFFFCE4EC),
            Color(0xFFEDE7F6),
            Color(0xFFE0F7FA),
            Color(0xFFFFFDE7),
            Color(0xFFF3E5F5),
            Color(0xFFE8EAF6),
            Color(0xFFD7F9F1),
            Color(0xFFFFE0E0),
            Color(0xFFE6F4EA),
            Color(0xFFFFF4CC),
            Color(0xFFDDEBF7),
        )
        val matchesExisting = basePalette.any { areColorsEqual(it, initialColor) }
        if (matchesExisting) {
            basePalette
        } else {
            listOf(initialColor) + basePalette
        }
    }

    CustomBottomSheet(
        onDismiss = onDismiss,
        onProgress = onProgress,
        containerColor = selectedColor,
        sheetHeight = null,
        showDragHandle = true,
        showCloseButton = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Select Background Color",
                style = JasnifyTheme.typography.headingLarge,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            val chunkedColors = remember(colors) { colors.chunked(6) }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                chunkedColors.forEach { rowColors ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowColors.forEach { color ->
                            ColorCircle(
                                color = color,
                                isSelected = areColorsEqual(color, selectedColor),
                                onClick = {
                                    selectedColor = color
                                    onColorPreview(color)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextButton(
                text = "Done",
                onClick = { onConfirm(selectedColor) },
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Square,
                containerColor = ContentPrimary,
                contentColor = ContentInvPrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(if (isSelected) 2.dp else 0.dp, ContentPrimary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = ContentPrimary,
                modifier = Modifier.size(32.dp)
            )
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