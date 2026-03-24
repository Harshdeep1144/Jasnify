package com.harshdeep.jasnify.presentation.screens.venues

import com.harshdeep.jasnify.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardData
import com.harshdeep.jasnify.presentation.components.cards.VendorCardFull
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.theme.CloudWhisper
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.LightSkyBlue
import com.harshdeep.jasnify.theme.PaleLavender
import com.harshdeep.jasnify.theme.SoftMint
import com.harshdeep.jasnify.theme.SoftPeach
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

data class TimelineEvent(
    val date: String,
    val event: String,
    val venues: List<VendorCardData>
)

@Composable
fun VenueScreen(
    onVenueClick: (VendorCardData) -> Unit,
    onLocationSelectorClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val bottomTabs = listOf(
        TabItem("Explore", "explore", badgeCount = 24),
        TabItem("Saved", "saved", badgeCount = 3)
    )
    var selectedTab by remember { mutableStateOf("explore") }

    val viewOptions = listOf("By Timeline", "By List")
    var selectedViewType by remember { mutableStateOf(viewOptions[0]) }

    // Mock Timeline Data
    val timelineEvents = remember {
        listOf(
            TimelineEvent("09th Sept, 2025", "Mehendi Ceremony", MockData.sampleVenues1.take(4)),
            TimelineEvent("12th Sept, 2025", "The Wedding Day", MockData.sampleVenues1.drop(4)),
            TimelineEvent("15th Sept, 2025", "Reception Dinner", MockData.sampleVenues1.drop(2).take(3))
        )
    }

    // For Search Bar
    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.Transparent)
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Venue",
                    onBackClick = { onBackClick() },
                    onMenuClick = if (!isSearchActive) {
                        { /* Handle menu */ }
                    } else null,
                    isLargeTitle = true
                )

                // Show Segmented Control ONLY when "Saved" tab is active
                if (selectedTab == "saved") {
                    IosSegmentedControl(
                        options = viewOptions,
                        selectedOption = selectedViewType,
                        onOptionSelected = { selectedViewType = it },
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        bottomBar = {
            if(!isSearchActive){
                BottomTab(
                    items = bottomTabs,
                    selectedValue = selectedTab,
                    onItemSelected = { selectedTab = it }
                )
            }
        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- EXPLORE TAB CONTENT ---
            if (selectedTab == "explore") {

                item {
                    if(!isSearchActive){
                        Spacer(Modifier.height(12.dp))
                        LocationSelectorPill(location = "Patna, Bihar", onLocationSelectorClick)
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomSearchBar(
                            value = text,
                            onValueChange = { text = it },
                            onActiveChange = { active ->
                                isSearchActive = active
                            },
                            modifier = Modifier.weight(1f)
                        )

                        if(!isSearchActive){
                            Spacer(Modifier.width(8.dp))
                            FilterButton(
                                onClick = {}
                            )
                        }
                    }
                }

                if(!isSearchActive){
                    items(MockData.sampleVenues1) { venue ->
                        VendorCardFull(
                            vendor = venue,
                            onBookCallClick = {},
                            onFavoriteToggle = {},
                            onCardClick = {},
                            onChatClick = {},
                        )
                    }
                }
                else{
                    item{
                        RecentSearchesSection(
                            onVenueClick = {},
                            recentVenues = MockData.sampleVenues1,
                            onClearAll = {}
                        )
                    }
                }
                item{Spacer(Modifier.height(6.dp))}
            }

            // --- SAVED TAB CONTENT ---
            else {
                if (selectedViewType == "By Timeline") {
                    items(timelineEvents) { timelineItem ->
                        TimelineSection(
                            date = timelineItem.date,
                            event = timelineItem.event,
                            venues = timelineItem.venues,
                            onVenueClick = { /* Handle click */ }
                        )
                    }
                } else {
                    // Saved "By List" View
                    items(MockData.sampleVenues1.take(3)) { venue ->
                        VendorCardFull(
                            vendor = venue,
                            onBookCallClick = {},
                            onFavoriteToggle = {},
                            onCardClick = {},
                            onChatClick = {},
                        )
                    }
                }
                item{Spacer(Modifier.height(6.dp))}
            }
        }
    }
}




// --- SAVED TAB CONTENT ---

@Composable
fun TimelineSection(
    date: String,
    event: String,
    venues: List<VendorCardData>,
    onVenueClick: (VendorCardData) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val accentColors = listOf(CloudWhisper, SoftMint, PaleLavender, LightSkyBlue, SoftPeach)
    val randomBackgroundColor = remember { accentColors.random() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(20.dp, 0f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(20.dp, 0f)
            )
            .background(randomBackgroundColor),
    ) {
        TimelineHeader(date = date, event = event)

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(venues) { venue ->
                VendorCardCompact(
                    vendor = venue,
                    removeBg = true,
                    onCardClick = { onVenueClick(venue) }
                )
            }
        }

        // Carousel Indicator
        CarouselIndicator(
            listState = listState,
            totalItems = venues.size,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )
    }
}

// ---------- Helper Functions ----------------

@Composable
fun FilterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(100, 0f)
            )
            .clip(SquircleShape(100, 0f))
            .background(color = SurfaceSecondary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_filter),
            contentDescription = "Filter Button",
            tint = ContentSecondary,
            modifier = Modifier.size(28.dp)
        )
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
                val lastItem = visibleItems.last()
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

    // Track
    Box(
        modifier = modifier
            .width(trackWidth)
            .height(4.dp)
            .background(
                color = SurfaceSecondary,
                shape = CircleShape
            )
    ) {
        // Sliding Thumb
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(thumbWidth)
                // The thumb can move a maximum distance of (trackWidth - thumbWidth)
                .offset(x = (trackWidth - thumbWidth) * progress)
                .background(
                    color = ContentBrandDark,
                    shape = CircleShape
                )
        )
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
            .clip(SquircleShape(16.dp, 0f))
            .clickable {
                onLocationSelectorClick()
            },
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
                color = ContentBrandDark
            )
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = ContentBrandDark,
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 4.dp)
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

@Composable
fun TimelineHeader(date: String, event: String) {
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
            Spacer(Modifier.height(4.dp))
            Text(
                text = event,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentBrandDark,
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Options",
            tint = ContentPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVenueScreen() {

    JasnifyTheme {
        VenueScreen(
            onVenueClick = {},
            onLocationSelectorClick = {},
            onBackClick = {}
        )
    }
}