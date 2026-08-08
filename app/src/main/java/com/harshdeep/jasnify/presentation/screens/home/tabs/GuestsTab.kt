package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Contact
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.presentation.components.bottomdrawer.*
import com.harshdeep.jasnify.presentation.components.buttons.*
import com.harshdeep.jasnify.presentation.components.cards.GuestCard
import com.harshdeep.jasnify.presentation.components.cards.GuestCardType
import com.harshdeep.jasnify.presentation.components.cards.GuestTypeCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.theme.*
import com.harshdeep.jasnify.util.ContactHelper
import com.harshdeep.jasnify.util.SearchHistoryManager

enum class GuestsView {
    MAIN,
    MANAGE_GUEST_TYPES,
    GUEST_TYPE_DETAIL
}

@Composable
fun GuestsTab(
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val searchHistoryManager = remember { SearchHistoryManager(context) }
    var currentView by remember { mutableStateOf(GuestsView.MAIN) }
    var selectedGuestTypeForDetail by remember { mutableStateOf<String?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val guests = remember { mutableStateListOf<Guest>() }

    var showEmptyState by remember { mutableStateOf(true) }
    var selectedGuestForInfo by remember { mutableStateOf<Guest?>(null) }
    var selectedGuestForEdit by remember { mutableStateOf<Guest?>(null) }
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
                    showMenuSheet
        }
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

    // Pre-assign colors for existing types
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
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) -> {
                phoneContacts = ContactHelper.fetchContacts(context)
                showContactPicker = true
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    // Handle back button
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
                            topBar = {
                                if (!isSearchActive) {
                                    GuestsTopBar(
                                        onBackClick = {
                                            if (isMultiSelectMode) {
                                                isMultiSelectMode = false
                                                selectedGuestIds.clear()
                                            } else {
                                                onBackClick()
                                            }
                                        },
                                        onMenuClick = { showMenuSheet = true },
                                        isMultiSelectMode = isMultiSelectMode,
                                        selectedCount = selectedGuestIds.size,
                                        onDeleteSelected = {
                                            guests.removeAll { it.id in selectedGuestIds }
                                            selectedGuestIds.clear()
                                            isMultiSelectMode = false
                                            if (guests.isEmpty()) showEmptyState = true
                                        }
                                    )
                                }
                            },
                            floatingActionButton = {
                                if (isMultiSelectMode && selectedGuestIds.isNotEmpty() && inviteFilter != "Already invited") {
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
                                        leadingIcon = painterResource(id = R.drawable.ic_user_profile),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        size = ButtonSize.Large,
                                        containerColor = Color.Black,
                                        contentColor = Color.White,
                                        shapeStyle = ButtonShapeStyle.Round
                                    )
                                }
                            },
                            floatingActionButtonPosition = FabPosition.Center,
                            containerColor = BackgroundPrimary
                        ) { paddingValues ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                if (isSearchActive) {
                                    GuestSearchContent(
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { searchQuery = it },
                                        onBackClick = {
                                            isSearchActive = false
                                            searchQuery = ""
                                        },
                                        onSearchAction = {
                                            searchHistoryManager.addSearch(it)
                                        },
                                        recentSearches = searchHistoryManager.getRecentSearches(),
                                        onClearRecent = { searchHistoryManager.clearAll() },
                                        onRemoveRecent = { searchHistoryManager.removeSearch(it) },
                                        searchResults = filteredGuests,
                                        onGuestClick = { guest ->
                                            selectedGuestForInfo = guest
                                            searchHistoryManager.addSearch(searchQuery)
                                        },
                                        onAddGuestClick = onAddGuestClick,
                                        getGuestTypeColor = ::getGuestTypeColor,
                                        onInviteToggle = { guestId ->
                                            val index = guests.indexOfFirst { it.id == guestId }
                                            if (index != -1) {
                                                guests[index] = guests[index].copy(isInvited = !guests[index].isInvited)
                                            }
                                        }
                                    )
                                } else {
                                    if (guests.isEmpty() && showEmptyState) {
                                        GuestEmptyState(onAllowAccess = onAddGuestClick)
                                    } else {
                                        GuestListContent(
                                            guests = filteredGuests,
                                            searchQuery = searchQuery,
                                            onSearchQueryChange = { searchQuery = it },
                                            onAddClick = onAddGuestClick,
                                            onInviteToggle = { guestId ->
                                                val index = guests.indexOfFirst { it.id == guestId }
                                                if (index != -1) {
                                                    guests[index] = guests[index].copy(isInvited = !guests[index].isInvited)
                                                }
                                            },
                                            onViewDetails = { guest ->
                                                selectedGuestForInfo = guest
                                            },
                                            onFilterClick = { showTypeFilterSheet = true },
                                            selectedTypesFilter = selectedTypesFilter,
                                            getGuestTypeColor = ::getGuestTypeColor,
                                            inviteFilter = inviteFilter,
                                            onInviteFilterChange = { inviteFilter = it },
                                            isMultiSelectMode = isMultiSelectMode,
                                            selectedGuestIds = selectedGuestIds,
                                            onGuestSelectToggle = { id, selected ->
                                                if (selected) selectedGuestIds.add(id) else selectedGuestIds.remove(id)
                                            },
                                            onSearchClick = { isSearchActive = true }
                                        )
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
                            onInviteToggle = { guestId ->
                                val index = guests.indexOfFirst { it.id == guestId }
                                if (index != -1) {
                                    guests[index] = guests[index].copy(isInvited = !guests[index].isInvited)
                                }
                            }
                        )
                    }
                }
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
                guests.removeAll { it.id == selectedGuestForInfo?.id }
                selectedGuestForInfo = null
                if (guests.isEmpty()) showEmptyState = true
            }
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
                    guests.add(0, newGuest)
                    showAddGuestSheet = false
                    showEmptyState = false
                }
            },
            onAddNewTypeClick = { showAddTypeSheet = true },
            guestTypes = guestTypes.map { com.harshdeep.jasnify.domain.model.GuestType(name = it) },
            initialGuest = selectedGuestForEdit
        )
    }

    if (showAddTypeSheet) {
        AddGuestTypeBottomSheet(
            onDismiss = { showAddTypeSheet = false },
            onProgress = { sheetMotionProgress = it },
            onAddType = { newType ->
                if (!guestTypes.contains(newType)) {
                    guestTypes.add(newType)
                }
                showAddTypeSheet = false
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
            onContactsSelected = { selectedContacts, includePhoneNo ->
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
                    }
                }
                showEmptyState = false
                showContactPicker = false
            }
        )
    }

    if (showMenuSheet) {
        val menuItems = listOf(
            listOf(
                MenuSheetActionItem(
                    text = "Add Guests",
                    icon = painterResource(id = R.drawable.ic_plus),
                    iconPlacement = IconPlacement.Top,
                    onClick = {
                        showMenuSheet = false
                        onAddGuestClick()
                    }
                ),
                MenuSheetActionItem(
                    text = "Multi-Select",
                    icon = painterResource(id = R.drawable.ic_list),
                    iconPlacement = IconPlacement.Top,
                    onClick = {
                        showMenuSheet = false
                        isMultiSelectMode = true
                        inviteFilter = "Yet to invite"
                    }
                )
            ),
            listOf(
                MenuSheetActionItem(
                    text = "Manage Guest Type",
                    icon = painterResource(id = R.drawable.ic_book),
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
                    icon = painterResource(id = R.drawable.ic_user_profile),
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

@Composable
fun GuestsTopBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    isMultiSelectMode: Boolean = false,
    selectedCount: Int = 0,
    onDeleteSelected: () -> Unit = {}
) {
    Surface(
        color = BackgroundPrimary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopBarIconButton(
                icon = TopIcon.Predefined.BACK,
                onClick = onBackClick,
                backgroundStyle = ButtonBackground.OPAQUE
            )

            if (isMultiSelectMode) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "$selectedCount selected",
                    style = JasnifyTheme.typography.headingLarge,
                    color = ContentPrimary,
                    modifier = Modifier.weight(1f)
                )
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painterResource(id = R.drawable.ic_delete)),
                    onClick = onDeleteSelected,
                    backgroundStyle = ButtonBackground.OPAQUE
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Guests",
                    style = JasnifyTheme.typography.headingLarge,
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.weight(1f))

                TopBarIconButton(
                    icon = TopIcon.Predefined.MENU_VERTICAL,
                    onClick = onMenuClick,
                    backgroundStyle = ButtonBackground.OPAQUE
                )
            }
        }
    }
}

@Composable
fun GuestListContent(
    guests: List<Guest>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onInviteToggle: (String) -> Unit,
    onViewDetails: (Guest) -> Unit,
    onFilterClick: () -> Unit,
    selectedTypesFilter: List<String>,
    getGuestTypeColor: (String) -> Color,
    inviteFilter: String?,
    onInviteFilterChange: (String?) -> Unit,
    isMultiSelectMode: Boolean = false,
    selectedGuestIds: List<String> = emptyList(),
    onGuestSelectToggle: (String, Boolean) -> Unit = { _, _ -> },
    onSearchClick: () -> Unit = {}
) {
    var expandedGuestId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!isMultiSelectMode) {
            // Search and Add row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomSearchBar(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = "Search Guests",
                    modifier = Modifier.weight(1f),
                    onActiveChange = { if (it) onSearchClick() }
                )

                CustomTextButton(
                    onClick = onAddClick,
                    text = "Add",
                    leadingIcon = painterResource(id = R.drawable.ic_plus),
                    size = ButtonSize.Medium,
                    containerColor = Color(0xFF5D7371),
                    contentColor = Color.White,
                    shapeStyle = ButtonShapeStyle.Round
                )
            }
        }

        // Filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                label = when {
                    selectedTypesFilter.isEmpty() -> "Guest Type"
                    selectedTypesFilter.size == 1 -> selectedTypesFilter.first()
                    else -> "Guest Types (${selectedTypesFilter.size})"
                },
                hasDropdown = true,
                isSelected = selectedTypesFilter.isNotEmpty(),
                onClick = onFilterClick,
                shapeStyle = ChipShapeStyle.Round,
                hasStroke = true
            )
            FilterChip(
                label = "Yet to invite",
                isSelected = inviteFilter == "Yet to invite",
                onClick = {
                    onInviteFilterChange(if (inviteFilter == "Yet to invite") null else "Yet to invite")
                },
                shapeStyle = ChipShapeStyle.Round,
                hasStroke = true
            )
            FilterChip(
                label = "Already invited",
                isSelected = inviteFilter == "Already invited",
                onClick = {
                    onInviteFilterChange(if (inviteFilter == "Already invited") null else "Already invited")
                },
                shapeStyle = ChipShapeStyle.Round,
                hasStroke = true
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = if (isMultiSelectMode) 80.dp else 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (guests.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No guests match your search/filter.",
                            style = JasnifyTheme.typography.bodyLarge,
                            color = ContentSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(guests, key = { it.id }) { guest ->
                    val isExpanded = expandedGuestId == guest.id
                    val isSelected = selectedGuestIds.contains(guest.id)

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
                                onGuestSelectToggle(guest.id, !isSelected)
                            } else {
                                expandedGuestId = if (isExpanded) null else guest.id
                            }
                        },
                        onSelectToggle = { selected ->
                            onGuestSelectToggle(guest.id, selected)
                        },
                        onViewDetailsClick = { onViewDetails(guest) },
                        onInviteClick = { onInviteToggle(guest.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun GuestEmptyState(onAllowAccess: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_book),
            contentDescription = null,
            tint = ContentTertiary,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Nothing Found",
            style = JasnifyTheme.typography.displayMedium,
            color = ContentSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We need contacts access to show contacts.",
            style = JasnifyTheme.typography.bodyLarge,
            color = ContentTertiary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomTextButton(
            onClick = onAllowAccess,
            text = "Allow Contacts Access",
            size = ButtonSize.Medium,
            containerColor = Color.Black,
            contentColor = Color.White,
            shapeStyle = ButtonShapeStyle.Round,
            modifier = Modifier.fillMaxWidth()
        )
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
            Surface(
                color = BackgroundPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TopBarIconButton(
                        icon = TopIcon.Predefined.BACK,
                        onClick = onBackClick,
                        backgroundStyle = ButtonBackground.OPAQUE
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Guest Type",
                        style = JasnifyTheme.typography.headingLarge,
                        color = ContentPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(44.dp))
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                CustomTextButton(
                    onClick = onAddTypeClick,
                    text = "Add a Guest Type",
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.Large,
                    containerColor = Color(0xFF5D7371),
                    contentColor = Color.White,
                    shapeStyle = ButtonShapeStyle.Round
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
    }
}

@Composable
fun GuestTypeDetailScreen(
    typeName: String,
    guests: List<Guest>,
    onBackClick: () -> Unit,
    onInviteToggle: (String) -> Unit
) {
    var activeFilter by remember { mutableStateOf("Everyone") }

    val filteredGuests = remember(guests, activeFilter) {
        when (activeFilter) {
            "Yet to invite" -> guests.filter { !it.isInvited }
            "Already invited" -> guests.filter { it.isInvited }
            else -> guests
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = BackgroundPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopBarIconButton(
                            icon = TopIcon.Predefined.BACK,
                            onClick = onBackClick,
                            backgroundStyle = ButtonBackground.OPAQUE
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = typeName,
                            style = JasnifyTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = ContentPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit_pen),
                            contentDescription = "Edit Type",
                            modifier = Modifier.size(20.dp),
                            tint = ContentPrimary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            label = "Everyone",
                            isSelected = activeFilter == "Everyone",
                            onClick = { activeFilter = "Everyone" },
                            shapeStyle = ChipShapeStyle.Round,
                            hasStroke = true
                        )
                        FilterChip(
                            label = "Yet to invite",
                            isSelected = activeFilter == "Yet to invite",
                            onClick = { activeFilter = "Yet to invite" },
                            shapeStyle = ChipShapeStyle.Round,
                            hasStroke = true
                        )
                        FilterChip(
                            label = "Already invited",
                            isSelected = activeFilter == "Already invited",
                            onClick = { activeFilter = "Already invited" },
                            shapeStyle = ChipShapeStyle.Round,
                            hasStroke = true
                        )
                    }
                }
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredGuests, key = { it.id }) { guest ->
                GuestCard(
                    name = guest.name,
                    label = guest.type,
                    imageUrl = guest.imageUrl,
                    isInvited = guest.isInvited,
                    type = GuestCardType.INVITE_ACTION,
                    labelColor = Color(0xFF635994),
                    onInviteClick = { onInviteToggle(guest.id) }
                )
            }
        }
    }
}

@Composable
fun GuestSearchContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchAction: (String) -> Unit,
    recentSearches: List<String>,
    onClearRecent: () -> Unit,
    onRemoveRecent: (String) -> Unit,
    searchResults: List<Guest>,
    onGuestClick: (Guest) -> Unit,
    onAddGuestClick: () -> Unit,
    getGuestTypeColor: (String) -> Color,
    onInviteToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
    ) {
        // Search Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopBarIconButton(
                icon = TopIcon.Predefined.BACK,
                onClick = onBackClick,
                backgroundStyle = ButtonBackground.OPAQUE
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search Guests", style = JasnifyTheme.typography.bodyLarge) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = SurfaceSecondary,
                    focusedContainerColor = SurfaceSecondary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = ContentSecondary)
                        }
                    }
                },
                singleLine = true,
                textStyle = JasnifyTheme.typography.bodyLarge
            )
        }

        if (searchQuery.isEmpty()) {
            // Recent Searches
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recent Searches",
                            style = JasnifyTheme.typography.headingMedium,
                            color = ContentPrimary
                        )
                    }
                    Text(
                        text = "Clear all",
                        style = JasnifyTheme.typography.labelLarge,
                        color = Color(0xFF005858),
                        modifier = Modifier.clickable { onClearRecent() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentSearches) { search ->
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
                                onSearchAction(search)
                            }
                        )
                    }
                }
            }
        } else {
            // Search Results
            if (searchResults.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_cross),
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No guests found",
                        style = JasnifyTheme.typography.displaySmall,
                        color = ContentSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    CustomTextButton(
                        onClick = onAddGuestClick,
                        text = "Add New Guests",
                        leadingIcon = painterResource(id = R.drawable.ic_plus),
                        size = ButtonSize.Medium,
                        containerColor = Color(0xFF5D7371),
                        contentColor = Color.White,
                        shapeStyle = ButtonShapeStyle.Round
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults, key = { it.id }) { guest ->
                        GuestCard(
                            name = guest.name,
                            label = guest.type,
                            imageUrl = guest.imageUrl,
                            isInvited = guest.isInvited,
                            type = GuestCardType.INVITE_ACTION,
                            labelColor = getGuestTypeColor(guest.type),
                            onCardClick = {
                                onGuestClick(guest)
                                onSearchAction(searchQuery)
                            },
                            onInviteClick = { onInviteToggle(guest.id) }
                        )
                    }
                }
            }
        }
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
        GuestEmptyState(onAllowAccess = {})
    }
}
