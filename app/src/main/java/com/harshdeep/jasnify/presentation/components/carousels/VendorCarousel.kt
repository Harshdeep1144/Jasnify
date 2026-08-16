package com.harshdeep.jasnify.presentation.components.carousels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.states.CompactCardLoading
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

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
    val shimmerBrush = if (isLoading) shimmerBrush() else null
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
            if (isLoading && shimmerBrush != null) {
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
            if (isLoading && shimmerBrush != null) {
                items(5) {
                    CompactCardLoading(cardSize = cardSize, shimmerBrush = shimmerBrush)
                }
            } else {
                items(
                    items = vendors,
                    key = { it.id }
                ) { vendor ->
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



