package com.harshdeep.jasnify.presentation.components.states

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.theme.TopBrandGradientBrush
import sv.lib.squircleshape.SquircleShape

private val CellGroupShape = SquircleShape(CornerLarge, CornerSmoothingDefault)
private val PlanCardShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)

@Composable
fun ProfileLoadingState(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush()
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            // 1. User Header Skeleton with Top Brand Gradient
            item(key = "loading_user_header") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TopBrandGradientBrush)
                        .statusBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp, 28.dp, 28.dp, 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 128.dp Avatar Skeleton
                        Box(
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .background(brush)
                        )

                        Spacer(Modifier.height(12.dp))

                        // Username Skeleton
                        Box(
                            modifier = Modifier
                                .size(width = 140.dp, height = 24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )

                        Spacer(Modifier.height(4.dp))

                        // User Handle Skeleton
                        Box(
                            modifier = Modifier
                                .size(width = 100.dp, height = 16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )

                        Spacer(Modifier.height(16.dp))

                        // "Edit Profile" Button Skeleton
                        Box(
                            modifier = Modifier
                                .size(width = 110.dp, height = 36.dp)
                                .clip(RoundedCornerShape(100))
                                .background(brush)
                        )
                    }
                }
            }

            // 2. Plan Cards Carousel Skeleton (280.dp width)
            item(key = "loading_plan_cards") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState(), enabled = false)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(2) {
                        Surface(
                            modifier = Modifier.width(280.dp),
                            color = SurfaceSecondary,
                            shape = PlanCardShape
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 100.dp, height = 20.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(brush)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(width = 60.dp, height = 14.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(brush)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(width = 60.dp, height = 22.dp)
                                            .clip(RoundedCornerShape(100))
                                            .background(brush)
                                    )
                                }
                                Spacer(Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .size(width = 80.dp, height = 16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brush)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Grid Action Cells Skeleton (Manage Events / My Enquiries)
            item(key = "loading_grid_actions") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(2) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CellGroupShape)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                    shape = CellGroupShape
                                ),
                            color = SurfacePrimary,
                            shape = CellGroupShape
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(brush)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brush)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brush)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Menu Items Skeleton (Account Settings, Appearance/Notifications, Terms/Privacy, Logout)
            item(key = "loading_menu_items") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Single Item: Account Settings
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CellGroupShape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                shape = CellGroupShape
                            )
                            .background(SurfacePrimary)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(width = 130.dp, height = 16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )
                        }
                    }

                    // Grouped Items: Appearance + Notifications
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                shape = CellGroupShape
                            )
                            .clip(CellGroupShape)
                            .background(SurfacePrimary)
                    ) {
                        repeat(2) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(brush)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(width = if (index == 0) 100.dp else 115.dp, height = 16.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(brush)
                                    )
                                }
                            }
                        }
                    }

                    // Grouped Items: Terms of Use + Privacy Policy
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                shape = CellGroupShape
                            )
                            .clip(CellGroupShape)
                            .background(SurfacePrimary)
                    ) {
                        repeat(2) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(brush)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(width = if (index == 0) 105.dp else 115.dp, height = 16.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(brush)
                                    )
                                }
                            }
                        }
                    }

                    // Single Item: Logout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CellGroupShape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                shape = CellGroupShape
                            )
                            .background(SurfacePrimary)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(width = 65.dp, height = 16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )
                        }
                    }
                }
            }

            item(key = "loading_bottom_spacer") {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Profile Loading State Preview")
@Composable
private fun ProfileLoadingStatePreview() {
    JasnifyTheme {
        ProfileLoadingState()
    }
}