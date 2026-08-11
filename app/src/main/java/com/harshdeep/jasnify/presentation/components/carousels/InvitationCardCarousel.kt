package com.harshdeep.jasnify.presentation.components.carousels

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.InvitationCardData
import com.harshdeep.jasnify.presentation.components.cards.InvitationCardItem
import com.harshdeep.jasnify.theme.*
import kotlin.math.absoluteValue

@SuppressLint("FrequentlyChangingValue")
@Composable
fun InvitationCardCarousel(
    modifier: Modifier = Modifier,
    cardData: InvitationCardData = InvitationCardData(),
    cardWidth: Dp = 280.dp,
    cardHeight: Dp = 373.dp,
    showControls: Boolean = true,
    isLiked: (Int) -> Boolean = { false },
    onLikeClick: (InvitationCardData) -> Unit = {},
    onShareClick: (InvitationCardData) -> Unit = {},
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

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val currentCard = cardData.copy(backgroundRes = backgrounds[actualIndex])
                InvitationCardItem(
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
fun InvitationCardCarouselPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundPrimary
    ) {
        Box(contentAlignment = Alignment.Center) {
            InvitationCardCarousel()
        }
    }
}