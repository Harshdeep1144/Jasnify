package com.harshdeep.jasnify.presentation.screens.main.tabs.vendors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.others.IosSegmentedControl
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.sections.TimelineSection
import com.harshdeep.jasnify.presentation.components.states.StandaloneEmptyState
import com.harshdeep.jasnify.theme.BackgroundPrimary

@Composable
fun AllSavedVendorsContent(
    onBackClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    vendorSavedDestinations: Map<String, String>,
    timelineEvents: List<TimelineEvent>,
    allVendors: List<Vendor>,
    selectedViewType: String,
    onSelectedViewTypeChange: (String) -> Unit,
    isLoading: Boolean = false,
    onTimelineSeeAll: (TimelineEvent) -> Unit = { _ -> },
    gridState: LazyGridState = rememberLazyGridState()
) {
    val viewOptions = listOf("By Timeline", "All Saved")

    val savedVendorsList = remember(vendorSavedDestinations, allVendors) {
        allVendors.filter { v -> vendorSavedDestinations.containsKey("${v.name}-${v.category}") }
            .map { it.copy(favorite = true) }
    }

    val savedTimelineEvents = remember(vendorSavedDestinations, timelineEvents, allVendors) {
        val list = mutableListOf<TimelineEvent>()
        val defaultSaved = allVendors.filter { v -> vendorSavedDestinations["${v.name}-${v.category}"] == "mysaved" }
            .map { it.copy(favorite = true) }

        if (defaultSaved.isNotEmpty()) {
            list.add(TimelineEvent(id = "mysaved", date = "Default List", event = "My Saved List", venues = emptyList()))
        }

        timelineEvents.forEach { event ->
            val eventVendors = allVendors.filter { v -> vendorSavedDestinations["${v.name}-${v.category}"] == event.id }
                .map { it.copy(favorite = true) }
            if (eventVendors.isNotEmpty()) {
                list.add(event.copy(venues = emptyList()))
            }
        }
        list
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = "Saved Vendors",
                    onBackClick = onBackClick,
                    backIcon = TopIcon.Predefined.BACK,
                    isLargeTitle = true
                )
            }
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 12.dp),
            state = gridState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    items(savedVendorsList) { vendor ->
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
                            isLoading = true,
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                    }
                } else if (savedTimelineEvents.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        StandaloneEmptyState(message = "No plans here yet", iconRes = R.drawable.ic_receipt)
                    }
                } else {
                    items(savedTimelineEvents, span = { GridItemSpan(2) }) { timelineItem ->
                        val vendorsForEvent = allVendors.filter { v ->
                            vendorSavedDestinations["${v.name}-${v.category}"] == timelineItem.id
                        }.map { it.copy(favorite = true) }

                        TimelineSection(
                            date = timelineItem.date,
                            event = timelineItem.event,
                            vendors = vendorsForEvent,
                            onVendorClick = onVendorClick,
                            onVendorFavoriteToggle = onFavoriteToggle,
                            onSeeAllClick = { onTimelineSeeAll(timelineItem) },
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                    }
                }
            }
            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(24.dp)) }
        }
    }
}
