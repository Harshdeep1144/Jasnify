package com.harshdeep.jasnify.presentation.components.states

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.theme.TopBrandGradientBrush
import sv.lib.squircleshape.SquircleShape

private val BudgetSetCardShape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault)

private val FirstItemShape = SquircleShape(
    topStart = CornerLarge,
    topEnd = CornerLarge,
    bottomStart = CornerExtraSmall,
    bottomEnd = CornerExtraSmall,
    cornerSmoothing = CornerSmoothingDefault
)
private val LastItemShape = SquircleShape(
    topStart = CornerExtraSmall,
    topEnd = CornerExtraSmall,
    bottomStart = CornerLarge,
    bottomEnd = CornerLarge,
    cornerSmoothing = CornerSmoothingDefault
)
private val MiddleItemShape = SquircleShape(CornerExtraSmall, CornerSmoothingDefault)

@Composable
fun BudgetLoadingState(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush(),
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceSecondary)
    ) {
        // Gradient overlay spanning behind the TopBar and Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(TopBrandGradientBrush)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar wrapper matching BudgetTrackerContent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Budget Tracker",
                    onBackClick = onBackClick,
                    onMenuClick = {},
                    isLargeTitle = true,
                    buttonStyle = ButtonBackground.TRANSLUCENT
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                userScrollEnabled = false
            ) {
                // Budget Summary Card Skeleton
                item(key = "loading_budget_summary") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(BudgetSetCardShape)
                                .background(Color.White.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Total Budget Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                                    }

                                    // Edit button shimmer placeholder
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(brush)
                                    )
                                }

                                // Remaining Funds Block
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 110.dp, height = 12.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(brush)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 150.dp, height = 28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(brush)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(brush)
                                        )
                                    }
                                }

                                // View Summary Button Placeholder
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(SquircleShape(CornerLarge, CornerSmoothingDefault))
                                        .background(brush)
                                )
                            }
                        }
                    }
                }

                // Header: Title + Search Bar + Filter Button
                item(key = "loading_search_filter_header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 140.dp, height = 24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Search bar skeleton
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(100))
                                    .background(SurfacePrimary)
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
                                            .size(width = 110.dp, height = 16.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(brush)
                                    )
                                }
                            }

                            // Filter button skeleton
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(SquircleShape(100, 0f))
                                    .background(SurfacePrimary)
                            )
                        }
                    }
                }

                // Expense Card Shimmers
                val shimmerCardCount = 4
                items(shimmerCardCount, key = { "loading_card_$it" }) { index ->
                    val isFirst = index == 0
                    val isLast = index == shimmerCardCount - 1

                    val itemShape = when {
                        isFirst -> FirstItemShape
                        isLast -> LastItemShape
                        else -> MiddleItemShape
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 1.dp)
                            .background(color = SurfacePrimary, shape = itemShape)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Emoji Avatar Placeholder
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                            )

                            // Title & Category Placeholders
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 120.dp, height = 16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brush)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(width = 75.dp, height = 12.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brush)
                                )
                            }

                            // Amount Label Placeholder
                            Box(
                                modifier = Modifier
                                    .size(width = 60.dp, height = 20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )
                        }
                    }
                }

                item(key = "loading_bottom_spacer") {
                    Spacer(modifier = Modifier.height(124.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Budget Loading State Preview")
@Composable
private fun BudgetLoadingStatePreview() {
    JasnifyTheme {
        BudgetLoadingState()
    }
}