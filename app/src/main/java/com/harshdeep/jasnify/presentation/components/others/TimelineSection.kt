package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VenueCardCompact
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TimelineSection(
    date: String,
    event: String,
    venues: List<Venue> = emptyList(),
    vendors: List<Vendor> = emptyList(),
    onVenueClick: (Venue) -> Unit = {},
    onVenueFavoriteToggle: (Venue) -> Unit = {},
    onVendorClick: (Vendor) -> Unit = {},
    onVendorFavoriteToggle: (Vendor) -> Unit = {},
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val listState = rememberLazyListState()
    val hasItems = venues.isNotEmpty() || vendors.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(20.dp, CornerSmoothingDefault))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                shape = SquircleShape(20.dp, CornerSmoothingDefault)
            )
            .background(SurfaceSecondary),
    ) {
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentBrandDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (!hasItems) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items saved yet.",
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                if (venues.isNotEmpty()) {
                    items(venues) { venue ->
                        VenueCardCompact(
                            venue = venue,
                            onCardClick = { onVenueClick(venue) },
                            onFavoriteToggle = { onVenueFavoriteToggle(venue) },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            compactCardSize = CompactCardSize.SMALL
                        )
                    }
                }
                if (vendors.isNotEmpty()) {
                    items(vendors) { vendor ->
                        VendorCardCompact(
                            vendor = vendor,
                            onCardClick = { onVendorClick(vendor) },
                            onFavoriteToggle = { onVendorFavoriteToggle(vendor) },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            compactCardSize = CompactCardSize.SMALL
                        )
                    }
                }
            }

            CarouselIndicator(
                listState = listState,
                totalItems = venues.size + vendors.size,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
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

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(4.dp)
            .background(color = SurfaceSecondary, shape = CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(thumbWidth)
                .offset(x = (trackWidth - thumbWidth) * progress)
                .background(color = ContentBrandDark, shape = CircleShape)
        )
    }
}
