package com.harshdeep.jasnify.presentation.components.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.cards.VenueCardCompact
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.theme.*

@Composable
fun VenueCarousel(
    title: String,
    venues: List<Venue>, 
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {},
    onVenueClick: (Venue) -> Unit = {},
    onFavoriteToggle: (Venue) -> Unit = {},
    onOfferClick: (Venue) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clickable { onSeeAllClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = JasnifyTheme.typography.headingLarge,
                fontWeight = FontWeight.Medium,
                color = ContentPrimary,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "See All",
                tint = ContentPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Carousel
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(venues) { venue ->
                VenueCardCompact(
                    venue = venue,
                    onCardClick = { onVenueClick(venue) },
                    onFavoriteToggle = { onFavoriteToggle(venue) },
                    onOfferClick = { onOfferClick(venue) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVenueCarousel() {
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
        onVenueClick = { /* Handle click */ },
        onFavoriteToggle = { /* Handle favorite */ },
        onOfferClick = { /* Handle offer click */ }
    )
}
