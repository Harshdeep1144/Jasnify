package com.harshdeep.jasnify.presentation.screens.home.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomDeleteSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.Checklist
import com.harshdeep.jasnify.presentation.components.cards.ChecklistCard
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.screens.room.RoomScreen
import kotlinx.coroutines.delay
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.ChecklistItem
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds


sealed interface ChecklistScreenState {
    object List : ChecklistScreenState
    data class Detail(val checklist: Checklist?, val isAddingNew: Boolean) : ChecklistScreenState
    object Archives : ChecklistScreenState
    object ManageRoomAccess : ChecklistScreenState
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChecklistsTab(
    onBottomBarVisibilityChange: (Boolean) -> Unit = {}
) {
    var isGridView by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("All") }
    var showMenuSheet by remember { mutableStateOf(false) }
    var selectedChecklist by remember { mutableStateOf<Checklist?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var showArchives by remember { mutableStateOf(false) }
    var showRoomAccess by remember { mutableStateOf(false) }
    var navigatedFromArchives by remember { mutableStateOf(false) }
    var showDiscardToast by remember { mutableStateOf(false) }

    // --- Toast State Management ---
    var toastData by remember { mutableStateOf(ToastData()) }
    LaunchedEffect(toastData.message) {
        if (toastData.message != null) {
            delay(3000.milliseconds)
            toastData = toastData.copy(message = null)
        }
    }

    // User Directory State
    var budgetRoomUsers by remember {
        mutableStateOf(
            listOf(
                User("Anand K.", "viratanand", UserRole.OWNER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Steve R.", "captainamerica", UserRole.EDITOR, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Tony S.", "ironman", UserRole.EDITOR, "https://images.unsplash.com/photo-1531427186611-ecfd6d936c79?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Bruce B.", "hulk", UserRole.VIEWER, "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Thor O.", "thor", UserRole.EDITOR, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Natasha R.", "blackwidow", UserRole.VIEWER, "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&h=150&q=80"),
                User("Clint B.", "hawkeye", UserRole.VIEWER, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150&q=80")
            )
        )
    }

    var showRoomMenuBottomSheet by remember { mutableStateOf(false) }
    var userToRemove by remember { mutableStateOf<User?>(null) }

    // Search and Focus states
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Focus Requester to request keyboard focus immediately when active
    val searchFocusRequester = remember { FocusRequester() }
    var wasFocused by remember { mutableStateOf(false) }

    // Core focusManager integration
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

    LaunchedEffect(currentScreen) {
        onBottomBarVisibilityChange(currentScreen == ChecklistScreenState.List)
    }

    LaunchedEffect(showDiscardToast) {
        if (showDiscardToast) {
            delay(2000.milliseconds)
            showDiscardToast = false
        }
    }

    // Handle requesting focus immediately when search is activated
    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            wasFocused = false
            // Allow transition or layout composition to settle before requesting focus
            delay(100.milliseconds)
            searchFocusRequester.requestFocus()
        }
    }

    // Parent BackHandler only manages non-detail screen transitions (Archives & Search)
    // Detail Screen manages its own back behavior now to ensure autocompletion/saving of changes.
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

    val checklists = remember {
        mutableStateListOf(
            Checklist(
                id = "1",
                title = "Shopping for Bride",
                dateTime = "Today, 09:30 PM",
                items = listOf(
                    ChecklistItem(text = "Purchase Outfits"),
                    ChecklistItem(text = "Make Appointment for Makeup"),
                    ChecklistItem(text = "Book Jewellery")
                ),
                bgColor = SoftMint,
                isPinned = true
            ),
            Checklist(
                id = "2",
                title = "Catering Arrangement",
                dateTime = "Today, 10:28 AM",
                items = listOf(
                    ChecklistItem(text = "Confirm Menu Selection"),
                    ChecklistItem(text = "Finalize Guest List"),
                    ChecklistItem(text = "Arrange Table Settings")
                ),
                bgColor = PaleLavender
            ),
            Checklist(
                id = "3",
                title = "Saturday To-Dos",
                dateTime = "Yesterday, 04:50 PM",
                items = listOf(
                    ChecklistItem(text = "Pick up floral arrangements"),
                    ChecklistItem(text = "Confirm limousine service booking"),
                    ChecklistItem(text = "Finalize seating chart presentation")
                ),
                bgColor = SoftPeach
            )
        )
    }

    val archivedChecklists = remember {
        mutableStateListOf(
            Checklist(
                id = "archived_1",
                title = "Audio-Visual Setup",
                dateTime = "Yesterday, 9:00 AM",
                items = listOf(
                    ChecklistItem(text = "Test Equipment"),
                    ChecklistItem(text = "Confirm Speaker Arrangements"),
                    ChecklistItem(text = "Check Lighting Levels")
                ),
                bgColor = PaleLavender
            ),
            Checklist(
                id = "archived_2",
                title = "Guest Transportation",
                dateTime = "Yesterday, 11:00 AM",
                items = listOf(
                    ChecklistItem(text = "Book Shuttle Services"),
                    ChecklistItem(text = "Verify Arrival Times"),
                    ChecklistItem(text = "Coordinate with Drivers")
                ),
                bgColor = SoftPeach
            )
        )
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                        fadeOut(animationSpec = tween(90))
            },
            label = "screen_navigation_transition"
        ) { targetScreenState ->
            when (targetScreenState) {
                is ChecklistScreenState.Detail -> {
                    ChecklistDetailScreen(
                        checklist = targetScreenState.checklist,
                        isAddingNew = targetScreenState.isAddingNew,
                        onBackClick = { updatedChecklist ->
                            focusManager.clearFocus()
                            if (updatedChecklist != null) {
                                // Empty check is already filtered and cleaned by the detail screen
                                val isEmpty = updatedChecklist.title.isBlank() && updatedChecklist.items.isEmpty()

                                if (isEmpty && targetScreenState.isAddingNew) {
                                    showDiscardToast = true
                                } else if (updatedChecklist.title.isNotBlank() || updatedChecklist.items.isNotEmpty()) {
                                    val index = checklists.indexOfFirst { it.id == updatedChecklist.id }
                                    if (index != -1) {
                                        checklists[index] = updatedChecklist
                                    } else {
                                        val archIndex = archivedChecklists.indexOfFirst { it.id == updatedChecklist.id }
                                        if (archIndex != -1) {
                                            archivedChecklists[archIndex] = updatedChecklist
                                        } else {
                                            checklists.add(0, updatedChecklist)
                                        }
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
                            checklists.removeAll { it.id == id }
                            archivedChecklists.removeAll { it.id == id }
                            selectedChecklist = null
                            isAddingNew = false
                            if (navigatedFromArchives) {
                                showArchives = true
                                navigatedFromArchives = false
                            }
                        },
                        onTogglePin = { id ->
                            val index = checklists.indexOfFirst { it.id == id }
                            if (index != -1) {
                                checklists[index] = checklists[index].copy(isPinned = !checklists[index].isPinned)
                            }
                        },
                        onArchive = { id ->
                            focusManager.clearFocus()
                            val index = checklists.indexOfFirst { it.id == id }
                            if (index != -1) {
                                val item = checklists.removeAt(index)
                                archivedChecklists.add(0, item.copy(isPinned = false))
                            } else {
                                val archIndex = archivedChecklists.indexOfFirst { it.id == id }
                                if (archIndex != -1) {
                                    val item = archivedChecklists.removeAt(archIndex)
                                    checklists.add(0, item)
                                }
                            }
                            selectedChecklist = null
                            isAddingNew = false
                            if (navigatedFromArchives) {
                                showArchives = true
                                navigatedFromArchives = false
                            }
                        },
                        isArchived = archivedChecklists.any { it.id == targetScreenState.checklist?.id },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SurfaceSecondary)
                    ){
                        RoomScreen(
                            allUsers = budgetRoomUsers,
                            currentUserRole = UserRole.OWNER,
                            isSelf = { it.username == "viratanand" },
                            onBackClick = {
                                focusManager.clearFocus()
                                showRoomAccess = false
                            },
                            onMenuClick = {
                                focusManager.clearFocus()
                                showRoomMenuBottomSheet = true
                            },
                            onRoleChange = { targetUser, newRole ->
                                budgetRoomUsers = budgetRoomUsers.map { user ->
                                    if (user.username == targetUser.username) user.copy(role = newRole) else user
                                }
                            },
                            onRemove = { targetUser ->
                                userToRemove = targetUser
                            },
                            onReport = { targetUser ->
                                toastData = ToastData("${targetUser.name} reported", ToastType.DEFAULT)
                            },
                            onLeave = {
                                toastData = ToastData("You left the room", ToastType.DEFAULT)
                                showRoomAccess = false
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                ChecklistScreenState.List -> {
                    Scaffold(
                        topBar = {
                            Column(
                                modifier = Modifier.background(BackgroundPrimary)
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                            ){
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
                                                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
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
                                        CustomTopBar(
                                            title = "Checklist",
                                            titleIcon = TopIcon.Predefined.CHECKLIST,
                                            isLeftAligned = true,
                                            isLargeTitle = true,
                                            secondaryIcon = TopIcon.Predefined.SEARCH,
                                            onSecondaryClick = { isSearchActive = true },
                                            onMenuClick = {
                                                focusManager.clearFocus()
                                                showMenuSheet = true
                                            },
                                            buttonStyle = ButtonBackground.OPAQUE
                                        )
                                    }
                                }
                            }
                        },
                        floatingActionButton = {
                            CustomIconButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    isAddingNew = true
                                },
                                icon = painterResource(R.drawable.ic_plus),
                                size = ButtonSize.Large,
                                modifier = Modifier
                                    .offset(y = 20.dp)
                                    .shadow(16.dp, CircleShape)
                            )
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

                            val filteredAndSortedChecklists = remember(searchQuery, checklists, selectedFilter) {
                                checklists.filter {
                                    it.title.contains(searchQuery, ignoreCase = true) ||
                                            it.items.any { item -> item.text.contains(searchQuery, ignoreCase = true) }
                                }.let { list ->
                                    when (selectedFilter) {
                                        "Recent First" -> list.sortedWith(compareByDescending<Checklist> { it.isPinned }.thenByDescending { it.dateTime })
                                        "Oldest First" -> list.sortedWith(compareByDescending<Checklist> { it.isPinned }.thenBy { it.dateTime })
                                        else -> list.sortedByDescending { it.isPinned }
                                    }
                                }
                            }

                            val boundsTransformSpec = BoundsTransform { _, _ ->
                                tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            }

                            if (isGridView) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(bottom = 12.dp),
                                    modifier = Modifier.fillMaxSize()
                                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                                ) {
                                    items(items = filteredAndSortedChecklists, key = { it.id }) { checklist ->
                                        Box(
                                            modifier = Modifier.sharedBounds(
                                                sharedContentState = rememberSharedContentState(key = "bounds-${checklist.id}"),
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
                                    modifier = Modifier.fillMaxSize()
                                        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                                ) {
                                    items(items = filteredAndSortedChecklists, key = { it.id }) { checklist ->
                                        Box(
                                            modifier = Modifier.sharedBounds(
                                                sharedContentState = rememberSharedContentState(key = "bounds-${checklist.id}"),
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

    if (showMenuSheet) {
        MenuBottomSheet(
            items = listOf(
                // Row 1: Side-by-side split grid (with icons on top)
                listOf(
                    MenuSheetActionItem(
                        text = if (isGridView) "List View" else "Grid View",
                        icon = if (isGridView) painterResource(R.drawable.ic_list) else painterResource(R.drawable.ic_grid),
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
                        text = "Manage Room Access",
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
            onCancelClick = { showMenuSheet = false }
        )
    }

    if (showRoomMenuBottomSheet) {
        MenuBottomSheet(
            items = listOf(
                listOf(
                    MenuSheetActionItem(
                        text = "Copy Link",
                        icon = painterResource(R.drawable.ic_link),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            toastData = ToastData("Link Copied!", ToastType.SUCCESS)
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Add New Members",
                        icon = painterResource(R.drawable.ic_plus),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            // Handle add new members logic
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Leave Room",
                        icon = painterResource(R.drawable.ic_logout),
                        iconPlacement = IconPlacement.Left,
                        contentColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            showRoomMenuBottomSheet = false
                            showRoomAccess = false
                        }
                    )
                )
            ),
            onCancelClick = {
                showRoomMenuBottomSheet = false
            }
        )
    }

    if (userToRemove != null) {
        CustomDeleteSheet(
            heading = "Remove Member from Checklist Room?",
            subHeading = "They will not be able to access this room anymore.",
            confirmButtonText = "Remove",
            onDismiss = {
                userToRemove = null
            },
            onConfirmRemove = {
                val target = userToRemove
                if (target != null) {
                    budgetRoomUsers = budgetRoomUsers.filter { it.username != target.username }
                    toastData = ToastData("${target.name} removed from room", ToastType.SUCCESS)
                }
                userToRemove = null
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = showDiscardToast || toastData.message != null,
            enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight + 500 }),
            exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight + 500 }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
                .zIndex(100f)
        ) {
            CustomToast(
                message = toastData.message ?: "Empty list discarded",
                type = toastData.type
            )
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
    onTogglePin: (String) -> Unit = {},
    onArchive: (String) -> Unit = {},
    isArchived: Boolean = false,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    var title by remember { mutableStateOf(checklist?.title ?: "") }

    // Automatically pre-populate with an empty checklist item if adding a new list
    var items by remember {
        mutableStateOf(
            if (isAddingNew && (checklist?.items == null || checklist.items.isEmpty())) {
                listOf(ChecklistItem(id = UUID.randomUUID().toString()))
            } else {
                checklist?.items ?: emptyList()
            }
        )
    }

    var bgColor by remember { mutableStateOf(checklist?.bgColor ?: SoftMint) }
    var colorBeforePicker by remember { mutableStateOf(bgColor) }
    var isPinned by remember { mutableStateOf(checklist?.isPinned ?: false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Text Formatting states
    var isFormattingActive by remember { mutableStateOf(false) }
    var isBoldActive by remember { mutableStateOf(false) }
    var isItalicActive by remember { mutableStateOf(false) }
    var isUnderlineActive by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Focus Requester specifically mapped to the first checklist item
    val firstItemFocusRequester = remember { FocusRequester() }

    // Generate/Reuse the active card timestamp dynamically
    val cardDateTimeString = remember {
        checklist?.dateTime ?: run {
            val sdf = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
            sdf.format(Date())
        }
    }

    // Undo/Redo State History
    val history = remember { mutableStateListOf<Pair<String, List<ChecklistItem>>>() }
    var historyIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(checklist) {
        if (history.isEmpty()) {
            history.add(Pair(title, items))
            historyIndex = 0
        }
    }

    // Automated focusing behavior mapped to the first checklist item
    LaunchedEffect(isAddingNew) {
        if (isAddingNew) {
            delay(250.milliseconds) // Settle UI transitions
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

    // Auto-save handling for System Back gesture and hardware button back press
    BackHandler {
        focusManager.clearFocus()
        val cleanedItems = items.filter { it.text.isNotBlank() }
        val result = Checklist(
            id = checklist?.id ?: UUID.randomUUID().toString(),
            title = title,
            dateTime = cardDateTimeString,
            items = cleanedItems,
            bgColor = bgColor,
            isPinned = isPinned
        )
        onBackClick(result)
    }

    val sharedBoundsModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
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

    val sharedTitleModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = "title-${checklist?.id ?: "new_title_element"}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(Color.Transparent)
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    onBackClick = {
                        focusManager.clearFocus()
                        // Filter out empty/blank checklist items when user exits & saves
                        val cleanedItems = items.filter { it.text.isNotBlank() }

                        val result = Checklist(
                            id = checklist?.id ?: UUID.randomUUID().toString(),
                            title = title,
                            dateTime = cardDateTimeString,
                            items = cleanedItems,
                            bgColor = bgColor,
                            isPinned = isPinned
                        )
                        onBackClick(result)
                    },
                    backIcon = TopIcon.CustomPainter(painterResource(R.drawable.ic_check)),
                    secondaryIcon =  if(isPinned) TopIcon.Predefined.PIN_FILLED else TopIcon.Predefined.PIN,
                    onSecondaryClick = {
                        focusManager.clearFocus()
                        isPinned = !isPinned
                        checklist?.id?.let { onTogglePin(it) }
                    },
                    onMenuClick = {
                        focusManager.clearFocus()
                        showMenu = true
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
                .imePadding() // Automatically docks layout and toolbar above system keyboard
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
                        title = it
                        saveToHistory(it, items)
                    },
                    textStyle = JasnifyTheme.typography.headingXLarge.copy(
                        fontWeight = if (isBoldActive) FontWeight.Bold else FontWeight.Medium,
                        fontStyle = if (isItalicActive) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = if (isUnderlineActive) TextDecoration.Underline else TextDecoration.None
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(sharedTitleModifier),
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
                        // Dedicate the firstItemFocusRequester to the first index
                        val focusRequester = if (index == 0) firstItemFocusRequester else remember { FocusRequester() }
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
                                onTextChanged = { newText ->
                                    val updated = items.map { if (it.id == item.id) it.copy(text = newText) else it }
                                    items = updated
                                    saveToHistory(title, updated)
                                },
                                onCheckedChange = { isChecked ->
                                    val updated = items.map { if (it.id == item.id) it.copy(isChecked = isChecked) else it }
                                    items = updated
                                    saveToHistory(title, updated)
                                },
                                onRemove = {
                                    val updated = items.filter { it.id != item.id }
                                    items = updated
                                    saveToHistory(title, updated)
                                },
                                modifier = Modifier.pointerInput(item.id) {
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
                                                val targetIndex = if (dragOffset > itemHeightPx) {
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
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val updated = items + ChecklistItem(id = UUID.randomUUID().toString())
                                    items = updated
                                    saveToHistory(title, updated)
                                }
                                .padding(start = 32.dp, 8.dp, 8.dp, 8.dp)
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
                    colorBeforePicker = bgColor // Keep a snapshot of original color before previewing
                    showColorPicker = true
                }
            )
        }
    }

    if (showColorPicker) {
        ColorPickerBottomSheet(
            initialColor = colorBeforePicker,
            onColorPreview = { previewColor ->
                bgColor = previewColor // Instant temporary preview
            },
            onConfirm = { finalColor ->
                bgColor = finalColor
                colorBeforePicker = finalColor // Permanently update verified color
                showColorPicker = false
            },
            onDismiss = {
                bgColor = colorBeforePicker // Revert background to original color if dismissed
                showColorPicker = false
            }
        )
    }

    if (showMenu) {
        MenuBottomSheet(
            items = listOf(
                listOf(
                    MenuSheetActionItem(
                        text = if (isArchived) "Unarchive" else "Archive",
                        icon = painterResource(R.drawable.ic_box),
                        iconPlacement = IconPlacement.Left,
                        onClick = {
                            showMenu = false
                            checklist?.id?.let { onArchive(it) }
                        }
                    )
                ),
                listOf(
                    MenuSheetActionItem(
                        text = "Delete",
                        icon = painterResource(R.drawable.ic_delete),
                        iconPlacement = IconPlacement.Left,
                        contentColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            showMenu = false
                            showDeleteConfirmation = true
                        }
                    )
                )
            ),
            onCancelClick = { showMenu = false }
        )
    }

    if (showDeleteConfirmation) {
        CustomDeleteSheet(
            heading = "Are you sure?",
            subHeading = "The checklist will be deleted permanently.",
            confirmButtonText = "Delete Checklist",
            onDismiss = { showDeleteConfirmation = false },
            onConfirmRemove = {
                showDeleteConfirmation = false
                checklist?.id?.let { onDelete(it) }
            }
        )
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
                "Recent First" -> list.sortedByDescending { it.dateTime }
                "Oldest First" -> list.sortedBy { it.dateTime }
                else -> list
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(BackgroundPrimary)
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
                modifier = Modifier.fillMaxWidth()
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

            LazyColumn(
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
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
                    iconColor = if(canUndo) ContentPrimary else ContentTertiary,
                    onClick = onUndo
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(R.drawable.ic_redo)),
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f,
                    iconColor = if(canRedo) ContentPrimary else ContentTertiary,
                    onClick = onRedo
                )
            }
        }
    }
}


/**
 * Helper function to robustly compare Compose Color states.
 * Bypasses ColorSpace representation mismatches or float precision discrepancies.
 */
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
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    val colors = remember(initialColor) {
        val basePalette = listOf(
            Color(0xFFE1F5FE), Color(0xFFE8F5E9), Color(0xFFFFF3E0), Color(0xFFFCE4EC), Color(0xFFEDE7F6), Color(0xFFE0F7FA), Color(0xFFFFFDE7),
            Color(0xFFF3E5F5), Color(0xFFE8EAF6), Color(0xFFD7F9F1), Color(0xFFFFE0E0), Color(0xFFE6F4EA), Color(0xFFFFF4CC), Color(0xFFDDEBF7),
        )
        val matchesExisting = basePalette.any { areColorsEqual(it, initialColor) }
        if (matchesExisting) {
            basePalette
        } else {
            listOf(initialColor) + basePalette
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = selectedColor,
        shape = RoundedCornerShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(56.dp)
                    .height(4.dp)
                    .background(ContentTertiary, shape = RoundedCornerShape(100))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Select Background Color",
                style = JasnifyTheme.typography.headingLarge,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chunk the dynamic colors list to render a responsive and complete grid row layout
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
                                    onColorPreview(color) // Triggers real-time screen color preview
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextButton(
                text = "Done",
                onClick = { onConfirm(selectedColor) }, // Commits chosen color permanently
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
        ChecklistsTab()
    }
}