package com.harshdeep.jasnify.presentation.components.carousels

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.theme.*
import kotlin.math.absoluteValue

@SuppressLint("FrequentlyChangingValue")
@Composable
fun CardCarousel(
    modifier: Modifier = Modifier,
    cardData: CardData = CardData(),
    cardWidth: Dp = 280.dp,
    cardHeight: Dp = 373.dp,
    showControls: Boolean = true,
    isLiked: (Int) -> Boolean = { false },
    onLikeClick: (CardData) -> Unit = {},
    onShareClick: (CardData) -> Unit = {},
    onCardClick: (CardData) -> Unit = {},
    pagerState: PagerState = rememberPagerState(
        initialPage = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % 5),
        pageCount = { Int.MAX_VALUE }
    )
) {
    val backgrounds = listOf(
        R.drawable.bg_invitation_card_01,
        R.drawable.bg_invitation_card_02,
        R.drawable.bg_invitation_card_03,
        R.drawable.bg_invitation_card_04,
        R.drawable.bg_invitation_card_05
    )

    val itemCount = backgrounds.size

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 60.dp),
            pageSpacing = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
        ) { page ->
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
            val actualIndex = page % itemCount
            val currentCard = cardData.copy(backgroundRes = backgrounds[actualIndex])

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.96f else 1f,
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onCardClick(currentCard) },
                contentAlignment = Alignment.Center
            ) {
                CardItem(
                    data = currentCard,
                    pageOffset = pageOffset,
                    showControls = showControls,
                    isLiked = isLiked(backgrounds[actualIndex]),
                    onLikeClick = { onLikeClick(currentCard) },
                    onShareClick = { onShareClick(currentCard) },
                    modifier = Modifier.size(cardWidth, cardHeight)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Indicators
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
