package com.harshdeep.jasnify.presentation.screens.home.tabs

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Contact
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddGuestInfoBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddGuestTypeBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ConfirmationBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.ContactPickerBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomSuccessBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.GuestDetailsBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.IconPlacement
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.MenuSheetActionItem
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SelectGuestTypeBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.GuestCard
import com.harshdeep.jasnify.presentation.components.cards.GuestCardType
import com.harshdeep.jasnify.presentation.components.cards.GuestTypeCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.pill360Shadow
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.util.ContactHelper
import com.harshdeep.jasnify.util.SearchHistoryManager
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

enum class GuestsView {
    MAIN,
    MANAGE_GUEST_TYPES,
    GUEST_TYPE_DETAIL
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GuestsTab(
    onBottomBarVisibilityChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
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

    val guests = remember { mutableStateListOf<Guest>() }

    var showEmptyState by remember { mutableStateOf(true) }
    var selectedGuestForInfo by remember { mutableStateOf<Guest?>(null) }
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

    var isHeaderVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }
    val mainListState = rememberLazyListState()

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

                if (delta > 0) { // Scrolling up (showing)
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) { // Scrolling down (hiding)
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
            delay(2000.milliseconds)
            toastData = null
        }
    }

    var hasContactPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Clear selection when filters change
    LaunchedEffect(inviteFilter, selectedTypesFilter) {
        selectedGuestIds.clear()
    }

    var sheetMotionProgress by remember { mutableFloatStateOf(0f) }

    val isAnyBottomSheetOpen by remember {
        derivedStateOf {
            selectedGuestForInfo != null ||
                    selectedGuestForEdit != null ||
                    showAddGuestSheet ||
                    showAddTypeSheet ||
                    showTypeFilterSheet ||
                    showContactPicker ||
                    showMenuSheet ||
                    guestToDeleteForInfo != null ||
                    showMultiDeleteConfirmation ||
                    showSuccessSheet
        }
    }

    val isBottomBarVisible by remember {
        derivedStateOf {
            !isAnyBottomSheetOpen && currentView == GuestsView.MAIN && !isMultiSelectMode && toastData?.message == null && isHeaderVisible
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

    fun getGuestTypeColor(type: String): Color {
        return typeColors.getOrPut(type) {
            colorPalette[typeColors.size % colorPalette.size]
        }
    }

    val filteredGuests by remember(searchQuery, selectedTypesFilter, inviteFilter) {
        derivedStateOf {
            guests.filter { guest ->
                val matchesSearch = guest.name.contains(searchQuery, ignoreCase = true)
                val matchesType = selectedTypesFilter.isEmpty() || selectedTypesFilter.contains(guest.type)
                val matchesInvite = when (inviteFilter) {
                    "Yet to invite" -> !guest.isInvited
                    "Already invited" -> guest.isInvited
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

    val onInviteToggle = { guestId: String ->
        val index = guests.indexOfFirst { it.id == guestId }
        if (index != -1) {
            guests[index] = guests[index].copy(isInvited = !guests[index].isInvited)
        }
    }

    BackHandler(enabled = currentView != GuestsView.MAIN || isSearchActive) {
        if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
        } else {
            when (currentView) {
                GuestsView.GUEST_TYPE_DETAIL -> currentView = GuestsView.MANAGE_GUEST_TYPES
                GuestsView.MANAGE_GUEST_TYPES -> currentView = GuestsView.MAIN
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
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
            AnimatedContent(
                targetState = currentView,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ViewTransition"
            ) { view ->
                when (view) {
                    GuestsView.MAIN -> {
                        Scaffold(
                            containerColor = BackgroundPrimary,
                            contentWindowInsets = WindowInsets(0, 0, 0, 0)
                        ) { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                if (isSearchActive) {
                                    GuestSearchContent(
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
                                        getGuestTypeColor = ::getGuestTypeColor,
                                        onInviteToggle = onInviteToggle
                                    )
                                } else {
                                    if (guests.isEmpty() && showEmptyState) {
                                        GuestEmptyState(
                                            hasContactPermission = hasContactPermission,
                                            onAddGuestClick = onAddGuestClick
                                        )
                                    } else {
                                        var expandedGuestId by remember { mutableStateOf<String?>(null) }

                                        // Pinned status bar background overlay ensuring no content bleeds into status bar
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .windowInsetsTopHeight(WindowInsets.statusBars)
                                                .background(Color.Transparent)
                                                .zIndex(100f)
                                        )

                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .statusBarsPadding(),
                                            state = mainListState,
                                        ) {
                                            item {
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

                                            stickyHeader {
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
                                                                .padding(12.dp),
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

                                                            CustomTextButton(
                                                                onClick = onAddGuestClick,
                                                                text = "Add",
                                                                leadingIcon = painterResource(id = R.drawable.ic_plus),
                                                                shapeStyle = ButtonShapeStyle.Round
                                                            )
                                                        }
                                                    }

                                                    LazyRow(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        item {
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
                                                        item {
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
                                                        item {
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

                                            if (filteredGuests.isEmpty()) {
                                                item {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(320.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Top,
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
                                                itemsIndexed(
                                                    filteredGuests,
                                                    key = { _, guest -> guest.id }
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
                                                            isInvited = guest.isInvited,
                                                            type = when {
                                                                isMultiSelectMode -> GuestCardType.SELECTABLE
                                                                else -> GuestCardType.INVITE_ACTION
                                                            },
                                                            isSelected = isSelected,
                                                            showActions = isExpanded,
                                                            lastUpdatedBy = guest.lastUpdatedBy,
                                                            lastUpdatedAt = guest.lastUpdatedAt,
                                                            labelColor = getGuestTypeColor(guest.type),
                                                            onCardClick = {
                                                                if (isMultiSelectMode) {
                                                                    if (isSelected) {
                                                                        selectedGuestIds.remove(guest.id)
                                                                    } else {
                                                                        selectedGuestIds.add(guest.id)
                                                                    }
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
                                                    item {
                                                        FooterJansify(modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
                                                    }
                                                }
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
                                                        selectedGuestIds.forEach { id ->
                                                            val index = guests.indexOfFirst { it.id == id }
                                                            if (index != -1) {
                                                                guests[index] = guests[index].copy(
                                                                    isInvited = true,
                                                                    invitedBy = "Me",
                                                                    invitedAt = java.text.SimpleDateFormat("MMM dd, yyyy, hh:mma", java.util.Locale.getDefault()).format(java.util.Date())
                                                                )
                                                            }
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
                            guestTypes = guestTypes.map { typeName ->
                                com.harshdeep.jasnify.domain.model.GuestType(
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
                            typeName = selectedGuestTypeForDetail ?: "",
                            guests = guests.filter { it.type == selectedGuestTypeForDetail },
                            onBackClick = { currentView = GuestsView.MANAGE_GUEST_TYPES },
                            getGuestTypeColor = ::getGuestTypeColor,
                            onViewDetails = { guest -> selectedGuestForInfo = guest },
                            onEditTypeClick = {
                                selectedGuestTypeForEdit = selectedGuestTypeForDetail
                                showAddTypeSheet = true
                            },
                            onInviteToggle = onInviteToggle
                        )
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
        GuestDetailsBottomSheet(
            guest = selectedGuestForInfo!!,
            onDismiss = { selectedGuestForInfo = null },
            onProgress = { sheetMotionProgress = it },
            onEditClick = {
                selectedGuestForEdit = selectedGuestForInfo
                selectedGuestForInfo = null
            },
            onInviteClick = {
                val guestId = selectedGuestForInfo!!.id
                val index = guests.indexOfFirst { it.id == guestId }
                if (index != -1) {
                    val updatedGuest = guests[index].copy(
                        isInvited = !guests[index].isInvited,
                        invitedBy = if (!guests[index].isInvited) "Me" else null,
                        invitedAt = if (!guests[index].isInvited) java.text.SimpleDateFormat("MMM dd, yyyy, hh:mma", java.util.Locale.getDefault()).format(java.util.Date()) else null
                    )
                    guests[index] = updatedGuest
                    selectedGuestForInfo = updatedGuest
                }
            },
            onDeleteClick = {
                guestToDeleteForInfo = selectedGuestForInfo
                selectedGuestForInfo = null
            }
        )
    }

    guestToDeleteForInfo?.let { guest ->
        val formattedName = guest.name.trim().split("\\s+".toRegex()).let { parts ->
            if (parts.size >= 2) "${parts[0]} ${parts[1].take(1)}." else parts[0]
        }
        ConfirmationBottomSheet(
            heading = "Are you sure?",
            subHeading = "${guest.name} will be removed from the guest list.",
            confirmButtonText = "Remove $formattedName",
            onDismiss = { guestToDeleteForInfo = null },
            onConfirm = {
                guests.removeAll { it.id == guest.id }
                guestToDeleteForInfo = null
                if (guests.isEmpty()) showEmptyState = true
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
                val n1 = selectedGuests[0].name.trim().split("\\s+".toRegex())[0]
                val n2 = selectedGuests[1].name.trim().split("\\s+".toRegex())[0]
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
                guests.removeAll { it.id in selectedGuestIds }
                selectedGuestIds.clear()
                isMultiSelectMode = false
                showMultiDeleteConfirmation = false
                if (guests.isEmpty()) showEmptyState = true
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
            onAddClick = { newGuest ->
                if (selectedGuestForEdit != null) {
                    val index = guests.indexOfFirst { it.id == selectedGuestForEdit!!.id }
                    if (index != -1) {
                        guests[index] = newGuest.copy(
                            id = selectedGuestForEdit!!.id,
                            lastUpdatedBy = "Me",
                            lastUpdatedAt = java.text.SimpleDateFormat("MMM dd, yyyy, hh:mma", java.util.Locale.getDefault()).format(java.util.Date())
                        )
                    }
                    selectedGuestForEdit = null
                } else {
                    val exists = guests.any {
                        it.name.equals(newGuest.name, ignoreCase = true) &&
                                it.contactNo == newGuest.contactNo &&
                                it.contactNo.isNotBlank()
                    }
                    if (exists) {
                        toastData = ToastData("${newGuest.name} already exists", ToastType.DEFAULT)
                    } else {
                        guests.add(0, newGuest)
                        showAddGuestSheet = false
                        showEmptyState = false
                    }
                }
            },
            onAddNewTypeClick = { showAddTypeSheet = true },
            guestTypes = guestTypes.map { com.harshdeep.jasnify.domain.model.GuestType(name = it) },
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

                    guests.forEachIndexed { idx, guest ->
                        if (guest.type == selectedGuestTypeForEdit) {
                            guests[idx] = guest.copy(type = newType)
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
                com.harshdeep.jasnify.domain.model.GuestType(
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
            }
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
                    var duplicateCount = 0
                    selectedContacts.forEach { contact ->
                        val phoneNumber = if (includePhoneNo) contact.phoneNumber else ""
                        val exists = guests.any {
                            it.name.equals(contact.name, ignoreCase = true) && it.contactNo == phoneNumber
                        }
                        if (!exists) {
                            guests.add(
                                0,
                                Guest(
                                    name = contact.name,
                                    contactNo = phoneNumber,
                                    imageUrl = contact.photoUri,
                                    type = "Others"
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

                    showEmptyState = false
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

    if (showMenuSheet) {
        val menuItems = listOf(
            listOfNotNull(
                MenuSheetActionItem(
                    text = "Add Guests",
                    icon = painterResource(id = R.drawable.ic_add_circle),
                    iconPlacement = IconPlacement.Top,
                    onClick = {
                        showMenuSheet = false
                        onAddGuestClick()
                    }
                ),
                if (guests.isNotEmpty()) {
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
                    text = "Manage Room Access",
                    icon = painterResource(id = R.drawable.ic_user_default),
                    iconPlacement = IconPlacement.Left,
                    onClick = { showMenuSheet = false }
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

@Preview(showBackground = true)
@Composable
fun PreviewGuestsTab() {
    JasnifyTheme {
        GuestsTab()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGuestEmptyState() {
    JasnifyTheme {
        GuestEmptyState(
            hasContactPermission = false,
            onAddGuestClick = {}
        )
    }
}

@Composable
fun GuestEmptyState(
    hasContactPermission: Boolean,
    onAddGuestClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = null,
                    tint = ContentTertiary,
                    modifier = Modifier.size(84.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No guests to invite",
                    style = JasnifyTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ContentTertiary
                )
                Spacer(modifier = Modifier.height(24.dp))

                CustomTextButton(
                    onClick = onAddGuestClick,
                    text = if (hasContactPermission) "Add Guests" else "Allow Contacts Access",
                    leadingIcon = if (hasContactPermission) painterResource(id = R.drawable.ic_plus) else null,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (hasContactPermission) "Select and Import multiple \n contacts at once." else "We need contacts \n access to show contacts.",
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentTertiary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun GuestTypeScreen(
    guestTypes: List<com.harshdeep.jasnify.domain.model.GuestType>,
    onBackClick: () -> Unit,
    onAddTypeClick: () -> Unit,
    onTypeClick: (com.harshdeep.jasnify.domain.model.GuestType) -> Unit,
    getGuestThumbnails: (String) -> List<String>
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(BackgroundPrimary)
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Guest Type",
                    onBackClick = onBackClick
                )
            }
        },
        containerColor = BackgroundSecondary,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(guestTypes, key = { it.name }) { type ->
                    GuestTypeCard(
                        label = type.name,
                        guestCount = type.guestCount,
                        showChecker = false,
                        imageUrls = getGuestThumbnails(type.name),
                        onClick = { onTypeClick(type) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.Transparent,
                                0.25f to BackgroundSecondary.copy(alpha = 0.15f),
                                0.55f to BackgroundSecondary.copy(alpha = 0.65f),
                                0.80f to BackgroundSecondary.copy(alpha = 0.92f),
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
                            onClick = onAddTypeClick,
                            text = "Add a Guest Type",
                            leadingIcon = painterResource(id = R.drawable.ic_plus),
                            modifier = Modifier.fillMaxWidth(),
                            shapeStyle = ButtonShapeStyle.Round
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GuestTypeDetailScreen(
    typeName: String,
    guests: List<Guest>,
    onBackClick: () -> Unit,
    getGuestTypeColor: (String) -> Color,
    onViewDetails: (Guest) -> Unit,
    onEditTypeClick: () -> Unit,
    onInviteToggle: (String) -> Unit
) {
    var activeFilter by remember { mutableStateOf("Everyone") }
    var expandedGuestId by remember { mutableStateOf<String?>(null) }

    val filteredGuests = remember(guests, activeFilter) {
        when (activeFilter) {
            "Yet to invite" -> guests.filter { !it.isInvited }
            "Already invited" -> guests.filter { it.isInvited }
            else -> guests
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.statusBarsPadding()
            ) {
                CustomTopBar(
                    onBackClick = onBackClick
                )
            }
        },
        containerColor = BackgroundPrimary,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = typeName,
                    style = JasnifyTheme.typography.displayLarge,
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))

                CustomIconButton(
                    icon = painterResource(R.drawable.ic_edit),
                    size = ButtonSize.Small,
                    contentColor = ContentPrimary,
                    containerColor = ContentInvPrimary,
                    onClick = onEditTypeClick
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        label = "Everyone",
                        isSelected = activeFilter == "Everyone",
                        onClick = { activeFilter = "Everyone" },
                        shapeStyle = ChipShapeStyle.Round,
                        hasStroke = true
                    )
                }
                item {
                    FilterChip(
                        label = "Yet to invite",
                        isSelected = activeFilter == "Yet to invite",
                        onClick = { activeFilter = "Yet to invite" },
                        shapeStyle = ChipShapeStyle.Round,
                        hasStroke = true
                    )
                }
                item {
                    FilterChip(
                        label = "Already invited",
                        isSelected = activeFilter == "Already invited",
                        onClick = { activeFilter = "Already invited" },
                        shapeStyle = ChipShapeStyle.Round,
                        hasStroke = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 12.dp, top = 0.dp, bottom = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (filteredGuests.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_book),
                                    contentDescription = null,
                                    tint = ContentTertiary,
                                    modifier = Modifier.size(84.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No guests to invite",
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
                    itemsIndexed(filteredGuests, key = { _, guest -> guest.id }) { index, guest ->
                        val isExpanded = expandedGuestId == guest.id

                        val topRadius = if (index == 0) CornerLargeIncrease else CornerExtraSmall
                        val bottomRadius = if (index == filteredGuests.lastIndex) CornerLargeIncrease else CornerExtraSmall
                        val itemShape = SquircleShape(
                            topStart = topRadius,
                            topEnd = topRadius,
                            bottomStart = bottomRadius,
                            bottomEnd = bottomRadius,
                            cornerSmoothing = CornerSmoothingDefault
                        )

                        GuestCard(
                            name = guest.name,
                            label = guest.type,
                            imageUrl = guest.imageUrl,
                            isInvited = guest.isInvited,
                            type = GuestCardType.INVITE_ACTION,
                            labelColor = getGuestTypeColor(guest.type),
                            showActions = isExpanded,
                            onCardClick = {
                                expandedGuestId = if (isExpanded) null else guest.id
                            },
                            onViewDetailsClick = { onViewDetails(guest) },
                            onInviteClick = { onInviteToggle(guest.id) },
                            lastUpdatedBy = guest.lastUpdatedBy,
                            lastUpdatedAt = guest.lastUpdatedAt,
                            cardShape = itemShape
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GuestSearchContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    recentSearches: List<String>,
    onClearRecent: () -> Unit,
    onRemoveRecent: (String) -> Unit,
    searchResults: List<Guest>,
    onGuestClick: (Guest) -> Unit,
    onAddGuestClick: () -> Unit,
    getGuestTypeColor: (String) -> Color,
    onInviteToggle: (String) -> Unit
) {
    var wasFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
    ) {
        CustomSearchBar(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Search Guests",
            onActiveChange = { active ->
                if (active) {
                    wasFocused = true
                } else if (wasFocused) {
                    onBackClick()
                    wasFocused = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp, 12.dp, 0.dp)
                .focusRequester(focusRequester)
        )

        if (searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clock_forward),
                            contentDescription = null,
                            tint = ContentPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recent Searches",
                            style = JasnifyTheme.typography.headingMedium,
                            fontWeight = FontWeight.Medium,
                            color = ContentPrimary
                        )
                    }
                    Text(
                        text = "Clear all",
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentBrandDark,
                        modifier = Modifier.clickable { onClearRecent() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentSearches.forEach { search ->
                        FilterChip(
                            label = search,
                            trailingIcon = Icons.Default.Close,
                            hasStroke = true,
                            shapeStyle = ChipShapeStyle.Round,
                            onTrailingIconClick = {
                                onRemoveRecent(search)
                            },
                            onClick = {
                                onSearchQueryChange(search)
                            }
                        )
                    }
                }
            }
        } else {
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_book),
                            contentDescription = null,
                            tint = ContentTertiary,
                            modifier = Modifier.size(84.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No guests to invite",
                            style = JasnifyTheme.typography.displayMedium.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentTertiary
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        CustomTextButton(
                            onClick = onAddGuestClick,
                            text = "Add New Guests",
                            leadingIcon = painterResource(id = R.drawable.ic_plus),
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(searchResults, key = { _, guest -> guest.id }) { index, guest ->
                        val topRadius = if (index == 0) CornerLargeIncrease else CornerExtraSmall
                        val bottomRadius = if (index == searchResults.lastIndex) CornerLargeIncrease else CornerExtraSmall
                        val itemShape = SquircleShape(
                            topStart = topRadius,
                            topEnd = topRadius,
                            bottomStart = bottomRadius,
                            bottomEnd = bottomRadius,
                            cornerSmoothing = CornerSmoothingDefault
                        )

                        GuestCard(
                            name = guest.name,
                            label = guest.type,
                            imageUrl = guest.imageUrl,
                            isInvited = guest.isInvited,
                            type = GuestCardType.INVITE_ACTION,
                            labelColor = getGuestTypeColor(guest.type),
                            onCardClick = {
                                onGuestClick(guest)
                            },
                            onInviteClick = { onInviteToggle(guest.id) },
                            cardShape = itemShape,
                            lastUpdatedBy = guest.lastUpdatedBy,
                            lastUpdatedAt = guest.lastUpdatedAt
                        )
                    }
                }
            }
        }
    }
}