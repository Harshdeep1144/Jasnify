package com.harshdeep.jasnify.presentation.screens.venues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VenueCardCompact
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.states.StandaloneEmptyState
import com.harshdeep.jasnify.presentation.components.sections.TimelineSection

@Composable
fun VenueSavedContent(
    savedTimelineEvents: List<TimelineEvent>,
    savedVenuesList: List<Venue>,
    selectedViewType: String,
    onSelectedViewTypeChange: (String) -> Unit,
    onVenueClick: (Venue) -> Unit,
    onFavoriteToggle: (Venue) -> Unit,
    onTimelineSeeAll: (TimelineEvent) -> Unit,
    isLoading: Boolean
) {
    val viewOptions = listOf("By Timeline", "All Saved")

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .background(Color.Transparent),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            IosSegmentedControl(
                options = viewOptions,
                selectedOption = selectedViewType,
                onOptionSelected = onSelectedViewTypeChange,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(44.dp)
            )
        }

        if (selectedViewType == "By Timeline") {
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
                items(savedTimelineEvents, span = { GridItemSpan(2) }) { timelineItem ->
                    TimelineSection(
                        date = timelineItem.date,
                        event = timelineItem.event,
                        venues = timelineItem.venues,
                        onVenueClick = onVenueClick,
                        onVenueFavoriteToggle = onFavoriteToggle,
                        onSeeAllClick = {
                            onTimelineSeeAll(timelineItem)
                        }
                    )
                }
            }
        } else {
            if (isLoading) {
                items(6) {
                    VenueCardCompact(
                        venue = Venue(),
                        isLoading = true,
                        modifier = Modifier.fillMaxWidth(),
                        compactCardSize = CompactCardSize.SMALL
                    )
                }
            } else if (savedVenuesList.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                }
            } else {
                items(
                    items = savedVenuesList,
                    key = { it.id.ifEmpty { it.name } }
                ) { venueItem ->
                    VenueCardCompact(
                        venue = venueItem,
                        onFavoriteToggle = { onFavoriteToggle(venueItem) },
                        onCardClick = { onVenueClick(venueItem) },
                        modifier = Modifier.fillMaxWidth(),
                        compactCardSize = CompactCardSize.SMALL
                    )
                }
            }
        }
        item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(6.dp)) }
    }
}
