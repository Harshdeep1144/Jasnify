package com.harshdeep.jasnify.presentation.screens.main.tabs.vendors

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.edit
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.domain.model.SavedVendor
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardFull
import com.harshdeep.jasnify.presentation.components.carousels.VendorCarousel
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.BottomTab
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.TabItem
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.TimelineSection
import com.harshdeep.jasnify.presentation.components.sections.TrendingAiSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.components.states.EmptyState
import com.harshdeep.jasnify.presentation.components.states.SearchSuggestionItem
import com.harshdeep.jasnify.presentation.components.states.StandaloneEmptyState
import com.harshdeep.jasnify.presentation.navigation.ScreenTransitions
import com.harshdeep.jasnify.theme.BackgroundPrimary

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VendorCategoryDetailContent(
    category: VendorCategoryItem,
    allVendors: List<Vendor>,
    savedVendorsForCategory: List<SavedVendor>,
    selectedCity: String,
    onBackClick: () -> Unit,
    onLocationClick: () -> Unit,
    onMenuClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    vendorSavedDestinations: Map<String, String>,
    timelineEvents: List<TimelineEvent>,
    selectedTab: String,
    onSelectedTabChange: (String) -> Unit,
    selectedViewType: String,
    onSelectedViewTypeChange: (String) -> Unit,
    isLoading: Boolean = false,
    onTimelineSeeAll: (TimelineEvent) -> Unit = { _ -> },
    onOfferClick: (Vendor) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState(),
    isBottomBarVisible: Boolean = true
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current

    val topBarMaxScrollPx = with(density) { 56.dp.toPx() }
    val topBarScrollProgress by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (listState.firstVisibleItemScrollOffset / topBarMaxScrollPx).coerceIn(0f, 1f)
            }
        }
    }

    val viewOptions = listOf("By Timeline", "All Saved")
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var recentSearchesNames by remember { mutableStateOf(getCategoryRecentSearches(context, category.name)) }

    val filters = listOf("Most Relevant", "Top-Rated", "Price: Highest First", "Price: Lowest First")
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var appliedFilterOptions by remember { mutableStateOf(setOf<String>()) }

    val filteredVendors = remember(allVendors, searchQuery, selectedFilterIndex, appliedFilterOptions, vendorSavedDestinations) {
        val baseList = allVendors.ifEmpty { MockData.sampleVendors.filter { it.category == category.name } }
        var result = baseList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.locality.contains(searchQuery, ignoreCase = true) ||
                    it.city.contains(searchQuery, ignoreCase = true)
        }

        result = when (selectedFilterIndex) {
            1 -> result.sortedByDescending { it.rating }
            2 -> result.sortedByDescending { parsePrice(it.priceStartsFrom) }
            3 -> result.sortedBy { parsePrice(it.priceStartsFrom) }
            else -> result
        }.distinctBy { it.id }

        result.map { it.copy(favorite = vendorSavedDestinations.containsKey("${it.name}-${it.category}")) }
    }

    LaunchedEffect(isSearchActive) {
        if (!isSearchActive && searchQuery.isNotEmpty() && filteredVendors.isEmpty()) {
            searchQuery = ""
        }
    }

    var internalBottomBarVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }
    val categoryDetailNestedScrollConnection = remember(selectedTab, listState, gridState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                val canScroll = if (selectedTab == "explore") {
                    listState.canScrollForward || listState.canScrollBackward
                } else {
                    gridState.canScrollForward || gridState.canScrollBackward
                }
                if (!canScroll) return Offset.Zero

                if (delta > 0) {
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) {
                    if (scrollAccumulator > 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                }

                if (scrollAccumulator > 150f && !internalBottomBarVisible) {
                    internalBottomBarVisible = true
                    scrollAccumulator = 0f
                } else if (scrollAccumulator < -150f && internalBottomBarVisible) {
                    internalBottomBarVisible = false
                    scrollAccumulator = 0f
                }
                return Offset.Zero
            }
        }
    }

    val bottomTabs = remember(allVendors.size, savedVendorsForCategory.size) {
        listOf(
            TabItem("Explore", "explore", badgeCount = allVendors.size),
            TabItem("Saved", "saved", badgeCount = savedVendorsForCategory.size)
        )
    }

    val recentVendorsList = remember(recentSearchesNames, allVendors) {
        recentSearchesNames.mapNotNull { name ->
            allVendors.find { it.name == name }
        }
    }

    var searchBarOnlyHeightPx by remember { mutableIntStateOf(0) }

    // Dynamically calculate when inline filter chips reach the bottom edge of sticky search bar
    val isChipsSticky by remember {
        derivedStateOf {
            val inlineFilterItem = listState.layoutInfo.visibleItemsInfo.find { it.key == "filters_inline_item" }
            if (inlineFilterItem != null) {
                val threshold = if (searchBarOnlyHeightPx > 0) searchBarOnlyHeightPx else 150
                inlineFilterItem.offset <= threshold
            } else {
                listState.firstVisibleItemIndex >= 4
            }
        }
    }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchQuery = ""
        focusManager.clearFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .nestedScroll(categoryDetailNestedScrollConnection)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Pinned status bar background overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                        .background(BackgroundPrimary)
                        .zIndex(100f)
                )

                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val isSaved = targetState == "saved"
                        if (isSaved) {
                            ScreenTransitions.SlideInFromRightTransition togetherWith ScreenTransitions.SlideOutToLeftTransition
                        } else {
                            ScreenTransitions.SlideInFromLeftTransition togetherWith ScreenTransitions.SlideOutToRightTransition
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "CategoryTabTransition"
                ) { currentTab ->
                    if (currentTab == "explore") {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding(),
                            state = listState
                        ) {
                            item(key = "top_bar") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .graphicsLayer {
                                            alpha = (1f - topBarScrollProgress).coerceIn(0f, 1f)
                                            translationY = -topBarScrollProgress * 30f
                                        }
                                ) {
                                    AnimatedContent(
                                        targetState = isSearchActive,
                                        transitionSpec = {
                                            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                                        },
                                        label = "CategoryTopBarSearchTransition"
                                    ) { active ->
                                        CustomTopBar(
                                            title = if (active) "Search ${category.name}" else category.name,
                                            subtitle = if (active) null else selectedCity,
                                            onBackClick = if (active) {
                                                {
                                                    isSearchActive = false
                                                    searchQuery = ""
                                                    focusManager.clearFocus()
                                                }
                                            } else onBackClick,
                                            backIcon = if (active) TopIcon.Predefined.DOWN else TopIcon.Predefined.BACK,
                                            onMenuClick = if (active) null else onMenuClick,
                                            onDropdownClick = if (active) null else onLocationClick,
                                            buttonStyle = ButtonBackground.OPAQUE
                                        )
                                    }
                                }
                            }

                            stickyHeader(key = "search_and_sticky_filters_header") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BackgroundPrimary)
                                        .zIndex(10f)
                                        .padding(bottom = 4.dp)
                                        .onGloballyPositioned { coordinates ->
                                            if (!isChipsSticky) {
                                                searchBarOnlyHeightPx = coordinates.size.height
                                            }
                                        }
                                ) {
                                    Spacer(Modifier.height(12.dp))
                                    CustomSearchBar(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        onActiveChange = { isSearchActive = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        isAiSearch = true,
                                        placeholder = "Search ${category.name}"
                                    )

                                    if (isChipsSticky && !isSearchActive && searchQuery.isEmpty()) {
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            itemsIndexed(
                                                items = filters,
                                                key = { _, filter -> "sticky_filter_$filter" }
                                            ) { index, filter ->
                                                val isSelected = selectedFilterIndex == index
                                                FilterChip(
                                                    label = filter,
                                                    isSelected = isSelected,
                                                    hasStroke = true,
                                                    shapeStyle = ChipShapeStyle.Round,
                                                    onClick = { selectedFilterIndex = index }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (isSearchActive && searchQuery.isNotEmpty()) {
                                if (filteredVendors.isEmpty()) {
                                    item(key = "empty_category_search") {
                                        EmptyState(message = "No matches for \"$searchQuery\"")
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                } else {
                                    items(
                                        items = filteredVendors.take(8),
                                        key = { "cat_search_${it.id}" }
                                    ) { vendor ->
                                        SearchSuggestionItem(
                                            title = vendor.name,
                                            subtitle = "${vendor.locality}, ${vendor.city}",
                                            onClick = {
                                                onVendorClick(vendor)
                                                focusManager.clearFocus()
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            } else if (!isSearchActive && searchQuery.isNotEmpty()) {
                                items(
                                    items = filteredVendors,
                                    key = { "cat_filtered_${it.id}" }
                                ) { vendor ->
                                    VendorCardFull(
                                        vendor = vendor,
                                        onCardClick = {
                                            saveCategoryRecentSearch(context, category.name, vendor.name)
                                            recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                            onVendorClick(vendor)
                                        },
                                        onFavoriteToggle = { onFavoriteToggle(vendor) },
                                        onOfferClick = { onOfferClick(vendor) },
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            } else if (!isSearchActive) {
                                item(key = "top_rated_carousel") {
                                    VendorCarousel(
                                        title = "Top-Rated ${category.name}",
                                        vendors = filteredVendors.filter { it.rating >= 4.5 },
                                        isLoading = isLoading,
                                        onVendorClick = { vendor ->
                                            saveCategoryRecentSearch(context, category.name, vendor.name)
                                            recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                            onVendorClick(vendor)
                                        },
                                        onFavoriteToggle = onFavoriteToggle,
                                        onOfferClick = { onOfferClick(it) }
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                item(key = "cat_explore_divider") {
                                    OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                item(key = "filters_inline_item") {
                                    if (!isChipsSticky) {
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            itemsIndexed(
                                                items = filters,
                                                key = { _, filter -> "inline_filter_$filter" }
                                            ) { index, filter ->
                                                val isSelected = selectedFilterIndex == index
                                                FilterChip(
                                                    label = filter,
                                                    isSelected = isSelected,
                                                    hasStroke = true,
                                                    shapeStyle = ChipShapeStyle.Round,
                                                    onClick = { selectedFilterIndex = index }
                                                )
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(44.dp))
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                if (isLoading && filteredVendors.isEmpty()) {
                                    items(5, key = { "loading_$it" }) {
                                        VendorCardFull(
                                            vendor = Vendor(),
                                            isLoading = true,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                } else if (filteredVendors.isEmpty()) {
                                    item(key = "no_vendors_found") {
                                        EmptyState(message = "No vendors found in this category")
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                } else {
                                    items(
                                        items = filteredVendors,
                                        key = { it.id }
                                    ) { vendor ->
                                        VendorCardFull(
                                            vendor = vendor,
                                            onCardClick = {
                                                saveCategoryRecentSearch(context, category.name, vendor.name)
                                                recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                                onVendorClick(vendor)
                                            },
                                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                                            onOfferClick = { onOfferClick(vendor) },
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }

                                item(key = "cat_footer") {
                                    FooterJansify()
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            } else {
                                item(key = "cat_trending") {
                                    TrendingAiSearchesSection(onTrendingClick = { query ->
                                        searchQuery = query
                                        focusManager.clearFocus()
                                    })
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                if (recentVendorsList.isNotEmpty()) {
                                    item(key = "cat_recent_searches") {
                                        RecentSearchesSection(
                                            recentVendors = recentVendorsList,
                                            onVendorClick = { vendor ->
                                                saveCategoryRecentSearch(context, category.name, vendor.name)
                                                recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                                onVendorClick(vendor)
                                            },
                                            onRemoveVendor = { vendor ->
                                                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                                val current = getCategoryRecentSearches(context, category.name).toMutableList()
                                                current.remove(vendor.name)
                                                prefs.edit { putString("${KEY_RECENT_SEARCHES}_${category.name}", current.joinToString("|||")) }
                                                recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                                item(key = "cat_bottom_spacer") { Spacer(Modifier.height(24.dp)) }
                                item(key = "cat_extra_spacer") { Spacer(Modifier.height(100.dp)) }
                            }
                        }
                    } else {
                        val savedVendorsList = remember(savedVendorsForCategory, allVendors) {
                            allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name } }.map { it.copy(favorite = true) }
                        }

                        val savedTimelineEvents = remember(savedVendorsForCategory, timelineEvents, allVendors) {
                            val list = mutableListOf<TimelineEvent>()
                            val defaultSaved = allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name && it.destination == "mysaved" } }.map { it.copy(favorite = true) }

                            if (defaultSaved.isNotEmpty()) {
                                list.add(TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List", venues = emptyList()))
                            }

                            timelineEvents.forEach { event ->
                                val eventVendors = allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name && it.destination == event.id } }.map { it.copy(favorite = true) }
                                if (eventVendors.isNotEmpty()) {
                                    list.add(event.copy(venues = emptyList()))
                                }
                            }
                            list
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .padding(horizontal = 12.dp),
                            state = gridState,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(span = { GridItemSpan(2) }, key = "saved_top_bar") {
                                CustomTopBar(
                                    title = category.name,
                                    subtitle = selectedCity,
                                    onBackClick = onBackClick,
                                    backIcon = TopIcon.Predefined.BACK,
                                    onMenuClick = onMenuClick,
                                    onDropdownClick = onLocationClick,
                                    buttonStyle = ButtonBackground.OPAQUE
                                )
                            }

                            item(span = { GridItemSpan(2) }) {
                                IosSegmentedControl(
                                    options = viewOptions,
                                    selectedOption = selectedViewType,
                                    onOptionSelected = onSelectedViewTypeChange,
                                    modifier = Modifier.padding(top = 12.dp).height(44.dp)
                                )
                            }

                            if (selectedViewType == "All Saved") {
                                if (isLoading) {
                                    items(6) {
                                        VendorCardCompact(
                                            vendor = Vendor(),
                                            isLoading = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            compactCardSize = CompactCardSize.SMALL
                                        )
                                    }
                                } else if (savedVendorsList.isEmpty()) {
                                    item(span = { GridItemSpan(2) }) {
                                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                                    }
                                } else {
                                    items(
                                        items = savedVendorsList,
                                        key = { it.id }
                                    ) { vendor ->
                                        VendorCardCompact(
                                            vendor = vendor,
                                            onCardClick = { onVendorClick(vendor) },
                                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                                            modifier = Modifier.fillMaxWidth(),
                                            compactCardSize = CompactCardSize.SMALL
                                        )
                                    }
                                }
                            } else {
                                if (isLoading) {
                                    items(3, span = { GridItemSpan(2) }) {
                                        TimelineSection(
                                            date = "Loading...",
                                            event = "Fetching your plans",
                                            isLoading = true
                                        )
                                    }
                                } else if (savedTimelineEvents.isEmpty()) {
                                    item(span = { GridItemSpan(2) }) {
                                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                                    }
                                } else {
                                    items(
                                        items = savedTimelineEvents,
                                        key = { it.id },
                                        span = { GridItemSpan(2) }
                                    ) { timelineItem ->
                                        val vendorsForEvent = allVendors.filter { v -> savedVendorsForCategory.any { it.vendorName == v.name && it.destination == timelineItem.id } }.map { it.copy(favorite = true) }
                                        TimelineSection(
                                            date = timelineItem.date,
                                            event = timelineItem.event,
                                            vendors = vendorsForEvent,
                                            onVendorClick = onVendorClick,
                                            onVendorFavoriteToggle = onFavoriteToggle,
                                            onSeeAllClick = { onTimelineSeeAll(timelineItem) }
                                        )
                                    }
                                }
                            }
                            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(24.dp)) }
                            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(100.dp)) }
                        }
                    }
                }

                if (!isSearchActive) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        AnimatedVisibility(
                            visible = internalBottomBarVisible,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it }),
                            label = "CategoryBottomTabVisibility"
                        ) {
                            BottomTab(
                                items = bottomTabs,
                                selectedValue = selectedTab,
                                onItemSelected = onSelectedTabChange
                            )
                        }
                    }
                }
            }
        }
    }
}
