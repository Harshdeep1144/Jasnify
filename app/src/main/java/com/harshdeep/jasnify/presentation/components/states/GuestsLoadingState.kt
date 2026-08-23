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
import androidx.compose.foundation.layout.width
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
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun GuestsLoadingState(
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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Top Bar
            item(key = "loading_top_bar") {
                CustomTopBar(
                    title = "Guests",
                    titleIcon = painterResource(R.drawable.ill_guests),
                    menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                    isLeftAligned = true,
                    isLargeTitle = true,
                    buttonStyle = ButtonBackground.OPAQUE,
                    onMenuClick = {}
                )
            }

            // 2. Search Bar + Add Button Row
            item(key = "loading_search_add_row") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Bar Skeleton
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
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
                                    .size(width = 120.dp, height = 16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Add Button Skeleton
                    Box(
                        modifier = Modifier
                            .size(width = 84.dp, height = 48.dp)
                            .clip(RoundedCornerShape(100))
                            .background(brush)
                    )
                }
            }

            // 3. Filter Chips LazyRow
            item(key = "loading_filter_chips") {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(width = 104.dp, height = 36.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(brush)
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(width = 110.dp, height = 36.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(brush)
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(width = 118.dp, height = 36.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(brush)
                        )
                    }
                }
            }

            // 4. Guest Cards Stack (using exact continuous squircle border shapes)
            val guestCardCount = 7
            items(guestCardCount, key = { "loading_guest_$it" }) { index ->
                val topRadius = if (index == 0) CornerLargeIncrease else CornerExtraSmall
                val bottomRadius = if (index == guestCardCount - 1) CornerLargeIncrease else CornerExtraSmall

                val itemShape = SquircleShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomStart = bottomRadius,
                    bottomEnd = bottomRadius,
                    cornerSmoothing = CornerSmoothingDefault
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 1.dp)
                        .clip(itemShape)
                        .background(SurfaceSecondary)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Skeleton
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(brush)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Name and Guest Type Label Skeleton
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.38f)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )
                        }

                        // Mark Invited Action Button Skeleton
                        Box(
                            modifier = Modifier
                                .size(width = 96.dp, height = 36.dp)
                                .clip(RoundedCornerShape(100))
                                .background(brush)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Guests Loading State Preview")
@Composable
private fun GuestsLoadingStatePreview() {
    JasnifyTheme {
        GuestsLoadingState()
    }
}