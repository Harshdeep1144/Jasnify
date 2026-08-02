package com.harshdeep.jasnify.presentation.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VenueCardCompact
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.states.CompactCardLoading
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

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
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val listState = rememberLazyListState()
    val totalItemsCount = venues.size + vendors.size
    val hasItems = totalItemsCount > 0 || isLoading
    val showSeeAll = totalItemsCount > 3

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
            Column(modifier = Modifier.weight(1f)) {
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
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (showSeeAll && onSeeAllClick != null) {
                CustomTextButton(
                    onClick = onSeeAllClick,
                    text = "See all",
                    type = ButtonType.Tertiary,
                    size = ButtonSize.Small
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
                if (isLoading) {
                    items(3) {
                        CompactCardLoading(cardSize = CompactCardSize.SMALL)
                    }
                } else {
                    if (venues.isNotEmpty()) {
                        items(venues) { venue ->
                            VenueCardCompact(
                                venue = venue,
                                onCardClick = { onVenueClick(venue) },
                                onFavoriteToggle = { onVenueFavoriteToggle(venue) },
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
                                compactCardSize = CompactCardSize.SMALL
                            )
                        }
                    }
                }
            }

            if (!isLoading) {
                CarouselIndicator(
                    listState = listState,
                    totalItems = totalItemsCount,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
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
            .background(color = SurfacePrimary, shape = CircleShape)
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

@Composable
fun SavedTimelineItemsScreen(
    title: String,
    date: String,
    event: String,
    venues: List<Venue> = emptyList(),
    vendors: List<Vendor> = emptyList(),
    onVenueClick: (Venue) -> Unit = {},
    onVenueFavoriteToggle: (Venue) -> Unit = {},
    onVendorClick: (Vendor) -> Unit = {},
    onVendorFavoriteToggle: (Vendor) -> Unit = {},
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CustomTopBar(
                    title = title,
                    onBackClick = onBackClick,
                    backIcon = TopIcon.Predefined.BACK_2,
                    isLeftAligned = true,
                    buttonStyle = ButtonBackground.TRANSPARENT
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = event,
                            style = JasnifyTheme.typography.headingLarge,
                            color = ContentBrandDark,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = date,
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentSecondary
                        )
                    }

                    CustomIconButton(
                        onClick = {},
                        icon = painterResource(R.drawable.ic_edit),
                        size = ButtonSize.Small,
                        type = ButtonType.Tertiary
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (venues.isNotEmpty()) {
                items(venues) { venue ->
                    VenueCardCompact(
                        venue = venue,
                        onCardClick = { onVenueClick(venue) },
                        onFavoriteToggle = { onVenueFavoriteToggle(venue) },
                        compactCardSize = CompactCardSize.MEDIUM // Larger size for grid
                    )
                }
            }

            if (vendors.isNotEmpty()) {
                items(vendors) { vendor ->
                    VendorCardCompact(
                        vendor = vendor,
                        onCardClick = { onVendorClick(vendor) },
                        onFavoriteToggle = { onVendorFavoriteToggle(vendor) },
                        compactCardSize = CompactCardSize.MEDIUM // Larger size for grid
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun TimelineSectionPreview() {
    JasnifyTheme {
        TimelineSection(
            date = "24 Dec 2026",
            event = "Wedding Ceremony",
            venues = listOf(Venue(id = "1"), Venue(id = "2")),
            vendors = listOf(Vendor(id = "1"), Vendor(id = "2")),
            isLoading = false,
            onSeeAllClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SavedTimelineItemsScreenPreview() {
    JasnifyTheme {
        SavedTimelineItemsScreen(
            title = "Saved Items",
            date = "24 Dec 2026",
            event = "Wedding Ceremony",
            venues = listOf(Venue(id = "1"), Venue(id = "2")),
            vendors = listOf(Vendor(id = "1"), Vendor(id = "2")),
            onBackClick = {}
        )
    }
}
