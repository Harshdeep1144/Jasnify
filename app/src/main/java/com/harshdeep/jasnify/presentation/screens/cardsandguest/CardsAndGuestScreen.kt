package com.harshdeep.jasnify.presentation.screens.cardsandguest

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddGuestInfoBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.AddGuestTypeBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.GuestInfoBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SelectGuestTypeBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.*
import com.harshdeep.jasnify.presentation.components.cards.GuestCard
import com.harshdeep.jasnify.presentation.components.cards.GuestCardType
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.theme.*

@Composable
fun CardsAndGuestScreen(
    onBackClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Guests") }
    var searchQuery by remember { mutableStateOf("") }
    
    val guests = remember {
        mutableStateListOf(
            Guest(id = "1", name = "Kavita N.", type = "College", isInvited = false, imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330"),
            Guest(id = "2", name = "Akriti R.", type = "Close Friend", isInvited = false, imageUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80", lastUpdatedBy = "Anand K.", lastUpdatedAt = "Aug 24, 2025, 01:04pm"),
            Guest(id = "3", name = "Tarun C.", type = "Office", isInvited = true),
            Guest(id = "4", name = "Lina A.", type = "Close Friend", isInvited = true, imageUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2"),
            Guest(id = "5", name = "Sameer N.", type = "College", isInvited = false, imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e"),
            Guest(id = "6", name = "Rahul M.", type = "Apartment", isInvited = false, imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"),
            Guest(id = "7", name = "Sanjay B.", type = "Family", isInvited = false)
        )
    }

    var showEmptyState by remember { mutableStateOf(false) }
    var selectedGuestForInfo by remember { mutableStateOf<Guest?>(null) }
    var selectedGuestForEdit by remember { mutableStateOf<Guest?>(null) }
    var showAddGuestSheet by remember { mutableStateOf(false) }
    var showAddTypeSheet by remember { mutableStateOf(false) }
    var showTypeFilterSheet by remember { mutableStateOf(false) }
    var selectedTypesFilter by remember { mutableStateOf<List<String>>(emptyList()) }

    val guestTypes = remember {
        mutableStateListOf(
            "Family", "Close Friend", "Office", "College", "Apartment"
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
        guestTypes.forEachIndexed { index, type ->
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

    val filteredGuests = remember(guests, searchQuery, selectedTypesFilter) {
        guests.filter { guest ->
            val matchesSearch = guest.name.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedTypesFilter.isEmpty() || selectedTypesFilter.contains(guest.type)
            matchesSearch && matchesType
        }
    }

    Scaffold(
        topBar = {
            CardsAndGuestsTopBar(
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                onBackClick = onBackClick,
                onMenuClick = onMenuClick
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TabContentTransition"
            ) { tab ->
                if (tab == "Guests") {
                    if (showEmptyState) {
                        GuestEmptyState(onAllowAccess = { showEmptyState = false })
                    } else {
                        GuestListContent(
                            guests = filteredGuests,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            onAddClick = { showAddGuestSheet = true },
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
                            getGuestTypeColor = ::getGuestTypeColor
                        )
                    }
                } else {
                    // Cards tab placeholder
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Cards Tab Content", style = JasnifyTheme.typography.displayMedium)
                    }
                }
            }
        }
    }

    if (selectedGuestForInfo != null) {
        GuestInfoBottomSheet(
            guest = selectedGuestForInfo!!,
            onDismiss = { selectedGuestForInfo = null },
            onEditClick = {
                selectedGuestForEdit = selectedGuestForInfo
                selectedGuestForInfo = null
            },
            onInviteClick = {
                val guestId = selectedGuestForInfo!!.id
                val index = guests.indexOfFirst { it.id == guestId }
                if (index != -1) {
                    guests[index] = guests[index].copy(isInvited = !guests[index].isInvited)
                    selectedGuestForInfo = guests[index]
                }
            }
        )
    }

    if (showAddGuestSheet || selectedGuestForEdit != null) {
        AddGuestInfoBottomSheet(
            onDismiss = {
                showAddGuestSheet = false
                selectedGuestForEdit = null
            },
            onAddClick = { newGuest ->
                if (selectedGuestForEdit != null) {
                    val index = guests.indexOfFirst { it.id == selectedGuestForEdit!!.id }
                    if (index != -1) {
                        guests[index] = newGuest.copy(id = selectedGuestForEdit!!.id)
                    }
                    selectedGuestForEdit = null
                } else {
                    guests.add(0, newGuest)
                    showAddGuestSheet = false
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
            onApply = { types ->
                selectedTypesFilter = types
                showTypeFilterSheet = false
            }
        )
    }
}

@Composable
fun CardsAndGuestsTopBar(
    selectedTab: String,
    onTabSelect: (String) -> Unit,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
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

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarTabText(
                    text = "Cards",
                    isSelected = selectedTab == "Cards",
                    onClick = { onTabSelect("Cards") }
                )
                TopBarTabText(
                    text = "Guests",
                    isSelected = selectedTab == "Guests",
                    onClick = { onTabSelect("Guests") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TopBarIconButton(
                icon = TopIcon.Predefined.MENU_VERTICAL,
                onClick = onMenuClick,
                backgroundStyle = ButtonBackground.OPAQUE
            )
        }
    }
}

@Composable
fun TopBarTabText(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Text(
            text = text,
            style = JasnifyTheme.typography.headingLarge,
            color = if (isSelected) Color(0xFF005858) else ContentSecondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .background(Color(0xFF005858), CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(7.dp))
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
    getGuestTypeColor: (String) -> Color
) {
    var expandedGuestId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
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
                modifier = Modifier.weight(1f)
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
                isSelected = false,
                onClick = { /* Handle filter */ },
                shapeStyle = ChipShapeStyle.Round,
                hasStroke = true
            )
            FilterChip(
                label = "Already invited",
                isSelected = false,
                onClick = { /* Handle filter */ },
                shapeStyle = ChipShapeStyle.Round,
                hasStroke = true
            )
        }

        // Guest List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(guests, key = { it.id }) { guest ->
                val isExpanded = expandedGuestId == guest.id
                GuestCard(
                    name = guest.name,
                    label = guest.type,
                    imageUrl = guest.imageUrl,
                    isInvited = guest.isInvited,
                    type = if (isExpanded) GuestCardType.VIEW_DETAILS else GuestCardType.INVITE_ACTION,
                    isExpanded = isExpanded,
                    lastUpdatedBy = guest.lastUpdatedBy,
                    lastUpdatedAt = guest.lastUpdatedAt,
                    labelColor = getGuestTypeColor(guest.type),
                    onCardClick = {
                        expandedGuestId = if (isExpanded) null else guest.id
                    },
                    onViewDetailsClick = { onViewDetails(guest) },
                    onInviteClick = { onInviteToggle(guest.id) }
                )
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

@Preview(showBackground = true)
@Composable
fun PreviewCardsAndGuestScreen() {
    JasnifyTheme {
        CardsAndGuestScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGuestEmptyState() {
    JasnifyTheme {
        GuestEmptyState(onAllowAccess = {})
    }
}
