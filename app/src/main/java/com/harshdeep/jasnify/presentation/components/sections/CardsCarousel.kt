package com.harshdeep.jasnify.presentation.components.sections

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.cards.VenueCardCompact
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.states.CompactCardLoading
import com.harshdeep.jasnify.presentation.components.states.FullCardLoading
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun VenueCarousel(
    title: String,
    venues: List<Venue>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onSeeAllClick: (() -> Unit)? = null,
    onVenueClick: (Venue) -> Unit = {},
    onFavoriteToggle: (Venue) -> Unit = {},
    onOfferClick: (Venue) -> Unit = {},
    cardSize: CompactCardSize = CompactCardSize.SMALL
) {
    val shimmerBrush = shimmerBrush()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .then(
                    if (onSeeAllClick != null && !isLoading) {
                        Modifier.clickable { onSeeAllClick() }
                    } else {
                        Modifier
                    }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                // Shimmering Heading
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
            } else {
                Text(
                    text = title,
                    style = JasnifyTheme.typography.headingLarge,
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary,
                )
            }
            // Show arrow icon only if onSeeAllClick callback is provided
            if (onSeeAllClick != null && !isLoading) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "See All",
                    tint = ContentPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = !isLoading
        ) {
            if (isLoading) {
                items(5) {
                    CompactCardLoading(cardSize = cardSize, shimmerBrush = shimmerBrush)
                }
            } else {
                items(venues) { venue ->
                    VenueCardCompact(
                        venue = venue,
                        onCardClick = { onVenueClick(venue) },
                        onFavoriteToggle = { onFavoriteToggle(venue) },
                        onOfferClick = { onOfferClick(venue) },
                        compactCardSize = cardSize
                    )
                }
            }
        }
    }
}

@Composable
fun VendorCarousel(
    title: String,
    vendors: List<Vendor>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onSeeAllClick: (() -> Unit)? = null,
    onVendorClick: (Vendor) -> Unit = {},
    onFavoriteToggle: (Vendor) -> Unit = {},
    onOfferClick: (Vendor) -> Unit = {},
    cardSize: CompactCardSize = CompactCardSize.SMALL
) {
    val shimmerBrush = shimmerBrush()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .then(
                    if (onSeeAllClick != null && !isLoading) {
                        Modifier.clickable { onSeeAllClick() }
                    } else {
                        Modifier
                    }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                // Shimmering Heading
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
            } else {
                Text(
                    text = title,
                    style = JasnifyTheme.typography.headingLarge,
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary,
                )
            }
            // Show arrow icon only if onSeeAllClick callback is provided
            if (onSeeAllClick != null && !isLoading) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "See All",
                    tint = ContentPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = !isLoading
        ) {
            if (isLoading) {
                items(5) {
                    CompactCardLoading(cardSize = cardSize, shimmerBrush = shimmerBrush)
                }
            } else {
                items(vendors) { vendor ->
                    VendorCardCompact(
                        vendor = vendor,
                        onCardClick = { onVendorClick(vendor) },
                        onFavoriteToggle = { onFavoriteToggle(vendor) },
                        onOfferClick = { onOfferClick(vendor) },
                        compactCardSize = cardSize
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVenueCarouselWithSeeAll() {
    val sampleVenues = List(5) {
        Venue(
            name = "Hotel Imperial Inn",
            location = "Sampatchak, Patna",
            type = null,
            rating = 4.4,
            totalReviews = "1k",
            priceStartsFrom = "₹2,999",
            images = listOf("")
        )
    }

    VenueCarousel(
        title = "Trending Venues in Patna",
        venues = sampleVenues,
        onSeeAllClick = { /* Navigate to See All */ },
        onVenueClick = { /* Handle click */ },
        onFavoriteToggle = { /* Handle favorite */ },
        onOfferClick = { /* Handle offer click */ }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewVenueCarouselWithoutSeeAll() {
    val sampleVenues = List(5) {
        Venue(
            name = "Hotel Imperial Inn",
            location = "Sampatchak, Patna",
            type = null,
            rating = 4.4,
            totalReviews = "1k",
            priceStartsFrom = "₹2,999",
            images = listOf("")
        )
    }

    VenueCarousel(
        title = "Featured Venues",
        venues = sampleVenues,
        onSeeAllClick = null, // No See All click passed
        onVenueClick = { /* Handle click */ }
    )
}