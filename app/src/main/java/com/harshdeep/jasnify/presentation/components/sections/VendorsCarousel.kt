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
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardData
import com.harshdeep.jasnify.theme.*

@Composable
fun VendorsCarousel(
    title: String,
    vendors: List<VendorCardData>, 
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {},
    onVendorClick: (VendorCardData) -> Unit = {},
    onFavoriteToggle: (VendorCardData) -> Unit = {},
    onOfferClick: (VendorCardData) -> Unit = {}
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vendors) { vendor ->
                VendorCardCompact(
                    vendor = vendor,
                    onCardClick = { onVendorClick(vendor) },
                    onFavoriteToggle = { onFavoriteToggle(vendor) },
                    onOfferClick = { onOfferClick(vendor) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVendorsCarousel() {
    val sampleVenues = List(5) {
        VendorCardData(
            vendorName = "Hotel Imperial Inn",
            location = "Sampatchak, Patna",
            vendorType = null,
            rating = 4.4,
            totalReviews = "1k",
            services = listOf(),
            priceStartsFrom = "₹2,999",
            images = listOf("")
        )
    }

    VendorsCarousel(
        title = "Trending Venues in Patna",
        vendors = sampleVenues,
        onVendorClick = { /* Handle click */ },
        onFavoriteToggle = { /* Handle favorite */ },
        onOfferClick = { /* Handle offer click */ }
    )
}