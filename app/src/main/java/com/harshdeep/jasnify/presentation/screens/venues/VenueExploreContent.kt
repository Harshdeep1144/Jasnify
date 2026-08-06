package com.harshdeep.jasnify.presentation.screens.venues

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.cards.VenueCardFull
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.states.EmptyState
import com.harshdeep.jasnify.presentation.components.sections.RecentSearchesSection
import com.harshdeep.jasnify.presentation.components.states.SearchSuggestionItem
import com.harshdeep.jasnify.presentation.components.sections.TrendingAiSearchesSection

@Composable
fun VenueExploreContent(
    exploreVenues: List<Venue>,
    filteredAndSortedExploreVenues: List<Venue>,
    selectedLocation: String,
    onVenueClick: (Venue) -> Unit,
    onLocationSelectorClick: () -> Unit,
    onFavoriteToggle: (Venue) -> Unit,
    onShowFilterDialogChange: (Boolean) -> Unit,
    isLoading: Boolean,
    listState: LazyListState,
    text: String,
    onTextChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isSearchActive) {
        if (!isSearchActive && text.isNotEmpty() && filteredAndSortedExploreVenues.isEmpty()) {
            onTextChange("")
        }
    }

    val searchBarTopPadding by animateDpAsState(
        targetValue = if (isSearchActive) 0.dp else 12.dp,
        label = "searchBarTopPadding"
    )

    var recentSearches by remember {
        mutableStateOf(getRecentSearches(context))
    }

    BackHandler(enabled = isSearchActive) {
        onSearchActiveChange(false)
        onTextChange("")
        focusManager.clearFocus()
    }

    val recentVenuesList = remember(recentSearches, exploreVenues) {
        recentSearches.mapNotNull { name ->
            exploreVenues.find { it.name == name }
        }
    }

    val handleVenueClick: (Venue) -> Unit = { venue ->
        saveRecentSearch(context, venue.name)
        recentSearches = getRecentSearches(context)
        onVenueClick(venue)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column{
                AnimatedVisibility(
                    visible = !isSearchActive,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Spacer(Modifier.height(12.dp))
                        LocationSelectorPill(
                            location = selectedLocation,
                            onLocationSelectorClick = onLocationSelectorClick
                        )
                    }
                }
                Spacer(Modifier.height(searchBarTopPadding))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomSearchBar(
                        value = text,
                        onValueChange = onTextChange,
                        onActiveChange = onSearchActiveChange,
                        modifier = Modifier.weight(1f),
                        isAiSearch = true,
                        placeholder = "Type your choices"
                    )

                    AnimatedVisibility(
                        visible = !isSearchActive,
                        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.width(8.dp))
                            FilterButton(onClick = { onShowFilterDialogChange(true) })
                        }
                    }
                }
            }
        }

        if (isSearchActive && text.isNotEmpty()) {
            if (filteredAndSortedExploreVenues.isEmpty()) {
                item {
                    EmptyState(
                        message = "No matches for \"$text\"",
                        iconRes = R.drawable.ic_receipt
                    )
                }
            } else {
                items(filteredAndSortedExploreVenues.take(8)) { venue ->
                    SearchSuggestionItem(
                        title = venue.name,
                        subtitle = "${venue.locality}, ${venue.city}",
                        onClick = {
                            handleVenueClick(venue)
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        } else if (!isSearchActive && text.isNotEmpty()) {
            items(
                items = filteredAndSortedExploreVenues,
                key = { it.id.ifEmpty { it.name } },
                contentType = { "venue" }
            ) { venueItem ->
                VenueCardFull(
                    venue = venueItem,
                    onFavoriteToggle = { onFavoriteToggle(venueItem) },
                    onCardClick = { handleVenueClick(venueItem) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )
            }
        } else if (!isSearchActive) {
            if (isLoading && filteredAndSortedExploreVenues.isEmpty()) {
                items(5) {
                    VenueCardFull(
                        venue = Venue(),
                        isLoading = true,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            } else if (filteredAndSortedExploreVenues.isEmpty()) {
                item {
                    EmptyState(message = "No venues found")
                }
            } else {
                items(
                    items = filteredAndSortedExploreVenues,
                    key = { it.id.ifEmpty { it.name } },
                    contentType = { "venue" }
                ) { venueItem ->
                    VenueCardFull(
                        venue = venueItem,
                        onFavoriteToggle = { onFavoriteToggle(venueItem) },
                        onCardClick = { handleVenueClick(venueItem) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )
                }
            }
        } else {
            item {
                TrendingAiSearchesSection(
                    onTrendingClick = { query ->
                        onTextChange(query)
                        focusManager.clearFocus()
                    }
                )
            }
            if (recentVenuesList.isNotEmpty()) {
                item {
                    RecentSearchesSection(
                        onVenueClick = handleVenueClick,
                        recentVenues = recentVenuesList,
                        onRemoveVenue = { venue ->
                            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                            val current = getRecentSearches(context).toMutableList()
                            current.remove(venue.name)
                            prefs.edit {
                                putString(
                                    KEY_RECENT_SEARCHES,
                                    current.joinToString("|||")
                                )
                            }
                            recentSearches = getRecentSearches(context)
                        }
                    )
                }
            }
        }
        if (!isSearchActive) {
            item {
                FooterJansify()
            }
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}
