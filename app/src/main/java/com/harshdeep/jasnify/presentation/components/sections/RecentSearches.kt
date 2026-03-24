package com.harshdeep.jasnify.presentation.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardData
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun RecentSearchesSection(
    recentVenues: List<VendorCardData>,
    onClearAll: () -> Unit,
    onVenueClick: (VendorCardData) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 0.dp),
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
                onClick = onClearAll,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Clear all",
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentBrandDark
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Horizontal list of recently searched items
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(recentVenues) { venue ->
                VendorCardCompact(
                    vendor = venue,
                    onCardClick = { onVenueClick(venue) },
                    compactCardSize = CompactCardSize.SMALL
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RecentSearchesSectionPreview() {
    Box(
        modifier = Modifier.padding(horizontal = 0.dp)
    ){
        RecentSearchesSection(
            onVenueClick = {},
            recentVenues = MockData.sampleVenues1,
            onClearAll = {}
        )
    }
}