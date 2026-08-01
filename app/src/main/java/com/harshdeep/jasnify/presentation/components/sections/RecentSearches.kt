package com.harshdeep.jasnify.presentation.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VenueCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun RecentSearchesSection(
    recentVenues: List<Venue> = emptyList(),
    recentVendors: List<Vendor> = emptyList(),
    onRemoveVenue: ((Venue) -> Unit)? = null,
    onRemoveVendor: ((Vendor) -> Unit)? = null,
    onVenueClick: ((Venue) -> Unit)? = null,
    onVendorClick: ((Vendor) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (recentVenues.isEmpty() && recentVendors.isEmpty()) return
    
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = ContentPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Recent Searches",
                    style = JasnifyTheme.typography.headingMedium,
                    color = ContentPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            TextButton(
                onClick =  { isEditing = !isEditing },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (isEditing) "Done" else "Edit",
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentBrandDark
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (recentVenues.isNotEmpty()) {
                items(recentVenues) { venue ->
                    VenueCardCompact(
                        venue = venue,
                        onCardClick = { onVenueClick?.invoke(venue) },
                        onRemoveClick = if (isEditing) { { onRemoveVenue?.invoke(venue) } } else null,
                        showLikeButton = false,
                        compactCardSize = CompactCardSize.SMALL
                    )
                }
            } else if (recentVendors.isNotEmpty()) {
                items(recentVendors) { vendor ->
                    VendorCardCompact(
                        vendor = vendor,
                        onCardClick = { onVendorClick?.invoke(vendor) },
                        onRemoveClick = if (isEditing) { { onRemoveVendor?.invoke(vendor) } } else null,
                        showLikeButton = false,
                        compactCardSize = CompactCardSize.SMALL
                    )
                }
            }
        }
    }
}
