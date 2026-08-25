package com.harshdeep.jasnify.presentation.screens.invitation_cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.getTemplateElements
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.CardItem
import com.harshdeep.jasnify.presentation.components.carousels.CardCarousel
import com.harshdeep.jasnify.presentation.components.explore.ExploreTrendingCards
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

private val CardCarouselShape = SquircleShape(CornerMedium, CornerSmoothingDefault)
private val GridTopShape = SquircleShape(
    CornerLargeIncrease,
    CornerLargeIncrease,
    0.dp,
    0.dp,
    CornerSmoothingDefault
)
private val GridBackgroundBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to SurfacePrimary.copy(alpha = 0.50f),
        0.40f to SurfacePrimary.copy(alpha = 0.70f),
        0.80f to SurfacePrimary.copy(alpha = 0.90f),
        1.0f to SurfacePrimary
    )
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExploreTabContent(
    jasnifyCards: List<CardData>,
    likedCards: List<CardData>,
    lazyListState: LazyListState,
    pagerState: PagerState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    canEdit: Boolean,
    nestedScrollConnection: NestedScrollConnection,
    onCardClick: (CardData, String) -> Unit,
    onLikeToggle: (CardData) -> Unit,
    onShareTrigger: (CardData) -> Unit,
    onEditDetailsClick: (CardData) -> Unit,
    onWhatsappShare: (CardData) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val trendingOffset by remember {
            derivedStateOf {
                val layoutInfo = lazyListState.layoutInfo
                val trendingItem = layoutInfo.visibleItemsInfo.find { it.index == 1 }
                trendingItem?.offset
            }
        }

        trendingOffset?.let { offsetPx ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(540.dp)
                    .offset(y = with(density) { offsetPx.toDp() } - 70.dp)
                    .drawWithCache {
                        val brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0.00f to Color(0xFFFFC09A).copy(alpha = 0.25f),
                                0.30f to Color(0xFFFFC09A).copy(alpha = 0.40f),
                                0.55f to Color(0xFFFFC09A).copy(alpha = 0.18f),
                                0.75f to BackgroundPrimary.copy(alpha = 0.08f),
                                1.00f to BackgroundPrimary.copy(alpha = 0.05f),
                            ),
                            radius = 360.dp.toPx()
                        )
                        onDrawBehind {
                            scale(
                                scaleX = 1.5f,
                                scaleY = 0.7f,
                                pivot = center
                            ) {
                                drawCircle(
                                    brush = brush,
                                    radius = 360.dp.toPx(),
                                    center = center
                                )
                            }
                        }
                    }
            )
        }

        val editPainter = painterResource(id = R.drawable.ic_edit)
        val sharePainter = painterResource(id = R.drawable.ic_share)
        val whatsappPainter = painterResource(id = R.drawable.ic_whatsapp)

        val chunkedCards = remember(jasnifyCards) {
            jasnifyCards.chunked(2)
        }

        val top10Cards = remember(jasnifyCards) {
            jasnifyCards.sortedByDescending { (it.likesCount + it.sharesCount) / 2.0 }
                .take(10)
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "carousel_section", contentType = "carousel") {
                Column(
                    modifier = Modifier.padding(vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (top10Cards.isNotEmpty()) {
                        val currentCarouselCard = top10Cards[pagerState.currentPage % top10Cards.size]
                        val carouselKey = "carousel_${currentCarouselCard.id}"

                        with(sharedTransitionScope) {
                            Box(
                                modifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = carouselKey),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    zIndexInOverlay = 1f,
                                    clipInOverlayDuringTransition = OverlayClip(CardCarouselShape)
                                )
                            ) {
                                CardCarousel(
                                    cards = top10Cards,
                                    pagerState = pagerState,
                                    isLiked = { card -> likedCards.any { it.id == card.id } },
                                    onLikeClick = onLikeToggle,
                                    onCardClick = { card -> onCardClick(card, carouselKey) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (canEdit) {
                                CustomIconButton(
                                    icon = editPainter,
                                    onClick = { onEditDetailsClick(currentCarouselCard) },
                                    type = ButtonType.Secondary
                                )
                            }

                            CustomTextButton(
                                text = "Share Card",
                                onClick = { onShareTrigger(currentCarouselCard) },
                                containerColor = ContentPrimary,
                                contentColor = ContentInvPrimary,
                                trailingIcon = sharePainter,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            CustomIconButton(
                                icon = whatsappPainter,
                                onClick = { onWhatsappShare(currentCarouselCard) },
                                containerColor = Color(0xFF1BA911),
                                contentColor = ContentInvPrimary
                            )
                        }
                    }
                }
            }

            item(key = "trending_header", contentType = "trending_header") {
                Spacer(Modifier.height(64.dp))
                ExploreTrendingCards()
            }

            item(key = "template_grid", contentType = "template_grid") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    chunkedCards.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowItems.forEach { card ->
                                val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                                val isLiked = card.likedBy.contains(currentUserId)
                                val interactionSource = remember { MutableInteractionSource() }
                                val isPressed by interactionSource.collectIsPressedAsState()
                                val scale by animateFloatAsState(
                                    targetValue = if (isPressed) 0.96f else 1f,
                                    label = "scale"
                                )
                                val trendingKey = "trending_${card.id}"

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        with(sharedTransitionScope) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(280f / 373f)
                                                    .sharedBounds(
                                                        sharedContentState = rememberSharedContentState(key = trendingKey),
                                                        animatedVisibilityScope = animatedVisibilityScope,
                                                        zIndexInOverlay = 1f,
                                                        clipInOverlayDuringTransition = OverlayClip(RectangleShape)
                                                    )
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .graphicsLayer {
                                                            scaleX = scale
                                                            scaleY = scale
                                                        }
                                                        .clip(RectangleShape)
                                                        .clickable(
                                                            interactionSource = interactionSource,
                                                            indication = null
                                                        ) { onCardClick(card, trendingKey) }
                                                ) {
                                                    CardItem(
                                                        data = card,
                                                        showControls = false,
                                                        isLiked = isLiked,
                                                        shape = RectangleShape,
                                                        onLikeClick = { onLikeToggle(card) },
                                                        onShareClick = { onShareTrigger(card) },
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                }
                                            }
                                        }

                                        CardInteractionRow(
                                            card = card,
                                            isLiked = isLiked,
                                            onLikeClick = { onLikeToggle(card) },
                                            onShareClick = { onShareTrigger(card) }
                                        )
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item(key = "footer", contentType = "footer") {
                Spacer(modifier = Modifier.height(16.dp))
                FooterJansify()
            }
        }
    }
}

@Composable
private fun CardInteractionRow(
    card: CardData,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onLikeClick
            )
        ) {
            Icon(
                painter = painterResource(id = if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_top_bar_heart),
                contentDescription = "Like",
                tint = if (isLiked) Color.Unspecified else ContentPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${card.likesCount}",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentPrimary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onShareClick
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share",
                tint = ContentPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${card.sharesCount}",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentPrimary
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, backgroundColor = 0xFFFAF7F2, widthDp = 390, heightDp = 844)
@Composable
fun ExploreTabContentPreview() {
    JasnifyTheme {
        val sampleTemplates = remember {
            listOf(
                CardData(
                    id = "template_0",
                    backgroundRes = R.drawable.bg_invitation_card_01,
                    bgName = "Classic Elegance",
                    elements = getTemplateElements(0)
                ),
                CardData(
                    id = "template_1",
                    backgroundRes = R.drawable.bg_invitation_card_02,
                    bgName = "Floral Romance",
                    elements = getTemplateElements(1)
                ),
                CardData(
                    id = "template_2",
                    backgroundRes = R.drawable.bg_invitation_card_03,
                    bgName = "Golden Glamour",
                    elements = getTemplateElements(2)
                ),
                CardData(
                    id = "template_3",
                    backgroundRes = R.drawable.bg_invitation_card_04,
                    bgName = "Modern Minimalist",
                    elements = getTemplateElements(3)
                )
            )
        }

        val exploreLazyListState = rememberLazyListState()
        val explorePagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { sampleTemplates.size }
        )

        val dummyNestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
            }
        }

        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                ExploreTabContent(
                    jasnifyCards = sampleTemplates,
                    likedCards = emptyList(),
                    lazyListState = exploreLazyListState,
                    pagerState = explorePagerState,
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    canEdit = true,
                    nestedScrollConnection = dummyNestedScrollConnection,
                    onCardClick = { _, _ -> },
                    onLikeToggle = {},
                    onShareTrigger = {},
                    onEditDetailsClick = {},
                    onWhatsappShare = {}
                )
            }
        }
    }
}