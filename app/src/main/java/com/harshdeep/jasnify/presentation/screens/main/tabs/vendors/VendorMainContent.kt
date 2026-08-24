package com.harshdeep.jasnify.presentation.screens.main.tabs.vendors

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.edit
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardFull
import com.harshdeep.jasnify.presentation.components.carousels.VendorCarousel
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.sections.ExploreCategoriesHorizontal
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.TrendingAiSearchesSection
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryGrid
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.components.states.EmptyState
import com.harshdeep.jasnify.presentation.components.states.SearchSuggestionItem
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.SurfacePrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VendorMainContent(
    selectedCity: String,
    categories: List<VendorCategoryItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    onMenuClick: () -> Unit,
    onLocationClick: () -> Unit,
    onCategoryClick: (VendorCategoryItem) -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    onOfferClick: (Vendor) -> Unit = {},
    recentVendorsList: List<Vendor>,
    focusManager: FocusManager,
    context: Context,
    onRecentSearchesUpdate: (List<String>) -> Unit,
    allVendors: List<Vendor>,
    isLoading: Boolean,
    listState: LazyListState = rememberLazyListState()
) {
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

    val dynamicVendorPlaceholders = remember(categories) {
        if (categories.isNotEmpty()) {
            categories.map { it.name.lowercase() }
        } else {
            listOf(
                "photographers",
                "makeup artists",
                "mehendi artists",
                "decorators",
                "caterers",
                "djs & music"
            )
        }
    }

    val filteredAllVendors = remember(allVendors, searchQuery) {
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            allVendors
        } else {
            allVendors.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.locality.contains(query, ignoreCase = true) ||
                        it.city.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true)
            }
        }
    }

    val makeupVendors = remember(allVendors) {
        allVendors.filter { it.category == "Makeup" }
    }
    val photographyVendors = remember(allVendors) {
        allVendors.filter { it.category == "Photography" }
    }
    val mehendiVendors = remember(allVendors) {
        allVendors.filter { it.category == "Mehendi" }
    }

    LaunchedEffect(isSearchActive) {
        if (!isSearchActive && searchQuery.isNotEmpty() && filteredAllVendors.isEmpty()) {
            onSearchQueryChange("")
        }
    }

    val vendorTitlePainter = painterResource(R.drawable.ill_vendors)

    Scaffold(
        containerColor = BackgroundPrimary,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                state = listState,
                contentPadding = PaddingValues(bottom = 0.dp)
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
                            label = "TopBarSearchTransition"
                        ) { active ->
                            CustomTopBar(
                                title = if (active) "Search Vendors" else "Vendors",
                                subtitle = if (active) null else selectedCity,
                                onBackClick = if (active) {
                                    {
                                        onSearchActiveChange(false)
                                        onSearchQueryChange("")
                                        focusManager.clearFocus()
                                    }
                                } else null,
                                onMenuClick = if (active) null else onMenuClick,
                                onDropdownClick = if (active) null else onLocationClick,
                                titleIcon = if (active) null else vendorTitlePainter,
                                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                                backIcon = TopIcon.Predefined.DOWN,
                                isLargeTitle = true,
                                isLeftAligned = !active,
                                buttonStyle = ButtonBackground.OPAQUE
                            )
                        }
                    }
                }

                stickyHeader(key = "search_header", contentType = "sticky_search") {
                    Column(
                        modifier = Modifier
                            .background(SurfacePrimary)
                            .fillMaxWidth()
                            .zIndex(10f)
                            .padding(bottom = 4.dp)
                    ) {
                        Spacer(Modifier.height(12.dp))
                        CustomSearchBar(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            onActiveChange = onSearchActiveChange,
                            placeholderPrefix = "Search for ",
                            dynamicPlaceholders = dynamicVendorPlaceholders,
                            cycleIntervalMs = 2800L,
                            isAiSearch = true,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (isSearchActive && searchQuery.isNotEmpty()) {
                    if (filteredAllVendors.isEmpty()) {
                        item(key = "empty_search", contentType = "empty_state") {
                            EmptyState(message = "No matches for \"$searchQuery\"")
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    } else {
                        items(
                            items = filteredAllVendors.take(8),
                            key = { "search_${it.id}" },
                            contentType = { "suggestion_item" }
                        ) { vendor ->
                            SearchSuggestionItem(
                                title = vendor.name,
                                subtitle = "${vendor.category} • ${vendor.locality}, ${vendor.city}",
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
                        items = filteredAllVendors,
                        key = { "filtered_${it.id}" },
                        contentType = { "vendor_full_card" }
                    ) { vendor ->
                        VendorCardFull(
                            vendor = vendor,
                            onCardClick = { onVendorClick(vendor) },
                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                            onOfferClick = { onOfferClick(vendor) },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else if (!isSearchActive) {
                    item(key = "categories_grid", contentType = "categories_grid") {
                        VendorCategoryGrid(categories = categories, onCategoryClick = onCategoryClick)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item(key = "explore_divider", contentType = "divider") {
                        OrDivider(text = "EXPLORE", dividerGap = 0.dp, modifier = Modifier.padding(horizontal = 24.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item(key = "carousel_makeup", contentType = "vendor_carousel") {
                        VendorCarousel(
                            title = "Top Makeup Artists in $selectedCity",
                            vendors = makeupVendors,
                            isLoading = isLoading,
                            onVendorClick = { vendor ->
                                saveRecentSearch(context, vendor.name)
                                onRecentSearchesUpdate(getRecentSearches(context))
                                onVendorClick(vendor)
                            },
                            onFavoriteToggle = onFavoriteToggle,
                            onOfferClick = { onOfferClick(it) },
                            cardSize = CompactCardSize.MEDIUM
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item(key = "carousel_photography", contentType = "vendor_carousel") {
                        VendorCarousel(
                            title = "Best Photographers in $selectedCity",
                            vendors = photographyVendors,
                            isLoading = isLoading,
                            onVendorClick = { vendor ->
                                saveRecentSearch(context, vendor.name)
                                onRecentSearchesUpdate(getRecentSearches(context))
                                onVendorClick(vendor)
                            },
                            onFavoriteToggle = onFavoriteToggle,
                            onOfferClick = { onOfferClick(it) },
                            cardSize = CompactCardSize.MEDIUM
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item(key = "carousel_mehendi", contentType = "vendor_carousel") {
                        VendorCarousel(
                            title = "Expert Mehendi Artists in $selectedCity",
                            vendors = mehendiVendors,
                            isLoading = isLoading,
                            onVendorClick = { vendor ->
                                saveRecentSearch(context, vendor.name)
                                onRecentSearchesUpdate(getRecentSearches(context))
                                onVendorClick(vendor)
                            },
                            onFavoriteToggle = onFavoriteToggle,
                            onOfferClick = { onOfferClick(it) },
                            cardSize = CompactCardSize.MEDIUM
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item(key = "explore_horizontal", contentType = "explore_horizontal") {
                        DashedDivider()
                        ExploreCategoriesHorizontal(categories = categories, onCategoryClick = onCategoryClick)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item(key = "footer", contentType = "footer") {
                        FooterJansify()
                    }
                } else {
                    item(key = "trending_searches", contentType = "trending_searches") {
                        TrendingAiSearchesSection(onTrendingClick = { query ->
                            onSearchQueryChange(query)
                            focusManager.clearFocus()
                        })
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (recentVendorsList.isNotEmpty()) {
                        item(key = "recent_searches_section", contentType = "recent_searches") {
                            RecentSearchesSection(
                                recentVendors = recentVendorsList,
                                onVendorClick = onVendorClick,
                                onRemoveVendor = { vendor ->
                                    val current = getRecentSearches(context).toMutableList()
                                    current.remove(vendor.name)
                                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit { putString(KEY_RECENT_SEARCHES, current.joinToString("|||")) }
                                    onRecentSearchesUpdate(getRecentSearches(context))
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    item(key = "spacer_bottom", contentType = "spacer") { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}