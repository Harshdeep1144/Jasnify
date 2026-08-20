package com.harshdeep.jasnify.presentation.screens.main.tabs.vendors

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.res.painterResource
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
import com.harshdeep.jasnify.presentation.components.carousels.HighlightedVendors
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
import com.harshdeep.jasnify.presentation.screens.venues.KEY_RECENT_SEARCHES
import com.harshdeep.jasnify.presentation.screens.venues.PREFS_NAME
import com.harshdeep.jasnify.theme.BackgroundPrimary

private val FilterOptions = listOf("Most Relevant", "Top-Rated", "Price: Highest First", "Price: Lowest First")
private val ViewOptions = listOf("By Timeline", "All Saved")

@Immutable
data class CategoryHighlightedTheme(
    val backgroundColor: Color,
    val titleColor: Color,
    val subtitleColor: Color
)

fun getCategoryHighlightedTheme(categoryName: String): CategoryHighlightedTheme {
    val normalized = categoryName.lowercase().trim()
    return when {
        normalized.contains("photo") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFD9E9FF), // Soft Blue
            titleColor = Color(0xFF003680),
            subtitleColor = Color(0xFF003680)
        )
        normalized.contains("food") || normalized.contains("cater") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFFFF0DB), // Warm Peach/Orange
            titleColor = Color(0xFF6B3300),
            subtitleColor = Color(0xFF6B3300)
        )
        normalized.contains("groom") || normalized.contains("salon") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFDDF5F2), // Fresh Mint
            titleColor = Color(0xFF004D40),
            subtitleColor = Color(0xFF004D40)
        )
        normalized.contains("makeup") || normalized.contains("beauty") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFFFE3EC), // Soft Rose Pink
            titleColor = Color(0xFF6A0C38),
            subtitleColor = Color(0xFF6A0C38)
        )
        normalized.contains("mehendi") || normalized.contains("mehndi") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFEFE9DC), // Warm Sand / Mehndi
            titleColor = Color(0xFF4E3600),
            subtitleColor = Color(0xFF4E3600)
        )
        normalized.contains("jewel") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFFFF7CC), // Golden Cream
            titleColor = Color(0xFF5A4600),
            subtitleColor = Color(0xFF5A4600)
        )
        normalized.contains("outfit") || normalized.contains("cloth") || normalized.contains("wear") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFF0E5FF), // Lavender/Violet
            titleColor = Color(0xFF38006B),
            subtitleColor = Color(0xFF38006B)
        )
        normalized.contains("entertain") || normalized.contains("music") || normalized.contains("dj") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFE5EAFF), // Electric Blue
            titleColor = Color(0xFF0E2278),
            subtitleColor = Color(0xFF0E2278)
        )
        normalized.contains("gift") -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFFFE8DD), // Coral
            titleColor = Color(0xFF662200),
            subtitleColor = Color(0xFF662200)
        )
        else -> CategoryHighlightedTheme(
            backgroundColor = Color(0xFFD9E9FF),
            titleColor = Color(0xFF003680),
            subtitleColor = Color(0xFF003680)
        )
    }
}

@DrawableRes
fun getCategoryIllustrationRes(categoryName: String): Int {
    val normalized = categoryName.lowercase().trim()
    return when {
        normalized.contains("groom") -> R.drawable.ill_vendor_grooming
        normalized.contains("makeup") -> R.drawable.ill_vendor_makeup
        normalized.contains("photo") -> R.drawable.ill_vendor_photographers
        normalized.contains("mehendi") || normalized.contains("mehndi") -> R.drawable.ill_vendor_mehendi
        normalized.contains("jewel") -> R.drawable.ill_vendor_jewellery
        normalized.contains("outfit") || normalized.contains("cloth") || normalized.contains("wear") -> R.drawable.ill_bride_and_groom
        normalized.contains("entertain") || normalized.contains("music") || normalized.contains("dj") -> R.drawable.ill_vendor_entertainment
        normalized.contains("food") || normalized.contains("cater") -> R.drawable.ill_vendor_food_serve
        normalized.contains("gift") -> R.drawable.ill_vendor_gifts
        else -> R.drawable.ill_vendor_grooming
    }
}

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

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var recentSearchesNames by remember { mutableStateOf(getCategoryRecentSearches(context, category.name)) }

    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    val categoryIllustration = remember(category.name) {
        getCategoryIllustrationRes(category.name)
    }

    val categoryHighlightTheme = remember(category.name) {
        getCategoryHighlightedTheme(category.name)
    }

    val filteredVendors = remember(allVendors, searchQuery, selectedFilterIndex, vendorSavedDestinations, category.name) {
        val baseList = allVendors.ifEmpty { MockData.sampleVendors.filter { it.category == category.name } }
        val query = searchQuery.trim()
        val searched = if (query.isEmpty()) {
            baseList
        } else {
            baseList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.locality.contains(query, ignoreCase = true) ||
                        it.city.contains(query, ignoreCase = true)
            }
        }

        val sorted = when (selectedFilterIndex) {
            1 -> searched.sortedByDescending { it.rating }
            2 -> searched.sortedByDescending { parsePrice(it.priceStartsFrom) }
            3 -> searched.sortedBy { parsePrice(it.priceStartsFrom) }
            else -> searched
        }.distinctBy { it.id }

        sorted.map { it.copy(favorite = vendorSavedDestinations.containsKey("${it.name}-${it.category}")) }
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
        val vendorMap = allVendors.associateBy { it.name }
        recentSearchesNames.mapNotNull { name -> vendorMap[name] }
    }

    var searchBarOnlyHeightPx by remember { mutableIntStateOf(0) }

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
                            item(key = "top_bar", contentType = "header") {
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

                            stickyHeader(key = "search_and_sticky_filters_header", contentType = "sticky_search_header") {
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
                                                items = FilterOptions,
                                                key = { _, filter -> "sticky_filter_$filter" },
                                                contentType = { _, _ -> "filter_chip" }
                                            ) { index, filter ->
                                                FilterChip(
                                                    label = filter,
                                                    isSelected = selectedFilterIndex == index,
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
                                    item(key = "empty_category_search", contentType = "empty_state") {
                                        EmptyState(message = "No matches for \"$searchQuery\"")
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                } else {
                                    items(
                                        items = filteredVendors.take(8),
                                        key = { "cat_search_${it.id}" },
                                        contentType = { "suggestion_item" }
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
                                    key = { "cat_filtered_${it.id}" },
                                    contentType = { "vendor_full_card" }
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
                                item(key = "highlighted_top_vendors_header", contentType = "highlighted_vendors") {
                                    val topVendors = remember(filteredVendors) {
                                        val highRated = filteredVendors.filter { it.rating >= 4.5 }
                                        highRated.ifEmpty { filteredVendors.take(6) }
                                    }
                                    HighlightedVendors(
                                        title = category.name,
                                        subtitle = "TOP-RATED",
                                        vendors = topVendors,
                                        isHeadingTop = false,
                                        headerImage = painterResource(id = categoryIllustration),
                                        backgroundColor = categoryHighlightTheme.backgroundColor,
                                        titleColor = categoryHighlightTheme.titleColor,
                                        subtitleColor = categoryHighlightTheme.subtitleColor,
                                        buttonText = null,
                                        onButtonClick = null,
                                        onVendorClick = { vendor ->
                                            saveCategoryRecentSearch(context, category.name, vendor.name)
                                            recentSearchesNames = getCategoryRecentSearches(context, category.name)
                                            onVendorClick(vendor)
                                        },
                                        onFavoriteToggle = onFavoriteToggle,
                                        onOfferClick = { onOfferClick(it) },
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                item(key = "cat_explore_divider", contentType = "divider") {
                                    OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                item(key = "filters_inline_item", contentType = "inline_filters") {
                                    if (!isChipsSticky) {
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            itemsIndexed(
                                                items = FilterOptions,
                                                key = { _, filter -> "inline_filter_$filter" },
                                                contentType = { _, _ -> "filter_chip" }
                                            ) { index, filter ->
                                                FilterChip(
                                                    label = filter,
                                                    isSelected = selectedFilterIndex == index,
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
                                    items(5, key = { "loading_$it" }, contentType = { "loading_card" }) {
                                        VendorCardFull(
                                            vendor = Vendor(),
                                            isLoading = true,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                } else if (filteredVendors.isEmpty()) {
                                    item(key = "no_vendors_found", contentType = "empty_state") {
                                        EmptyState(message = "No vendors found in this category")
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                } else {
                                    items(
                                        items = filteredVendors,
                                        key = { it.id },
                                        contentType = { "vendor_full_card" }
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

                                item(key = "cat_footer", contentType = "footer") {
                                    FooterJansify()
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            } else {
                                item(key = "cat_trending", contentType = "trending_searches") {
                                    TrendingAiSearchesSection(onTrendingClick = { query ->
                                        searchQuery = query
                                        focusManager.clearFocus()
                                    })
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                if (recentVendorsList.isNotEmpty()) {
                                    item(key = "cat_recent_searches", contentType = "recent_searches") {
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
                                item(key = "cat_bottom_spacer", contentType = "spacer") { Spacer(Modifier.height(24.dp)) }
                                item(key = "cat_extra_spacer", contentType = "spacer") { Spacer(Modifier.height(100.dp)) }
                            }
                        }
                    } else {
                        val savedVendorsList = remember(savedVendorsForCategory, allVendors) {
                            val savedNames = savedVendorsForCategory.map { it.vendorName }.toSet()
                            allVendors.filter { it.name in savedNames }.map { it.copy(favorite = true) }
                        }

                        val savedTimelineEvents = remember(savedVendorsForCategory, timelineEvents, allVendors) {
                            val list = mutableListOf<TimelineEvent>()
                            val defaultSaved = allVendors.filter { v ->
                                savedVendorsForCategory.any { it.vendorName == v.name && it.destination == "mysaved" }
                            }.map { it.copy(favorite = true) }

                            if (defaultSaved.isNotEmpty()) {
                                list.add(TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List", venues = emptyList()))
                            }

                            timelineEvents.forEach { event ->
                                val eventVendors = allVendors.filter { v ->
                                    savedVendorsForCategory.any { it.vendorName == v.name && it.destination == event.id }
                                }.map { it.copy(favorite = true) }

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
                            item(span = { GridItemSpan(2) }, key = "saved_top_bar", contentType = "header") {
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

                            item(span = { GridItemSpan(2) }, key = "saved_segmented_control", contentType = "segmented_control") {
                                IosSegmentedControl(
                                    options = ViewOptions,
                                    selectedOption = selectedViewType,
                                    onOptionSelected = onSelectedViewTypeChange,
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .height(44.dp)
                                )
                            }

                            if (selectedViewType == "All Saved") {
                                if (isLoading) {
                                    items(6, contentType = { "loading_card" }) {
                                        VendorCardCompact(
                                            vendor = Vendor(),
                                            isLoading = true,
                                            modifier = Modifier.fillMaxWidth(),
                                            compactCardSize = CompactCardSize.SMALL
                                        )
                                    }
                                } else if (savedVendorsList.isEmpty()) {
                                    item(span = { GridItemSpan(2) }, key = "empty_saved_all", contentType = "empty_state") {
                                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                                    }
                                } else {
                                    items(
                                        items = savedVendorsList,
                                        key = { it.id },
                                        contentType = { "compact_vendor_card" }
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
                                    items(3, span = { GridItemSpan(2) }, contentType = { "loading_timeline" }) {
                                        TimelineSection(
                                            date = "Loading...",
                                            event = "Fetching your plans",
                                            isLoading = true
                                        )
                                    }
                                } else if (savedTimelineEvents.isEmpty()) {
                                    item(span = { GridItemSpan(2) }, key = "empty_saved_timeline", contentType = "empty_state") {
                                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                                    }
                                } else {
                                    items(
                                        items = savedTimelineEvents,
                                        key = { it.id },
                                        span = { GridItemSpan(2) },
                                        contentType = { "timeline_section" }
                                    ) { timelineItem ->
                                        val vendorsForEvent = allVendors.filter { v ->
                                            savedVendorsForCategory.any { it.vendorName == v.name && it.destination == timelineItem.id }
                                        }.map { it.copy(favorite = true) }

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
                            item(span = { GridItemSpan(2) }, contentType = "spacer") { Spacer(Modifier.height(24.dp)) }
                            item(span = { GridItemSpan(2) }, contentType = "spacer") { Spacer(Modifier.height(100.dp)) }
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