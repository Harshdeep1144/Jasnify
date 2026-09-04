package com.harshdeep.jasnify.presentation.components.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary

@Composable
fun TrendingAiSearchesSection(
    onTrendingClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    queries: List<String> = listOf(
        "4.5+ Rated",
        "Hotels for 800 guests",
        "Expert Photographers",
        "Luxury Venues",
    )
) {
    val aiIcon = ImageVector.vectorResource(id = R.drawable.ic_ai)

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trend_up),
                    contentDescription = "Trending",
                    tint = ContentPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Trending AI Searches",
                    style = JasnifyTheme.typography.headingMedium,
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary
                )
            }

            CustomIconButton(
                onClick = { },
                icon = painterResource(R.drawable.ic_info),
                contentColor = ContentPrimary,
                containerColor = SurfacePrimary,
                size = ButtonSize.Small
            )
        }
        Spacer(Modifier.height(12.dp))

        if (queries.size <= 6) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(queries) { query ->
                    FilterChip(
                        label = query,
                        isSelected = false,
                        shapeStyle = ChipShapeStyle.Round,
                        size = ChipSize.Small,
                        leadingIcon = aiIcon,
                        onClick = { onTrendingClick(query) },
                        hasStroke = true,
                    )
                }
            }
        } else {
            val midIndex = (queries.size + 1) / 2
            val firstRowItems = queries.take(midIndex)
            val secondRowItems = queries.drop(midIndex)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(firstRowItems) { query ->
                        FilterChip(
                            label = query,
                            isSelected = false,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            leadingIcon = aiIcon,
                            onClick = { onTrendingClick(query) },
                            hasStroke = true,
                        )
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(secondRowItems) { query ->
                        FilterChip(
                            label = query,
                            isSelected = false,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            leadingIcon = aiIcon,
                            onClick = { onTrendingClick(query) },
                            hasStroke = true,
                        )
                    }
                }
            }
        }
    }
}
