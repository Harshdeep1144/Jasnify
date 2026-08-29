package com.harshdeep.jasnify.presentation.components.carousels

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("FrequentlyChangingValue")
@Composable
fun CardCarousel(
    modifier: Modifier = Modifier,
    cards: List<CardData> = emptyList(),
    cardWidth: Dp = 280.dp,
    cardHeight: Dp = 373.dp,
    showControls: Boolean = true,
    isLiked: (CardData) -> Boolean = { false },
    onLikeClick: (CardData) -> Unit = {},
    onCardClick: (CardData) -> Unit = {},
    pagerState: PagerState = rememberPagerState(
        initialPage = (Int.MAX_VALUE / 2),
        pageCount = { if (cards.isEmpty()) 0 else Int.MAX_VALUE }
    )
) {
    if (cards.isEmpty()) return

    val itemCount = cards.size
    var autoScrollEnabled by remember { mutableStateOf(true) }

    // Track user drag gestures
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // Stop auto-scrolling when the user touches/swipes the pager
    LaunchedEffect(isDragged) {
        if (isDragged) {
            autoScrollEnabled = false
        }
    }

    // Auto-scroll loop on launch
    LaunchedEffect(autoScrollEnabled, cards) {
        if (autoScrollEnabled && cards.isNotEmpty()) {
            while (autoScrollEnabled) {
                delay(3000.milliseconds)
                if (autoScrollEnabled && !pagerState.isScrollInProgress) {
                    try {
                        val nextPage = pagerState.currentPage + 1
                        if (nextPage < Int.MAX_VALUE) {
                            pagerState.animateScrollToPage(nextPage)
                        } else {
                            pagerState.scrollToPage(Int.MAX_VALUE / 2)
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val horizontalPadding = (maxWidth - cardWidth) / 2

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                // Negative spacing brings the side cards closer to the center card
                pageSpacing = (-20).dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight)
            ) { page ->
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                val actualIndex = page % itemCount
                val currentCard = cards[actualIndex]

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                // Base offset scaling: Center is 1.0f, adjacent cards scale down to 0.82f
                val offsetScale = lerp(1f, 0.82f, pageOffset.coerceIn(0f, 1f))

                val pressScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.90f else 1f,
                    label = "pressScale"
                )

                val finalScale = offsetScale * pressScale

                // Stop auto-scrolling if a specific card item is pressed
                LaunchedEffect(isPressed) {
                    if (isPressed) {
                        autoScrollEnabled = false
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = finalScale
                            scaleY = finalScale
                            // Slight alpha drop on off-center cards adds more depth
                            alpha = lerp(1f, 0.65f, pageOffset.coerceIn(0f, 1f))
                        }
                        .noRippleClickable {
                            autoScrollEnabled = false
                            onCardClick(currentCard)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CardItem(
                        data = currentCard,
                        pageOffset = pageOffset,
                        shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault),
                        showControls = showControls,
                        isLiked = isLiked(currentCard),
                        onLikeClick = {
                            autoScrollEnabled = false
                            onLikeClick(currentCard)
                        },
                        modifier = Modifier
                            .width(cardWidth)
                            .fillMaxHeight()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Page Indicators
        val currentActualPage = pagerState.currentPage % itemCount

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(itemCount) { i ->
                val isActive = currentActualPage == i
                val dotWidth by animateDpAsState(
                    targetValue = if (isActive) 16.dp else 4.dp,
                    label = "dotWidth"
                )
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .height(4.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(if (isActive) ContentBrand else SurfaceSecondary)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardCarouselPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundPrimary
    ) {
        Box(contentAlignment = Alignment.Center) {
            CardCarousel()
        }
    }
}