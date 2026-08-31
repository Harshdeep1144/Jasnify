package com.harshdeep.jasnify.presentation.components.states

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

private val CardCarouselShape = SquircleShape(CornerMedium, CornerSmoothingDefault)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CardsLoadingState(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush()
) {
    val cardWidth: Dp = 280.dp
    val cardHeight: Dp = 373.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. Top Bar (Matching centered title + back and menu actions)
            CustomTopBar(
                title = "Cards",
                onBackClick = {},
                onMenuClick = {},
                backIcon = TopIcon.Predefined.BACK,
                menuIcon = TopIcon.Predefined.MENU_VERTICAL,
                isLargeTitle = true,
                buttonStyle = ButtonBackground.OPAQUE
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = false
            ) {
                // 2. Carousel Section (Center Card + Peeking Side Cards + Dots + Buttons)
                item {
                    Column(
                        modifier = Modifier.padding(vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Carousel with Side Peeking Cards
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(cardHeight)
                        ) {
                            val sideOffset = cardWidth + 8.dp

                            // Left Peeking Card Skeleton
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(x = -sideOffset)
                                    .width(cardWidth)
                                    .fillMaxHeight()
                                    .clip(CardCarouselShape)
                                    .background(brush)
                            )

                            // Right Peeking Card Skeleton
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(x = sideOffset)
                                    .width(cardWidth)
                                    .fillMaxHeight()
                                    .clip(CardCarouselShape)
                                    .background(brush)
                            )

                            // Main Center Card Skeleton
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .width(cardWidth)
                                    .fillMaxHeight()
                                    .clip(CardCarouselShape)
                                    .background(brush)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Carousel Dot Indicators
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .height(4.dp)
                                    .width(16.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                            )
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(brush)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons (Edit, Share Card, WhatsApp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Edit Icon Button
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Share Card Pill Button
                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(150.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // WhatsApp Icon Button
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                            )
                        }
                    }
                }

                // 3. Trending Header Placeholder
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 60.dp, height = 10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(brush)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(width = 190.dp, height = 20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 4. Grid Rows
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(2) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        // Card Image Skeleton (Sharp corners)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(280f / 373f)
                                                .clip(RectangleShape)
                                                .background(brush)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Likes & Shares Skeleton
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clip(CircleShape)
                                                        .background(brush)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 18.dp, height = 12.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(brush)
                                                )
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clip(CircleShape)
                                                        .background(brush)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 18.dp, height = 12.dp)
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

                // 5. Bottom Padding
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAF7F2,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun CardsLoadingStatePreview() {
    JasnifyTheme {
        CardsLoadingState()
    }
}