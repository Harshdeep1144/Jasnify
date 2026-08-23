package com.harshdeep.jasnify.presentation.components.states

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

private val CategoryLargeChipShape = SquircleShape(CornerLarge, CornerSmoothingDefault)
private val CategorySmallChipShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

@Composable
fun VendorsLoadingState(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush()
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            userScrollEnabled = false,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Top Bar
            item(key = "loading_top_bar") {
                CustomTopBar(
                    title = "Vendors",
                    titleIcon = painterResource(R.drawable.ill_vendors),
                    menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                    backIcon = TopIcon.Predefined.DOWN,
                    isLargeTitle = true,
                    isLeftAligned = true,
                    buttonStyle = ButtonBackground.OPAQUE,
                    onMenuClick = {}
                )
            }

            // 2. Search Bar Skeleton
            item(key = "loading_search_bar") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfacePrimary)
                        .padding(bottom = 4.dp)
                ) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 12.dp)
                            .clip(RoundedCornerShape(100))
                            .background(SurfaceSecondary)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(100)
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                            )
                            Box(
                                modifier = Modifier
                                    .size(width = 140.dp, height = 16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // 3. Vendor Category Grid Skeleton (3 columns x 3 rows = 9 items)
            item(key = "loading_categories_grid") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(120.dp)
                                        .clip(CategoryLargeChipShape)
                                        .background(SurfaceSecondary)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                                            shape = CategoryLargeChipShape
                                        )
                                        .padding(vertical = 24.dp, horizontal = 24.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(12.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(brush)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 4. Explore Divider
            item(key = "loading_explore_divider") {
                OrDivider(
                    text = "EXPLORE",
                    dividerGap = 0.dp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 5. Vendor Carousels (Makeup, Photography, Mehendi)
            val carouselTitles = listOf(
                "Top Makeup Artists",
                "Best Photographers",
                "Expert Mehendi Artists"
            )

            carouselTitles.forEachIndexed { index, _ ->
                item(key = "loading_carousel_$index") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Carousel Title Header
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .size(width = 210.dp, height = 18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )

                        // Carousel Row with CompactCardLoading(CompactCardSize.MEDIUM)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            userScrollEnabled = false,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(3) {
                                CompactCardLoading(
                                    cardSize = CompactCardSize.MEDIUM,
                                    shimmerBrush = brush
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // 6. Explore Categories Horizontal Skeleton
            item(key = "loading_explore_horizontal") {
                DashedDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .size(width = 160.dp, height = 18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false
                    ) {
                        items(5) {
                            Box(
                                modifier = Modifier
                                    .size(width = 130.dp, height = 56.dp)
                                    .clip(CategorySmallChipShape)
                                    .background(SurfaceSecondary)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                                        shape = CategorySmallChipShape
                                    )
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(brush)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 50.dp, height = 10.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(brush)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(width = 38.dp, height = 8.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(brush)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Vendors Loading State Preview")
@Composable
private fun VendorsLoadingStatePreview() {
    JasnifyTheme {
        VendorsLoadingState()
    }
}