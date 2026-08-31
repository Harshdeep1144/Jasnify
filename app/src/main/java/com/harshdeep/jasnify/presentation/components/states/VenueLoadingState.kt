package com.harshdeep.jasnify.presentation.components.states

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun VenueLoadingState(
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
            item {
                CustomTopBar(
                    title = "Venue",
                    menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                    backIcon = TopIcon.Predefined.BACK,
                    isLargeTitle = true,
                    isLeftAligned = false,
                    buttonStyle = ButtonBackground.OPAQUE,
                    onBackClick = {},
                    onMenuClick = {}
                )
            }

            // 2. Search Bar Skeleton
            item {
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
                    Spacer(Modifier.height(8.dp))
                }
            }

            // 3. Location Pill Skeleton
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .height(56.dp)
                        .clip(SquircleShape(16.dp))
                        .background(SurfaceSecondary)
                )
            }

            // 4. Venue Cards Skeletons
            items(4) {
                FullCardLoading(
                    modifier = Modifier.padding(12.dp),
                    shimmerBrush = brush
                )
            }
        }
    }
}

@Preview
@Composable
private fun VenueLoadingStatePreview() {
    JasnifyTheme {
        VenueLoadingState()
    }
}
